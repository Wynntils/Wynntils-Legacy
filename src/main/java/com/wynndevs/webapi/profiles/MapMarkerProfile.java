package com.wynndevs.webapi.profiles;

public class MapMarkerProfile {

    String name;
    int x;
    int y;
    int z;

    public MapMarkerProfile(String name, int x, int y, int z) {
        this.name = name; this.x = x; this.y = y; this.z = z;
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

}
