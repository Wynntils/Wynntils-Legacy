/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent.Pre e)  {
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:"))
            QuestManager.requestQuestBookReading();
        else if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]"))
            QuestManager.requestQuestBookReading();
        else if(e.getMessage().getFormattedText().contains("§6[Quest Completed]"))
            QuestManager.requestQuestBookReading();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookItem(PlayerInteractEvent.RightClickItem e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book") && !e.getItemStack().getDisplayName().endsWith("À")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookBlock(PlayerInteractEvent.RightClickBlock e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookEntity(PlayerInteractEvent.EntityInteract e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook && !Reference.onWars && !Reference.onNether) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }
}
