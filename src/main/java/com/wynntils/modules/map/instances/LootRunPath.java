/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.CubicSplines;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import java.util.*;

public class LootRunPath {

    private CubicSplines.Spline3D spline;
    private LinkedHashSet<BlockPos> chests;

    private transient List<Location> lastSmoothSample;
    private transient List<Vector3d> lastSmoothDerivative;
    private transient List<Location> lastRoughSample;
    private transient List<Vector3d> lastRoughDerivative;

    private transient Long2ObjectMap<List<List<Location>>> lastSmoothSampleByChunk;
    private transient Long2ObjectMap<List<List<Vector3d>>> lastSmoothDerivativeByChunk;
    private transient Long2ObjectMap<List<List<Location>>> lastRoughSampleByChunk;
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

    private void changed() {
        lastSmoothSample = null;
        lastSmoothDerivative = null;
        lastSmoothSampleByChunk = null;
        lastSmoothDerivativeByChunk = null;
        lastRoughSample = null;
        lastRoughDerivative = null;
        lastRoughSampleByChunk = null;
        lastRoughDerivativeByChunk = null;
    }

    public List<Location> getSmoothPoints() {
        if (lastSmoothSample == null) {
            Pair<List<Location>, List<Vector3d>> smooth = spline.sample();
            lastSmoothSample = smooth.a;
            lastSmoothDerivative = smooth.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastSmoothSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastSmoothDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastSmoothSample.size(); i++) {
                Location location = lastSmoothSample.get(i);
                Vector3d direction = lastSmoothDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastSmoothSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastSmoothSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastSmoothDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastSmoothSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastSmoothDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastSmoothDerivative.get(i));
            }
        }
        return lastSmoothSample;
    }

    public List<Vector3d> getSmoothDirections() {
        if (lastSmoothDerivative == null) {
            Pair<List<Location>, List<Vector3d>> smooth = spline.sample();
            lastSmoothSample = smooth.a;
            lastSmoothDerivative = smooth.b;
        }
        return lastSmoothDerivative;
    }

    public Long2ObjectMap<List<List<Location>>> getSmoothPointsByChunk() {
        if (lastSmoothSampleByChunk == null) {
            Pair<List<Location>, List<Vector3d>> smooth = spline.sample();
            lastSmoothSample = smooth.a;
            lastSmoothDerivative = smooth.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastSmoothSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastSmoothDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastSmoothSample.size(); i++) {
                Location location = lastSmoothSample.get(i);
                Vector3d direction = lastSmoothDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastSmoothSample.get(i - 1)) < 32D) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastSmoothSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastSmoothDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastSmoothSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastSmoothDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastSmoothDerivative.get(i));
            }
        }
        return lastSmoothSampleByChunk;
    }

    public Long2ObjectMap<List<List<Vector3d>>> getSmoothDirectionsByChunk() {
        if (lastSmoothDerivativeByChunk == null) {
            Pair<List<Location>, List<Vector3d>> smooth = spline.sample();
            lastSmoothSample = smooth.a;
            lastSmoothDerivative = smooth.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastSmoothSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastSmoothDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastSmoothSample.size(); i++) {
                Location location = lastSmoothSample.get(i);
                Vector3d direction = lastSmoothDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastSmoothSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastSmoothSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastSmoothDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastSmoothSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastSmoothDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastSmoothDerivative.get(i));
            }
        }
        return lastSmoothDerivativeByChunk;
    }

    public List<Location> getRoughPoints() {
        if (lastRoughSample == null) {
            Pair<List<Location>, List<Vector3d>> rough = spline.sample(1);
            lastRoughSample = rough.a;
            lastRoughDerivative = rough.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastRoughSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastRoughDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastRoughSample.size(); i++) {
                Location location = lastRoughSample.get(i);
                Vector3d direction = lastRoughDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastRoughSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastRoughSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastRoughDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastRoughSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastRoughDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastRoughDerivative.get(i));
            }
        }
        return lastRoughSample;
    }

    public List<Vector3d> getRoughDirections() {
        if (lastRoughDerivative == null) {
            Pair<List<Location>, List<Vector3d>> rough = spline.sample(1);
            lastRoughSample = rough.a;
            lastRoughDerivative = rough.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastRoughSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastRoughDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastRoughSample.size(); i++) {
                Location location = lastRoughSample.get(i);
                Vector3d direction = lastRoughDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastRoughSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastRoughSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastRoughDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastRoughSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastRoughDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastRoughDerivative.get(i));
            }
        }
        return lastRoughDerivative;
    }

    public Long2ObjectMap<List<List<Location>>> getRoughPointsByChunk() {
        if (lastRoughSampleByChunk == null) {
            Pair<List<Location>, List<Vector3d>> rough = spline.sample(1);
            lastRoughSample = rough.a;
            lastRoughDerivative = rough.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastRoughSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastRoughDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastRoughSample.size(); i++) {
                Location location = lastRoughSample.get(i);
                Vector3d direction = lastRoughDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastRoughSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    }

                    lastChunkPos = currentChunkPos;
                    lastRoughSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastRoughDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastRoughSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastRoughDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastRoughDerivative.get(i));
            }
        }
        return lastRoughSampleByChunk;
    }

    public Long2ObjectMap<List<List<Vector3d>>> getRoughDirectionsByChunk() {
        if (lastRoughDerivativeByChunk == null) {
            Pair<List<Location>, List<Vector3d>> rough = spline.sample(1);
            lastRoughSample = rough.a;
            lastRoughDerivative = rough.b;

            ChunkPos lastChunkPos = null;
            List<Location> lastLocationList = null;
            List<Vector3d> lastDirectionList = null;
            lastRoughSampleByChunk = new Long2ObjectOpenHashMap<>();
            lastRoughDerivativeByChunk = new Long2ObjectOpenHashMap<>();
            for (int i = 0; i < lastRoughSample.size(); i++) {
                Location location = lastRoughSample.get(i);
                Vector3d direction = lastRoughDerivative.get(i);
                ChunkPos currentChunkPos = new ChunkPos(MathHelper.fastFloor(location.x) >> 4, MathHelper.fastFloor(location.z) >> 4);
                if (!currentChunkPos.equals(lastChunkPos)) {
                    if (lastChunkPos != null && location.distance(lastRoughSample.get(i - 1)) < 32) {
                        lastLocationList.add(location);
                        lastDirectionList.add(direction);
                    } else {
                        lastLocationList.add(null);
                        lastDirectionList.add(null);
                    }

                    lastChunkPos = currentChunkPos;
                    lastRoughSampleByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastRoughDerivativeByChunk.putIfAbsent(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z), new ArrayList<>());
                    lastLocationList = new ArrayList<>();
                    lastDirectionList = new ArrayList<>();
                    lastRoughSampleByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastLocationList);
                    lastRoughDerivativeByChunk.get(ChunkPos.asLong(currentChunkPos.x, currentChunkPos.z)).add(lastDirectionList);
                }
                lastLocationList.add(location);
                lastDirectionList.add(lastRoughDerivative.get(i));
            }
        }
        return lastRoughDerivativeByChunk;
    }

    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

}
