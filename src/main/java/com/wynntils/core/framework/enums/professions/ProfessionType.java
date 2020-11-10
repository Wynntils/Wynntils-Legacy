/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.framework.enums.professions;

import com.wynntils.core.utils.StringUtils;

public enum ProfessionType {

    // gathering
    WOODCUTTING("Ⓒ"),
    MINING("Ⓑ"),
    FISHING("Ⓚ"),
    FARMING("Ⓙ"),

    // crafting
    ALCHEMISM("Ⓛ"),
    ARMOURING("Ⓗ"),
    COOKING("Ⓐ"),
    JEWELING("Ⓓ"),
    SCRIBING("Ⓔ"),
    TAILORING("Ⓕ"),
    WEAPONSMITHING("Ⓖ"),
    WOODWORKING("Ⓘ"),

    // handled by leaderboard
    OVERALL("");

    String icon;

    ProfessionType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return StringUtils.capitalizeFirst(name().toLowerCase());
    }

    public static ProfessionType fromMessage(String input) {
        for(ProfessionType type : values()) {
            if (!input.toLowerCase().contains(type.getIcon().toLowerCase() + " " + type.getName().toLowerCase())) continue;

            return type;
        }

        return null;
    }

}
