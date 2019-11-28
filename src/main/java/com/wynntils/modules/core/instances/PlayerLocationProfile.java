package com.wynntils.modules.core.instances;

import com.wynntils.modules.core.managers.PlayerEntityManager;
import com.wynntils.modules.map.overlays.objects.MapPlayerIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.UUID;

public class PlayerLocationProfile {

    public final UUID uuid;
    private String username;
    private int x;
    private int y;
    private int z;
    private WeakReference<EntityPlayer> entityRef = new WeakReference<>(null);
    private boolean isTrackable = false;
    private boolean hasHat = false;

    private static LinkedHashMap<UUID, PlayerLocationProfile> profiles = new LinkedHashMap<>();

    private PlayerLocationProfile(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public static PlayerLocationProfile getInstance(UUID uuid, String username) {
        PlayerLocationProfile existingInstance = profiles.get(uuid);
        if (existingInstance == null) {
            PlayerLocationProfile newProfile = new PlayerLocationProfile(uuid, username);
            profiles.put(uuid, newProfile);
            return newProfile;
        } else {
            return existingInstance;
        }
    }

    public NetworkPlayerInfo getPlayerInfo() {
        NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        return conn == null ? null : conn.getPlayerInfo(uuid);
    }

    public boolean isOnWorld() {
        return getPlayerInfo() != null;
    }

    public boolean hasEntityInWorld() {
        return PlayerEntityManager.containsUUID(uuid);
    }

    /**
     * @return true if the player entity was found on the world and location can be live-updated.
     */
    public boolean updateFromWorld() {
        EntityPlayer e = entityRef.get();
        boolean canRender = e != null && e.getDistance(Minecraft.getMinecraft().player) < 30;
        if (e != null && canRender) {
            x = (int) e.posX;
            y = (int) e.posY;
            z = (int) e.posZ;
            return true;
        }
        e = PlayerEntityManager.getPlayerByUUID(uuid);
        if (e == null) {
            return false;
        }
        hasHat = e.isWearing(EnumPlayerModelParts.HAT);
        entityRef = new WeakReference<>(e);
        x = (int) e.posX;
        y = (int) e.posY;
        z = (int) e.posZ;
        return true;
    }

    public void updateManually(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private void updateLocation() {
        if (!isTrackable || !isOnWorld()) {
            x = y = z = Integer.MIN_VALUE;
            return;
        }

        if (updateFromWorld()) {
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
            }
        }
        return username;
    }

    public void setTrackable(boolean trackable) {
        if (isTrackable != trackable) {
            isTrackable = trackable;
            if (isTrackable) {
                MapPlayerIcon.addFriend(this);
            } else {
                MapPlayerIcon.removeFriend(this);
            }
        }
    }

    public boolean isTrackable() {
        return isTrackable;
    }

    public boolean hasHat() {
        return hasHat;
    }

}
