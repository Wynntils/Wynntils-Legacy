package com.wynntils.core.events.custom;

import com.wynntils.modules.core.managers.SocketManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SocketEvent extends Event {

    Socket socket = SocketManager.getSocket();

    public SocketEvent() {
    }

    public static class ConnectionEvent extends SocketEvent {

        public ConnectionEvent(Object... args) {
            System.out.println("Connected to websocket");
        }
    }


}
