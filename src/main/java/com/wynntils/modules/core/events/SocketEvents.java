package com.wynntils.modules.core.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.interfaces.Listener;
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
}
