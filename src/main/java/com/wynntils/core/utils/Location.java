/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import net.minecraft.util.math.BlockPos;

public class Location {

    double x, y, z;

    public Location(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
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

    public void add(Location loc) {
        x += loc.getX();
        y += loc.getY();
        z += loc.getZ();
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void substract(Location loc) {
        x -= loc.getX();
        y -= loc.getY();
        z -= loc.getZ();
    }

    public void substract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public void substract(double amount) {
        this.x -= amount;
        this.y -= amount;
        this.z -= amount;
    }

    public void multiply(Location loc) {
        x *= loc.getX();
        y *= loc.getY();
        z *= loc.getZ();
    }

    public void multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public void multiply(double amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

}
