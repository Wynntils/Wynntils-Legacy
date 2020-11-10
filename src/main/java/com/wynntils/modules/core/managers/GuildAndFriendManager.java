/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.core.managers;

import com.wynntils.modules.core.instances.OtherPlayerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildAndFriendManager {

    private static class UnresolvedInfo {
        Boolean inGuild;
        Boolean isFriend;
    }

    private static Map<String, UnresolvedInfo> unresolvedNames = new HashMap<>();

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
            if (u.inGuild != null) p.setInGuild(u.inGuild);
            if (u.isFriend != null) p.setIsFriend(u.isFriend);
        }
    }

    public enum As {
        FRIEND, GUILD
    }

    public static void changePlayer(String name, boolean to, As as, boolean tryResolving) {
        OtherPlayerProfile p = OtherPlayerProfile.getInstanceByName(name);
        if (p != null) {
            switch (as) {
                case FRIEND: p.setIsFriend(to); break;
                case GUILD: p.setInGuild(to); break;
            }
            return;
        }

        UnresolvedInfo newInfo = new UnresolvedInfo();
        UnresolvedInfo u = unresolvedNames.getOrDefault(name, newInfo);
        switch (as) {
            case FRIEND: u.isFriend = to ? Boolean.TRUE : Boolean.FALSE; break;
            case GUILD: u.inGuild = to ? Boolean.TRUE : Boolean.FALSE; break;
        }

        if (u == newInfo) unresolvedNames.put(name, newInfo);
        if (tryResolving) tryResolveNames();
    }

}
