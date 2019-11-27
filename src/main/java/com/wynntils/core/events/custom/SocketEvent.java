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

        public FriendEvent(UUID uuid) {
            this.uuid = uuid;
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
                super(uuid);
                this.x = x;
                this.y = y;
                this.z = z;
            }

        }

        /**
         * Called when socket says to stop tracking player
         * (When they have unfriended you)
         */
        public static class StopTracking extends FriendEvent {

            public StopTracking(UUID uuid) {
                super(uuid);
            }

        }

    }


}
