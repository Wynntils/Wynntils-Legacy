/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillPointOverlay implements Listener {
	
    private static final String colourPattern = "(?i)§[0-9A-FK-OR]";

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (!e.getGui().getLowerInv().getName().contains("skill points remaining")) return;

        for (int i = 0; i < e.getGui().getLowerInv().getSizeInventory(); i++) {
            ItemStack stack = e.getGui().getLowerInv().getStackInSlot(i);
            if (stack.isEmpty() || !stack.hasDisplayName() || stack.getTagCompound().hasKey("wynntilsAnalyzed")) continue; // display name also checks for tag compound

            stack.getTagCompound().setBoolean("wynntilsAnalyzed", true);
            String lore = ItemUtils.getStringLore(stack);
            String name = stack.getDisplayName();

            String value;
            if (name.contains("Upgrade")) {// Skill Points

                lore = lore.replaceAll(colourPattern, "");
                String[] tokens = lore.split("[0-9]{1,3} points");
                for (String token : tokens) {
                    lore = lore.replace(token, "");
                }
                String[] numbers = lore.split(" ");
                value = numbers[0];
            } else if (name.contains("Profession")) { // Profession Icons
                lore = lore.replaceAll(colourPattern, "");

                String[] tokens = lore.split("Level: [0-9]{1,3}");
                for (String token : tokens) {
                    lore = lore.replace(token, "");
                }
                String[] numbers = lore.split(" ");
                value = numbers[1];
            } else if (name.contains("'s Info")) { // Combat level on Info
                lore = lore.replaceAll(colourPattern, "");

                String[] tokens = lore.split("Combat Lv: [0-9]{1,3}");
                for (String token : tokens) {
                    lore = lore.replace(token, "");
                }
                String[] numbers = lore.split(" ");
                value = numbers[2];
            } else continue;

            try {
                int count = Integer.parseInt(value);
                stack.setCount(count == 0 ? 1 : count);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
