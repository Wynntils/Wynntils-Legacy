package com.wynntils.modules.core.managers;

import com.wynntils.modules.core.instances.OtherPlayerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;
import java.util.UUID;

public class PartyGuildFriendManager {

    private static class UnresolvedInfo {
        boolean inGuild;
        boolean inParty;
        boolean isFriend;
    }

    private static HashMap<String, UnresolvedInfo> unresolvedNames = new HashMap<>();

    public static void tryResolveNames() {
        // Try to resolve names from the connection map
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;
        NetHandlerPlayClient conn = player.connection;
        if (conn == null) return;
        for (NetworkPlayerInfo i : conn.getPlayerInfoMap()) {
            String name = i.getGameProfile().getName();
            if (name == null) continue;
            tryResolveName(i.getGameProfile().getId(), name);
        }
    }

    public static void tryResolveName(UUID uuid, String name) {
        UnresolvedInfo u = unresolvedNames.remove(name);
        if (u != null) {
            OtherPlayerProfile p = OtherPlayerProfile.getInstance(uuid, name);
            p.setInGuild(u.inGuild);
            p.setInParty(u.inParty);
            p.setIsFriend(u.isFriend);
        }
    }

    public enum As {
        FRIEND, PARTY, GUILD
    }

    public static void changePlayer(String name, boolean to, As as, boolean tryResolving) {
        OtherPlayerProfile p = OtherPlayerProfile.getInstanceByName(name);
        if (p != null) {
            switch (as) {
                case FRIEND: p.setIsFriend(to); break;
                case PARTY: p.setInParty(to); break;
                case GUILD: p.setInGuild(to); break;
            }
            return;
        }
        UnresolvedInfo newInfo = new UnresolvedInfo();
        UnresolvedInfo u = unresolvedNames.getOrDefault(name, newInfo);
        switch (as) {
            case FRIEND: u.isFriend = to; break;
            case PARTY: u.inParty = to; break;
            case GUILD: u.inGuild = to; break;
        }
        if (u == newInfo) unresolvedNames.put(name, newInfo);
        if (tryResolving) tryResolveNames();
    }

}
