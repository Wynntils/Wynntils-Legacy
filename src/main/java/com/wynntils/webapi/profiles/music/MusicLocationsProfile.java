/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles.music;

import com.wynntils.core.utils.objects.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicLocationsProfile {

    Map<String, String> dungeons = new HashMap<>();
    Map<String, String> entries = new HashMap<>();
    Map<String, String> bosses = new HashMap<>();

    List<SongAreaProfile> areas = new ArrayList<>();

    public MusicLocationsProfile() { }

    public String getDungeonTrack(String name) {
        return dungeons.getOrDefault(name, null);
    }

    public String getEntryTrack(String name) {
        return entries.getOrDefault(name, null);
    }

    public String getBossTrack(String name) {
        return bosses.getOrDefault(name, null);
    }

    public SongAreaProfile getAreaTrack(Location loc) {
        for (SongAreaProfile area : areas) {
            if (!area.getRegion().isInside(loc)) continue;

            return area;
        }

        return null;
    }

    public SongAreaProfile getAreaTrack(String id) {
        for (SongAreaProfile area : areas) {
            if (!area.getId().equalsIgnoreCase(id)) continue;

            return area;
        }

        return null;
    }

}
