/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.utilities.managers.HealthPotionManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryData extends PlayerData {

    private static final Pattern UNPROCESSED_NAME_REGEX = Pattern.compile("^§fUnprocessed [a-zA-Z ]+§8 \\[(?:0|[1-9][0-9]*)/([1-9][0-9]*)]$");
    private static final Pattern UNPROCESSED_LORE_REGEX = Pattern.compile("^§7Unprocessed Material \\[Weight: ([1-9][0-9]*)]$");

    private static final Pattern HEALTH_POTION_REGEX = Pattern.compile("(?:\\[\\+\\d+ ❤] )?Potions? of Healing \\[(\\d+)/(\\d+)]");
    private static final Pattern INGREDIENT_SPLIT_PATTERN = Pattern.compile("§f(\\d+) x (.+)");

    public InventoryData() { }

    /**
     * @return The number of free slots in the user's inventory
     *
     * -1 if unable to determine
     */
    public int getFreeInventorySlots() {
        EntityPlayerSP player = getPlayer();
        ClassType currentClass = get(CharacterData.class).getCurrentClass();

        if (currentClass == ClassType.NONE || player == null) return -1;
        return (int) player.inventory.mainInventory.stream().filter(ItemStack::isEmpty).count();
    }

    /**
     * @return The amount of items inside the players ingredient pouch (parsed from the items lore)
     * If countSlotsOnly is true, it only counts the number of used slots
     *
     * -1 if unable to determine
     */
    public int getIngredientPouchCount(boolean countSlotsOnly) {
        EntityPlayerSP player = getPlayer();
        ClassType currentClass = get(CharacterData.class).getCurrentClass();

        if (currentClass == ClassType.NONE || player == null) return -1;
        ItemStack pouch = player.inventory.mainInventory.get(13);
        NBTTagCompound nbt = pouch.getTagCompound();
        int count = 0;

        List<String> lore = ItemUtils.getLore(pouch);

        if (nbt != null && nbt.hasKey("originalItems")) {
            int[] slots = nbt.getIntArray("originalItems");
            for (int slot : slots) {
                if (slot == 0)
                    break;

                if (countSlotsOnly)
                    count += 1;
                else
                    count += slot;
            }
        }
        else {
            boolean foundFirstItem = false;
            for (String line : lore) {
                if (line == null)
                    continue;

                Matcher matcher = INGREDIENT_SPLIT_PATTERN.matcher(line);

                //Account for ironman
                if (!matcher.matches() && foundFirstItem)
                    break;
                else if (!matcher.matches())
                    continue;

                foundFirstItem = true;

                int itemCount = Integer.parseInt(matcher.group(1));

                if (countSlotsOnly)
                    count += 1;
                else
                    count += itemCount;
            }
        }
        return count;
    }

    /**
     * @return Total number of health potions in inventory
     */
    public String getHealthPotionCharges() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return "0/0";

        NonNullList<ItemStack> contents = player.inventory.mainInventory;

        for (ItemStack item : contents) {
            if (HealthPotionManager.isHealthPotion(item)) {
                Matcher nameMatcher = HEALTH_POTION_REGEX.matcher(TextFormatting.getTextWithoutFormattingCodes(item.getDisplayName()));
                if (!nameMatcher.matches()) continue;

                return nameMatcher.group(1) + "/" + nameMatcher.group(2);
            }
        }

        return "0/0";
    }

    /**
     * @return Total number of mana potions in inventory
     */
    public int getManaPotions() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return 0;

        NonNullList<ItemStack> contents = player.inventory.mainInventory;

        int count = 0;

        for (ItemStack item : contents) {
            if (!item.isEmpty() && item.hasDisplayName() && item.getDisplayName().contains("Potion of Mana")) {
                count++;
            }
        }

        return count;
    }

    /**
     * @return Total number of emeralds in inventory (Including blocks and LE)
     */
    public int getMoney() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return 0;

        return ItemUtils.countMoney(player.inventory);
    }

    /**
     * @return The maximum number of soul points the current player can have
     *
     * Note: If veteran, this should always be 15, but currently might return the wrong value
     */
    public int getMaxSoulPoints() {
        int maxIfNotVeteran = 10 + MathHelper.clamp(get(CharacterData.class).getLevel() / 15, 0, 5);
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
        EntityPlayerSP player = getPlayer();
        ClassType currentClass = get(CharacterData.class).getCurrentClass();
        if (currentClass == ClassType.NONE || player == null) return -1;

        ItemStack soulPoints = player.inventory.mainInventory.get(8);
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
        EntityPlayerSP player = getPlayer();
        ClassType currentClass = get(CharacterData.class).getCurrentClass();

        if (currentClass == ClassType.NONE || player.world == null) return -1;
        int ticks = ((int) (player.world.getWorldTime() % 24000) + 24000) % 24000;

        return ((24000 - ticks) % 24000);
    }
}
