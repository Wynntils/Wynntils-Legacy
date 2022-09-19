/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.entity.EntityPlayerSP;

import java.text.DecimalFormat;

public class CharacterData extends PlayerData {

    private static final int[] LEVEL_REQUIREMENTS = new int[] {110,190,275,385,505,645,790,940,1100,1370,1570,1800,2090,2400,2720,3100,3600,4150,4800,5300,5900,6750,7750,8900,10200,11650,13300,15200,17150,19600,22100,24900,28000,31500,35500,39900,44700,50000,55800,62000,68800,76400,84700,93800,103800,114800,126800,140000,154500,170300,187600,206500,227000,249500,274000,300500,329500,361000,395000,432200,472300,515800,562800,613700,668600,728000,792000,860000,935000,1040400,1154400,1282600,1414800,1567500,1730400,1837000,1954800,2077600,2194400,2325600,2455000,2645000,2845000,3141100,3404710,3782160,4151400,4604100,5057300,5533840,6087120,6685120,7352800,8080800,8725600,9578400,10545600,11585600,12740000,14418250,16280000,21196500,23315500,25649000,249232940};
    public static final DecimalFormat PER_FORMAT = new DecimalFormat("##.#");

    private ClassType currentClass = ClassType.NONE;
    private boolean reskinned = false;
    private int classId = CoreDBConfig.INSTANCE.lastSelectedClass;

    private int health = -1;
    private int maxHealth = -1;
    private int mana = -1;
    private int maxMana = -1;
    private int bloodPool = -1;
    private int maxBloodPool = -1;
    private int manaBank = -1;
    private int maxManaBank = -1;
    private int awakenedProgress = -1;
    private int level = -1;
    private float experiencePercentage = -1;
    private String elementalSpecialString = "";

    int lastLevel = 0;
    int lastXp = 0;

    public CharacterData() { }

    public void updatePlayerClass(ClassType newClass, boolean newClassIsReskinned) {
        // this updates your last class
        // this is needed because of the Wynncraft autojoin setting
        if (newClass != ClassType.NONE) {
            CoreDBConfig.INSTANCE.lastClass = newClass;
            CoreDBConfig.INSTANCE.lastClassIsReskinned = newClassIsReskinned;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
        }

        FrameworkManager.getEventBus().post(new WynnClassChangeEvent(newClass, newClassIsReskinned));
        currentClass = newClass;
        reskinned = newClassIsReskinned;
    }

    public int getXpNeededToLevelUp() {
        // Quick fix for crash bug - more investigation to be done.
        EntityPlayerSP player = getPlayer();
        try {
            if (player != null
                    && player.experienceLevel != 0
                    && currentClass != ClassType.NONE
                    && player.experienceLevel <= LEVEL_REQUIREMENTS.length
                    && lastLevel != player.experienceLevel) {
                lastLevel = player.experienceLevel;
                lastXp = LEVEL_REQUIREMENTS[player.experienceLevel - 1];
            }

            return currentClass == ClassType.NONE
                    || (player != null &&
                    (player.experienceLevel == 0 || player.experienceLevel > LEVEL_REQUIREMENTS.length)) ? -1 : lastXp;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public String getCurrentXPAsPercentage() {
        EntityPlayerSP player = getPlayer();
        if (player == null || currentClass == ClassType.NONE) return "";

        return PER_FORMAT.format(player.experience * 100);
    }

    public int getCurrentXP() {
        EntityPlayerSP player = getPlayer();
        if (player == null || currentClass == ClassType.NONE) return -1;

        return (int)(getXpNeededToLevelUp() * player.experience);
    }

    public float getExperiencePercentage() {
        return currentClass == ClassType.NONE ? -1 : experiencePercentage;
    }

    public int getLevel() {
        return currentClass == ClassType.NONE ? -1 : level;
    }

    public int getMaxMana() {
        return currentClass == ClassType.NONE ? -1 : maxMana;
    }

    public int getMaxManaBank() {
        return maxManaBank;
    }

    public int getMaxHealth() {
        return currentClass == ClassType.NONE ? -1 : maxHealth;
    }

    public int getMaxBloodPool() {
        return currentClass == ClassType.NONE ? -1 : maxBloodPool;
    }

    public int getCurrentHealth() {
        return currentClass == ClassType.NONE ? -1 : health;
    }

    public int getCurrentMana() {
        return currentClass == ClassType.NONE ? -1 : mana;
    }

    public int getManaBank() {
        return manaBank;
    }

    public int getCurrentBloodPool() {
        return currentClass == ClassType.NONE ? -1 : bloodPool;
    }

    public int getCurrentAwakenedProgress() {
        return currentClass == ClassType.NONE ? -1 : awakenedProgress;
    }

    public int getMaxAwakenedProgress() {
        return currentClass == ClassType.NONE ? -1 : 200;
    }

    public String getElementalSpecialString() {
        return elementalSpecialString;
    }

    public int getClassId() {
        return classId;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

    public boolean isLoaded() {
        return currentClass != ClassType.NONE;
    }

    public boolean isReskinned() {
        return reskinned;
    }

    public void setClassId(int id) {
        this.classId = id;

        CoreDBConfig.INSTANCE.lastSelectedClass = id;
        CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setManaBank(int manaBank) {
        this.manaBank = manaBank;
    }

    public void setMaxManaBank(int maxManaBank) {
        this.maxManaBank = maxManaBank;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public void setBloodPool(int bloodPool) {
        this.bloodPool = bloodPool;
    }

    public void setMaxBloodPool(int maxBloodPool) {
        this.maxBloodPool = maxBloodPool;
    }

    public void setAwakenedProgress(int awakenedProgress) {
        this.awakenedProgress = awakenedProgress;
    }

    public void setElementalSpecialString(String elementalSpecialString) {
        this.elementalSpecialString = elementalSpecialString;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperiencePercentage(float experiencePercentage) {
        this.experiencePercentage = experiencePercentage;
    }

}
