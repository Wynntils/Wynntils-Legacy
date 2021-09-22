/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils;

import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes;

public class ItemUtils {

    private static final Pattern LEVEL_PATTERN = Pattern.compile("(?:Combat|Crafting|Mining|Woodcutting|Farming|Fishing) Lv\\. Min: ([0-9]+)");
    private static final Pattern LEVEL_RANGE_PATTERN = Pattern.compile("Lv\\. Range: " + TextFormatting.WHITE.toString() + "([0-9]+)-([0-9]+)");

    public final static Pattern ID_PATTERN = Pattern.compile("(^\\+?(?<Value>-?\\d+)(?: to \\+?(?<UpperValue>-?\\d+))?(?<Suffix>%|/\\ds| tier)?(?<Stars>\\*{0,3}) (?<ID>[a-zA-Z 0-9]+))");

    public final static Pattern ITEM_QUALITY = Pattern.compile("(?<Quality>Normal|Unique|Rare|Legendary|Fabled|Mythic|Set) Item(?: \\[(?<Rolls>\\d+)])?(?: \\[[0-9,]+" + EmeraldSymbols.E + "])?");
    public final static Pattern MARKET_PRICE = Pattern.compile(" - (?<Quantity>\\d x )?(?<Value>(?:,?\\d{1,3})+)" + EmeraldSymbols.E);

    public static final NBTTagCompound UNBREAKABLE = new NBTTagCompound();

    /**
     * Get the lore NBT tag from an item
     */
    public static NBTTagList getLoreTag(ItemStack item) {
        if (item.isEmpty()) return null;
        NBTTagCompound display = item.getSubCompound("display");
        if (display == null || !display.hasKey("Lore")) return null;

        NBTBase loreBase = display.getTag("Lore");
        NBTTagList lore;
        if (loreBase.getId() != 9) return null;

        lore = (NBTTagList) loreBase;
        if (lore.getTagType() != 8) return null;

        return lore;
    }

    /**
     * Get the lore from an item
     *
     * @return an {@link List} containing all item lore
     */
    public static List<String> getLore(ItemStack item) {
        NBTTagList loreTag = getLoreTag(item);

        List<String> lore = new ArrayList<>();
        if (loreTag == null) return lore;

        for (int i = 0; i < loreTag.tagCount(); ++i) {
            lore.add(loreTag.getStringTagAt(i));
        }

        return lore;
    }

    /**
     * Replace the lore on an item's NBT tag.
     *
     * @param stack
     * @param lore
     */
    public static void replaceLore(ItemStack stack, List<String> lore) {
        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();
        lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
        display.setTag("Lore", tag);
        nbt.setTag("display", display);
        stack.setTagCompound(nbt);
    }

    /**
     * Same as {@link #getLore(ItemStack)}, but after calling
     * {@link TextFormatting#getTextWithoutFormattingCodes(String) getTextWithoutFormattingCodes} on each lore line
     *
     * @return A List containing all item lore without formatting codes
     */
    public static List<String> getUnformattedLore(ItemStack item) {
        NBTTagList loreTag = getLoreTag(item);

        List<String> lore = new ArrayList<>();
        if (loreTag == null) return lore;

        for (int i = 0; i < loreTag.tagCount(); ++i) {
            lore.add(TextFormatting.getTextWithoutFormattingCodes(loreTag.getStringTagAt(i)));
        }

        return lore;
    }

    public static String getStringLore(ItemStack is) {
        StringBuilder toReturn = new StringBuilder();
        for (String x : getLore(is)) {
            toReturn.append(x);
        }
        return toReturn.toString();
    }

