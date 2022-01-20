/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public class SkillPointAllocation {
    
    private int strength;
    private int dexterity;
    private int intelligence;
    private int defence;
    private int agility;
    
    public SkillPointAllocation(int strength, int dexterity, int intelligence, int defence, int agility) {
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.defence = defence;
        this.agility = agility;
    }
    
    public SkillPointAllocation() {
        this(0, 0, 0, 0, 0);
    }
    
    public void setStrength(int strength) {
        this.strength = strength;
    }
    
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }
    
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    
    public void setDefence(int defense) {
        this.defence = defense;
    }
    
    public void setAgility(int agility) {
        this.agility = agility;
    }
    
    public int getStrength() {
        return this.strength;
    }
    
    public int getDexterity() {
        return this.dexterity;
    }
    
    public int getIntelligence() {
        return this.intelligence;
    }
    
    public int getDefence() {
        return this.defence;
    }
    
    public int getAgility() {
        return this.agility;
    }
    
    public int getTotalSkillPoints() {
        return this.strength + this.dexterity + this.intelligence + this.defence + this.agility;
    }
    
    public int[] getAsArray() {
        return new int[] {this.strength, this.dexterity, this.intelligence, this.defence, this.agility};
    }

}
