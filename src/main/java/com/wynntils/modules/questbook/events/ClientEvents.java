/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent.Pre e)  {
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:"))
            QuestManager.requestQuestBookReading();
        else if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]"))
            QuestManager.requestQuestBookReading();
        else if(e.getMessage().getUnformattedText().contains("[Quest Completed]") && e.getMessage().getUnformattedText().indexOf("[") >= 5)
            QuestManager.requestQuestBookReading();
        else if(e.getMessage().getUnformattedText().contains("[Mini-Quest Completed]") && e.getMessage().getUnformattedText().indexOf("[") >= 5)
            QuestManager.requestQuestBookReading();
        else if(e.getMessage().getUnformattedText().contains(TextFormatting.BOLD.toString()) && e.getMessage().getUnformattedText().contains("Level Up!")) {
            QuestManager.requestQuestBookReading();
        }
    }

    @SubscribeEvent
    public void startReading(WynnWorldEvent.Leave e) {
        QuestManager.clearData();
    }

    boolean openQuestBook = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnQuestBook(PacketEvent.PlayerUseItemEvent e) {
        if(!QuestBookConfig.INSTANCE.allowCustomQuestbook
                || !Reference.onWorld || Reference.onNether || Reference.onWars
                || Minecraft.getMinecraft().player.inventory.currentItem != 7) return;

        openQuestBook = true;
        if(!QuestManager.isClicksAllowed()) e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnQuestBook(PacketEvent.PlayerUseItemOnBlockEvent e) {
        if(!QuestBookConfig.INSTANCE.allowCustomQuestbook
                || !Reference.onWorld || Reference.onNether || Reference.onWars
                || Minecraft.getMinecraft().player.inventory.currentItem != 7) return;

        openQuestBook = true;
        if(!QuestManager.isClicksAllowed()) e.setCanceled(true);
    }

    @SubscribeEvent
    public void updateQuestBook(TickEvent.ClientTickEvent e) {
        if(!Reference.onWorld || Reference.onNether || Reference.onWars) return;
        if(Minecraft.getMinecraft().player.inventory.getStackInSlot(7).isEmpty() || Minecraft.getMinecraft().player.inventory.getStackInSlot(7).getItem() != Items.WRITTEN_BOOK) return;

        if(openQuestBook) {
            openQuestBook = false;
            QuestBookModule.gui.open();
        }

        if(QuestManager.getCurrentQuestsData().size() <= 0) QuestManager.requestQuestBookReading();
    }



    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        if(e.getCurrentClass() != ClassType.NONE) {
            QuestManager.setTrackedQuest(null);
            QuestManager.clearData();
        }
    }

}
