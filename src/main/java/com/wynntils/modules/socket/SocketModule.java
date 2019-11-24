package com.wynntils.modules.socket;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.socket.events.ClientEvents;
import com.wynntils.modules.socket.events.ServerEvents;
import com.wynntils.webapi.WebManager;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;

@ModuleInfo(name = "socket", displayName = "Socket")
public class SocketModule extends Module {

    private static SocketModule module;

    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }

    public static SocketModule getModule() {
        return module;
    }

    public void onEnable() {
        module = this;

        System.out.println("SOCKET MODULE ENABLED");

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

        socket.on(Socket.EVENT_CONNECT, (Object... args) -> {
            System.out.println("Connected to websocket");
            socket.emit("authenticate", WebManager.getAccount().getToken());
        }).on("event", (Object... args) -> {
            // Do something
        }).on(Socket.EVENT_DISCONNECT, (Object... args) -> System.out.println("Disconnected from websocket"));


        socket.connect();

        registerEvents(new ServerEvents());
        registerEvents(new ClientEvents());

    }

}
