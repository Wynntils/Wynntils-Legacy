/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.managers.CompassManager;

public class MapCompassIcon extends MapIcon {
    private static MapCompassIcon instance = null;

    public static MapCompassIcon getCompass() {
        if (instance == null && Textures.Map.map_icons != null) instance = new MapCompassIcon();
        return instance;
    }

    private MapCompassIcon() {}

    @Override public AssetsTexture getTexture() {
        return Textures.Map.map_icons;
    }

    @Override public int getPosX() {
        return (int) CompassManager.getCompassLocation().getX();
    }

    @Override public int getPosZ() {
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

    @Override public boolean isEnabled() {
        return true;
    }

}
