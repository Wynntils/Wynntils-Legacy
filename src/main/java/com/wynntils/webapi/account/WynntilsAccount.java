/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.account;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.professions.GatheringMaterial;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.request.PostRequest;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CryptManager;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

import java.io.File;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WynntilsAccount {

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("wynntils-accounts-%d").build());

    String token;
    boolean ready = false;
    boolean failed = false;

    HashMap<String, String> encodedConfigs = new HashMap<>();
    HashMap<String, String> md5Verifications = new HashMap<>();
    CloudConfigurations configurationUploader;
    List<Request> queue = new ArrayList<>();
    List<Request> allQueue = new ArrayList<>();

    public WynntilsAccount() { }

    public String getToken() {
        return token;
    }

    public HashMap<String, String> getEncodedConfigs() {
        return encodedConfigs;
    }

    public void dumpEncodedConfig(String name) {
        encodedConfigs.remove(name);
    }

    int connectionAttempts = 0;

    public boolean login() {
        if (WebManager.getApiUrls() == null || connectionAttempts >= 4) {
            failed = true;
            queue.clear();
            if (!allQueue.isEmpty()) {
                for (Request request : allQueue) {
                    WebManager.getHandler().addRequest(request);
                }
                WebManager.getHandler().dispatchAsync();
                allQueue.clear();
            }
            return false;
        }
        connectionAttempts++;

        RequestHandler handler = WebManager.getHandler();

        String baseUrl = WebManager.getApiUrls().get("Athena");
        String[] secretKey = new String[1]; // it's an array for the lambda below be able to set it's value

        // generating secret key

        Request getPublicKey = new Request(baseUrl + "/auth/getPublicKey", "getPublicKey")
                .handleJsonObject(json -> {
                    if (!json.has("publicKeyIn")) {
                        failed = true;
                        queue.clear();
                        if (!allQueue.isEmpty()) {
                            for (Request request : allQueue) {
                                WebManager.getHandler().addRequest(request);
                            }
                            WebManager.getHandler().dispatchAsync();
                            allQueue.clear();
                        }
                        return false;
                    }

                    secretKey[0] = parseAndJoinPublicKey(json.get("publicKeyIn").getAsString());
                    // response

                    JsonObject authParams = new JsonObject();
                    authParams.addProperty("username", ModCore.mc().getSession().getUsername());
                    authParams.addProperty("key", secretKey[0]);
                    authParams.addProperty("version", Reference.VERSION + "_" + Reference.BUILD_NUMBER);

                    Request responseEncryption = new PostRequest(baseUrl + "/auth/responseEncryption", "responseEncryption")
                            .postJsonElement(authParams)
                            .handleJsonObject(response -> {
                                if (!response.has("authToken")) {
                                    failed = true;
                                    queue.clear();
                                    if (!allQueue.isEmpty()) {
                                        for (Request request : allQueue) {
                                            WebManager.getHandler().addRequest(request);
                                        }
                                        WebManager.getHandler().dispatchAsync();
                                        allQueue.clear();
                                    }
                                    return false;
                                }

                                token = response.get("authToken").getAsString();

                                // md5 hashes
                                JsonObject hashes = response.getAsJsonObject("hashes");
                                hashes.entrySet().forEach((k) -> md5Verifications.put(k.getKey(), k.getValue().getAsString()));

                                configurationUploader = new CloudConfigurations(service, token);

                                // configurations
                                JsonObject configFiles = response.getAsJsonObject("configFiles");
                                configFiles.entrySet().forEach((k) -> encodedConfigs.put(k.getKey(), k.getValue().getAsString()));

                                ready = true;
                                if (!queue.isEmpty()) {
                                    for (Request request : queue) {
                                        handler.addRequest(request);
                                    }
                                    handler.dispatchAsync();
                                }

                                Reference.LOGGER.info("Successfully connected to Athena!");
                                return true;
                            }).onError(t -> {
                                login();
                                return true;
                            });

                    handler.addAndDispatch(responseEncryption, true);
                    return true;
                }).onError(t -> {
                    login();
                    return true;
                }).onTimeout(() -> {
                    failed = true;
                    queue.clear();
                    if (!allQueue.isEmpty()) {
                        for (Request request : allQueue) {
                            WebManager.getHandler().addRequest(request);
                        }
                        WebManager.getHandler().dispatchAsync();
                        allQueue.clear();
                    }
                });

        handler.addAndDispatch(getPublicKey, true);

        return true;
    }

    public void updateDiscord(String id, String username) {
        if (failed || WebManager.getApiUrls() == null) return;

        JsonObject postData = new JsonObject();
        postData.addProperty("authToken", token);
        postData.addProperty("id", id);
        postData.addProperty("username", username);

        Request request = new PostRequest(WebManager.getApiUrls().get("Athena") + "/user/updateDiscord", "updateDiscord")
                .postJsonElement(postData)
                .handleJsonObject(json -> true);

        if (ready) {
            RequestHandler handler = WebManager.getHandler();
            handler.addAndDispatch(request, true);
        } else {
            queue.add(request);
        }
    }

    public void sendGatheringSpot(ProfessionType type, GatheringMaterial material, Location loc) {
        if (failed || WebManager.getApiUrls() == null) return;

        JsonObject postData = new JsonObject();
        postData.addProperty("authToken", token);

        JsonObject spot = new JsonObject();
        spot.addProperty("type", type.toString());
        spot.addProperty("material", material.toString());
        spot.addProperty("x", (int)loc.getX());
        spot.addProperty("y", (int)loc.getY());
        spot.addProperty("z", (int)loc.getZ());

        postData.add("spot", spot);

        Request request = new PostRequest(WebManager.getApiUrls().get("Athena") + "/telemetry/sendGatheringSpot", "gatheringSpot" + loc.toString())
                .postJsonElement(postData)
                .handleJsonObject(json -> true);

        if (ready) {
            WebManager.getHandler().addAndDispatch(request, true);
        } else {
            queue.add(request);
        }
    }

    public void uploadConfig(File f) {
        if (failed || configurationUploader == null) {
            if(!login()) return;

            uploadConfig(f); // try again
            return;
        }

        configurationUploader.queueConfig(f);
    }

    public void queue(Request request) {
        allQueue.add(request);
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isFailed() {
        return failed;
    }

    private String parseAndJoinPublicKey(String key) {
        try {
            byte[] publicKeyBy = DatatypeConverter.parseHexBinary(key);

            SecretKey secretkey = CryptManager.createNewSharedKey();

            PublicKey publicKey = CryptManager.decodePublicKey(publicKeyBy);

            String s1 = (new BigInteger(CryptManager.getServerIdHash("", publicKey, secretkey))).toString(16);

            Minecraft mc = ModCore.mc();
            mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), s1.toLowerCase());

            byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretkey.getEncoded());

            return DatatypeConverter.printHexBinary(secretKeyEncrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public String getMD5Verification(String key) {
        String digest = md5Verifications.getOrDefault(key, null);
        return MD5Verification.isMd5Digest(digest) ? digest : null;
    }

}
