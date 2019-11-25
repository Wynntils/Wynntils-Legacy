package com.wynntils.modules.core.events;

import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.core.managers.SocketManager;
import com.wynntils.webapi.WebManager;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SocketEvents implements Listener {

    Socket socket = SocketManager.getSocket();

    @SubscribeEvent
    public void connectionEvent(SocketEvent.ConnectionEvent e) {
        socket.emit("authenticate", WebManager.getAccount().getToken());
    }
}
