/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;

public class DailyReminderManager {

    public static void checkDailyReminder(EntityPlayer p) {
        if(!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        if(System.currentTimeMillis() > UtilitiesConfig.Data.INSTANCE.dailyReminder) {
            p.sendMessage(new TextComponentString("§8[§7!§8] §fDaily rewards §7are available to claim!"));
            ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));

            UtilitiesConfig.Data.INSTANCE.dailyReminder = System.currentTimeMillis() + 1800000;
            UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
        }
    }

    public static void openedDaily() {
        if(!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        UtilitiesConfig.Data.INSTANCE.dailyReminder = System.currentTimeMillis() + 86400000;
        UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    public static void openedDailyInventory(GuiScreenEvent.InitGuiEvent.Post e) {
        if(!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        if(e.getGui() instanceof GuiContainer && ((GuiContainer)e.getGui()).inventorySlots.getSlot(0).inventory.getName().contains("skill points remaining")) {
            if(!((GuiContainer) e.getGui()).inventorySlots.getSlot(22).getHasStack()) {
                UtilitiesConfig.Data.INSTANCE.dailyReminder = System.currentTimeMillis() + 86400000;
                UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
            }
        }
    }

}
