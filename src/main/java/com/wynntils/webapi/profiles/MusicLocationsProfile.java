/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.profiles;

import java.util.HashMap;

public class MusicLocationsProfile {

    HashMap<String, String> dungeons = new HashMap<>();
    HashMap<String, String> entries = new HashMap<>();

    public MusicLocationsProfile() { }

    public String getDungeonTrack(String name) {
        return dungeons.getOrDefault(name, null);
    }

    public String getEntryTrack(String name) {
        return entries.getOrDefault(name, null);
    }

}
