/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.rendering.colors.CustomColor;

public class WaypointProfile {

    String name;
    double x, y, z;
    int zoomNeeded;
    CustomColor color;
    WaypointType type;

    public WaypointProfile(String name, double x, double y, double z, CustomColor color, WaypointType type, int zoomNeeded) {
        this.name = name; this.x = x; this.y = y; this.z = z; this.color = color; this.type = type; this.zoomNeeded = zoomNeeded;
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

    public int getZoomNeeded() {
        return zoomNeeded;
    }

    public WaypointType getType() {
        return type;
    }

    public enum WaypointType {

        FLAG("Flag"),
        DIAMOND("Diamond"),
        SIGN("Sign"),
        STAR("Star"),
        TURRET("Turret"),
        LOOTCHEST_T4("Chest (T4)"),
        LOOTCHEST_T3("Chest (T3"),
        LOOTCHEST_T2("Chest (T2)"),
        LOOTCHEST_T1("Chest (T1)");

        private String displayName;

        WaypointType(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

    }
}
