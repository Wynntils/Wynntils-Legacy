/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.utils.objects;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple3i;

public class Location extends Point3d {

    public Location(double x, double y, double z) {
        super(x, y, z);
    }

    public Location(Entity entity) {
        super(entity.posX, entity.posY, entity.posZ);
    }

    public Location(Tuple3d t) {
        super(t);
    }

    public Location(Tuple3f t) {
        super(t);
    }

    public Location(Tuple3i t) {
        super(t.x, t.y, t.z);
    }

    // Convert from net.minecraft.util.math vectors (Don't import so the names aren't confusing)
    public Location(net.minecraft.util.math.Vec3i v) {
        super(v.getX(), v.getY(), v.getZ());
    }

    public Location(net.minecraft.util.math.Vec3d v) {
        super(v.x, v.y, v.z);
    }

    public Location add(Point3d loc) {
        x += loc.x;
        y += loc.y;
        z += loc.z;

        return this;
    }

    public Location add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    // An egregious circumvention of the type system; Should have two signatures:
    // public void subtract(Vector3d) and public Vector3d subtract(Point3d), but that would
    // be confusing for method chaining, so just subtract *anything*, and this location might become
    // a vector if we subtracted another location.
    public Location subtract(Tuple3d loc) {
        x -= loc.x;
        y -= loc.y;
        z -= loc.z;

        return this;
    }

    public Location subtract(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Location subtract(double amount) {
        this.x -= amount;
        this.y -= amount;
        this.z -= amount;

        return this;
    }

    public Location multiply(Tuple3d loc) {
        x *= loc.x;
        y *= loc.y;
        z *= loc.z;

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

    public final BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public final net.minecraft.util.math.Vec3d toMinecraftVec3d() {
        return new net.minecraft.util.math.Vec3d(x, y, z);
    }

    public Location clone() {
        return new Location(x, y, z);
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
