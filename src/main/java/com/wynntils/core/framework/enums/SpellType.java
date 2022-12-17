/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums;

public enum SpellType {
    ARROW_STORM(ClassType.ARCHER, 1, "Arrow Storm", 6, 0),
    ESCAPE(ClassType.ARCHER, 2, "Escape", 3, 0),
    BOMB(ClassType.ARCHER, 3, "Arrow Bomb", 8, 0),
    ARROW_SHIELD(ClassType.ARCHER, 4, "Arrow Shield", 8, 1),

    SPIN_ATTACK(ClassType.ASSASSIN, 1, "Spin Attack", 6, 0),
    DASH(ClassType.ASSASSIN, 2, "Dash", 2, 0),
    MULTI_HIT(ClassType.ASSASSIN, 3, "Multi Hit", 8, 0),
    SMOKE_BOMB(ClassType.ASSASSIN, 4, "Smoke Bomb", 8, 0),

    BASH(ClassType.WARRIOR, 1, "Bash", 6, 0),
    CHARGE(ClassType.WARRIOR, 2, "Charge", 4, 0),
    UPPERCUT(ClassType.WARRIOR, 3, "Uppercut", 9, 0),
    WAR_SCREAM(ClassType.WARRIOR, 4, "War Scream", 7, -1),

    HEAL(ClassType.MAGE, 1, "Heal", 8, -1),
    TELEPORT(ClassType.MAGE, 2, "Teleport", 4, 0),
    METEOR(ClassType.MAGE, 3, "Meteor", 8, 0),
    ICE_SNAKE(ClassType.MAGE, 4, "Ice Snake", 6, -1),

    TOTEM(ClassType.SHAMAN, 1, "Totem", 4, 0),
    HAUL(ClassType.SHAMAN, 2, "Haul", 3, -1),
    AURA(ClassType.SHAMAN, 3, "Aura", 8, 0),
    UPROOT(ClassType.SHAMAN, 4, "Uproot", 6, 0),

    // Unspecified spells
    FIRST_SPELL(ClassType.NONE, 1, "1st Spell", 0, 0),
    SECOND_SPELL(ClassType.NONE, 2, "2nd Spell", 0, 0),
    THIRD_SPELL(ClassType.NONE, 3, "3rd Spell", 0, 0),
    FOURTH_SPELL(ClassType.NONE, 4, "4th Spell", 0, 0);


    private ClassType classType;
    private int spellNumber;
    private String name;
    private int startManaCost;
    private int gradeManaChange;

    public ClassType getClassType() {
        return classType;
    }

    public int getSpellNumber() {
        return spellNumber;
    }

    public String getName() {
        return name;
    }

    public int getUnlockLevel(int grade) {
        int unlockLevel = (spellNumber - 1) * 10 + 1;
        if (grade == 1) return unlockLevel;
        if (grade == 2) return unlockLevel + 15;
        if (grade == 3) return unlockLevel + 35;
        assert(false);
        return 0;
    }

    public int getGrade(int level) {
        int compareLevel = level - (spellNumber - 1) * 10;
        if (compareLevel  >= 36) {
            return 3;
        } else if (compareLevel >= 16) {
            return 2;
        } else if (compareLevel >= 1) {
            return 1;
        } else {
            // not unlocked
            return 0;
        }
    }

    private int getUnreducedManaCost(int level) {
        return startManaCost + (getGrade(level)-1) * gradeManaChange;
    }

    SpellType(ClassType classType, int spellNumber, String name, int startManaCost, int gradeManaChange) {
        this.classType = classType;
        this.spellNumber = spellNumber;
        this.name = name;
        this.startManaCost = startManaCost;
        this.gradeManaChange = gradeManaChange;
    }

    public static SpellType fromName(String name) {
        for (SpellType spellType : values()) {
            if (name.matches("^" + spellType.name + "\\b.*")) {
                return spellType;
            }
        }
        return null;
    }
    public SpellType forOtherClass(ClassType otherClass) {
        return forClass(otherClass, getSpellNumber());
    }

    public static SpellType forClass(ClassType classRequired, int spellNumber) {
        for (SpellType spellType : values()) {
            if (spellType.classType.equals(classRequired)
                    && spellType.spellNumber == spellNumber) {
                return spellType;
            }
        }
        return null;
    }

    public String getGenericName() {
        return forClass(ClassType.NONE, getSpellNumber()).getName();
    }

    public String getGenericAndSpecificName() {
        return getGenericName() + " (" + getName() + ")";
    }

}
