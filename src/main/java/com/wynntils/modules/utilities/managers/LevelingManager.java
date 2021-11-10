package com.wynntils.modules.utilities.managers;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.objects.TimedSet;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Idea: Maybe add a "Time Left" option to see how long leveling up takes at the current XP rate?
 */

@SuppressWarnings("ConstantConditions")
public class LevelingManager {
    private static final TimedSet<Integer> XP_SET = new TimedSet<>(60, TimeUnit.SECONDS);
    private static int currentXp = -1;

    /**
     * Update the leveling manager.
     * Can be called multiple times per tick, won't update if the XP didn't change.
     */
    public static void update() {
        int newXp = PlayerInfo.get(CharacterData.class).getCurrentXP();

        if (currentXp == -1) { // init value
            currentXp = newXp;
        }

        if (newXp < currentXp) { // level up
            currentXp = 0;
        } else if (currentXp == 0) { // login (xp gets added from 0 when selecting class)
            currentXp = newXp;
        }

        if (newXp > currentXp) { // update if changed
            XP_SET.put(newXp - currentXp);
            currentXp = newXp;
        }

        XP_SET.releaseEntries();
    }

    /**
     * Get XP/Minute as a raw integer
     * @return The XP value gathered in one minute
     */
    public static int getXpPerMinute() {
        update();
        if (XP_SET.isEmpty()) return 0;

        int sum = 0;
        for (Integer integer : XP_SET) {
            sum += integer;
        }
        return sum;
    }

    /**
     * Get the amount of XP adding events.
     * Note: This can effectively be used as a kill counter, because enemies will give you XP upon death.
     * (Idea: Maybe check if area damage was dealt before to avoid getting a "kill" by getting XP through
     * dungeons/quests etc.? Might be useful to make another set which only counts while area damage is dealt)
     * @return The amount of XP adding events in one minute
     */
    public static int getXpEventsPerMinute() {
        update();
        return XP_SET.size();
    }

    /**
     * Same as getXpPerMinute(), but returns a relative percentage to the current level instead of an
     * absolute XP count. Formatted as a string with 2 decimals.
     * @return Formatted string with the relative XP in one minute
     */
    public static String getXpPercentPerMinute() {
        update();
        int LPM = getXpPerMinute();
        int maxXp = PlayerInfo.get(CharacterData.class).getXpNeededToLevelUp();
        double PPM = ((double) LPM / (double) maxXp) * 100D;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(PPM);
    }
}
