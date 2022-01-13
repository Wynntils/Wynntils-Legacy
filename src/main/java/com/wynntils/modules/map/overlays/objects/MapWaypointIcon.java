/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.instances.WaypointProfile.WaypointType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MapWaypointIcon extends MapTextureIcon {

    public static final int HIDDEN_ZOOM = -1;

    private static FloatBuffer currentColorBuf = BufferUtils.createFloatBuffer(16);
    private static int[] sizeMapping;
    private static int waypointTypesCount;
    private static final int texPosXIndex = 0;
    private static final int texPosZIndex = 1;
    private static final int texSizeXIndex = 2;
    private static final int texSizeZIndex = 3;

    private static void setSize(WaypointType type, int texPosX, int texPosZ, int texSizeX, int texSizeZ) {
        assert type.ordinal() < waypointTypesCount;
        int i = type.ordinal() * 4;
        sizeMapping[i + texPosXIndex] = texPosX;
        sizeMapping[i + texPosZIndex] = texPosZ;
        sizeMapping[i + texSizeXIndex] = texSizeX;
        sizeMapping[i + texSizeZIndex] = texSizeZ;
    }

    static {
        waypointTypesCount = WaypointType.class.getEnumConstants().length;

        assert waypointTypesCount == 13 : "If you added a new waypoint type, specify the dimensions here";

        sizeMapping = new int[waypointTypesCount * 4];

        setSize(WaypointType.LOOTCHEST_T1, 136, 35, 154, 53);
        setSize(WaypointType.LOOTCHEST_T2, 118, 35, 136, 53);
        setSize(WaypointType.LOOTCHEST_T3,  82, 35, 100, 53);
        setSize(WaypointType.LOOTCHEST_T4, 100, 35, 118, 53);
        setSize(WaypointType.DIAMOND, 172, 37, 190, 55);
        setSize(WaypointType.FLAG, 154, 36, 172, 54);
        setSize(WaypointType.SIGN, 190, 36, 208, 54);
        setSize(WaypointType.STAR, 208, 36, 226, 54);
        setSize(WaypointType.TURRET, 229, 37, 241, 53);
        setSize(WaypointType.FARMING, 24, 53, 42, 71);
        setSize(WaypointType.FISHING, 42, 53, 60, 71);
        setSize(WaypointType.MINING, 60, 53, 78, 71);
        setSize(WaypointType.WOODCUTTING, 78, 53, 96, 71);
    }

    private WaypointProfile wp;

    public MapWaypointIcon(WaypointProfile wp) {
        WaypointType type = wp.getType();
        assert type.ordinal() < waypointTypesCount : "Invalid enum value in WaypointType: " + wp.getType().ordinal();

        this.wp = wp;
    }

    /**
     * Return a MapWaypointIcon that can render a WaypointType being free from position information
     */
    public static MapWaypointIcon getFree(WaypointType type) {
        return getFree(type, null);
    }

    public static MapWaypointIcon getFree(WaypointType type, CustomColor color) {
        return new MapWaypointIcon(new WaypointProfile("", 0, 0, 0, color, type, 0));
    }

    @Override public AssetsTexture getTexture() {
        return Textures.Map.map_icons;
    }

    @Override public int getPosX() {
        return (int)wp.getX();
    }

    @Override public int getPosZ() {
        return (int)wp.getZ();
    }

    @Override public String getName() {
        return wp.getName();
    }

    @Override
    public int getTexPosX() {
        return sizeMapping[wp.getType().ordinal() * 4 + texPosXIndex];
    }

    @Override
    public int getTexPosZ() {
        return sizeMapping[wp.getType().ordinal() * 4 + texPosZIndex];
    }

    @Override
    public int getTexSizeX() {
        return sizeMapping[wp.getType().ordinal() * 4 + texSizeXIndex];
    }

    @Override
    public int getTexSizeZ() {
        return sizeMapping[wp.getType().ordinal() * 4 + texSizeZIndex];
    }

    @Override
    public float getSizeX() {
        int i = wp.getType().ordinal() * 4;
        return (sizeMapping[i + texSizeXIndex] - sizeMapping[i + texPosXIndex]) / 2.5f;
    }

    @Override
    public float getSizeZ() {
        int i = wp.getType().ordinal() * 4;
        return (sizeMapping[i + texSizeZIndex] - sizeMapping[i + texPosZIndex]) / 2.5f;
    }

    @Override
    public int getZoomNeeded() {
        return wp.getZoomNeeded();
    }

    @Override
    public boolean isEnabled(boolean forMinimap) {
        return wp.getZoomNeeded() != HIDDEN_ZOOM;
    }

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        int distancePlayerWp = 0;
        float percentage = 1f;
        // TODO: Find a better solution to detect whether icon is being drawn on minimap
        if (MapConfig.Waypoints.INSTANCE.iconFade && McIf.mc().currentScreen == null) {
            // If negative the waypoint is above the player
            distancePlayerWp = (int) (McIf.player().posY - wp.getY());

            if (MathHelper.abs(distancePlayerWp) > MapConfig.Waypoints.INSTANCE.iconFadeScale) return;
            percentage = (float) ((1 - (MathHelper.abs(distancePlayerWp) / (float) MapConfig.Waypoints.INSTANCE.iconFadeScale)) * 0.8 + 0.2);
        }

        CustomColor color = wp.getColor();
        if (color != null) {
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, currentColorBuf);
            if (distancePlayerWp < 0) {
                // Lighten icon
                GL11.glColor4f(color.r, color.g, color.b, color.a * percentage * currentColorBuf.get(3));
            } else {
                // Darken icon
                GL11.glColor4f(color.r * percentage, color.g * percentage, color.b * percentage, color.a * currentColorBuf.get(3));
            }
        }
        super.renderAt(renderer, centreX, centreZ, sizeMultiplier, blockScale);
        if (color != null) {
            GL11.glColor4f(currentColorBuf.get(0), currentColorBuf.get(1), currentColorBuf.get(2), currentColorBuf.get(3));
        }
    }

    public WaypointProfile getWaypointProfile() {
        return wp;
    }

    private static List<MapIcon> waypoints = null;


    public static List<MapIcon> getWaypoints() {
        if (waypoints == null) {
            resetWaypoints();
        }
        return waypoints;
    }

    public static void resetWaypoints() {
        if (waypoints == null) {
            waypoints = new ArrayList<>();
        } else {
            waypoints.clear();
        }
        MapConfig.Waypoints.INSTANCE.waypoints.forEach(c -> waypoints.add(new MapWaypointIcon(c)));
    }
}
