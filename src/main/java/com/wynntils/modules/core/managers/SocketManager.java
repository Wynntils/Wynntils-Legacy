package com.wynntils.modules.core.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.FrameworkManager;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

public class SocketManager {

    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }

    public static void registerSocket() {
        Reference.LOGGER.info("Register Socket");

        IO.Options opts = new IO.Options();
        String[] trans = {"websocket"};
        opts.transports = trans;

        boolean local = true; // testing mode

        String url;
        if (local) {
            url = "http://localhost:3000";
        } else {
            url = "https://dev.wynntils.com";
        }

        try {
            socket = IO.socket(url, opts);
        } catch (URISyntaxException e) {
            System.out.println("SOCKET ERROR : ");
            System.out.println(e.getMessage());
        }

        // Register Events

        registerEvents();

        socket.connect();
    }

    private static void registerEvents() {
        Gson gson = new Gson();
        // Register Events
        socket.on(Socket.EVENT_CONNECT, (Object... args) -> {
            Reference.LOGGER.info("Websocket connection event...");
            FrameworkManager.getEventBus().post(new SocketEvent.ConnectionEvent(args));
        }).on("update player location on map", (Object... args) -> {
            // Trigger forge event ~
            String json = (String) args[0];

            SocketEvent.UpdateFriendLocation profile = gson.fromJson(json, SocketEvent.UpdateFriendLocation.class);

            FrameworkManager.getEventBus().post(new SocketEvent.UpdateFriendLocation(profile.username, profile.uuid, profile.x, profile.y, profile.z));
        }).on("update player locations on map", (Object... args) -> {
            // Trigger forge event ~
            System.out.println(args);
            String json = (String) args[0];

            JsonArray a = gson.fromJson(json, JsonArray.class);

            a.forEach(j -> {
                SocketEvent.UpdateFriendLocation profile = gson.fromJson(j, SocketEvent.UpdateFriendLocation.class);

                FrameworkManager.getEventBus().post(new SocketEvent.UpdateFriendLocation(profile.username, profile.uuid, profile.x, profile.y, profile.z));
            });

        }).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("Disconnected from websocket"));
    }
}
