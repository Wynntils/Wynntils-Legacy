/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillPointOverlay implements Listener {

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (!e.getGui().getLowerInv().getName().contains("skill points remaining")) return;

        for (int i = 0; i < e.getGui().getLowerInv().getSizeInventory(); i++) {
            ItemStack stack = e.getGui().getLowerInv().getStackInSlot(i);
            if (stack.isEmpty() || !stack.hasDisplayName() || stack.getTagCompound().hasKey("wynntilsAnalyzed")) continue; // display name also checks for tag compound

            stack.getTagCompound().setBoolean("wynntilsAnalyzed", true);
            String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
            String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());

            int value = 0;

            if (name.contains("Upgrade")) {// Skill Points
                int start = lore.indexOf(" points ")-3;

                String number = lore.substring(start, start+3).trim();

                value = Integer.parseInt(number);
            } else if (name.contains("Profession")) { // Profession Icons
                int start = lore.indexOf("Level: ")+7;
                int end = lore.indexOf("XP: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ")+11;
                int end = lore.indexOf("Class: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("Damage Info")) { //Average Damage
            	//Ensure lore keys will exist
            	if (lore.contains("[Put an allowed weapon in your hotbar]"))
            		continue;

                int start = lore.indexOf("Total Damage (+Bonus): ")+23;
                int end = lore.indexOf(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER ? "[LRL]" : "[RLR]");

                if (start < 0 || end > lore.length()) continue;
                String[] numbers = lore.substring(start, end).split("-");
                if (numbers[0].isEmpty() || numbers[1].isEmpty()) continue;

                int min = Integer.parseInt(numbers[0]);
                int max = Integer.parseInt(numbers[1]);

                value = Math.round((max+min)/2);
            } else if (name.contains("Daily Rewards")) { //Daily Reward Multiplier
                int start = lore.indexOf("Streak Multiplier: ")+19;
                int end = lore.indexOf("Log in everyday to");

                value = Integer.parseInt(lore.substring(start, end));
            } else continue;

            stack.setCount(value <= 0 ? 1 : value);
        }
    }

}
