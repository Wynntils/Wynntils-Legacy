/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.modules.map.configs.MapConfig;

import java.util.List;

/**
 * Represents something drawn on the main map or minimap
 */
public abstract class MapIcon {
    /**
     * If {@link #getZoomNeeded()} returns this, the icon will always be visible
     */
    public static final int ANY_ZOOM = -1000;

    // This position definitely wont be rendered on the map
    public static final int NO_LOCATION = Integer.MIN_VALUE / 2;

    /**
     * @return The x coordinate in the Minecraft world of this icon
     */
    public abstract int getPosX();

    /**
     * @return The z coordinate in the Minecraft world of this icon
     */
    public abstract int getPosZ();

    /**
     * @return The name rendered above this icon when hovering over it in the main map
     */
    public abstract String getName();

    /**
     * @return The width of the icon when rendered 1:1 (px)
     */
    public abstract float getSizeX();

    /**
     * @return The height of the icon when rendered 1:1 (px)
     */
    public abstract float getSizeZ();

    /**
     * @return The zoom amount needed in the main map to render this icon. (Icons are always rendered)
     */
    public abstract int getZoomNeeded();

    /**
     * @param forMinimap If true, return whether enabled for minimap instead of main map
     *
     * @return Whether this icon should be rendered or not (Usually based on a config)
     */
    public abstract boolean isEnabled(boolean forMinimap);

    /**
     * Render this icon
     *
     * @param renderer What to use to render
     * @param centreX The x position of centre of the icon (on the screen)
     * @param centreZ As centreX, but for z position
     * @param sizeMultiplier The width should be {@link #getSizeX()} * sizeMultiplier, and the height {@link #getSizeZ()} * sizeMultiplier
     * @param blockScale The number of pixels on screen that represent one Minecraft block. Used for icons that span multiple blocks.
     */
    public abstract void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale);

    /**
     * If true, this icon should be rendered rotated (e.g. in a rotated minimap)
     */
    public abstract boolean followRotation();

    public static List<MapIcon> getApiMarkers(MapConfig.IconTexture iconTexture) {
        return MapApiIcon.getApiMarkers(iconTexture);
    }

    public static List<MapIcon> getWaypoints() {
        return MapWaypointIcon.getWaypoints();
    }

    public static MapIcon getCompass() {
        return MapCompassIcon.getCompass();
    }

    public static List<MapIcon> getPathWaypoints() {
        return MapPathWaypointIcon.getPathWaypoints();
    }
}
