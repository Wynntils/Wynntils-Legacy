/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.core.instances.FakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class Utils {

    public static HashMap<String, String> getItemFieldName = new HashMap<>();
    public static HashMap<String, Integer> getItemFieldRank = new HashMap<>();
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Wynntils Utilities").build());
    private static Pattern WYYNCRAFT_SERVERS_WINDOW_TITLE_PATTERN = Pattern.compile("Wynncraft Servers: Page \\d+");
    private static Random random = new Random();

    /**
     * Runs a runnable after the determined time
     *
     * @param r the runnable
     * @param timeUnit the time unit
     * @param amount the amount of the specified time unit
     */
    public static void runAfter(Runnable r, TimeUnit timeUnit, long amount) {
        executorService.scheduleAtFixedRate(r, 0, amount, timeUnit);
    }

    /**
     * @return the main random instance
     */
    public static Random getRandom() {
        return random;
    }

    public static ScheduledFuture runTaskTimer(Runnable r, TimeUnit timeUnit, long amount) {
        return executorService.scheduleAtFixedRate(r, 0, amount, timeUnit);
    }

    public static void runAsync(Runnable r) {
        executorService.submit(r);
    }

    /**
     * Removes the invisible character À
     *
     * @param input string
     * @return input string without the invisible character
     */
    public static String stripInvisibleChar(String input) {
        return input.replace("À", "");
    }

    /**
     * Removes the percentage box (e.g. [96%])
     *
     * @param input string
     * @return input string without the percentage box
     */
    public static String stripPercentage(String input) {
        return input.replaceAll(" \\[\\d{1,3}%\\]", "");
    }

    /**
     * Removes the "Perfect"-rainbow from input string
     *
     * @param input string
     * @return input string without the "Perfect"-rainbow
     */
    public static String stripPerfect(String input) {
        return TextFormatting.getTextWithoutFormattingCodes(input).replace("Perfect ", "");
    }

    /**
     * Removes characters from input string based on extended.
     *
     * @param input string
     * @param extended
     *      0 - Removes "Perfect"-rainbow, percentage box, invisible characters and colours.
     *      1 - Removes "Perfect"-rainbow, invisible characters and colours.
     *      2 - Removes invisible characters and colours.
     *      3 - Removes colours.
     * @return input string with removed characters
     */
    public static String stripExtended(String input, int extended) {
        switch (extended) {
            default:
            case 0:
                input = stripPercentage(input);
            case 1:
                input = stripPerfect(input);
            case 2:
                input = stripInvisibleChar(input);
            case 3:
                return TextFormatting.getTextWithoutFormattingCodes(input);
        }
    }

    /**
     * Returns a cut string after x characters
     *
     * @param x
     *        Original String
     * @param amount
     *        The max string char amount
     *
     * @return Original string cut after x characters
     */
    public static String removeAfterChar(String x, int amount) {
        String toReturn = x;
        if(toReturn.length() > amount) {
            toReturn = toReturn.substring(0, toReturn.length() - (toReturn.length() - amount));
            toReturn = toReturn + "...";
        }
        return toReturn;
    }


    private static final String[] directions = new String[]{ "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

    /**
     * Get short direction string for a given yaw
     *
     * @param yaw player's yaw
     * @return Two or one character string
     */
    public static String getPlayerDirection(float yaw) {
        int index = (int) (MathHelper.positiveModulo(yaw + 202.5f, 360.0f) / 45.0f);

        return 0 <= index && index < 8 ? directions[index] : directions[0];
    }

    /**
     * Get the lore NBT tag from an item
     */
    public static NBTTagList getLoreTag(ItemStack item) {
        if (item.isEmpty()) return null;
        NBTTagCompound display = item.getSubCompound("display");
        if (display != null && display.hasKey("Lore")) {
            NBTBase loreBase = display.getTag("Lore");
            NBTTagList lore;
            if (loreBase.getId() == 9 && (lore = (NBTTagList) loreBase).getTagType() == 8) {
                return lore;
            }
        }
        return null;
    }

    /**
     * Get the lore from an item
     *
     * @return an {@link List} containing all item lore
     */
    public static List<String> getLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        if(item.isEmpty()) {
            return lore;
        }
        NBTTagList loreTag = getLoreTag(item);
        if (loreTag != null) {
            for (int i = 0; i < loreTag.tagCount(); ++i) {
                lore.add(loreTag.getStringTagAt(i));
            }
        }
        return lore;
    }

    public static String getStringLore(ItemStack is){
        StringBuilder toReturn = new StringBuilder();
        for (String x : getLore(is)) {
            toReturn.append(x);
        }
        return toReturn.toString();
    }

    public static String arrayWithCommas(ArrayList<String> values) {
        StringBuilder total = new StringBuilder();

        for (String value : values) {
            if(total.toString().equals("")) {
                total = new StringBuilder(value);
                continue;
            }
            total.append(", ").append(value);
        }

        return total.toString().endsWith(", ") ? total.substring(0, total.length() - 2) + "." : total + ".";
    }

    public static String firstCharToUpper(String[] array) {
        StringBuilder result = new StringBuilder();

        result.append(array[0].toLowerCase());

        for(int i = 1; i < array.length; i++) {
            result.append(StringUtils.capitalize(array[i]));
        }

        return result.toString();
    }

    public static String getFieldName(String key) {
        if(getItemFieldName.size() <= 0) {
            getItemFieldName.put("Mana Regen", "manaRegen");
            getItemFieldName.put("Health Regen", "healthRegen");
            getItemFieldName.put("rawHealth Regen", "healthRegenRaw");

            getItemFieldName.put("Life Steal", "lifeSteal");
            getItemFieldName.put("Mana Steal", "manaSteal");
            getItemFieldName.put("XP Bonus", "xpBonus");
            getItemFieldName.put("Loot Bonus", "lootBonus");
            getItemFieldName.put("Stealing", "emeraldStealing");
            getItemFieldName.put("Strength", "strengthPoints");
            getItemFieldName.put("Dexterity", "dexterityPoints");
            getItemFieldName.put("Intelligence", "intelligencePoints");
            getItemFieldName.put("Agility", "agilityPoints");
            getItemFieldName.put("Defence", "defensePoints");
            getItemFieldName.put("Thorns", "thorns");
            getItemFieldName.put("Exploding", "exploding");
            getItemFieldName.put("Walk Speed", "speed");
            getItemFieldName.put("Attack Speed", "attackSpeedBonus");
            getItemFieldName.put("tier Attack Speed", "attackSpeedBonus");
            getItemFieldName.put("Poison", "poison");
            getItemFieldName.put("Health", "healthBonus");
            getItemFieldName.put("Soul Point Regen", "soulPoints");
            getItemFieldName.put("Reflection", "reflection");
            getItemFieldName.put("Spell Damage", "spellDamage");
            getItemFieldName.put("rawSpell Damage", "spellDamageRaw");
            getItemFieldName.put("Melee Damage", "damageBonus");
            getItemFieldName.put("rawMelee Damage", "damageBonusRaw");
            getItemFieldName.put("Main Attack Damage", "damageBonus");  // Name changed on Beta
            getItemFieldName.put("rawMain Attack Damage", "damageBonusRaw");    // Name change on Beta
            getItemFieldName.put("Neutral Spell Damage", "spellDamage");    // Name Change on Beta
            getItemFieldName.put("rawNeutral Spell Damage", "spellDamageRaw");  // Name Change on Beta

            getItemFieldName.put("Fire Damage", "bonusFireDamage");
            getItemFieldName.put("Water Damage", "bonusWaterDamage");
            getItemFieldName.put("Air Damage", "bonusAirDamage");
            getItemFieldName.put("Thunder Damage", "bonusThunderDamage");
            getItemFieldName.put("Earth Damage", "bonusEarthDamage");
            getItemFieldName.put("Fire Defence", "bonusFireDefense");
            getItemFieldName.put("Water Defence", "bonusWaterDefense");
            getItemFieldName.put("Air Defence", "bonusAirDefense");
            getItemFieldName.put("Thunder Defence", "bonusThunderDefense");
            getItemFieldName.put("Earth Defence", "bonusEarthDefense");
        }

        return getItemFieldName.getOrDefault(key, null);
    }

    public static String getFieldName(String key, String suffix) {
        String result = getFieldName(key);

        if (suffix == null) {
            String rawResult = getItemFieldName.getOrDefault("raw" + key, null);
            return (rawResult == null ? result : rawResult);
        }

        return result;
    }

    public static Integer getFieldRank(String key) {
        if (getItemFieldRank.size() <= 0) {
            getItemFieldRank.put("attackSpeedBonus", 101);

            getItemFieldRank.put("damageBonusRaw", 102);
            getItemFieldRank.put("damageBonus", 103);

            getItemFieldRank.put("spellDamageRaw", 104);
            getItemFieldRank.put("spellDamage", 105);

            getItemFieldRank.put("healthBonus", 306);
            getItemFieldRank.put("healthRegenRaw", 307);
            getItemFieldRank.put("healthRegen", 308);
            getItemFieldRank.put("lifeSteal", 309);

            getItemFieldRank.put("manaRegen", 410);
            getItemFieldRank.put("manaSteal", 411);

            getItemFieldRank.put("bonusEarthDamage", 512);
            getItemFieldRank.put("bonusThunderDamage", 513);
            getItemFieldRank.put("bonusWaterDamage", 514);
            getItemFieldRank.put("bonusFireDamage", 515);
            getItemFieldRank.put("bonusAirDamage", 516);

            getItemFieldRank.put("bonusEarthDefense", 617);
            getItemFieldRank.put("bonusThunderDefense", 618);
            getItemFieldRank.put("bonusWaterDefense", 619);
            getItemFieldRank.put("bonusFireDefense", 620);
            getItemFieldRank.put("bonusAirDefense", 621);

            getItemFieldRank.put("strengthPoints", 722);
            getItemFieldRank.put("dexterityPoints", 723);
            getItemFieldRank.put("intelligencePoints", 724);
            getItemFieldRank.put("defensePoints", 725);
            getItemFieldRank.put("agilityPoints", 726);

            getItemFieldRank.put("exploding", 827);
            getItemFieldRank.put("poison", 828);
            getItemFieldRank.put("thorns", 829);
            getItemFieldRank.put("reflection", 830);

            getItemFieldRank.put("speed", 831);
            getItemFieldRank.put("sprint", 832);       // Not properly implimented yet
            getItemFieldRank.put("sprintRegen", 833);  // Not properly implimented yet
            getItemFieldRank.put("jump", 834);         // Not properly implimented yet

            getItemFieldRank.put("soulPoints", 1035);
            getItemFieldRank.put("emeraldStealing", 1036);
            getItemFieldRank.put("lootBonus", 1037);
            getItemFieldRank.put("lootQuality", 1038);   // Not properly implimented yet

            getItemFieldRank.put("xpBonus", 1039);
            getItemFieldRank.put("gatherXp", 1040);      // Not properly implimented yet
            getItemFieldRank.put("gatherSpeed", 1041);   // Not properly implimented yet

            getItemFieldRank.put("firstSpellCost", 1242); // Not properly implimented yet
            getItemFieldRank.put("secondSpellCost", 1243); // Not properly implimented yet
            getItemFieldRank.put("thirdSpellCost", 1244); // Not properly implimented yet
            getItemFieldRank.put("forthSpellCost", 1245); // Not properly implimented yet
        }

        return getItemFieldRank.getOrDefault(key, 10000);
    }

    //ported from a really really fucking old C# code because im lazy, dont judge -SHCM
    public static String getCutString(String inputIN, String startIN, String endIN, boolean keepStartAndEndIN)
    {
        StringBuilder returning = new StringBuilder();
        StringBuilder read = new StringBuilder();
        boolean collecting = false;

        for(char chr : inputIN.toCharArray())
        if (collecting)
        {
            returning.append(chr);
            if (returning.toString().endsWith(endIN))
            {
                return (keepStartAndEndIN ? (startIN + returning) : returning.toString().replace(endIN, ""));
            }
        }
        else
        {
            read.append(chr);
            if (read.toString().endsWith(startIN))
                collecting = true;
        }
        return "";
    }

    /**
     Copy a file from a location to another

     @param sourceFile The source file
     @param destFile Where it will be
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (destFile == null || !destFile.exists()) {
            destFile = new File(sourceFile.getParent() + "/mods/Wynntils.jar");
            sourceFile.renameTo(destFile);
            return;
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyInstance(Object original, Object target) throws Exception {
        if(original.getClass() != target.getClass()) {
            return;
        }
        for(Field f : original.getClass().getDeclaredFields()) {
            if(!f.isAccessible()) f.setAccessible(true);

            f.set(target, f.get(original));
        }
    }

    public static String toMD5(String msg) {
        return new MD5Verification(msg.getBytes(StandardCharsets.UTF_8)).getMd5();
    }

    public static float easeOut(float current, float goal, float jump, float speed) {
        if (Math.floor(Math.abs(goal - current) / jump) > 0) {
            return current + (goal - current) / speed;
        } else {
            return goal;
        }
    }

    public static String[] wrapText(String s, int max) {
        String[] stringArray = s.split(" ");
        StringBuilder result = new StringBuilder();
        int length = 0;

        for (String string: stringArray) {
            if (length + string.length() >= max) {
                result.append('|');
                length = 0;
            }
            result.append(string).append(' ');
            length += string.length() + 1; //+1 for the space following
        }

        return result.toString().split("\\|");
    }

    public static String[] wrapTextBySize(String s, int maxPixels) {
        SmartFontRenderer renderer = ScreenRenderer.fontRenderer;
        int spaceSize = renderer.getStringWidth(" ");

        String[] stringArray = s.split(" ");
        StringBuilder result = new StringBuilder();
        int length = 0;

        for (String string : stringArray) {
            if (length + renderer.getStringWidth(string) >= maxPixels) {
                result.append('|');
                length = 0;
            }
            result.append(string).append(' ');
            length += renderer.getStringWidth(string) + spaceSize;
        }

        return result.toString().split("\\|");
    }

    public static String getPlayerHPBar(EntityPlayer entityPlayer) {
        int health = (int) (0.3f + (entityPlayer.getHealth() / entityPlayer.getMaxHealth()) * 15 ); //0.3f for better experience rounding off near full hp
        String healthBar = TextFormatting.DARK_RED + "[" + TextFormatting.RED + "|||||||||||||||" + TextFormatting.DARK_RED + "]";
        healthBar = healthBar.substring(0, 5 + Math.min(health, 15)) + TextFormatting.DARK_GRAY + healthBar.substring(5 + Math.min(health, 15));
        if (health < 8) { healthBar = healthBar.replace(TextFormatting.RED.toString(), TextFormatting.GOLD.toString()); }
        return healthBar;
    }

    /**
     * Creates a Fake scoreboard
     *
     * @param name Scoreboard Name
     * @param rule Collision Rule
     * @return the Scoreboard Team
     */
    public static ScorePlayerTeam createFakeScoreboard(String name, Team.CollisionRule rule) {
        Scoreboard mc = Minecraft.getMinecraft().world.getScoreboard();
        if(mc.getTeam(name) != null) return mc.getTeam(name);

        ScorePlayerTeam team = mc.createTeam(name);
        team.setCollisionRule(rule);

        mc.addPlayerToTeam(Minecraft.getMinecraft().player.getName(), name);
        return team;
    }

    /**
     * Deletes a fake scoreboard from existence
     *
     * @param name the scoreboard name
     */
    public static void removeFakeScoreboard(String name) {
        Scoreboard mc = Minecraft.getMinecraft().world.getScoreboard();
        if(mc.getTeam(name) == null) return;

        mc.removeTeam(mc.getTeam(name));
    }

    /**
     * Search for a Wynncraft World.
     * only works if the user is on lobby!
     *
     * @param worldNumber The world to join
     */
    public static void joinWorld(int worldNumber) {
        if(!Reference.onServer || Reference.onWorld) return;

        FakeInventory serverSelector = new FakeInventory(WYYNCRAFT_SERVERS_WINDOW_TITLE_PATTERN, 0);
        serverSelector.onReceiveItems(c -> {
            Pair<Integer, ItemStack> world = c.findItem("World " + worldNumber, FilterType.EQUALS_IGNORE_CASE);
            if (world != null) {
                c.clickItem(world.a, 1, ClickType.PICKUP);
                c.close();
                return;
            }

            Pair<Integer, ItemStack> nextPage = c.findItem("Next Page", FilterType.CONTAINS);
            if (nextPage != null) serverSelector.clickItem(nextPage.a, 1, ClickType.PICKUP);
            else c.close();
        });

        serverSelector.open();
    }

    private static HashMap<String, CustomColor> registeredColors = new HashMap<>();

    /**
     * Generates a Color based in the input string
     * The color will be always the same if the string is the same
     *
     * @param input the input stream
     * @return the color
     */
    public static CustomColor colorFromString(String input) {
        if(registeredColors.containsKey(input)) return registeredColors.get(input);

        CRC32 crc32 = new CRC32();
        crc32.update(input.getBytes());

        String hex = "#" + Integer.toHexString((int)crc32.getValue()).substring(0, 6);

        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);

        CustomColor color = new CustomColor(r/255f, g/255f, b/255f);
        registeredColors.put(input, color);

        return color;
    }

    public static CustomColor colorFromHex(String hex) {
        if(registeredColors.containsKey(hex)) return registeredColors.get(hex);

        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);

        CustomColor color = new CustomColor(r/255f, g/255f, b/255f);
        registeredColors.put(hex, color);

        return color;
    }

    public static String millisToString(long duration) {
        long millis = duration % 1000,
             second = (duration / 1000) % 60,
             minute = (duration / (1000 * 60)) % 60,
             hour = (duration / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
    }

    /**
     * Opens a guiScreen without cleaning the users keys/mouse movents
     *
     * @param screen the provided screen
     */
    public static void displayGuiScreen(GuiScreen screen) {
        Minecraft mc = Minecraft.getMinecraft();

        GuiScreen oldScreen = mc.currentScreen;

        GuiOpenEvent event = new GuiOpenEvent(screen);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        screen = event.getGui();

        if (oldScreen == screen) return;
        if (oldScreen != null) {
            oldScreen.onGuiClosed();
        }

        mc.currentScreen = screen;

        if (screen != null) {
            Minecraft.getMinecraft().setIngameNotInFocus();

            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            screen.setWorldAndResolution(mc, i, j);
            mc.skipRenderWorld = false;
        } else {
            mc.getSoundHandler().resumeSounds();
            mc.setIngameFocus();
        }
    }

    private static int doubleClickTime = -1;

    /**
     * @return Maximum milliseconds between clicks to count as a double click
     */
    public static int getDoubleClickTime() {
        if (doubleClickTime < 0) {
            Object prop = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
            if (prop instanceof Integer) {
                doubleClickTime = (Integer) prop;
            }
            if (doubleClickTime < 0) {
                doubleClickTime = 500;
            }
        }
        return doubleClickTime;
    }

    /**
     * Write a String, `s`, to the clipboard. Clears if `s` is null.
     */
    public static void copyToClipboard(String s) {
        if (s == null) {
            clearClipboard();
        } else {
            copyToClipboard(new StringSelection(s));
        }
    }

    public static void copyToClipboard(StringSelection s) {
        if (s == null) {
            clearClipboard();
        } else {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
        }
    }

    public static void clearClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
            public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[0]; }
            public boolean isDataFlavorSupported(DataFlavor flavor) { return false; }
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException { throw new UnsupportedFlavorException(flavor); }
        }, null);
    }

    /**
     * @return A String read from the clipboard, or null if the clipboard does not contain a string
     */
    public static String pasteFromClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return null;
        }
    }

    /**
     * @return `s.getBytes("UTF-8").length`, but without encoding the string
     */
    public static int utf8Length(String s) {
        if (s == null) return 0;
        return s.codePoints().map(c -> c < 0x80 ? 1 : c < 0x800 ? 2 : c < 0x10000 ? 3 : 4).sum();
    }

    /**
     * @return `true` if `c` is a valid Unicode code point (in [0, 0x10FFFF] and not a surrogate)
     */
    public static boolean isValidCodePoint(int c) {
                                           /* low surrogates */             /* high surrogates */
        return 0 <= c && c <= 0x10FFFF && !(0xD800 <= c && c <= 0xDBFF) && !(0xDC00 <= c && c <= 0xDFFF);
    }

    private static final Pattern numberRegex = Pattern.compile("0|-?[1-9][0-9]*");

    /**
     * @return `true` if `s` is an integer and can fit in an `int`
     */
    public static boolean isValidInteger(String s) {
        if (s == null || s.length() > 11 || !numberRegex.matcher(s).matches()) return false;
        if (s.length() < 10) return true;
        long parsed = Long.parseLong(s);
        return (int) parsed == parsed;
    }

    /**
     * @return `true` if `s` is an integer and can fit in a `long`
     */
    public static boolean isValidLong(String s) {
        if (s == null || s.length() > 20 || !numberRegex.matcher(s).matches()) return false;
        if (s.length() < 19) return true;
        try {
            Long.parseLong(s);  // Could overflow
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }

    public static void tab(GuiTextField... tabList) {
        tab(Arrays.asList(tabList));
    }

    /**
     * Given a list of text fields, blur the currently focused field and focus the
     * next one. Focuses the first one if there is no focused field or the last field is focused.
     */
    public static void tab(List<GuiTextField> tabList) {
        int focusIndex = -1;
        for (int i = 0; i < tabList.size(); ++i) {
            GuiTextField field = tabList.get(i);
            if (field.isFocused()) {
                focusIndex = i;
                field.setCursorPosition(0);
                field.setSelectionPos(0);
                field.setFocused(false);
                break;
            }
        }
        focusIndex = (focusIndex + 1) % tabList.size();
        GuiTextField selected = tabList.get(focusIndex);
        selected.setFocused(true);
        selected.setCursorPosition(0);
        selected.setSelectionPos(selected.getText().length());
    }

    private static final Item EMERALD_BLOCK = Item.getItemFromBlock(Blocks.EMERALD_BLOCK);

    /**
     * @return the total amount of emeralds in an inventory, including blocks and le
     */
    public static int countMoney(IInventory inv) {
        if (inv == null) return 0;

        int money = 0;

        for (int i = 0, len = inv.getSizeInventory(); i < len; i++) {
            ItemStack it = inv.getStackInSlot(i);
            if (it.isEmpty()) continue;

            if (it.getItem() == Items.EMERALD) {
                money += it.getCount();
            } else if (it.getItem() == EMERALD_BLOCK) {
                money += it.getCount() * 64;
            } else if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                money += it.getCount() * (64 * 64);
            }
        }

        return money;
    }

}
