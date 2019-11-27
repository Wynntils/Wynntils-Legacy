package com.wynntils.modules.core.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Location;
import com.wynntils.webapi.WebManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

public class SocketEvents implements Listener {

    public static HashMap<String, Location> friendLocations = new HashMap<String, Location>();

    @SubscribeEvent
    public void connectionEvent(SocketEvent.ConnectionEvent e) {
        Reference.LOGGER.info("Connected to websocket");

        Socket socket = e.getSocket();
        socket.emit("authenticate", WebManager.getAccount().getToken());
    }

    @SubscribeEvent
    public void updatePlayerLocation(SocketEvent.UpdateFriendLocation e) {
        // Draw icon on map at x y z
        friendLocations.put(e.username, new Location(e.x, e.y, e.z));
    }


}
