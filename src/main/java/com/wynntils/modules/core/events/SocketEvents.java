package com.wynntils.modules.core.events;

import com.google.gson.Gson;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.SocketEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.core.instances.OtherPlayerProfile;
import com.wynntils.modules.core.managers.SocketManager;
import com.wynntils.webapi.WebManager;
import io.socket.client.Ack;
import io.socket.client.Socket;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SocketEvents implements Listener {

    boolean reconnection = false;

    @SubscribeEvent
    public void connectionEvent(SocketEvent.ConnectionEvent e) {
        Reference.LOGGER.info("Connected to websocket");
        Socket socket = e.getSocket();

        socket.emit("authenticate", WebManager.getAccount().getToken(), (Ack) args -> {
            if (reconnection) {  // If this is a reconnect, send friend list and world again.
                if (!PlayerInfo.getPlayerInfo().getFriendList().isEmpty())
                    socket.emit("update friends", new Gson().toJson(PlayerInfo.getPlayerInfo().getFriendList()));

                if (Reference.onWorld) SocketManager.getSocket().emit("join world", Reference.getUserWorld());

                if (Reference.onWorld && PlayerInfo.getPlayerInfo().getPlayerParty().isPartying()
                        && !PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers().isEmpty())
                    SocketManager.getSocket().emit("update party", new Gson().toJson(PlayerInfo.getPlayerInfo().getPlayerParty().getPartyMembers()));

                reconnection = false;
            }

            if (WebManager.getPlayerProfile().getGuildName() != null && WebManager.getPlayerProfile().getGuildName() != "") {
                socket.emit("set guild", WebManager.getPlayerProfile().getGuildName());
            }
        });
    }

    @SubscribeEvent
    public void reconnectionEvent(SocketEvent.ReConnectionEvent e) {
        Reference.LOGGER.info("Reconnection successful");

        reconnection = true;
    }

    @SubscribeEvent
    public void updatePlayerLocation(SocketEvent.OtherPlayerEvent.LocationUpdate e) {
        OtherPlayerProfile profile = OtherPlayerProfile.getInstance(e.uuid, e.username);
        profile.setOnWorld(true);
        profile.setMutualFriend(e.isMutualFriend);
        profile.setInGuild(e.isInGuild);
        profile.setInParty(e.isPartyMember);

        profile.updateLocation(e.x, e.y, e.z);
    }

    @SubscribeEvent
    public void playerLeft(SocketEvent.OtherPlayerEvent.Left e) {
        OtherPlayerProfile.getInstance(e.uuid, e.username).setOnWorld(false);
    }

    @SubscribeEvent
    public void unfriend(SocketEvent.OtherPlayerEvent.Unfriend e) {
        OtherPlayerProfile.getInstance(e.uuid, e.username).setMutualFriend(false);
    }

}
