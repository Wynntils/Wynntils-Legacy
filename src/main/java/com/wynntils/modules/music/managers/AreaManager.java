/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.music.MusicAreaProfile;

public class AreaManager {

    private static MusicAreaProfile currentArea = null;

    public static void update(Location playerPosition) {
        currentArea = WebManager.getMusicLocations().getAreaTrack(playerPosition);
        if (currentArea == null) return;

        MusicManager.playSong(currentArea.getTrackName(), true);
    }

    public static MusicAreaProfile getCurrentArea() {
        return currentArea;
    }

    public static boolean isTerritoryUpdateBlocked() {
        return currentArea != null && currentArea.isIgnoreTerritory();
    }

}
