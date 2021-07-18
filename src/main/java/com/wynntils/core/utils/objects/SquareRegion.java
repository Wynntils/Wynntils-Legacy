/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.objects;

import net.minecraft.entity.Entity;

public class SquareRegion {

    double startX, startZ;
    double endX, endZ;

    public SquareRegion(double startX, double startZ, double endX, double endZ) {
        this.startX = startX;
        this.startZ = startZ;
        this.endX = endX;
        this.endZ = endZ;
    }

    public boolean isInside(double x, double z) {
        // this inverts the region if needed
        if (endX < startX) {
            double oldStartX = startX;
            startX = endX;
            endX = oldStartX;
        }

        if (endZ < startZ) {
            double oldStartZ = startZ;
            startZ = endZ;
            endZ = oldStartZ;
        }

        return startX <= x && endX >= x && startZ <= z && endZ >= z;
    }

    public double sqdist(double x, double z) {
        return (getCenterX() - x) * (getCenterX() - x) + (getCenterZ() - z) * (getCenterZ() - z);
    }

    public double getCenterX() {
        return (endX + startX)/2;
    }

    public double getCenterZ() {
        return (endZ + startZ)/2;
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

    public Location getEndLocation() {
        return new Location(endX, 0, endZ);
    }

}
