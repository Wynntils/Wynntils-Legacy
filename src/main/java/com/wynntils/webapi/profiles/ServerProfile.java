/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.core.utils.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        return (int) (Integer.parseInt(getUptime()) / TimeUnit.MINUTES.toMillis(1));
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
