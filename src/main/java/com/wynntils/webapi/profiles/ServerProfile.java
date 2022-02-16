/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.utils.StringUtils;

import java.util.Set;

public class ServerProfile {

    long firstSeen;
    Set<String> players;

    public ServerProfile(long firstSeem, Set<String> players) {
        this.firstSeen = firstSeem; this.players = players;
    }

    public Set<String> getPlayers() {
        return players;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public String getUptime() {
        return StringUtils.millisToLongString(System.currentTimeMillis() - firstSeen);
    }

    public int getUptimeMinutes() {
        long milliseconds = System.currentTimeMillis() - getFirstSeen();
        return (int) (milliseconds / (1000 * 60));
    }

    /**
     * This makes the firstSeem match the user computer time instead of the server time
     *
     * @param serverTime the input server time
     */
    public void matchTime(long serverTime) {
        firstSeen = firstSeen - (System.currentTimeMillis() - serverTime);
    }

}