    /**
     * Determines the equipment type of the given item.
     *
     * @param item
     * @return The ItemType of the item, or null if invalid or not an equipment piece
     */
    public static ItemType getItemType(ItemStack item) {
        for (Entry<ItemType, String[]> e : WebManager.getMaterialTypes().entrySet()) {
            for (String id : e.getValue()) {
                if (id.matches("[A-Za-z_:]+")) {
                    if (Item.getByNameOrId(id).equals(item.getItem())) return e.getKey();
                } else {
                    int damageValue = 0;

                    String[] values = id.split(":");
                    int i = Integer.parseInt(values[0]);
                    if (values.length == 2) {
                        damageValue = Integer.parseInt(values[1]);
                    }

                    if (Item.getIdFromItem(item.getItem()) == i && item.getItemDamage() == damageValue) return e.getKey();
                }
            }
        }
        return null;
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

            if (it.getItem() == Items.EMERALD && it.getDisplayName().equals(TextFormatting.GREEN + "Emerald")) {
                money += it.getCount();
            } else if (it.getItem() == EMERALD_BLOCK && it.getDisplayName().equals(TextFormatting.GREEN + "Emerald Block")) {
                money += it.getCount() * 64;
            } else if (it.getItem() == Items.EXPERIENCE_BOTTLE && it.getDisplayName().equals(TextFormatting.GREEN + "Liquid Emerald")) {
                money += it.getCount() * (64 * 64);
            }
        }

        return money;
    }

    public static String describeMoney(int total) {
        int leCount = total / 4096;
        int leRest = total % 4096;
        int emCount = leRest % 64;
        int ebCount = leRest / 64;

        StringBuilder desc = new StringBuilder();
        if (leCount > 0) {
            desc.append(leCount);
            desc.append(" ");
            desc.append(EmeraldSymbols.LE);
            desc.append(" ");
        }
        if (ebCount > 0) {
            desc.append(ebCount);
            desc.append(" ");
            desc.append(EmeraldSymbols.BLOCKS);
            desc.append(" ");
        }
        if (emCount > 0) {
            desc.append(emCount);
            desc.append(" ");
            desc.append(EmeraldSymbols.EMERALDS);
            desc.append(" ");
        }
        return desc.toString();
    }

    public static IntRange getLevel(String lore) {
        Matcher m = LEVEL_PATTERN.matcher(lore);
        if (m.find()) return new IntRange(Integer.parseInt(m.group(1)));
        m = LEVEL_RANGE_PATTERN.matcher(lore);
        if (m.find()) return new IntRange(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
        return null;
    }

    /**
     * Returns if an item is identified
     */
    public static boolean isUnidentified(ItemStack stack) {
        return stack.getItem() == Items.STONE_SHOVEL && stack.getItemDamage() >= 1 && stack.getItemDamage() <= 6;
    }

    /**
     *  Gets the short id name from the display name
     */
    public static String toShortIdName(String longIdName, boolean raw) {
        String[] splitName = longIdName.split(" ");
        StringBuilder result = new StringBuilder(raw ? "raw" : "");
        for (String r : splitName) {
            if (r.startsWith("[")) continue;  // ignore ids
            result.append(Character.toUpperCase(r.charAt(0))).append(r.substring(1).toLowerCase(Locale.ROOT));
        }

        if (result.length() == 0) return "";
        if (!raw) result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
        return result.toString();
    }

    /**
     * Gets results from a box as a string
     */
    public static String getItemsFromBox(ItemStack stack) {
        String name = stack.getDisplayName();
        String itemType = getTextWithoutFormattingCodes(name).split(" ", 3)[1];
        String levelRange = null;

        List<String> lore = ItemUtils.getLore(stack);

        for (String aLore : lore) {
            if (aLore.contains("Lv. Range")) {
                levelRange = getTextWithoutFormattingCodes(aLore).replace("- Lv. Range: ", "");
                break;
            }
        }

        if (itemType == null || levelRange == null) return null;

        ItemGuessProfile igp = WebManager.getItemGuesses().get(levelRange);
        if (igp == null) return null;

        Map<String, String> rarityMap = igp.getItems().get(itemType);
        if (rarityMap == null) return null;

        ItemTier tier = ItemTier.fromTextColoredString(name);
        return rarityMap.get(tier.asCapitalizedName());
    }

    static {
        UNBREAKABLE.setBoolean("Unbreakable", true);
    }

}
