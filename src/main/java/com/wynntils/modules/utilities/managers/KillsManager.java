package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.objects.TimedSet;

import java.util.concurrent.TimeUnit;

public class KillsManager {
    private static final TimedSet<Boolean> KILLS_SET = new TimedSet<>(60, TimeUnit.SECONDS);
    public static void addKill() {
        KILLS_SET.put(true);
    }
    public static void update() {
        KILLS_SET.releaseEntries();
    }
    public static int getKillsPerMinute() {
        return KILLS_SET.size();
    }
}
