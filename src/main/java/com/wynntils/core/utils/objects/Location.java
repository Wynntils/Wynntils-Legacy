/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils.objects;

import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

public class Location {

    double x, y, z;

    public Location(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Location(BlockPos from) {
        this.x = from.getX(); this.y = from.getY(); this.z = from.getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Location add(Location loc) {
        x += loc.getX();
        y += loc.getY();
        z += loc.getZ();

        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Location substract(Location loc) {
        x -= loc.getX();
        y -= loc.getY();
        z -= loc.getZ();

        return this;
    }

    public Location substract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Location substract(double amount) {
        this.x -= amount;
        this.y -= amount;
        this.z -= amount;

        return this;
    }

    public Location multiply(Location loc) {
        x *= loc.getX();
        y *= loc.getY();
        z *= loc.getZ();

        return this;
    }

    public Location multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;

        return this;
    }

    public Location multiply(double amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;

        return this;
    }

    public double distance(Location to) {
        return Math.sqrt(
                Math.pow(x - to.getX(), 2) +
                Math.pow(y - to.getY(), 2) +
                Math.pow(z - to.getZ(), 2)
        );
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof Location) {
            Location loc = (Location) obj;
            return x == loc.x && y == loc.y && z == loc.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new double[]{ x, y, z });
    }
}
