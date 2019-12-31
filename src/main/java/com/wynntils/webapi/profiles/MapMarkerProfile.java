/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.utils.StringUtils;

public class MapMarkerProfile {

    String name;
    int x;
    int y;
    int z;
    String icon;

    public MapMarkerProfile(String name, int x, int y, int z, String icon) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.icon = icon;
        ensureNormalized();
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getIcon() {
        return icon;
    }

    public void ensureNormalized() {
        if (name != null) name = StringUtils.normalizeBadString(name);
        icon = icon.replace(".png", "");
    }

}
