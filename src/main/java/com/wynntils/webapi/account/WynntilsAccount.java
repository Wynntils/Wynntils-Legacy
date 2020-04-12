/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.account;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.utils.helpers.MD5Verification;
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
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class WynntilsAccount {

    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("wynntils-accounts-%d").build());

    String token;
    boolean ready = false;

    HashMap<String, String> encondedConfigs = new HashMap<>();
    HashMap<String, String> md5Verifications = new HashMap<>();
    CloudConfigurations configurationUploader;

    public WynntilsAccount() { }

    public String getToken() {
        return token;
    }

    public HashMap<String, String> getEncondedConfigs() {
        return encondedConfigs;
    }

    int connectionAttempts = 0;

    public void login() {
        if (WebManager.getApiUrls() == null || connectionAttempts >= 4) return;
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
        authParams.addProperty("username", ModCore.mc().getSession().getUsername());
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
                    configFiles.entrySet().forEach((k) -> encondedConfigs.put(k.getKey(), k.getValue().getAsString()));

                    ready = true;

                    Reference.LOGGER.info("Succesfully connected to Athena!");
                    return true;
                }).onError(t -> { login(); return true; });

        handler.addAndDispatch(responseEncryption);
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

    public void uploadConfig(File f) {
        if (!ready || configurationUploader == null) return;

        configurationUploader.queueConfig(f);
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
