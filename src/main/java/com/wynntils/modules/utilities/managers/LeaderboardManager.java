/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.LeaderboardProfile;

import java.util.HashMap;
import java.util.UUID;

public class LeaderboardManager {

    private static HashMap<UUID, LeaderboardProfile> leaderboard = new HashMap<>();

    public static synchronized void updateLeaders() {
        WebManager.getLeaderboard(result -> leaderboard = result);
    }

    public static boolean isLeader(UUID uuid) {
        return leaderboard.containsKey(uuid);
    }

    public static LeaderboardProfile getLeader(UUID uuid) {
        return leaderboard.get(uuid);
    }

}
