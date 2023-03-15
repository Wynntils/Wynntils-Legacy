package com.wynntils.modules.utilities.managers;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.objects.TimedSet;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;


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
     * Get expected grind time for leveling up
     * @return The Grind Time for Leveling Up in Minutes
     */
    public static int getLevelingGrindTime() {
        CharacterData p = PlayerInfo.get(CharacterData.class);
        int perminute = getXpPerMinute();
        int required = p.getXpNeededToLevelUp() - p.getCurrentXP();
        if (perminute != 0) {
            return required / perminute * 60;
        } else {
            return 0;
        }
    }

    /**
     * Get XP/Minute as a raw integer
     * @return The XP value gathered in one minute
     */
    public static int getXpPerMinute() {
        if (XP_SET.isEmpty()) return 0;

        int sum = 0;
        for (Integer integer : XP_SET) {
            sum += integer;
        }
        return sum;
    }

    /**
     * Same as getXpPerMinute(), but returns a relative percentage to the current level instead of an
     * absolute XP count. Formatted as a string with 2 decimals.
     * @return Formatted string with the relative XP in one minute
     */
    public static String getXpPercentPerMinute() {
        int LPM = getXpPerMinute();
        int maxXp = PlayerInfo.get(CharacterData.class).getXpNeededToLevelUp();
        double PPM = ((double) LPM / (double) maxXp) * 100D;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(PPM);
    }
}
