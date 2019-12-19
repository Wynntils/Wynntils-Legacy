/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils.objects;

import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

public class Location extends Point3d {

    public Location(double x, double y, double z) {
        super(x, y, z);
    }

    public Location(Tuple3d t) {
        super(t);
    }

    public final void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public final void subtract(Vector3d loc) {
        x -= loc.x;
        y -= loc.y;
        z -= loc.z;
    }

    public final void subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

    public final void subtract(double amount) {
        this.x -= amount;
        this.y -= amount;
        this.z -= amount;
    }

    public final void multiply(Tuple3d loc) {
        x *= loc.x;
        y *= loc.y;
        z *= loc.z;
    }

    public final void multiply(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public final void multiply(double amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
    }

    public final BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof Tuple3d) {
            return equals((Tuple3d) obj);
        }
        return false;
    }

    public boolean equals(Tuple3d other) {
        if (other == null) return false;
        return x == other.x && y == other.y && z == other.z;
    }
}
