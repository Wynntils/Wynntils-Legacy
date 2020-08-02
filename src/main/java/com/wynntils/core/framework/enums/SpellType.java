/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SpellType {

    FIRST_SPELL  ("1st Spell", "^(?:Arrow Storm|Bolt Blizzard|Bash|Holy Blast|Heal|Remedy|Spin Attack|Whirlwind|Totem|Sky Emblem|1st Spell)\\b"),
    SECOND_SPELL ("2nd Spell", "^(?:Escape|Spider Jump|Charge|Leap|Teleport|Blink|Vanish|Shadow Clone|Haul|Soar|2nd Spell)\\b"),
    THIRD_SPELL  ("3rd Spell", "^(?:Bomb(?: Arrow)?|Creeper Dart|Uppercut|Heaven Jolt|Meteor|Dead Star|Multi Hit|Leopard Punches|Aura|Wind Surge|3rd Spell)\\b"),
    FOURTH_SPELL ("4th Spell", "^(?:Arrow Shield|Dagger Aura|War Scream|Cry of the Gods|Ice Snake|Crystal Reptile|Smoke Bomb|Blinding Cloud|Uproot|Gale Funnel|4th Spell)\\b");

    private final String shortName;
    private final Pattern regex;
    private String currentName;

    SpellType(String shortName, String regex) {
        this.shortName = shortName;
        this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.currentName = shortName;
    }

    public void updateCurrentName(String id) {
        Matcher m = regex.matcher(id);
        if (!m.find()) return;
        String currentName = m.group(0);
        if (currentName.equalsIgnoreCase(shortName)) return;
        this.currentName = currentName;
    }

    public String replaceWithShortName(String id) {
        return regex.matcher(id).replaceFirst(shortName);
    }

    public String replaceWithShortAndCurrentName(String id) {
        return regex.matcher(id).replaceFirst(shortName + " (" + currentName + ")");
    }

}
