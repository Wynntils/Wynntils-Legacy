/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class HorseData extends PlayerData {

    private int xp;
    private int level;
    private int tier;
    private int maxLevel;

    private String armour;
    private int inventorySlot = -1;

    public HorseData() { }

    @Override
    public void onRequest() {
        // Updates the horse data whenever this data is requested
        update();
    }

    /**
     * Updates the player horse information
     *
     * @param saddle the horse saddle item
     * @param inventorySlot which inventory slot the saddle is
     */
    public void update(ItemStack saddle, int inventorySlot) {
        this.inventorySlot = inventorySlot;

        List<String> lore = ItemUtils.getLore(saddle);

        tier = Integer.parseInt(lore.get(0).substring(7));
        level = Integer.parseInt(lore.get(1).substring(9, lore.get(1).indexOf("/")));
        maxLevel = Integer.parseInt(lore.get(1).substring(lore.get(1).indexOf("/")+1));
        armour = lore.get(3).substring(11);
        xp = Integer.parseInt(lore.get(4).substring(6, lore.get(4).indexOf("/")));
    }

    /**
     * Updates the player horse information
     */
    public void update() {
        NonNullList<ItemStack> inventory = getPlayer().inventory.mainInventory;

        if (inventorySlot != -1) {
            ItemStack stack = inventory.get(inventorySlot);

            if (!stack.isEmpty() && stack.hasDisplayName() && stack.getDisplayName().contains(" Horse")) {
                update(stack, inventorySlot);
                return;
            }
        }

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);

            if (!stack.isEmpty() && stack.hasDisplayName() && stack.getDisplayName().contains(" Horse")) {
                update(stack, i);
                return;
            }
        }

        // in case the horse is not found set slot to -1 indicating that the player doesn't have a horse
        inventorySlot = -1;
    }

    public boolean hasHorse() {
        return inventorySlot != -1;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getTier() {
        return tier;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getArmour() {
        return armour;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

}
