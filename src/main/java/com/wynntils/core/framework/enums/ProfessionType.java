package com.wynntils.core.framework.enums;

import com.wynntils.core.utils.StringUtils;

public enum ProfessionType {

    //gathering
    FISHING("Ⓚ"),
    WOODCUTTING("Ⓒ"),
    MINING("Ⓑ"),
    FARMING("Ⓙ"),

    //crafting
    SCRIBING("Ⓔ"),
    JEWELING("Ⓓ"),
    ALCHEMISM("Ⓛ"),
    COOKING("Ⓐ"),
    WEAPONSMITHING("Ⓖ"),
    TAILORING("Ⓕ"),
    WOODWORKING("Ⓘ"),
    ARMOURING("Ⓗ");

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
