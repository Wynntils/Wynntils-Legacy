/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.webapi.profiles.music;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.SquareRegion;

public class SongAreaProfile {

    String id;
    String trackName;
    boolean fastSwitch;

    SquareRegion region;

    public SongAreaProfile(String id, String trackName, boolean fastSwitch, SquareRegion region) {
        this.id = id;
        this.trackName = trackName;
        this.fastSwitch = fastSwitch;
        this.region = region;
    }

    public SquareRegion getRegion() {
        return region;
    }

    public String getId() {
        return id;
    }

    public String getTrackName() {
        return trackName;
    }

    public boolean isFastSwitch() {
        return fastSwitch;
    }

    public double distanceSquared(Location other) {
        return getRegion().getCenterLocation().distanceSquared(other);
    }

}
