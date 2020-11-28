/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.instances;

import com.google.gson.*;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.map.configs.MapConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PathWaypointProfile {

    public String name;
    public boolean isCircular = false;
    public boolean isEnabled = true;
    private CustomColor color = new CustomColor(CommonColors.RED);

    private transient int minX, minZ, maxX, maxZ, posX, posZ;
    private transient float sizeX, sizeZ;

    private List<PathPoint> points = new ArrayList<>();

    public PathWaypointProfile() {
        this("Path");
    }

    public PathWaypointProfile(String name) {
        this.name = name == null ? "Path" : name;
        points = new ArrayList<>();
        recalculateBounds();
    }

    public PathWaypointProfile(String name, boolean isCircular, boolean isEnabled, CustomColor color) {
        this(name, isCircular, isEnabled, color, null);
    }

    public PathWaypointProfile(String name, boolean isCircular, boolean isEnabled, CustomColor color, List<PathPoint> points) {
        this.name = name == null ? "Path" : name;
        this.isCircular = isCircular;
        this.isEnabled = isEnabled;
        setColor(color);
        this.points = points == null ? new ArrayList<>() : new ArrayList<>(points);
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
        isEnabled = other.isEnabled;
        color = other.color;
        this.points = new ArrayList<>(other.points.size());
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
    
    public PathWaypointProfile(LootRunPath lootrun) {
        name = "Lootrun path";
        color = MapConfig.LootRun.INSTANCE.activePathColour;
        this.points = new ArrayList<>(lootrun.getPoints().size());
        lootrun.getPoints().forEach(c -> points.add(new PathPoint((int) c.x, (int) c.z)));
        recalculateBounds();
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

    public PathWaypointProfile setColor(CustomColor color) {
        if (color == null) return this;
        this.color = new CustomColor(color);
        return this;
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

    public static class Serializer implements JsonDeserializer<PathWaypointProfile>, JsonSerializer<PathWaypointProfile> {

        public PathWaypointProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject o = json.getAsJsonObject();
            JsonElement nameEl = o.get("name");
            JsonElement isCircularEl = o.get("isCircular");
            JsonElement isEnabledEl = o.get("isEnabled");
            JsonElement pointsEl = o.get("points");
            JsonElement colourEl = o.get("color");

            String name = nameEl == null || !nameEl.isJsonPrimitive() || !((JsonPrimitive) nameEl).isString() ? null : nameEl.getAsString();
            boolean isCircular = isCircularEl == null || !isCircularEl.isJsonPrimitive() || !((JsonPrimitive) isCircularEl).isBoolean() ? false : isCircularEl.getAsBoolean();
            boolean isEnabled = isEnabledEl == null || !isEnabledEl.isJsonPrimitive() || !((JsonPrimitive) isEnabledEl).isBoolean() ? true : isEnabledEl.getAsBoolean();
            CustomColor colour = colourEl == null ? CommonColors.RED : context.deserialize(colourEl, CustomColor.class);

            ArrayList<PathPoint> points = new ArrayList<>();

            if (pointsEl != null && pointsEl.isJsonArray() && ((JsonArray) pointsEl).size() != 0) {
                JsonArray pointsJson = (JsonArray) pointsEl;
                if (pointsJson.size() != 0) {
                    if (pointsJson.get(0).isJsonObject()) {
                        // Old style, [{ x, z }, ...]
                        points.ensureCapacity(pointsJson.size());
                        for (JsonElement el : pointsJson) {
                            if (el.isJsonObject()) {
                                JsonElement x = ((JsonObject) el).get("x");
                                JsonElement z = ((JsonObject) el).get("z");
                                if (x != null && x.isJsonPrimitive() && ((JsonPrimitive) x).isNumber() && z != null && z.isJsonPrimitive() && ((JsonPrimitive) z).isNumber()) {
                                    points.add(new PathPoint(x.getAsNumber().intValue(), z.getAsNumber().intValue()));
                                }
                            }
                        }
                    } else {
                        // New style, [x1, z1, x2, z2, ...]
                        points.ensureCapacity(pointsJson.size() / 2);
                        for (int i = 0, size = pointsJson.size(); i + 1 < size;) {
                            JsonElement x = pointsJson.get(i++);
                            JsonElement z = pointsJson.get(i++);
                            if (x != null && x.isJsonPrimitive() && ((JsonPrimitive) x).isNumber() && z != null && z.isJsonPrimitive() && ((JsonPrimitive) z).isNumber()) {
                                points.add(new PathPoint(x.getAsNumber().intValue(), z.getAsNumber().intValue()));
                            }
                        }
                    }
                }
            }

            return new PathWaypointProfile(name, isCircular, isEnabled, colour, points);
        }

        public JsonObject serialize(PathWaypointProfile wp, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject serialized = new JsonObject();
            serialized.addProperty("name", wp.name);
            serialized.addProperty("isCircular", wp.isCircular);
            serialized.addProperty("isEnabled", wp.isEnabled);
            serialized.add("color", context.serialize(wp.color, CustomColor.class));
            JsonArray points = new JsonArray();
            for (PathPoint point : wp.points) {
                points.add(point.x);
                points.add(point.z);
            }
            serialized.add("points", points);
            return serialized;
        }

    }

}
