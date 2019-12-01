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

    public static class ReConnectionEvent extends SocketEvent {

        public ReConnectionEvent() {

        }

        public Socket getSocket() {
            return socket;
        }
    }

    public static class OtherPlayerEvent extends SocketEvent {

        public UUID uuid;
        public String username;

        public OtherPlayerEvent(UUID uuid) {
            this(uuid, null);
        }

        public OtherPlayerEvent(UUID uuid, String username) {
            this.uuid = uuid;
            this.username = username;
        }

        public Socket getSocket() {
            return socket;
        }

        /**
         * Fired when socket receives new location of player
         */
        public static class LocationUpdate extends OtherPlayerEvent {

            public int x, y, z;
            public boolean isMutualFriend;

            public LocationUpdate(UUID uuid, int x, int y, int z, boolean isMutualFriend) {
                this(uuid, null, x, y, z, isMutualFriend);
            }

            public LocationUpdate(UUID uuid, String username, int x, int y, int z, boolean isMutualFriend) {
                super(uuid, username);
                this.x = x;
                this.y = y;
                this.z = z;
                this.isMutualFriend = isMutualFriend;
            }

        }

        /**
         * Called when socket says that a player has unfriended you
         */
        public static class Unfriend extends OtherPlayerEvent {

            public Unfriend(UUID uuid) {
                this(uuid, null);
            }

            public Unfriend(UUID uuid, String username) {
                super(uuid, username);
            }

        }

        /**
         * Called when socket says that a player that was previously tracked has left your world
         */
        public static class Left extends OtherPlayerEvent {

            public Left(UUID uuid) {
                this(uuid, null);
            }

            public Left(UUID uuid, String username) {
                super(uuid, username);
            }

        }

    }


}
