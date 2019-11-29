package com.wynntils.core.events.custom;

import com.wynntils.modules.core.managers.SocketManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class SocketEvent extends Event {

    Socket socket;

    public SocketEvent() {
        this.socket = SocketManager.getSocket();
    }

    public static class ConnectionEvent extends SocketEvent {

        public ConnectionEvent() {

        }

        public Socket getSocket() {
            return socket;
        }
    }

    public static class FriendEvent extends SocketEvent {

        public UUID uuid;
        public String username;

        public FriendEvent(UUID uuid) {
            this(uuid, null);
        }

        public FriendEvent(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }

        public Socket getSocket() {
            return socket;
        }

        /**
         * Fired when socket receives new location of player
         */
        public static class LocationUpdate extends FriendEvent {

            public int x, y, z;

            public LocationUpdate(UUID uuid, int x, int y, int z) {
                this(uuid, null, x, y, z);
            }

            public LocationUpdate(UUID uuid, String username, int x, int y, int z) {
                super(uuid, username);
                this.x = x;
                this.y = y;
                this.z = z;
            }

        }

        /**
         * Called when socket says that a player has unfriended you
         */
        public static class Unfriend extends FriendEvent {

            public Unfriend(UUID uuid) {
                this(uuid, null);
            }

            public Unfriend(UUID uuid, String username) {
                super(uuid, username);
            }

        }

    }


}
