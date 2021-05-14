/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.account;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.professions.GatheringMaterial;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.request.PostRequest;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraft.util.CryptManager;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.SecretKey;

import java.io.File;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WynntilsAccount {

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("wynntils-accounts-%d").build());

    String token;
    boolean ready = false;

    HashMap<String, String> encodedConfigs = new HashMap<>();
    HashMap<String, String> md5Verifications = new HashMap<>();
    CloudConfigurations configurationUploader;

    public WynntilsAccount() { }

    public String getToken() {
        return token;
    }

    public boolean isConnected() {
        return ready;
    }

    public HashMap<String, String> getEncodedConfigs() {
        return encodedConfigs;
    }

    public void dumpEncodedConfig(String name) {
        encodedConfigs.remove(name);
    }

    int connectionAttempts = 0;

    public boolean login() {
        if (WebManager.getApiUrls() == null || connectionAttempts >= 4) return false;
        connectionAttempts++;

        RequestHandler handler = WebManager.getHandler();

        String baseUrl = WebManager.getApiUrls().get("Athena");
        String[] secretKey = new String[1]; // it's an array for the lambda below be able to set it's value

        // generating secret key

        Request getPublicKey = new Request(baseUrl + "/auth/getPublicKey", "getPublicKey")
                .handleJsonObject(json -> {
                    if (!json.has("publicKeyIn")) return false;

                    secretKey[0] = parseAndJoinPublicKey(json.get("publicKeyIn").getAsString());
                    return true;
                }).onError(t -> { login(); return true; });

        handler.addAndDispatch(getPublicKey);

        // response

        JsonObject authParams = new JsonObject();
        authParams.addProperty("username", McIf.mc().getSession().getUsername());
        authParams.addProperty("key", secretKey[0]);
        authParams.addProperty("version", Reference.VERSION + "_" + Reference.BUILD_NUMBER);

        Request responseEncryption = new PostRequest(baseUrl + "/auth/responseEncryption", "responseEncryption")
                .postJsonElement(authParams)
                .handleJsonObject(json -> {
                    if (!json.has("authToken")) return false;

                    token = json.get("authToken").getAsString();

                    // md5 hashes
                    JsonObject hashes = json.getAsJsonObject("hashes");
                    hashes.entrySet().forEach((k) -> md5Verifications.put(k.getKey(), k.getValue().getAsString()));

                    configurationUploader = new CloudConfigurations(service, token);

                    // configurations
                    JsonObject configFiles = json.getAsJsonObject("configFiles");
                    configFiles.entrySet().forEach((k) -> encodedConfigs.put(k.getKey(), k.getValue().getAsString()));

                    ready = true;

                    Reference.LOGGER.info("Successfully connected to Athena!");
                    return true;
                }).onError(t -> { login(); return true; });

        handler.addAndDispatch(responseEncryption);

        return true;
    }

    public void updateDiscord(String id, String username) {
        if (!ready || WebManager.getApiUrls() == null) return;

        JsonObject postData = new JsonObject();
        postData.addProperty("authToken", token);
        postData.addProperty("id", id);
        postData.addProperty("username", username);

        Request request = new PostRequest(WebManager.getApiUrls().get("Athena") + "/user/updateDiscord", "updateDiscord")
                .postJsonElement(postData)
                .handleJsonObject(json -> true);

        RequestHandler handler = WebManager.getHandler();
        handler.addAndDispatch(request, true);
    }

    public void sendGatheringSpot(ProfessionType type, GatheringMaterial material, Location loc) {
        if (!ready || WebManager.getApiUrls() == null) return;

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

        WebManager.getHandler().addAndDispatch(request, true);
    }

    public void uploadConfig(File f) {
        if (!ready || configurationUploader == null) return;

        configurationUploader.queueConfig(f);
    }

    private String parseAndJoinPublicKey(String key) {
        try {
            byte[] publicKeyBy = Hex.decodeHex(key.toCharArray());

            SecretKey secretkey = CryptManager.createNewSharedKey();

            PublicKey publicKey = CryptManager.decodePublicKey(publicKeyBy);

            String s1 = (new BigInteger(CryptManager.getServerIdHash("", publicKey, secretkey))).toString(16);

            McIf.mc().getSessionService().joinServer(McIf.mc().getSession().getProfile(), McIf.mc().getSession().getToken(), s1.toLowerCase());

            byte[] secretKeyEncrypted = CryptManager.encryptData(publicKey, secretkey.getEncoded());

            return Hex.encodeHexString(secretKeyEncrypted);
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
