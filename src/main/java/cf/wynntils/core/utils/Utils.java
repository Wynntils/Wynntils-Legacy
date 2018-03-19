package cf.wynntils.core.utils;

import cf.wynntils.core.framework.instances.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Utils {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00A7') + "[0-9A-FK-OR]");
    public static HashMap<String, String> getItemFieldName = new HashMap<>();

    /**
     * Removes all color codes from a string
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
     * Just a simple method to short other ones
     * @return RichPresence largeImageText
     */
    public static String getPlayerInfo() {
        Minecraft mc = Minecraft.getMinecraft();
        return /*ConfigValues.wynnRichPresence.discordConfig.showNicknameAndClass ?*/ mc.player.getName() + " | Level " + mc.player.experienceLevel + " " + PlayerInfo.getPlayerInfo().getCurrentClass().toString() /*: null*/;
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
        if(item == null || !item.hasTagCompound()) {
            return lore;
        }
        if (item.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = item.getTagCompound().getCompoundTag("display");

            if (nbttagcompound.getTagId("Lore") == 9) {
                NBTTagList nbttaglist3 = nbttagcompound.getTagList("Lore", 8);

                if (!nbttaglist3.hasNoTags()) {
                    for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                        lore.add(nbttaglist3.getStringTagAt(l1));
                    }
                }
            }
        }
        return lore;
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
            getItemFieldName.put("Defense", "defensePoints");
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
            getItemFieldName.put("Fire Defense", "bonusFireDefense");
            getItemFieldName.put("Water Defense", "bonusWaterDefense");
            getItemFieldName.put("Air Defense", "bonusAirDefense");
            getItemFieldName.put("Thunder Defense", "bonusThunderDefense");
            getItemFieldName.put("Earth Defense", "bonusEarthDefense");
        }

        return getItemFieldName.getOrDefault(key, null);
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
     Copy the a file from a location to another

     @param sourceFile The source file
     @param destFile Where it will be
     */
    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
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
}
