/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.CubicSplines;
import com.wynntils.core.utils.objects.Location;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LootRunPath {

    CubicSplines.Spline3D spline;
    ArrayList<Location> chests;

    transient List<Location> lastSample;
    transient boolean needToResample = true;

    public LootRunPath(Collection<? extends Point3d> points, ArrayList<Location> chests) {
        this.spline = new CubicSplines.Spline3D(points);
        this.chests = chests;
    }

    public LootRunPath() {
        this(Collections.emptyList(), new ArrayList<>());
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

    public void addChest(Location loc) {
        chests.add(loc);
    }

    public ArrayList<Location> getChests() {
        return chests;
    }

    public List<Location> getPoints() {
        return spline.getPoints();
    }

    public List<Location> getNormalizedPoints() {
        if (needToResample) {
            lastSample = spline.sample();
        }
        return lastSample;
    }

    public boolean isEmpty() {
        return getPoints().isEmpty();
    }

}
