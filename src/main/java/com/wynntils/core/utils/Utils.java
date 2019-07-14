/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.core.instances.FakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

public class Utils {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]");
    public static HashMap<String, String> getItemFieldName = new HashMap<>();
    public static HashMap<String, Integer> getItemFieldRank = new HashMap<>();
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Wynntils Utilities").build());

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

    public static ScheduledFuture runTaskTimer(Runnable r, TimeUnit timeUnit, long amount) {
        return executorService.scheduleAtFixedRate(r, 0, amount, timeUnit);
    }

    /**
     * Removes all registeredColors codes from a string
     *
     * @param input
     *        Input string
     *
     * @return input string without colored chars
     */
    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    /**
     * Removes the invisible character À
     *
     * @param input
     * @return input string without the invisible character
     */
    public static String stripInvisibleChar(String input) {
        return input.replace("À", "");
    }

    /**
     * Removes the percentage box (e.g. [96%])
     *
     * @param input
     * @return input string without the percentage box
     */
    public static String stripPercentage(String input) {
        return input.replaceAll(" \\[\\d{1,3}%\\]", "");
    }

    /**
     * Removes the "Perfect"-rainbow from input string
     *
     * @param input
     * @return input string without the "Perfect"-rainbow
     */
    public static String stripPerfect(String input) {
        return stripColor(input).replace("Perfect ", "");
    }

    /**
     * Removes characters from input string based on extended.
     *
     * @param input
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
                return stripColor(input);
        }
    }

    /**
     * Returns a cutted string after x characters
     *
     * @param x
     *        Original String
     * @param amount
     *        The max string char amount
     *
     * @return Original string cutted after x characters
     */
    public static String removeAfterChar(String x, int amount) {
        String toReturn = x;
        if(toReturn.length() > amount) {
            toReturn = toReturn.substring(0, toReturn.length() - (toReturn.length() - amount));
            toReturn = toReturn + "...";
        }
        return toReturn;
    }

    /**
     * Gets by text the current player drection
     *
     * @param yaw player's yaw
     * @return
     */
    public static String getPlayerDirection(float yaw){
        double num = (yaw + 202.5) / 45.0;
        while (num < 0.0) {
            num += 360.0;
        }
        int dir = (int) (num);
        dir = dir % 8;

        switch (dir) {
            case 1:
                return "NE";
            case 2:
                return "E";
            case 3:
                return "SE";
            case 4:
                return "S";
            case 5:
                return "SW";
            case 6:
                return "W";
            case 7:
                return "NW";
            default:
                return "N";
        }
    }

    /**
     * Get the lore from an item
     *
     * @param item
     *
     * @return an {@link List} containing all item lore
     */
    public static List<String> getLore(ItemStack item) {
        List<String> lore = new ArrayList<>();
        if(item.isEmpty() || !item.hasTagCompound()) {
            return lore;
        }
        if (item.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = item.getTagCompound().getCompoundTag("display");

            if (nbttagcompound.getTagId("Lore") == 9) {
                NBTTagList nbttaglist3 = nbttagcompound.getTagList("Lore", 8);

                if (!nbttaglist3.isEmpty()) {
                    for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                        lore.add(nbttaglist3.getStringTagAt(l1));
                    }
                }
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

    public static Integer getFieldRank(String key) {
        if (getItemFieldRank.size() <= 0) {
            getItemFieldRank.put("attackSpeedBonus", 1);

            getItemFieldRank.put("damageBonus", 2);
            getItemFieldRank.put("damageBonusRaw", 3);

            getItemFieldRank.put("spellDamage", 4);
            getItemFieldRank.put("spellDamageRaw", 5);

            getItemFieldRank.put("healthBonus", 6);
            getItemFieldRank.put("healthRegen", 7);
            getItemFieldRank.put("healthRegenRaw", 8);

            getItemFieldRank.put("lifeSteal", 9);
            getItemFieldRank.put("manaRegen", 10);
            getItemFieldRank.put("manaSteal", 11);

            getItemFieldRank.put("bonusEarthDamage", 12);
            getItemFieldRank.put("bonusThunderDamage", 13);
            getItemFieldRank.put("bonusWaterDamage", 14);
            getItemFieldRank.put("bonusFireDamage", 15);
            getItemFieldRank.put("bonusAirDamage", 16);

            getItemFieldRank.put("bonusEarthDefense", 17);
            getItemFieldRank.put("bonusThunderDefense", 18);
            getItemFieldRank.put("bonusWaterDefense", 19);
            getItemFieldRank.put("bonusFireDefense", 20);
            getItemFieldRank.put("bonusAirDefense", 21);

            getItemFieldRank.put("strengthPoints", 22);
            getItemFieldRank.put("dexterityPoints", 23);
            getItemFieldRank.put("intelligencePoints", 24);
            getItemFieldRank.put("defensePoints", 25);
            getItemFieldRank.put("agilityPoints", 26);

            getItemFieldRank.put("speed", 27);
            getItemFieldRank.put("exploding", 28);
            getItemFieldRank.put("poison", 29);
            getItemFieldRank.put("thorns", 30);
            getItemFieldRank.put("reflection", 31);

            getItemFieldRank.put("soulPoints", 32);
            getItemFieldRank.put("emeraldStealing", 33);
            getItemFieldRank.put("lootBonus", 34);
            getItemFieldRank.put("xpBonus", 35);
        }

        return getItemFieldRank.getOrDefault(key, 1000);
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

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
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
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(msg.getBytes(), 0, msg.length());
            return new BigInteger(1, m.digest()).toString(16);
        }catch (Exception ex) { }
        return msg;
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
        String result = "";
        int length = 0;

        for (String string: stringArray) {
            if (length + string.length() >= max) {
                result += "|";
                length = 0;
            }
            result += string + " ";
            length += string.length() + 1; //+1 for the space following
        }

        return result.split("\\|");
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
     * @param worldNumber
     */
    public static void joinWorld(int worldNumber) {
        if(!Reference.onServer || Reference.onWorld) return;

        FakeInventory serverSelector = new FakeInventory("Wynncraft Servers", 0);
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

        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);

        CustomColor color = new CustomColor(r/255f, g/255f, b/255f);
        registeredColors.put(input, color);

        return color;
    }

    public static CustomColor colorFromHex(String hex) {
        if(registeredColors.containsKey(hex)) return registeredColors.get(hex);

        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);

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

}
