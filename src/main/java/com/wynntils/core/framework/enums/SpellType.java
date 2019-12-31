/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.enums;

import java.util.regex.Pattern;

public enum SpellType {

    FIRST_SPELL  ("1st Spell", "(Arrow Storm|Bolt Blizzard|Bash|Holy Blast|Heal|Remedy|Spin Attack|Whirlwind|Totem|Sky Emblem)\b"),
    SECOND_SPELL ("2nd Spell", "(Escape|Spider Jump|Charge|Leap|Teleport|Blink|Vanish|Shadow Clone|Haul|Soar)\b"),
    THIRD_SPELL  ("3rd Spell", "(Bomb(?: Arrow)?|Creeper Dart|Uppercut|Heaven Jolt|Meteor|Dead Star|Multi Hit|Leopard Punches|Aura|Wind Surge)\b"),
    FOURTH_SPELL ("4th Spell", "(Arrow Shield|Dagger Aura|War Scream|Cry of the Gods|Ice Snake|Crystal Reptile|Smoke Bomb|Blinding Cloud|Uproot|Gale Funnel)\b");

    String shortName; Pattern regex;

    SpellType(String shortName, String regex) {
        this.shortName = shortName;
        this.regex = Pattern.compile(regex);
    }

    public String getShortName() {
        return shortName;
    }

    public Pattern getRegex() {
        return regex;
    }

    public static SpellType getSpell(String input) {
        for (SpellType type : values()) {
            if (type.regex.matcher(input).find()) return type;
        }

        return null;
    }

}
