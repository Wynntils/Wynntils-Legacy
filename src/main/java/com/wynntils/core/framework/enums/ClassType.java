/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.enums;

public enum ClassType {

    MAGE("Mage/Dark Wizard"),
    ARCHER("Archer/Hunter"),
    WARRIOR("Warrior/Knight"),
    ASSASSIN("Assassin/Ninja"),
    SHAMAN("Shaman/Skyseer"),

    // This represents the class selection menu
    NONE("none");

    String displayName;

    ClassType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
