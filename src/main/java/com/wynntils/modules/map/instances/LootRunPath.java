/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.objects.CubicSplines;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.map.configs.MapConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.*;

public class LootRunPath {

    private static List<CustomColor> COLORS = Arrays.asList(CommonColors.RED, CommonColors.ORANGE, CommonColors.YELLOW, CommonColors.GREEN, CommonColors.BLUE, new CustomColor(63, 0, 255), CommonColors.PURPLE);

    private CubicSplines.Spline3D spline;
    private LinkedHashSet<BlockPos> chests;

    private transient List<LootRunPath.LootRunPathLocation> lastSmoothSample;
    private transient List<Vector3d> lastSmoothDerivative;
    private transient List<LootRunPath.LootRunPathLocation> lastRoughSample;
    private transient List<Vector3d> lastRoughDerivative;

    private transient Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>> lastSmoothSampleByChunk;
    private transient Long2ObjectMap<List<List<Vector3d>>> lastSmoothDerivativeByChunk;
    private transient Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>> lastRoughSampleByChunk;
    private transient Long2ObjectMap<List<List<Vector3d>>> lastRoughDerivativeByChunk;

    public LootRunPath(Collection<? extends Point3d> points, Collection<? extends BlockPos> chests) {
        this.spline = new CubicSplines.Spline3D(points);
        this.chests = new LinkedHashSet<>(chests == null ? 11 : Math.max(2 * chests.size(), 11));
        if (chests != null) {
            for (BlockPos pos : chests) {
                this.chests.add(pos.toImmutable());
            }
        }
    }

    public LootRunPath() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public void addPoint(Point3d loc) {
        changed();
        spline.addPoint(loc);
    }

    public void addPoints(Collection<? extends Point3d> points) {
        changed();
        spline.addPoints(points);
    }

    public void addPointToFront(Location loc) {
        addPointsToFront(Collections.singletonList(loc));
    }

    public void addPointsToFront(Collection<? extends Point3d> points) {
        changed();
        spline.addPoints(0, points);
    }

    public void addChest(BlockPos loc) {
        chests.add(loc.toImmutable());
    }

    public Set<BlockPos> getChests() {
        return Collections.unmodifiableSet(chests);
    }

    public List<Location> getPoints() {
        return spline.getPoints();
    }

    public Location getLastPoint() {
        List<Location> points = getPoints();
        return points.isEmpty() ? null : points.get(points.size() - 1);
    }

    public void changed() {
        lastSmoothSample = null;
        lastSmoothDerivative = null;
        lastSmoothSampleByChunk = null;
        lastSmoothDerivativeByChunk = null;
        lastRoughSample = null;
        lastRoughDerivative = null;
        lastRoughSampleByChunk = null;
        lastRoughDerivativeByChunk = null;
    }

    private Pair<List<LootRunPath.LootRunPathLocation>, List<Vector3d>> generatePoints(int sampleRate) {
        Pair<List<Location>, List<Vector3d>> sample = spline.sample(sampleRate);
        List<Location> rawLocations = sample.a;
        List<LootRunPath.LootRunPathLocation> locations = new ArrayList<>();
        Iterator<CustomColor> colorIterator = COLORS.iterator();
        CustomColor currentColor = null;
        CustomColor nextColor = colorIterator.next();
        float changeRed = 0;
        float changeGreen = 0;
        float changeBlue = 0;
        for (int i = 0; i < rawLocations.size(); i++) {
            if (i % (sampleRate * MapConfig.LootRun.INSTANCE.cycleDistance) == 0) {
                currentColor = new CustomColor(nextColor);
                if (!colorIterator.hasNext()) {
                    colorIterator = COLORS.iterator();
                }
                nextColor = colorIterator.next();
                changeRed = (nextColor.r - currentColor.r) / (sampleRate * MapConfig.LootRun.INSTANCE.cycleDistance);
                changeGreen = (nextColor.g - currentColor.g) / (sampleRate * MapConfig.LootRun.INSTANCE.cycleDistance);
                changeBlue = (nextColor.b - currentColor.b) / (sampleRate * MapConfig.LootRun.INSTANCE.cycleDistance);
            } else {
                currentColor = new CustomColor(currentColor);
                currentColor.r += changeRed;
                currentColor.g += changeGreen;
                currentColor.b += changeBlue;
            }
            LootRunPathLocation location = new LootRunPathLocation(rawLocations.get(i), currentColor);
            locations.add(location);
        }
        List<LootRunPath.LootRunPathLocation> locationsSample = locations;
        List<Vector3d> derivative = sample.b;

        return new Pair<>(locationsSample, derivative);
    }

