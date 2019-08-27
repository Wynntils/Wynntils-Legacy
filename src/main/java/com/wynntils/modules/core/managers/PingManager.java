/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.CommandResponse;

import java.util.regex.Pattern;

public class PingManager {

    private static final Pattern pattern = Pattern.compile("(§4/toggle)");

    private static long lastPing = 1000;
    private static long lastCall = 0;

    public static void calculatePing() {
        if(System.currentTimeMillis() - lastCall < 15000) return;

        CommandResponse response = new CommandResponse("/toggle", (m, t) -> {
            lastPing = System.currentTimeMillis() - lastCall;
            Reference.LOGGER.info("Updated user ping to " + lastPing + "ms");
        }, pattern);

        response.setCancel(true);

        lastCall = System.currentTimeMillis();
        response.executeCommand();
    }

    /**
     * Returns the approximate player ping
     *
     * @return the approximate player ping
     */
    public static long getLastPing() {
        return lastPing;
    }

}
