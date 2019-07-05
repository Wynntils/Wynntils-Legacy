/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.modules.map.configs.MapConfig;

import java.util.List;

public abstract class MapIcon {
    public static final int ANY_ZOOM = -1000;

    public abstract AssetsTexture getTexture();
    public abstract int getPosX();
    public abstract int getPosZ();
    public abstract String getName();
    public abstract int getTexPosX();
    public abstract int getTexPosZ();
    public abstract int getTexSizeX();
    public abstract int getTexSizeZ();
    public abstract float getSizeX();
    public abstract float getSizeZ();
    public abstract int getZoomNeeded();
    public abstract boolean isEnabled();

    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier) {
        float sizeX = getSizeX() * sizeMultiplier;
        float sizeZ = getSizeZ() * sizeMultiplier;
        renderer.drawRectF(
                getTexture(),
                centreX - sizeX, centreZ - sizeZ,
                centreX + sizeX, centreZ + sizeZ,
                getTexPosX(), getTexPosZ(), getTexSizeX(), getTexSizeZ()
        );
    }

    public static List<MapIcon> getApiMarkers(MapConfig.IconTexture iconTexture) {
        return MapApiIcon.getApiMarkers(iconTexture);
    }

    public static List<MapIcon> getWaypoints() {
        return MapWaypointIcon.getWaypoints();
    }

    public static MapIcon getCompass() {
        return MapCompassIcon.getCompass();
    }
}
