package com.wynntils.modules.core.events;

import com.google.gson.Gson;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.core.instances.PlayerLocationProfile;
import com.wynntils.modules.core.managers.SocketManager;
import com.wynntils.webapi.WebManager;
import io.socket.client.Ack;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SocketEvents implements Listener {

    boolean sentAuth = false;

    @SubscribeEvent
    public void connectionEvent(SocketEvent.ConnectionEvent e) {
        Reference.LOGGER.info("Connected to websocket");

        Socket socket = e.getSocket();
        socket.emit("authenticate", WebManager.getAccount().getToken(), new Ack() {
            @Override
            public void call(Object... args) {
                System.out.println("Check ack");
                if (sentAuth) { // If this is a reconnect, send friend list and world again.
                    if (!PlayerInfo.getPlayerInfo().getFriendList().isEmpty()) {
                        String json = new Gson().toJson(PlayerInfo.getPlayerInfo().getFriendList());
                        socket.emit("update friends", json);
                    }
                    if (Reference.onWorld) SocketManager.getSocket().emit("join world", Reference.getUserWorld());
                }
                sentAuth = true;
            }
        });
    }

    @SubscribeEvent
    public void updatePlayerLocation(SocketEvent.FriendEvent.LocationUpdate e) {
        PlayerLocationProfile profile = PlayerLocationProfile.getInstance(e.uuid, e.username);
        if (!profile.isTrackable()) profile.setTrackable(true);
        profile.updateManually(e.x, e.y, e.z);
    }

    @SubscribeEvent
    public void stopTracking(SocketEvent.FriendEvent.StopTracking e) {
        PlayerLocationProfile.getInstance(e.uuid, e.username).setTrackable(false);
    }

}
