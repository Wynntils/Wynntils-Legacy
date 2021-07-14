package com.wynntils.webapi.profiles;

public class SeaskipperProfile {
    String name;

    int startX;
    int startZ;
    int endX;
    int endZ;

    int level;

    public SeaskipperProfile(String name, int startX, int startZ, int endX, int endZ, int level) {
        this.name = name;
        this.level = level;

        if (endX < startX) {
            this.startX = endX;
            this.endX = startX;
        } else {
            this.startX = startX;
            this.endX = endX;
        }

        if (endZ < startZ) {
            this.startZ = endZ;
            this.endZ = startZ;
        } else {
            this.startZ = startZ;
            this.endZ = endZ;
        }
    }

    public String getName() {
        return name;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartZ() {
        return startZ;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndZ() {
        return endZ;
    }

    public int getLevel() {
        return level;
    }
}


