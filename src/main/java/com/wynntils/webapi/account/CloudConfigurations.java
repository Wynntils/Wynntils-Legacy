/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.account;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.Reference;
import com.wynntils.webapi.WebManager;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CloudConfigurations {

    ScheduledExecutorService service;
    ScheduledFuture runningTask;
    String token;

    Gson gson = new Gson();

    public CloudConfigurations(ScheduledExecutorService service, String token) {
        this.service = service; this.token = token;
    }

    List<ConfigContainer> toUpload = Collections.synchronizedList(new ArrayList<>());

    public void queueConfig(String fileName, String base64) {
        toUpload.add(new ConfigContainer(fileName, base64));

        startUploadQueue();
    }

    private void startUploadQueue() {
        if(runningTask != null && !runningTask.isDone() && !runningTask.isCancelled()) return;

        runningTask = service.scheduleAtFixedRate(() -> {
            if(toUpload.size() == 0) return;

            Reference.LOGGER.info("Uploading configurations...");

            JsonArray body = new JsonArray();
            for(ConfigContainer container : toUpload) {
                body.add(gson.toJsonTree(container));
            }

            try{
                URLConnection st = new URL(WebManager.apiUrls.get("UserAccount") + "uploadConfig/" + token).openConnection();

                byte[] bodyBytes = body.toString().getBytes(Charsets.UTF_8);
                st.setRequestProperty("User-Agent", "WynntilsClient/v" + Reference.VERSION + "/B" + Reference.BUILD_NUMBER);
                st.setRequestProperty("Content-Length", "" + bodyBytes.length);
                st.setRequestProperty("Content-Type", "application/json");
                st.setDoOutput(true);

                OutputStream outputStream = null;
                try {
                    outputStream = st.getOutputStream();
                    IOUtils.write(bodyBytes, outputStream);
                }catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(outputStream);
                }

                JsonObject finalResult = new JsonParser().parse(IOUtils.toString(st.getInputStream())).getAsJsonObject();
                if(finalResult.has("result")) {
                    Reference.LOGGER.info("Configuration upload complete!");
                }else{
                    Reference.LOGGER.info("Configuration upload failed!");
                }

                toUpload.clear();
            }catch (Exception ex) { ex.printStackTrace(); }

        },0, 10, TimeUnit.SECONDS);
    }

    private class ConfigContainer {

        String fileName, base64;

        public ConfigContainer(String fileName, String base64) {
            this.fileName = fileName; this.base64 = base64;
        }

    }

}
