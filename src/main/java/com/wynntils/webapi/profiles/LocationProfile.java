/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles;

public class LocationProfile {
    String name;
    int x;
    int z;

    public LocationProfile(String name, int x, int z) {
        this.name = name;
        this.x = x;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public String getTranslatedName() {
        return getName();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
