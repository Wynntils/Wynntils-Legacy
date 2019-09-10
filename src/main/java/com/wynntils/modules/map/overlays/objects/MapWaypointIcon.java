package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.instances.WaypointProfile.WaypointType;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MapWaypointIcon extends MapTextureIcon {
    public static final int HIDDEN_ZOOM = -1;

    private static FloatBuffer currentColorBuf = BufferUtils.createFloatBuffer(16);
    private static int[] sizeMapping = null;
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

        assert waypointTypesCount == 9 : "If you added a new waypoint type, specify the dimensions here";

        sizeMapping = new int[waypointTypesCount * 4];

        setSize(WaypointType.LOOTCHEST_T1, 136, 35, 154, 53);
        setSize(WaypointType.LOOTCHEST_T2, 118, 35, 136, 53);
        setSize(WaypointType.LOOTCHEST_T3,  82, 35, 100, 53);
        setSize(WaypointType.LOOTCHEST_T4, 100, 35, 118, 53);
        setSize(WaypointType.DIAMOND, 172, 37, 190, 55);
        setSize(WaypointType.FLAG, 154, 36, 172, 54);
        setSize(WaypointType.SIGN, 190, 36, 208, 54);
        setSize(WaypointType.STAR, 208, 36, 226, 54);
        setSize(WaypointType.TURRET, 226, 36, 244, 54);
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

    @Override public int getTexPosX() {
        return sizeMapping[wp.getType().ordinal() * 4 + texPosXIndex];
    }

    @Override public int getTexPosZ() {
        return sizeMapping[wp.getType().ordinal() * 4 + texPosZIndex];
    }

    @Override public int getTexSizeX() {
        return sizeMapping[wp.getType().ordinal() * 4 + texSizeXIndex];
    }

    @Override public int getTexSizeZ() {
        return sizeMapping[wp.getType().ordinal() * 4 + texSizeZIndex];
    }

    @Override public float getSizeX() {
        int i = wp.getType().ordinal() * 4;
        return (sizeMapping[i + texSizeXIndex] - sizeMapping[i + texPosXIndex]) / 2.5f;
    }

    @Override public float getSizeZ() {
        int i = wp.getType().ordinal() * 4;
        return (sizeMapping[i + texSizeZIndex] - sizeMapping[i + texPosZIndex]) / 2.5f;
    }

    @Override public int getZoomNeeded() {
        return wp.getZoomNeeded();
    }

    @Override public boolean isEnabled(boolean forMinimap) {
        return wp.getZoomNeeded() != HIDDEN_ZOOM;
    }

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        CustomColor color = wp.getColor();
        if (color != null) {
            GL11.glGetFloat(GL11.GL_CURRENT_COLOR, currentColorBuf);
            GL11.glColor4f(color.r, color.g, color.b, color.a);
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
