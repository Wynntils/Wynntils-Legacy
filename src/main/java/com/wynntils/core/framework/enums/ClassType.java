/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.enums;

public enum ClassType {

    MAGE("Mage", "Dark Wizard"),
    ARCHER("Archer", "Hunter"),
    WARRIOR("Warrior", "Knight"),
    ASSASSIN("Assassin", "Ninja"),
    SHAMAN("Shaman", "Skyseer"),

    // This represents the class selection menu
    NONE("none", "none");

    private final String name;
    private final String reskinnedName;

    ClassType(String name, String reskinnedName) {
        this.name = name;
        this.reskinnedName = reskinnedName;
    }

    public static ClassType fromName(String className) {
        for (ClassType type : values()) {
            if (className.equalsIgnoreCase(type.name) || className.equalsIgnoreCase(type.reskinnedName)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isReskinned(String className) {
        for (ClassType type : values()) {
            if (className.equalsIgnoreCase(type.name)) return false;
            if (className.equalsIgnoreCase(type.reskinnedName)) return true;
        }
        return false;
    }

    public String getDisplayName() {
        return name + "/" + reskinnedName;
    }
}
