/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.Location;

import java.util.ArrayList;

public class LootRunPath {

    ArrayList<Location> points;
    ArrayList<Location> chests;

    public LootRunPath(ArrayList<Location> points, ArrayList<Location> chests) {
        this.points = points;
        this.chests = chests;
    }

    public LootRunPath() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public void addPoint(Location loc) {
        points.add(loc);
    }

    public void addChest(Location loc) {
        chests.add(loc);
    }

    public ArrayList<Location> getChests() {
        return chests;
    }

    public ArrayList<Location> getPoints() {
        return points;
    }

    public void normalizePath() {
        //TODO normalize calculations
    }

}
