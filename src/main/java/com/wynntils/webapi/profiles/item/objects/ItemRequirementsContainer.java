/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.objects;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.webapi.profiles.item.enums.ItemType;

public class ItemRequirementsContainer {

    String quest = null;
    ClassType classType = null;
    int level = 0;

    int strength = 0;
    int dexterity = 0;
    int intelligence = 0;
    int defense = 0;
    int agility = 0;

    public ItemRequirementsContainer() {}

    public ClassType getClassType() {
        return classType;
    }

    public int getAgility() {
        return agility;
    }

    public int getDefense() {
        return defense;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getLevel() {
        return level;
    }

    public int getStrength() {
        return strength;
    }

    public String getQuest() {
        return quest;
    }

    public boolean requiresClass(ItemType type) {
        return getRealClass(type) != null;
    }

    public boolean requiresQuest() {
        return quest != null;
    }

    public boolean hasRequirements(ItemType type) {
        return requiresQuest() || requiresClass(type) || level != 0 || strength != 0 || dexterity != 0 || intelligence != 0 || defense != 0 || agility != 0;
    }

    public ClassType getRealClass(ItemType type) {
        if(classType != null) return classType;

        if(type == ItemType.WAND) return ClassType.MAGE;
        if(type == ItemType.BOW) return ClassType.ARCHER;
        if(type == ItemType.SPEAR) return ClassType.WARRIOR;
        if(type == ItemType.DAGGER) return ClassType.ASSASSIN;
        if(type == ItemType.RELIK) return ClassType.SHAMAN;

        return null;
    }

}
