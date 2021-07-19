/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.objects;

import net.minecraft.entity.Entity;

public class SquareRegion {

    double startX, startZ;
    double endX, endZ;
    double width, height;
    double centerX, centerZ;

    /**
     * startX should be less than endX, startZ should be less than endZ
     */
    public SquareRegion(double startX, double startZ, double endX, double endZ) {
        // this inverts the region if needed
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

        this.centerX = (this.startX + this.endX)/2;
        this.centerZ = (this.startZ + this.endZ)/2;
        this.width = this.endX - this.startX;
        this.height = this.endZ - this.startZ;

    }

    public boolean isInside(double x, double z) {
        return startX <= x && endX >= x && startZ <= z && endZ >= z;
    }

    public double signedDist(double x, double z) {
        double offsetX = Math.abs(x - centerX) - width/2;
        double offsetZ = Math.abs(z - centerZ) - height/2;

        //-dist if inside region, else 0
        double innerDist = Math.max(Math.min(offsetX, 0), Math.min(offsetZ, 0));
        //dist if outside region, else 0
        double outerDist = Math.sqrt(Math.max(offsetX, 0) * Math.max(offsetX, 0) + Math.max(offsetZ, 0) * Math.max(offsetZ, 0));

        return innerDist + outerDist;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public boolean isInside(Entity in) {
        return isInside(in.posX, in.posZ);
    }

    public boolean isInside(Location loc) {
        return isInside(loc.getX(), loc.getZ());
    }

    public Location getStartLocation() {
        return new Location(startX, 0, startZ);
    }

    public Location getCenterLocation() {
        return new Location(centerX, 0, centerZ);
    }

    public Location getEndLocation() {
        return new Location(endX, 0, endZ);
    }
}
