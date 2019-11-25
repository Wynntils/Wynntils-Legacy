package com.wynntils.modules.core.managers;

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

        registerEvents();
    }

    private static void registerEvents() {
        socket.on(Socket.EVENT_CONNECT, (Object... args) -> FrameworkManager.getEventBus().post(new SocketEvent.ConnectionEvent(args))
        ).on("event", (Object... args) -> {
            // Trigger forge event ~
        }).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("Disconnected from websocket"));
    }
}
