/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.managers.CompassManager;

public class MapCompassIconInfo extends MapIconInfo {
    private static MapCompassIconInfo instance = null;

    public static MapCompassIconInfo getInstance() {
        if (instance == null && Textures.Map.map_icons != null) instance = new MapCompassIconInfo();
        return instance;
    }

    private MapCompassIconInfo() {
        super(Textures.Map.map_icons, "Compass Beacon", Integer.MAX_VALUE, Integer.MAX_VALUE, 2.5f, 0, 53, 14, 71, -1000);
    }

    @Override
    public int getPosX() {
        return (int) CompassManager.getCompassLocation().getX();
    }

    public int getPosZ() {
        return (int) CompassManager.getCompassLocation().getZ();
    }
}
