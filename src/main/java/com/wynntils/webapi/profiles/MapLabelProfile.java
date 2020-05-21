/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.profiles;

public class MapLabelProfile {

    String name;
    int x;
    int y;
    int z;
    String level;
    int layer;

    public MapLabelProfile(String name, int x, int y, int z, int layer, String level) {
        if (layer == 1) {
            this.name = name.toUpperCase();
        } else {
            this.name = name;
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.layer = layer;
        this.level = level;
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

    public int getLayer() {
        return layer;
    }

    public String getLevel() {
        return level;
    }
}