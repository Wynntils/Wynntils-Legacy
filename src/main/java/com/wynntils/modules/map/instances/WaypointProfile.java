/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.rendering.colors.CustomColor;

public class WaypointProfile {

    String name;
    double x, y, z;
    CustomColor color;
    WaypointType type;

    public WaypointProfile(String name, double x, double y, double z, CustomColor color, WaypointType type) {
        this.name = name; this.x = x; this.y = y; this.z = z; this.color = color; this.type = type;
    }

    public String getName() {
        return name;
    }

    public CustomColor getColor() {
        return color;
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

    public WaypointType getType() {
        return type;
    }

    public enum WaypointType {

        MARKER, LOOTCHEST_T3, LOOTCHEST_T4

    }

}
