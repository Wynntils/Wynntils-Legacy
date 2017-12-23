package com.wynndevs.wynnrp.profiles;

/**
 * Created by HeyZeer0 on 11/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class LocationProfile {

    String name;
    int startX;
    int startZ;
    int endX;
    int endZ;

    public LocationProfile(String name, int startX, int startZ, int endX, int endZ) {
        this.name = name;

        if(endX < startX) {
            this.startX = endX;
            this.endX = startX;
        }else{
            this.startX = startX;
            this.endX = endX;
        }

        if(endZ < startZ) {
            this.startZ = endZ;
            this.endZ = startZ;
        }else{
            this.startZ = startZ;
            this.endZ = endZ;
        }
    }

    public String getName() {
        return name;
    }

    public boolean insideArea(int x, int z) {
        if (startX <= x && endX >= x) {
            if (startZ <= z && endZ >= z) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return name + "|" + startX + "|" + startZ + "|" + endX + "|" + endZ;
    }

}
