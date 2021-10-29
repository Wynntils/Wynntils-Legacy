/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.framework.instances.containers.UnprocessedAmount;
import com.wynntils.core.utils.ItemUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
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
     * @return UnprocessedAmount((total weight of unprocessed materials), (maximum weight that can be held)).
     *
     * If there are no unprocessed materials, maximum will be -1.
     */
    public UnprocessedAmount getUnprocessedAmount() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return new UnprocessedAmount(0, 0);

        int maximum = -1;
        int amount = 0;

        for (int i = 0, len = player.inventory.getSizeInventory(); i < len; i++) {
            ItemStack it = player.inventory.getStackInSlot(i);
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
     * @return Number of health pot charges remaining
     */
    public int getHealthPotionCharges() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return 0;

        NonNullList<ItemStack> contents = player.inventory.mainInventory;

        for (ItemStack item : contents) {
            if (!item.isEmpty() && item.hasDisplayName() && item.getDisplayName().contains("Potion of Healing") || item.getDisplayName().contains("Potions of Healing")) {
                Matcher nameMatcher = HEALTH_POTION_REGEX.matcher(TextFormatting.getTextWithoutFormattingCodes(item.getDisplayName()));
                if (!nameMatcher.matches()) continue;

                return Integer.parseInt(nameMatcher.group(1));
            }
        }

        return 0;
    }

    /**
     * @return Max number of health pot charges
     */
    public int getHealthPotionMaxCharges() {
        EntityPlayerSP player = getPlayer();
        if (player == null) return 0;

        NonNullList<ItemStack> contents = player.inventory.mainInventory;

        for (ItemStack item : contents) {
            if (!item.isEmpty() && item.hasDisplayName() && item.getDisplayName().contains("Potion of Healing") || item.getDisplayName().contains("Potions of Healing")) {
                Matcher nameMatcher = HEALTH_POTION_REGEX.matcher(TextFormatting.getTextWithoutFormattingCodes(item.getDisplayName()));
                if (!nameMatcher.matches()) continue;

                return Integer.parseInt(nameMatcher.group(2));
            }
        }

        return 0;
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
