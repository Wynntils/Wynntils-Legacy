/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.core.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.BroadcastType;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import io.socket.client.IO;
import io.socket.client.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class SocketManager {

    private static Socket socket;
    private static final boolean local = false;

    public static void registerSocket() {
        if(Reference.onServer) return;

        if (WebManager.getApiUrls().get("EnableSocket").equalsIgnoreCase("false")
        && CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE) return;

        Reference.LOGGER.info("Connecting to the Socket Server...");

        SSLContext mySSLContext = null;
        try {
            mySSLContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        TrustManager[] trustAllCerts = new TrustManager[]{ new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) { }
            public void checkServerTrusted(X509Certificate[] chain, String authType) { }
        }};

        try {
            mySSLContext.init(null, trustAllCerts, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        IO.Options opts = new IO.Options();
        opts.transports = new String[] { "websocket" };
        opts.sslContext = mySSLContext;
        opts.hostnameVerifier = (hostname, session) -> true;

        String url;
        if (local) url = "http://localhost:3000";
        else url = WebManager.getApiUrls().get("Socket");

        try {
            socket = IO.socket(url, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Register Events
        registerEvents();

        socket.connect();
    }

    public static void disconnectSocket() {
        if (socket != null && socket.connected()) socket.disconnect();
    }

    private static void registerEvents() {
        Gson gson = new Gson();
        // Register Events
        socket.on(Socket.EVENT_CONNECT, (Object... args) -> {
            FrameworkManager.getEventBus().post(new SocketEvent.ConnectionEvent());
        }).on(Socket.EVENT_CONNECT_ERROR, (Object... args) -> {
            Reference.LOGGER.error("Socket failed to connect:", args[0]);
        }).on(Socket.EVENT_RECONNECT, (Object... args) -> {
            FrameworkManager.getEventBus().post(new SocketEvent.ReConnectionEvent());
        }).on("update player location on map", (Object... args) -> {
            // Trigger forge event ~
            String json = (String) args[0];

            FriendLocationUpdate profile = gson.fromJson(json, FriendLocationUpdate.class);
            FrameworkManager.getEventBus().post(new SocketEvent.OtherPlayerEvent.LocationUpdate(StringUtils.uuidFromString(profile.uuid), profile.username, profile.x, profile.y, profile.z, profile.isMutualFriend, profile.isPartyMember, profile.isInGuild));
        }).on("update player locations on map", (Object... args) -> {
            // Trigger forge event ~
            String json = (String) args[0];
            JsonArray a = gson.fromJson(json, JsonArray.class);
            a.forEach(j -> {
                FriendLocationUpdate profile = gson.fromJson(j, FriendLocationUpdate.class);
                FrameworkManager.getEventBus().post(new SocketEvent.OtherPlayerEvent.LocationUpdate(StringUtils.uuidFromString(profile.uuid), profile.username, profile.x, profile.y, profile.z, profile.isMutualFriend, profile.isPartyMember, profile.isInGuild));
            });
        }).on("left world", (Object... args) -> {
            String uuid = (String) args[0];
            String username = (String) args[1];

            FrameworkManager.getEventBus().post(new SocketEvent.OtherPlayerEvent.Left(StringUtils.uuidFromString(uuid), username));
        }).on("broadcast", (Object... args) -> {
            BroadcastType type = BroadcastType.valueOf((String)args[0]);
            if (type == null) type = BroadcastType.MESSAGE;

            FrameworkManager.getEventBus().post(new SocketEvent.BroadcastEvent(type, (String)args[1]));
        }).on("unfriend", (Object... args) -> {
            // Trigger forge event ~
            String uuid = (String) args[0];
            String username = (String) args[1];

            FrameworkManager.getEventBus().post(new SocketEvent.OtherPlayerEvent.Unfriend(StringUtils.uuidFromString(uuid), username));
        }).on(Socket.EVENT_DISCONNECT, (Object... args) -> Reference.LOGGER.info("Disconnected from the Socket Server"));
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void emitEvent(String eventName, Object... options) {
        if (socket == null) return;

        socket.emit(eventName, options);
    }

    private static class FriendLocationUpdate {

        public String username, uuid;
        public int x, y, z;
        public boolean isMutualFriend, isPartyMember, isInGuild;

    }

}
