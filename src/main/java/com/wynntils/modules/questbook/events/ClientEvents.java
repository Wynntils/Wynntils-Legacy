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
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
        else if(e.getMessage().getFormattedText().contains(TextFormatting.GOLD + "[Quest Completed]"))
            QuestManager.requestQuestBookReading();
    }

    @SubscribeEvent
    public void startReading(WynnWorldLeftEvent e) {
        QuestManager.clearData();
    }

    boolean cancelNextWindow = false;

    @SubscribeEvent
    public void cancelWindow(PacketEvent.InventoryReceived e) {
        if(!cancelNextWindow) return;

        Minecraft.getMinecraft().getConnection().sendPacket(new CPacketCloseWindow());

        e.setCanceled(true);
        cancelNextWindow = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookItem(PlayerInteractEvent.RightClickItem e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book") && !e.getItemStack().getDisplayName().endsWith("À")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestBookModule.gui.open();

                cancelNextWindow = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookBlock(PlayerInteractEvent.RightClickBlock e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestBookModule.gui.open();

                cancelNextWindow = true;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookEntity(PlayerInteractEvent.EntityInteract e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestBookModule.gui.open();

                cancelNextWindow = true;
            }
        }
    }

    @SubscribeEvent
    public void updateQuestBook(TickEvent.ClientTickEvent e) {
        if(!Reference.onWorld || Reference.onNether || Reference.onWars) return;
        if(Minecraft.getMinecraft().player.inventory.getStackInSlot(7).isEmpty() || Minecraft.getMinecraft().player.inventory.getStackInSlot(7).getItem() != Items.WRITTEN_BOOK) return;

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
