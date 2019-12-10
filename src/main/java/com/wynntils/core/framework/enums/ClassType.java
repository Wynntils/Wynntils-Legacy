/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.enums;

public enum ClassType {

    MAGE("Mage/Dark Wizard"),
    ARCHER("Archer/Hunter"),
    WARRIOR("Warrior/Knight"),
    ASSASSIN("Assassin/Ninja"),
    SHAMAN("Shaman/Skyseer"),
    NONE("none");

    String displayName;

    ClassType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
