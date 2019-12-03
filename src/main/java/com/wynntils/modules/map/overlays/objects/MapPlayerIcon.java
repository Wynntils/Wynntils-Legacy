package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.core.instances.OtherPlayerProfile;
import com.wynntils.modules.map.overlays.ui.WorldMapUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.client.renderer.GlStateManager.*;

public class MapPlayerIcon extends MapIcon {

    private OtherPlayerProfile profile;

    private MapPlayerIcon(OtherPlayerProfile profile) {
        this.profile = profile;
    }

    private static List<MapIcon> playerIcons = Collections.emptyList();

    public static void updatePlayers() {
        playerIcons = OtherPlayerProfile.getAllInstances().stream().filter(OtherPlayerProfile::isTrackable).map(MapPlayerIcon::new).collect(Collectors.toList());
    }

    public static List<MapIcon> getPlayers() {
        return playerIcons;
    }

    public OtherPlayerProfile getProfile() {
        return profile;
    }

    @Override
    public int getPosX() {
        int x = profile.getX();
        return x == Integer.MIN_VALUE ? NO_LOCATION : x;
    }

    @Override
    public int getPosZ() {
        int z = profile.getZ();
        return z == Integer.MIN_VALUE ? NO_LOCATION : z;
    }

    @Override
    public String getName() {
        return profile.getUsername();
    }

    @Override
    public float getSizeX() {
        return 8;
    }

    @Override
    public float getSizeZ() {
        return 8;
    }

    @Override
    public int getZoomNeeded() {
        return ANY_ZOOM;
    }

    @Override
    public boolean isEnabled(boolean forMinimap) {
        return profile.isTrackable() && profile.isOnWorld();
    }

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        color(1.0F, 1.0F, 1.0F, 1.0F);
        enableAlpha();
        disableBlend();
        disableBlend();

        { pushMatrix();
            float sizeX = getSizeX() * sizeMultiplier / 4;
            float sizeZ = getSizeZ() * sizeMultiplier / 4;
            boolean worldMapOpen = Minecraft.getMinecraft().currentScreen instanceof WorldMapUI;
            translate(centreX - (sizeX * (worldMapOpen ? 4 : -3)), centreZ - (sizeZ * (worldMapOpen ? 4 : -3)), 0);
            scale(sizeX, sizeZ, 1);
            ScreenRenderer.scale(1);
            ResourceLocation res = getResource();
            Minecraft.getMinecraft().getTextureManager().bindTexture(res);


            if (worldMapOpen) { // Is messy on minimap
                CommonColors outlineColor = null;
                if (profile.isMutualFriend()) {
                    outlineColor = CommonColors.GREEN;
                } else if (profile.isInParty()) {
                    outlineColor = CommonColors.YELLOW;
                } else if (profile.isGuildmate()) {
                    outlineColor = CommonColors.LIGHT_BLUE;
                }

                if (outlineColor != null) renderer.drawRect(outlineColor, -1, -1, 9, 9);

            }

            Gui.drawScaledCustomSizeModalRect(0, 0, 8.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);

            if (profile.hasHat())
                Gui.drawScaledCustomSizeModalRect(0, 0, 40.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);

        } popMatrix();
    }

    @Override
    public boolean followRotation() {
        return false;
    }

    @Override
    public boolean hasDynamicLocation() {
        return true;
    }

    private ResourceLocation cachedResource;
    private static final ResourceLocation STEVE_SKIN = DefaultPlayerSkin.getDefaultSkin(new UUID(0, 0));
    private static final ResourceLocation ALEX_SKIN = DefaultPlayerSkin.getDefaultSkin(new UUID(0, 1));
    static {
        assert DefaultPlayerSkin.getSkinType(new UUID(0, 0)).equals("default");
        assert DefaultPlayerSkin.getSkinType(new UUID(0, 1)).equals("slim");
    }

    public ResourceLocation getResource() {
        if (cachedResource != null && cachedResource != STEVE_SKIN && cachedResource != ALEX_SKIN) return cachedResource;
        NetworkPlayerInfo info = profile.getPlayerInfo();
        return cachedResource = (info != null ? info.getLocationSkin() : DefaultPlayerSkin.getDefaultSkin(profile.uuid));
    }

}
