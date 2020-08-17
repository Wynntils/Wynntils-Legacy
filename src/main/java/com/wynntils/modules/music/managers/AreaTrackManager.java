/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.music.SongAreaProfile;

public class AreaTrackManager {

    private static SongAreaProfile currentArea = null;

    /**
     * Tries to find an area song based on the provied location
     * @see SongAreaProfile
     * @see com.wynntils.webapi.profiles.music.MusicLocationsProfile
     *
     * @param playerPosition the provided location
     */
    public static void update(Location playerPosition) {
        currentArea = WebManager.getMusicLocations().getAreaTrack(playerPosition);
        if (currentArea == null) return;

        SoundTrackManager.findTrack(currentArea.getTrackName(), currentArea.isFastSwitch());
    }

    /**
     * @return the current area song or null
     */
    public static SongAreaProfile getCurrentArea() {
        return currentArea;
    }

    /**
     * This is used to overule territory songs
     * @return if territory update should be blocked
     */
    public static boolean isTerritoryUpdateBlocked() {
        return currentArea != null && currentArea.isIgnoreTerritory();
    }

}
