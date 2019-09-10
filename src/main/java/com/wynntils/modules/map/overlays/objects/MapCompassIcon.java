/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.configs.MapConfig;

public class MapCompassIcon extends MapTextureIcon {
    private static final MapCompassIcon instance = new MapCompassIcon();

    public static MapCompassIcon getCompass() {
        return instance;
    }

    private MapCompassIcon() {}

    @Override public AssetsTexture getTexture() {
        return Textures.Map.map_icons;
    }

    @Override public int getPosX() {
        if (!isEnabled(false)) return Integer.MIN_VALUE;
        return (int) CompassManager.getCompassLocation().getX();
    }

    @Override public int getPosZ() {
        if (!isEnabled(false)) return Integer.MIN_VALUE;
        return (int) CompassManager.getCompassLocation().getZ();
    }

    @Override public String getName() {
        return "Compass Beacon";
    }

    @Override public int getTexPosX() {
        return 0;
    }

    @Override public int getTexPosZ() {
        return 53;
    }

    @Override public int getTexSizeX() {
        return 14;
    }

    @Override public int getTexSizeZ() {
        return 71;
    }

    @Override public float getSizeX() {
        // return (getTexSizeX() - getTexPosX()) / 2.5f;
        return 5.6f;
    }

    @Override public float getSizeZ() {
        // return (getTexSizeZ() - getTexPosZ()) / 2.5f;
        return 7.2f;
    }

    @Override public int getZoomNeeded() {
        return ANY_ZOOM;
    }

    @Override public boolean isEnabled(boolean forMinimap) {
        return MapConfig.Waypoints.INSTANCE.compassMarker && CompassManager.getCompassLocation() != null;
    }

    public static final MapTextureIcon pointer = createStaticIcon(Textures.Map.map_icons, 14, 53, 24, 61, 5, 4);
}
