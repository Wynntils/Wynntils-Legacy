package com.wynntils.core.events.custom;

import com.wynntils.modules.core.managers.SocketManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SocketEvent extends Event {

    Socket socket;

    public SocketEvent() {
        this.socket = SocketManager.getSocket();
    }

    public static class ConnectionEvent extends SocketEvent {

        public ConnectionEvent(Object... args) {

        }

        public Socket getSocket() {
            return socket;
        }
    }

    public static class UpdateFriendLocation extends SocketEvent {

        public String username;
        public int x, y, z;

        public UpdateFriendLocation(String username, int x, int y, int z) {
            this.username = username;
            this.x = x;
            this.y = y;
            this.z = z;

        }

        public Socket getSocket() {
            return socket;
        }
    }


}
