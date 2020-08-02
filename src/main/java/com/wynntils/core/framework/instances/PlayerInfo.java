/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerInfo {

    private static PlayerInfo INSTANCE;
    private static final int[] LEVEL_REQUIREMENTS = new int[] {110,190,275,385,505,645,790,940,1100,1370,1570,1800,2090,2400,2720,3100,3600,4150,4800,5300,5900,6750,7750,8900,10200,11650,13300,15200,17150,19600,22100,24900,28000,31500,35500,39900,44700,50000,55800,62000,68800,76400,84700,93800,103800,114800,126800,140000,154500,170300,187600,206500,227000,249500,274000,300500,329500,361000,395000,432200,472300,515800,562800,613700,668600,728000,792000,860000,935000,1040400,1154400,1282600,1414800,1567500,1730400,1837000,1954800,2077600,2194400,2325600,2455000,2645000,2845000,3141100,3404710,3782160,4151400,4604100,5057300,5533840,6087120,6685120,7352800,8080800,8725600,9578400,10545600,11585600,12740000,14418250,16280000,21196500,23315500,25649000,249232940};
    public static final DecimalFormat PER_FORMAT = new DecimalFormat("##.#");
    private static final Pattern ACTIONBAR_PATTERN = Pattern.compile("(?:§❤ *([0-9]+)/([0-9]+))?.*? {2,}(?:§([LR])§-(?:§([LR])§-§([LR])?)?)?.*".replace("§", "(?:§[0-9a-fklmnor])*"));
    private static final boolean[] NO_SPELL = new boolean[0];

    /** Represents `L` in the currently casting spell */
    public static final boolean SPELL_LEFT = false;
    /** Represents `R` in the currently casting spell */
    public static final boolean SPELL_RIGHT = true;

    private final Minecraft mc;

    private ClassType currentClass = ClassType.NONE;
    private boolean currentClassIsReskinned = false;
    private int health = -1;
    private int maxHealth = -1;
    private int level = -1;

    private boolean[] lastSpell = NO_SPELL;
    private float experiencePercentage = -1;
    private int classId = CoreDBConfig.INSTANCE.lastSelectedClass;
    private HorseData horseData = null;

    private String lastActionBar;
    private String specialActionBar = null;

    private HashSet<String> friendList = new HashSet<>();
    private HashSet<String> guildList = new HashSet<>();
    private final PartyContainer playerParty = new PartyContainer();

    int lastLevel = 0;
    int lastXp = 0;

    public PlayerInfo(Minecraft mc) {
        this.mc = mc;

        INSTANCE = this;
    }

    public void updateActionBar(String actionBar) {
        if (currentClass == ClassType.NONE) return;

        // Avoid useless processing
        if (this.lastActionBar == null || !this.lastActionBar.equals(actionBar)) {
            this.lastActionBar = actionBar;

            if (actionBar.contains("|") || actionBar.contains("_")) {
                specialActionBar = StringUtils.getCutString(actionBar, "    ", "    " + TextFormatting.AQUA, false);
            } else {
                specialActionBar = null;
            }

            Matcher match = ACTIONBAR_PATTERN.matcher(actionBar);

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
                        lastSpell[i] = match.group(i + 3).charAt(0) == 'R' ? SPELL_RIGHT : SPELL_LEFT;
                    }
                }
            }
        }

        this.level = mc.player.experienceLevel;
        this.experiencePercentage = mc.player.experience;
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

    public void updatePlayerClass(ClassType newClass, boolean newClassIsReskinned) {
        // this updates your last class
        // this is needed because of the Wynncraft autojoin setting
        if (newClass != ClassType.NONE) {
            CoreDBConfig.INSTANCE.lastClass = newClass;
            CoreDBConfig.INSTANCE.lastClassIsReskinned = newClassIsReskinned;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
        }

        FrameworkManager.getEventBus().post(new WynnClassChangeEvent(newClass, newClassIsReskinned));
        this.currentClass = newClass;
        this.currentClassIsReskinned = newClassIsReskinned;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

    public boolean isCurrentClassReskinned() {
        return currentClassIsReskinned;
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
                    && mc.player.experienceLevel <= LEVEL_REQUIREMENTS.length
                    && lastLevel != mc.player.experienceLevel) {
                lastLevel = mc.player.experienceLevel;
                lastXp = LEVEL_REQUIREMENTS[mc.player.experienceLevel - 1];
            }
            return currentClass == ClassType.NONE || (mc.player != null && (mc.player.experienceLevel == 0 || mc.player.experienceLevel > LEVEL_REQUIREMENTS.length)) ? -1 : lastXp;
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public String getCurrentXPAsPercentage() { return currentClass == ClassType.NONE || mc.player == null ? "" : PER_FORMAT.format(mc.player.experience * 100); }

    public int getCurrentXP() { return currentClass == ClassType.NONE || mc.player == null? -1 : (int)((getXpNeededToLevelUp()) * mc.player.experience); }

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

    private static final Pattern level1SpellPattern = Pattern.compile("^(Left|Right|\\?)-(Left|Right|\\?)-(Left|Right|\\?)$");
    private static final Pattern lowLevelSpellPattern = Pattern.compile("^([LR?])-([LR?])-([LR?])$");
    private String lastParsedTitle = null;

    public boolean[] parseSpellFromTitle(String subtitle) {
        // Level 1: Left-Right-? in subtitle
        // Level 2-11: L-R-? in subtitle
        if (subtitle.equals(lastParsedTitle)) {
            return lastSpell;
        }
        lastParsedTitle = subtitle;
        if (subtitle.isEmpty()) {
            return (lastSpell = NO_SPELL);
        }
        String right = level == 1 ? "Right" : "R";
        Matcher m = (level == 1 ? level1SpellPattern : lowLevelSpellPattern).matcher(TextFormatting.getTextWithoutFormattingCodes(subtitle));
        if (!m.matches() || m.group(1).equals("?")) return (lastSpell = NO_SPELL);
        boolean spell1 = m.group(1).equals(right) ? SPELL_RIGHT : SPELL_LEFT;
        if (m.group(2).equals("?")) return (lastSpell = new boolean[]{ spell1 });
        boolean spell2 = m.group(2).equals(right) ? SPELL_RIGHT : SPELL_LEFT;
        if (m.group(3).equals("?")) return (lastSpell = new boolean[]{ spell1, spell2 });
        boolean spell3 = m.group(3).equals(right) ? SPELL_RIGHT : SPELL_LEFT;
        return (lastSpell = new boolean[]{ spell1, spell2, spell3 });
    }

    /**
     * Return an array of the last spell in the action bar.
     * Each value will be {@link #SPELL_LEFT} or {@link #SPELL_RIGHT}.
     *
     * @return A boolean[] whose length is 0, 1, 2 or 3.
     */
    public boolean[] getLastSpell() {
        if (getCurrentClass() == ClassType.NONE) {
            return NO_SPELL;
        }
        int level = getLevel();
        if (level <= 11) {
            String subtitle = (String) ReflectionFields.GuiIngame_displayedSubTitle.getValue(mc.ingameGUI);
            return parseSpellFromTitle(subtitle);
        }

        return lastSpell;
    }

    public static PlayerInfo getPlayerInfo() {
        if (INSTANCE == null) {
            return new PlayerInfo(Minecraft.getMinecraft());
        }

        return INSTANCE;
    }

    /**
     * @return Total number of emeralds in inventory (Including blocks and LE)
     */
    public int getMoney() {
        if (mc.player == null) return 0;
        return ItemUtils.countMoney(mc.player.inventory);
    }

    /**
     * @return Total number of health potions in inventory
     */
    public int getHealthPotions() {
        if (mc.player == null) return 0;
        NonNullList<ItemStack> contents = mc.player.inventory.mainInventory;

        int count = 0;

        for (ItemStack item : contents) {
            if (!item.isEmpty() && item.hasDisplayName() && item.getDisplayName().contains("Potion of Healing")) {
                count++;
            }
        }

        return count;
    }

    /**
     * @return Total number of mana potions in inventory
     */
    public int getManaPotions() {
        if (mc.player == null) return 0;
        NonNullList<ItemStack> contents = mc.player.inventory.mainInventory;

        int count = 0;

        for (ItemStack item : contents) {
            if (!item.isEmpty() && item.hasDisplayName() && item.getDisplayName().contains("Potion of Mana")) {
                count++;
            }
        }

        return count;
    }

    private static final Pattern UNPROCESSED_NAME_REGEX = Pattern.compile("^§fUnprocessed [a-zA-Z ]+§8 \\[(?:0|[1-9][0-9]*)/([1-9][0-9]*)]$");
    private static final Pattern UNPROCESSED_LORE_REGEX = Pattern.compile("^§7Unprocessed Material \\[Weight: ([1-9][0-9]*)]$");

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
        for (int i = 0, len = mc.player.inventory.getSizeInventory(); i < len; i++) {
            ItemStack it = mc.player.inventory.getStackInSlot(i);
            if (it.isEmpty()) continue;

            Matcher nameMatcher = UNPROCESSED_NAME_REGEX.matcher(it.getDisplayName());
            if (!nameMatcher.matches()) continue;

            NBTTagList lore = ItemUtils.getLoreTag(it);
            if (lore == null || lore.tagCount() == 0) continue;

            Matcher loreMatcher = UNPROCESSED_LORE_REGEX.matcher(lore.getStringTagAt(0));
            if (!loreMatcher.matches()) continue;

            // Found an unprocessed item

            if (maximum == -1) {
                maximum = Integer.parseInt(nameMatcher.group(1));
            }
            amount += Integer.parseInt(loreMatcher.group(1)) * it.getCount();

        }
        return new UnprocessedAmount(amount, maximum);
    }

    /**
     * @return The maximum number of soul points the current player can have
     *
     * Note: If veteran, this should always be 15, but currently might return the wrong value
     */
    public int getMaxSoulPoints() {
        int maxIfNotVeteran = 10 + MathHelper.clamp(getLevel() / 15, 0, 5);
        if (getSoulPoints() > maxIfNotVeteran) {
            return 15;
        }
        return maxIfNotVeteran;
    }

    /**
     * @return The current number of soul points the current player has
     *
     * -1 if unable to determine
     */
    public int getSoulPoints() {
        if (currentClass == ClassType.NONE || mc.player == null) return -1;
        ItemStack soulPoints = mc.player.inventory.mainInventory.get(8);
        if (soulPoints.getItem() != Items.NETHER_STAR && soulPoints.getItem() != Item.getItemFromBlock(Blocks.SNOW_LAYER)) {
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

    /**
     * @return The amount of items inside the players ingredient pouch (parsed from the items lore)
     * If countSlotsOnly is true, it only counts the number of used slots
     *
     * -1 if unable to determine
     */
    public int getIngredientPouchCount(boolean countSlotsOnly) {
        if (currentClass == ClassType.NONE || mc.player == null) return -1;
        ItemStack pouch = mc.player.inventory.mainInventory.get(13);
        int count = 0;

        List<String> lore = ItemUtils.getLore(pouch);

        for (int i = 4; i < lore.size(); i++) {
            String line = TextFormatting.getTextWithoutFormattingCodes(lore.get(i));

            int end = line.indexOf(" x ");

            if (end == -1) break;

            if (countSlotsOnly) {
                count++;
            } else {
                line = line.substring(0, end);
                count = count + Integer.parseInt(line);
            }
        }

        return count;
    }

    /**
     * @return The number of free slots in the user's inventory
     *
     * -1 if unable to determine
     */
    public int getFreeInventorySlots() {
        if (currentClass == ClassType.NONE || mc.player == null) return -1;
        return (int) mc.player.inventory.mainInventory.stream().filter(ItemStack::isEmpty).count();
    }

    public static class HorseData {
        public int xp;
        public int level;
        public int tier;
        public int maxLevel;
        public String armour;
        public int inventorySlot;

        public HorseData(int tier, int level, int xp, int maxLevel, String armour, int inventorySlot) {
            this.xp = xp;
            this.level = level;
            this.tier = tier;
            this.maxLevel = maxLevel;
            this.armour = armour;
            this.inventorySlot = inventorySlot;
        }

        public HorseData(ItemStack saddle, int inventorySlot) {
            this.inventorySlot = inventorySlot;

            List<String> lore = ItemUtils.getLore(saddle);

            tier = Integer.parseInt(lore.get(0).substring(7));
            level = Integer.parseInt(lore.get(1).substring(9, lore.get(1).indexOf("/")));
            maxLevel = Integer.parseInt(lore.get(1).substring(lore.get(1).indexOf("/")+1));
            armour = lore.get(3).substring(11);
            xp = Integer.parseInt(lore.get(4).substring(6, lore.get(4).indexOf("/")));
        }

    }

    /**
     * @return A HorseData object for the first horse found in the players inventory
     *
     */
    public HorseData getHorseData() {
        if (currentClass == ClassType.NONE || mc.player == null) return null;

        NonNullList<ItemStack> inventory = mc.player.inventory.mainInventory;

        if (horseData != null) {
            ItemStack stack = inventory.get(horseData.inventorySlot);

            if (!stack.isEmpty() && stack.hasDisplayName() && stack.getDisplayName().contains(" Horse")) {
                horseData = new HorseData(stack, horseData.inventorySlot);
                return horseData;
            }
        }

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);

            if (!stack.isEmpty() && stack.hasDisplayName() && stack.getDisplayName().contains(" Horse")) {
                horseData = new HorseData(stack, i);
                return horseData;
            }
        }

        return null;
    }

}
