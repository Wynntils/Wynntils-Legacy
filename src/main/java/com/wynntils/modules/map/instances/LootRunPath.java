/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.CubicSplines;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.util.math.BlockPos;

import javax.vecmath.Point3d;
import java.util.*;

public class LootRunPath {

    private CubicSplines.Spline3D spline;
    private LinkedHashSet<BlockPos> chests;

    private transient List<Location> lastSample;
    private transient boolean needToResample = true;

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
        needToResample = true;
        spline.addPoint(loc);
    }

    public void addPoints(Collection<? extends Point3d> points) {
        needToResample = true;
        spline.addPoints(points);
    }

    public void addPointToFront(Location loc) {
        addPointsToFront(Collections.singletonList(loc));
    }

    public void addPointsToFront(Collection<? extends Point3d> points) {
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

    public List<Location> getSmoothPoints() {
        if (needToResample) {
            lastSample = spline.sample();
        }
        return lastSample;
    }

    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

}
