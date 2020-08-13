/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.profiles;

import java.util.HashMap;

public class MusicLocationsProfile {

    HashMap<String, String> dungeons = new HashMap<>();

    public MusicLocationsProfile() { }

    public String getDungeonTrack(String name) {
        return dungeons.getOrDefault(name, null);
    }

}
