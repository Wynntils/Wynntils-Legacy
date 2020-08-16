/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.framework.enums.professions.ProfessionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LeaderboardProfile {

    String name;
    int timePlayed;

    Map<ProfessionType, Integer> ranks;

    public LeaderboardProfile(String name, int timePlayed, Map<ProfessionType, Integer> ranks) {
        this.name = name;
        this.timePlayed = timePlayed;
        this.ranks = ranks;
    }

    public String getName() {
        return name;
    }

    public Integer rankSize() {
        return getRanks().size();
    }

    public Integer getRankAt(ProfessionType profession) {
        return ranks.getOrDefault(profession, -1);
    }

    public Set<Map.Entry<ProfessionType, Integer>> getRanks() {
        return ranks.entrySet();
    }

    public int getTimePlayed() {
        return timePlayed;
    }

}
