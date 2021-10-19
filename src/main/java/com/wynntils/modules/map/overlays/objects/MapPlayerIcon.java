/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.core.instances.OtherPlayerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
    public void renderAt(float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        enableAlpha();
        disableBlend();

        beginGL(0, 0);
        {
            float sizeX = getSizeX() * sizeMultiplier;
            float sizeZ = getSizeZ() * sizeMultiplier;

            ResourceLocation res = getResource();

            CommonColors outlineColor = null;
            if (profile.isInParty())
                outlineColor = CommonColors.YELLOW;
            else if (profile.isMutualFriend())
                outlineColor = CommonColors.GREEN;
            else if (profile.isGuildmate())
                outlineColor = CommonColors.LIGHT_BLUE;

            if (outlineColor != null) drawRectF(outlineColor,
                    (centreX) - sizeX - .5f,
                    (centreZ) - sizeZ - .5f,
                    (centreX) + sizeX + .5f,
                    (centreZ) + sizeZ + .5f
            );

            McIf.mc().getTextureManager().bindTexture(res);

            drawScaledCustomSizeModalRect(
                    ((centreX + drawingOrigin().x) -sizeX),
                    ((centreZ + drawingOrigin().y) -sizeZ),
                    8f, 8, 8, 8,
                    sizeX * 2f,
                    sizeZ * 2f,
                    64f, 64f);

            if (profile.hasHat())
                drawScaledCustomSizeModalRect(-sizeX, -sizeZ, 40.0F, 8, 8, 8, sizeX * 2f, sizeZ * 2f, 64.0F, 64.0F);

        }
        endGL();

        enableBlend();
    }

    @Override
    public boolean followRotation() {
        return false;
    }

    @Override
    public boolean hasDynamicLocation() {
        return true;
    }

    public static void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + uWidth) * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + uWidth) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
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
