/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerInfo {

    private static PlayerInfo instance;
    private static final int[] xpNeeded = new int[] {110,190,275,385,505,645,790,940,1100,1370,1570,1800,2090,2400,2720,3100,3600,4150,4800,5300,5900,6750,7750,8900,10200,11650,13300,15200,17150,19600,22100,24900,28000,31500,35500,39900,44700,50000,55800,62000,68800,76400,84700,93800,103800,114800,126800,140000,154500,170300,187600,206500,227000,249500,274000,300500,329500,361000,395000,432200,472300,515800,562800,613700,668600,728000,792000,860000,935000,1040400,1154400,1282600,1414800,1567500,1730400,1837000,1954800,2077600,2194400,2325600,2455000,2645000,2845000,3141100,3404710,3782160,4151400,4604100,5057300,5533840,6087120,6685120,7352800,8080800,8725600,9578400,10545600,11585600,12740000,14418250,16280000,21196500,200268440};
    private static final DecimalFormat perFormat = new DecimalFormat("##.#");
    private static final Pattern actionbarPattern = Pattern.compile("(?:§❤ *([0-9]+)/([0-9]+))?.*? {2,}(?:§([LR])§-(?:§([LR])§-§([LR])?)?)?.*".replace("§", "(?:§[0-9a-fklmnor])*"));
    private static final boolean[] noSpell = new boolean[0];

    /** Represents `L` in the currently casting spell */
    public static final boolean SPELL_LEFT = false;
    /** Represents `R` in the currently casting spell */
    public static final boolean SPELL_RIGHT = true;

    private Minecraft mc;

    private ClassType currentClass = ClassType.NONE;
    private int health = -1;
    private int maxHealth = -1;
    private int level = -1;
    private boolean[] lastSpell = noSpell;
    private float experiencePercentage = -1;
    private int classId = CoreDBConfig.INSTANCE.lastSelectedClass;

    private String lastActionBar;
    private String specialActionBar = null;

    private HashSet<String> friendList = new HashSet<>();
    private HashSet<String> guildList = new HashSet<>();
    private PartyContainer playerParty = new PartyContainer();

    int lastLevel = 0;
    int lastXp = 0;

    public PlayerInfo(Minecraft mc) {
        this.mc = mc;

        instance = this;
    }

    public void updateActionBar(String lastActionBar) {
        //avoid useless processing
        if(this.lastActionBar != null && this.lastActionBar.equals(lastActionBar)) return;

        this.lastActionBar = lastActionBar;

        if (currentClass != ClassType.NONE) {
            if(lastActionBar.contains("|") || lastActionBar.contains("_"))  {
                specialActionBar = Utils.getCutString(lastActionBar,"    ","    " + TextFormatting.AQUA,false);
            }else{
                specialActionBar = null;
            }

            Matcher match = actionbarPattern.matcher(lastActionBar);

            if (match.matches()) {
                if (match.group(1) != null) {
                    this.health = Integer.parseInt(match.group(1));
                    this.maxHealth = Integer.parseInt(match.group(2));
                }

                if (match.group(3) != null) {
                    int size;
                    for (size = 1; size < 3; ++size) {
                        if (match.group(size + 3) == null) break;
                    }

                    lastSpell = new boolean[size];
                    for (int i = 0; i < size; ++i) {
                        lastSpell[i] = match.group(i + 3).charAt(0) == 'R';
                    }
                }
            } else {
                lastSpell = noSpell;
            }

            this.level = mc.player.experienceLevel;
            this.experiencePercentage = mc.player.experience;
        }
    }

    public HashSet<String> getFriendList() {
        return friendList;
    }

    public HashSet<String> getGuildList() {
        return guildList;
    }

    public void setFriendList(HashSet<String> value) {
        friendList = value;
    }

    public void setGuildList(HashSet<String> value) {
        guildList = value;
    }

    public String getSpecialActionBar() {
        return specialActionBar;
    }

    public String getLastActionBar() {
        return lastActionBar;
    }

    public void updatePlayerClass(ClassType currentClass) {
        if(currentClass != ClassType.NONE) {
            CoreDBConfig.INSTANCE.lastClass = currentClass;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
        }

        FrameworkManager.getEventBus().post(new WynnClassChangeEvent(this.currentClass, currentClass));
        this.currentClass = currentClass;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

    public int getCurrentHealth() {
        return currentClass == ClassType.NONE ? -1 : health;
    }

    public int getCurrentMana() { return currentClass == ClassType.NONE ? -1 : mc.player.getFoodStats().getFoodLevel(); }

    public int getMaxHealth() {
        return currentClass == ClassType.NONE ? -1 : maxHealth;
    }

    public float getExperiencePercentage() { return currentClass == ClassType.NONE ? -1 : experiencePercentage; }

    public int getXpNeededToLevelUp() {
        // Quick fix for crash bug - more investigation to be done.
        try {
            if (mc.player != null
                    && mc.player.experienceLevel != 0
                    && currentClass != ClassType.NONE
                    && mc.player.experienceLevel <= xpNeeded.length
                    && lastLevel != mc.player.experienceLevel) {
                lastLevel = mc.player.experienceLevel;
                lastXp = xpNeeded[mc.player.experienceLevel - 1];
            }
            return currentClass == ClassType.NONE || (mc.player != null && (mc.player.experienceLevel == 0 || mc.player.experienceLevel > xpNeeded.length)) ? -1 : lastXp;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public String getCurrentXPAsPercentage() { return currentClass == ClassType.NONE || mc.player == null ? "" : perFormat.format(mc.player.experience * 100); }

    public int getCurrentXP() { return currentClass == ClassType.NONE  || mc.player == null? -1 : (int)((getXpNeededToLevelUp()) * mc.player.experience); }

    public int getLevel() { return currentClass == ClassType.NONE ? -1 : level; }

    public int getMaxMana() {return currentClass == ClassType.NONE ? -1 : 20;}

    public PartyContainer getPlayerParty() {
        return playerParty;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int id) {
        this.classId = id;
        CoreDBConfig.INSTANCE.lastSelectedClass = id;
        CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
    }

    /**
     * Return an array of the last spell in the action bar.
     * Each value will be {@link #SPELL_LEFT} or {@link #SPELL_RIGHT}.
     *
     * @return A boolean[] whose length is 0, 1, 2 or 3.
     */
    public boolean[] getLastSpell() {
        return lastSpell;
    }

    public static PlayerInfo getPlayerInfo() {
        if(instance == null)
            return new PlayerInfo(Minecraft.getMinecraft());
        else
            return instance;
    }

    /**
     * @return Total number of emeralds in inventory (Including blocks and LE)
     */
    public int getMoney() {
        int money = 0;

        for (int i = 0; i < Minecraft.getMinecraft().player.inventory.getSizeInventory(); i++) {
            ItemStack it = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (it == null || it.isEmpty()) {
                continue;
            }

            if (it.getItem() == Items.EMERALD) {
                money += it.getCount();
                continue;
            }
            if (it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                money += it.getCount() * 64;
                continue;
            }
            if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                money += it.getCount() * (64 * 64);
            }
        }

        return money;
    }

    private static final Pattern unprocessedNameRegex = Pattern.compile("^§fUnprocessed [a-zA-Z ]+§8 \\[(?:0|[1-9][0-9]*)/([1-9][0-9]*)]$");
    private static final Pattern unprocessedLoreRegex = Pattern.compile("^§7Unprocessed Material \\[Weight: ([1-9][0-9]*)]$");

    public static class UnprocessedAmount {
        public int current;
        public int maximum;
        public UnprocessedAmount(int current, int maximum) {
            this.current = current;
            this.maximum = maximum;
        }
    }
    /**
     * @return UnprocessedAmount((total weight of unprocessed materials), (maximum weight that can be held)).
     *
     * If there are no unprocessed materials, maximum will be -1.
     */
    public UnprocessedAmount getUnprocessedAmount() {
        int maximum = -1;
        int amount = 0;
        for (int i = 0; i < Minecraft.getMinecraft().player.inventory.getSizeInventory(); i++) {
            ItemStack it = Minecraft.getMinecraft().player.inventory.getStackInSlot(i);
            if (it != null && !it.isEmpty()) {
                Matcher nameMatcher = unprocessedNameRegex.matcher(it.getDisplayName());
                if (nameMatcher.matches()) {
                    NBTTagList lore = Utils.getLoreTag(it);
                    if (lore != null && lore.tagCount() != 0) {
                        Matcher loreMatcher = unprocessedLoreRegex.matcher(lore.getStringTagAt(0));
                        if (loreMatcher.matches()) {
                            // Found an unprocessed item
                            if (maximum == -1) {
                                maximum = Integer.parseInt(nameMatcher.group(1));
                            }
                            amount += Integer.parseInt(loreMatcher.group(1)) * it.getCount();
                        }
                    }
                }
            }
        }
        return new UnprocessedAmount(amount, maximum);
    }

    /**
     * @return The maximum number of soul points the current player can have
     *
     * Note: If veteren, this should always be 15, but currently returns the wrong value
     */
    public int getMaxSoulPoints() {
        return 10 + MathHelper.clamp(getLevel() / 15, 0, 5);
    }

    /**
     * @return The current number of soul points the current player has
     *
     * -1 if unable to determine
     */
    public int getSoulPoints() {
        if (currentClass == ClassType.NONE || mc.player == null) return -1;
        ItemStack soulPoints = mc.player.inventory.mainInventory.get(8);
        if (soulPoints.getItem() != Items.NETHER_STAR) {
            return -1;
        }
        return soulPoints.getCount();
    }

    /**
     * @return Time in game ticks (1/20th of a second, 50ms) until next soul point
     *
     * -1 if unable to determine
     *
     * Also check that {@code {@link #getMaxSoulPoints()} >= {@link #getSoulPoints()}},
     * in which case soul points are already full
     */
    public int getTicksToNextSoulPoint() {
        if (currentClass == ClassType.NONE || mc.world == null) return -1;
        int ticks = ((int) (mc.world.getWorldTime() % 24000) + 24000) % 24000;
        return ((24000 - ticks) % 24000);
    }

}
