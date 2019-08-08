package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;

import java.util.ArrayList;
import java.util.List;

public class PathWaypointProfile {
    public String name;
    public boolean isCircular = false;
    public boolean isEnabled = true;
    private CustomColor color = CommonColors.RED;

    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private int posX;
    private int posZ;
    private float sizeX;
    private float sizeZ;

    private List<PathPoint> points = new ArrayList<>();

    public PathWaypointProfile() {
        this("Path");
    }

    public PathWaypointProfile(String name) {
        this.name = name == null ? "Path" : name;
        recalculateBounds();
    }

    public PathWaypointProfile(PathWaypointProfile other) {
        if (other == null) {
            this.name = "Path";
            recalculateBounds();
            return;
        }

        name = other.name;
        isCircular = other.isCircular;
        color = other.color;
        ((ArrayList<PathPoint>) points).ensureCapacity(other.points.size());
        other.points.forEach(c -> points.add(new PathPoint(c.x, c.z)));
        minX = other.minX;
        minZ = other.minZ;
        maxX = other.maxX;
        maxZ = other.maxZ;
        posX = other.posX;
        posZ = other.posZ;
        sizeX = other.sizeX;
        sizeZ = other.sizeZ;
    }

    public PathPoint getPoint(int index) {
        return points.get(index);
    }

    private void addPoint(int x, int z) {
        minX = Math.min(minX, x);
        minZ = Math.min(minZ, z);
        maxX = Math.max(maxX, x);
        maxZ = Math.max(maxZ, z);
        recalculatePos();
    }

    public void addPoint(PathPoint p) {
        points.add(p);
        addPoint(p.x, p.z);
    }

    public void insertPoint(int index, PathPoint p) {
        points.add(index, p);
        addPoint(p.x, p.z);
    }

    public void removePoint(PathPoint p) {
        points.remove(p);
        recalculateBounds();
    }

    public void removePoint(int index) {
        points.remove(index);
        recalculateBounds();
    }

    public int size() {
        return points.size();
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosZ() {
        return posZ;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public CustomColor getColor() {
        return color;
    }

    public void setColor(CustomColor color) {
        if (color == null) return;
        this.color = color;
    }

    private void recalculateBounds() {
        minX = Integer.MAX_VALUE;
        minZ = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxZ = Integer.MIN_VALUE;

        for (PathPoint p : points) {
            minX = Math.min(minX, p.x);
            minZ = Math.min(minZ, p.z);
            maxX = Math.max(maxX, p.x);
            maxZ = Math.max(maxZ, p.z);
        }

        recalculatePos();
    }

    private void recalculatePos() {
        posX = (minX + maxX) / 2;
        posZ = (minZ + maxZ) / 2;
        sizeX = (maxX - minX + 1) / 2f;
        sizeZ = (maxZ - minZ + 1) / 2f;
    }

    public static class PathPoint {
        private int x;
        private int z;

        public PathPoint(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }
    }
}
