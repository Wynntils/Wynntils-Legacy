package com.wynntils.core.utils;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

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

    public static String getStringLore(ItemStack is) {
        StringBuilder toReturn = new StringBuilder();
        for (String x : getLore(is)) {
            toReturn.append(x);
        }
        return toReturn.toString();
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