    private Pair<Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>>, Long2ObjectMap<List<List<Vector3d>>>> generatePointsByChunk(List<LootRunPathLocation> locationsList, List<Vector3d> derivativesList) {
        ChunkPos lastChunkPos = null;
        List<LootRunPath.LootRunPathLocation> lastLocationList = null;
        List<Vector3d> lastDirectionList = null;
        Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>> sampleByChunk = new Long2ObjectOpenHashMap<>();
        Long2ObjectMap<List<List<Vector3d>>> derivativeByChunk = new Long2ObjectOpenHashMap<>();
        for (int i = 0; i < locationsList.size(); i++) {
            LootRunPathLocation location = locationsList.get(i);
            Vector3d direction = derivativesList.get(i);
            ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.getLocation().x) >> 4, MathHelper.fastFloor(location.getLocation().z) >> 4);
            if (!currentChunkPos.equals(lastChunkPos)) {
                if (lastChunkPos != null && location.getLocation().distance(locationsList.get(i - 1).getLocation()) < 32) {
                    lastLocationList.add(location);
                    lastDirectionList.add(direction);
                }

                lastChunkPos = currentChunkPos;
                sampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                derivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                lastLocationList = new ArrayList<>();
                lastDirectionList = new ArrayList<>();
                sampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                derivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
            }
            lastLocationList.add(location);
            lastDirectionList.add(derivativesList.get(i));
        }
        return new Pair<>(sampleByChunk, derivativeByChunk);
    }

    private void generateSmoothPoints() {
        Pair<List<LootRunPath.LootRunPathLocation>, List<Vector3d>> points = generatePoints(10);
        this.lastSmoothSample = points.a;
        this.lastSmoothDerivative = points.b;

        Pair<Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>>, Long2ObjectMap<List<List<Vector3d>>>> pointsByChunk = generatePointsByChunk(lastSmoothSample, lastSmoothDerivative);
        this.lastSmoothSampleByChunk = pointsByChunk.a;
        this.lastSmoothDerivativeByChunk = pointsByChunk.b;
    }

    private void generateRoughPoints() {
        Pair<List<LootRunPath.LootRunPathLocation>, List<Vector3d>> points = generatePoints(1);
        this.lastRoughSample = points.a;
        this.lastRoughDerivative = points.b;

        Pair<Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>>, Long2ObjectMap<List<List<Vector3d>>>> pointsByChunk = generatePointsByChunk(lastRoughSample, lastRoughDerivative);
        this.lastRoughSampleByChunk = pointsByChunk.a;
        this.lastRoughDerivativeByChunk = pointsByChunk.b;
    }

    public List<LootRunPath.LootRunPathLocation> getSmoothPoints() {
        if (lastSmoothSample == null) {
            this.generateSmoothPoints();
        }
        return lastSmoothSample;
    }

    public List<Vector3d> getSmoothDirections() {
        if (lastSmoothDerivative == null) {
            this.generateSmoothPoints();
        }
        return lastSmoothDerivative;
    }

    public Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>> getSmoothPointsByChunk() {
        if (lastSmoothSampleByChunk == null) {
            this.generateSmoothPoints();
        }
        return lastSmoothSampleByChunk;
    }

    public Long2ObjectMap<List<List<Vector3d>>> getSmoothDirectionsByChunk() {
        if (lastSmoothDerivativeByChunk == null) {
            this.generateSmoothPoints();
        }
        return lastSmoothDerivativeByChunk;
    }

    public List<LootRunPath.LootRunPathLocation> getRoughPoints() {
        if (lastRoughSample == null) {
            this.generateRoughPoints();
        }
        return lastRoughSample;
    }

    public List<Vector3d> getRoughDirections() {
        if (lastRoughDerivative == null) {
            this.generateRoughPoints();
        }
        return lastRoughDerivative;
    }

    public Long2ObjectMap<List<List<LootRunPath.LootRunPathLocation>>> getRoughPointsByChunk() {
        if (lastRoughSampleByChunk == null) {
            this.generateRoughPoints();
        }
        return lastRoughSampleByChunk;
    }

    public Long2ObjectMap<List<List<Vector3d>>> getRoughDirectionsByChunk() {
        if (lastRoughDerivativeByChunk == null) {
            this.generateRoughPoints();
        }
        return lastRoughDerivativeByChunk;
    }

    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

    public static class LootRunPathLocation {
        private Location location;
        private CustomColor color;

        public LootRunPathLocation(Location location, CustomColor color) {
            this.location = location;
            this.color = color;
        }

        public Location getLocation() {
            return location;
        }

        public CustomColor getColor() {
            return color;
        }
    }

}
