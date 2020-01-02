/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.account;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.Reference;
import com.wynntils.webapi.WebManager;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CloudConfigurations {

    private ScheduledExecutorService service;
    private ScheduledFuture runningTask;
    private String token;

    private Gson gson = new Gson();

    public CloudConfigurations(ScheduledExecutorService service, String token) {
        this.service = service; this.token = token;
    }

    private final List<ConfigContainer> toUpload = new ArrayList<>();

    public void queueConfig(String fileName, String base64) {
        synchronized (toUpload) {
            toUpload.add(new ConfigContainer(fileName, base64));

            startUploadQueue();
        }
    }

    private void startUploadQueue() {
        if (runningTask != null && !runningTask.isDone() && !runningTask.isCancelled() || WebManager.getApiUrls() == null) return;

        runningTask = service.scheduleAtFixedRate(() -> {
            List<ConfigContainer> uploading;
            synchronized (toUpload) {
                uploading = removeDuplicates(toUpload);
                toUpload.clear();
            }

            if (uploading.isEmpty()) return;

            Reference.LOGGER.info("Uploading configurations...");

            JsonArray body = new JsonArray();
            for (ConfigContainer container : uploading) {
                body.add(gson.toJsonTree(container));
            }

            try {
                URLConnection st = new URL(WebManager.getApiUrls().get("UserAccount") + "uploadConfig/" + token).openConnection();

                byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
                st.setRequestProperty("User-Agent", "WynntilsClient/v" + Reference.VERSION + "/B" + Reference.BUILD_NUMBER);
                st.setRequestProperty("Content-Length", "" + bodyBytes.length);
                st.setRequestProperty("Content-Type", "application/json");
                st.setDoOutput(true);

                OutputStream outputStream = null;
                try {
                    outputStream = st.getOutputStream();
                    IOUtils.write(bodyBytes, outputStream);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(outputStream);
                }

                JsonObject finalResult = new JsonParser().parse(IOUtils.toString(st.getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
                if (finalResult.has("result")) {
                    Reference.LOGGER.info("Configuration upload complete!");
                } else {
                    Reference.LOGGER.info("Configuration upload failed!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();

                synchronized (toUpload) {
                    toUpload.addAll(0, uploading);
                }
            }

        }, 0, 10, TimeUnit.SECONDS);
    }

    private static List<ConfigContainer> removeDuplicates(List<ConfigContainer> toUpload) {
        HashSet<String> seen = new HashSet<>(toUpload.size() * 2);

        ArrayList<ConfigContainer> withoutDuplicates = new ArrayList<>(toUpload.size());

        // Newer configs trump older configs so reverse iterate
        for (int i = toUpload.size(); i-- > 0; ) {
            ConfigContainer cc = toUpload.get(i);
            if (seen.add(cc.fileName)) {
                withoutDuplicates.add(cc);
            }
        }

        Collections.reverse(withoutDuplicates);
        return withoutDuplicates;
    }

    private static class ConfigContainer {

        String fileName, base64;

        ConfigContainer(String fileName, String base64) {
            this.fileName = fileName; this.base64 = base64;
        }

    }

}
