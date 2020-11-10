/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.profiles;

public class MapLabelProfile extends LocationProfile {

    int y;
    int layer;
    String level;

    public MapLabelProfile(String name, int x, int y, int z, int layer, String level) {
        super(name, x, z);
        this.y = y;
        this.layer = layer;
        this.level = level;
        ensureNormalized();
    }

    public void ensureNormalized() {
        if (name != null && layer == 1) {
            this.name = name.toUpperCase();
        }
    }

    public int getY() {
        return y;
    }

    public int getLayer() {
        return layer;
    }

    public String getLevel() {
        return level;
    }

}