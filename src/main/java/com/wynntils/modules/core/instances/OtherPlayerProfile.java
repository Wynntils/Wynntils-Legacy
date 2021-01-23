/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.modules.core.managers.GuildAndFriendManager;
import com.wynntils.modules.core.managers.PlayerEntityManager;
import com.wynntils.modules.map.overlays.objects.MapPlayerIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OtherPlayerProfile {

    public final UUID uuid;
    private String username;
    private int x;
    private int y;
    private int z;
    private boolean hasHat = true;
    private boolean isFriend = false;
    private boolean isGuildmate = false;
    private boolean isMutualFriend = false;
    private boolean inSameWorld = false;

    private static Map<UUID, OtherPlayerProfile> profiles = new HashMap<>();
    private static Map<String, OtherPlayerProfile> nameMap = new HashMap<>();

    private OtherPlayerProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public static OtherPlayerProfile getInstance(UUID uuid, String username) {
        OtherPlayerProfile existingInstance = getInstanceIfExists(uuid, username);
        if (existingInstance == null) {
            OtherPlayerProfile newProfile = new OtherPlayerProfile(uuid, username);
            profiles.put(uuid, newProfile);
            if (username != null) {
                nameMap.put(username, newProfile);
                GuildAndFriendManager.tryResolveName(uuid, username);
            }

            return newProfile;
        } else {
            return existingInstance;
        }
    }

    private static OtherPlayerProfile getInstanceIfExists(UUID uuid, String username) {
        OtherPlayerProfile profile = profiles.get(uuid);
        if (profile != null && profile.username == null && username != null) {
            profile.username = username;
            nameMap.put(username, profile);

            GuildAndFriendManager.tryResolveName(uuid, username);
        }

        return profile;
    }

    public static OtherPlayerProfile getInstanceByName(String username) {
        return nameMap.get(username);
    }

    public static Collection<OtherPlayerProfile> getAllInstances() {
        return profiles.values();
    }

    public NetworkPlayerInfo getPlayerInfo() {
        NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        return conn == null ? null : conn.getPlayerInfo(uuid);
    }

    public boolean isOnWorld() {
        return inSameWorld;
    }

    public void setOnWorld(boolean onWorld) {
        boolean oldTrackable = isTrackable();
        inSameWorld = onWorld;

        if (isTrackable() != oldTrackable) {
            onTrackableChange();
        }
    }

    public boolean hasEntityInWorld() {
        return PlayerEntityManager.containsUUID(uuid);
    }

    /**
     * @return true if the player entity was found on the world and location can be live-updated.
     */
    public boolean updateLocationFromWorld() {
        EntityPlayer e = PlayerEntityManager.getPlayerByUUID(uuid);
        if (e == null) return false;

        hasHat = e.isWearing(EnumPlayerModelParts.HAT);

        if (e.isDead || e.getDistance(Minecraft.getMinecraft().player) >= 30) return false;
        x = (int) e.posX;
        y = (int) e.posY;
        z = (int) e.posZ;

        return true;
    }

    public void updateLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void updateLocation() {
        if (!isTrackable()) {
            x = y = z = Integer.MIN_VALUE;
            return;
        }

        if (updateLocationFromWorld()) {
            // TODO: tell socket server to stop sending player location if not already stopped
        } else {
            // TODO: tell socket server to start sending player location if stopped
        }

    }

    public int getX() {
        updateLocation();

        return x;
    }

    public int getY() {
        updateLocation();

        return y;
    }

    public int getZ() {
        updateLocation();

        return z;
    }

    public String getUsername() {
        if (username == null) {
            NetworkPlayerInfo info = getPlayerInfo();
            if (info != null) {
                username = info.getGameProfile().getName();
                nameMap.put(username, this);
                GuildAndFriendManager.tryResolveName(uuid, username);
            }
        }

        return username;
    }

    private void onTrackableChange() {
        MapPlayerIcon.updatePlayers();

        updateLocation();
    }

    public void setIsFriend(boolean isFriend) {
        boolean oldTrackable = isTrackable();
        this.isFriend = isFriend;
        this.isMutualFriend = isFriend && isMutualFriend;
        if (isTrackable() != oldTrackable) {
            onTrackableChange();
        }
    }

    public void setMutualFriend(boolean isMutualFriend) {
        boolean oldTrackable = isTrackable();
        this.isMutualFriend = isMutualFriend;
        this.isFriend = true;
        if (isTrackable() != oldTrackable) {
            onTrackableChange();
        }
    }

    public void setInGuild(boolean isInGuild) {
        boolean oldTrackable = isTrackable();
        this.isGuildmate = isInGuild;

        if (isTrackable() != oldTrackable) {
            onTrackableChange();
        }
    }

    public void setInParty(boolean isInGuild) {
        boolean oldTrackable = isTrackable();
        this.isGuildmate = isInGuild;
        if (isTrackable() != oldTrackable) {
            onTrackableChange();
        }
    }

    /**
     * @return Whether the current player is friends with the player in this OtherPlayerProfile
     */
    public boolean isFriend() {
        return isFriend;
    }

    /**
     * @return Whether the player in this OtherPlayerProfile is friends with the current player and vice versa
     */
    public boolean isMutualFriend() {
        return isMutualFriend;
    }

    public boolean isInParty() {
        String username = getUsername();
        if (username == null) return false;

        return PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers().contains(username);
    }

    public boolean isGuildmate() {
        return isGuildmate;
    }

    public boolean isTrackable() {
        return (isMutualFriend || isInParty() || isGuildmate) && inSameWorld;
    }

    public boolean hasHat() {
        return hasHat;
    }

}
