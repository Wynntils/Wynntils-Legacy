/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles;

public class MapMarkerProfile {

    String name;
    int x;
    int y;
    int z;
    String icon;

    public MapMarkerProfile(String name, int x, int y, int z, String icon){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.icon = icon;
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

    public String getIcon(){
        return icon.replace(".png", "");
    }

}
