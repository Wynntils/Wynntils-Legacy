package cf.wynntils.core.utils;

import com.wynndevs.ConfigValues;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.util.regex.Pattern;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class Utils {

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00A7') + "[0-9A-FK-OR]");

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
        return ConfigValues.wynnRichPresence.discordConfig.showNicknameAndClass ? mc.player.getName() + " | Level " + mc.player.experienceLevel + " " + getPlayerCurrentClass() : null;
    }

    /**
     * Just a simple way to get the current player class
     * @return Player Current Class
     */
    public static String getPlayerCurrentClass(){
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.experienceLevel > 0) {
            try {
                ItemStack book = mc.player.inventory.getStackInSlot(7);
                if (book.hasDisplayName() && book.getDisplayName().contains("Quest Book")) {
                    for (int i=0;i<36;i++) {
                        try {
                            ItemStack ItemTest = mc.player.inventory.getStackInSlot(i);
                            NBTTagList Lore = ItemTest.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                            for (int j = 1; j < Lore.tagCount(); j++) {
                                String ClassTest = Lore.get(j).toString();
                                if (ClassTest.contains("Class Req:") && ClassTest.charAt(2) == 'a'){
                                    return ClassTest.substring(18,ClassTest.lastIndexOf('/'));
                                }
                            }
                        }
                        catch (Exception ignored){
                        }
                    }
                }
            }
            catch (Exception ignored) {
                return "";
            }
        }
        return "";
    }


}
