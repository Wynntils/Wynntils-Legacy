package com.wynntils.modules.core.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.core.instances.PlayerLocationProfile;
import com.wynntils.webapi.WebManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SocketEvents implements Listener {
    @SubscribeEvent
    public void connectionEvent(SocketEvent.ConnectionEvent e) {
        Reference.LOGGER.info("Connected to websocket");

        Socket socket = e.getSocket();
        socket.emit("authenticate", WebManager.getAccount().getToken());
    }

    @SubscribeEvent
    public void updatePlayerLocation(SocketEvent.FriendEvent.LocationUpdate e) {
        PlayerLocationProfile profile = PlayerLocationProfile.getInstance(e.uuid, e.username);
        if (!profile.isTrackable()) profile.setTrackable(true);
        profile.updateManually(e.x, e.y, e.z);
    }

    @SubscribeEvent
    public void stopTracking(SocketEvent.FriendEvent.StopTracking e) {
        PlayerLocationProfile.getInstance(e.uuid, null).setTrackable(false);
    }

}
