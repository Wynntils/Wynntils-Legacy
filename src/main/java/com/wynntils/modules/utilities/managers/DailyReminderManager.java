/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;

public class DailyReminderManager {

    public static void checkDailyReminder(EntityPlayer p) {
        if (!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        if (System.currentTimeMillis() > UtilitiesConfig.Data.INSTANCE.dailyReminder) {
            TextComponentString text = new TextComponentString("");
            text.getStyle().setColor(TextFormatting.GRAY);

            TextComponentString openingBracket = new TextComponentString("[");
            openingBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            text.appendSibling(openingBracket);

            text.appendText("!");

            TextComponentString closingBracket = new TextComponentString("] ");
            closingBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            text.appendSibling(closingBracket);

            TextComponentString dailyRewards = new TextComponentString("Daily rewards ");
            dailyRewards.getStyle().setColor(TextFormatting.WHITE);
            text.appendSibling(dailyRewards);

            text.appendText("are available to claim!");

            p.sendMessage(text);
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));

            UtilitiesConfig.Data.INSTANCE.dailyReminder = System.currentTimeMillis() + 1800000;
            UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
        }
    }

    public static void openedDaily() {
        if (!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        long now = System.currentTimeMillis();
        UtilitiesConfig.Data.INSTANCE.lastOpenedDailyReward = now;
        UtilitiesConfig.Data.INSTANCE.dailyReminder = now + 86400000;
        UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    public static void openedDailyInventory(GuiScreenEvent.InitGuiEvent.Post e) {
        if (!UtilitiesConfig.INSTANCE.dailyReminder || !Reference.onWorld) return;

        if (Utils.isCharacterInfoPage(e.getGui())) {
            if (!((GuiContainer) e.getGui()).inventorySlots.getSlot(22).getHasStack()) {
                UtilitiesConfig.Data.INSTANCE.dailyReminder = System.currentTimeMillis() + 86400000;
                UtilitiesConfig.Data.INSTANCE.saveSettings(UtilitiesModule.getModule());
            }
        }
    }

}
