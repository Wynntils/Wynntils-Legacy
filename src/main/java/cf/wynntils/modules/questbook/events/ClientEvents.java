/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.events;

import cf.wynntils.core.events.custom.PreChatEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.questbook.QuestBookModule;
import cf.wynntils.modules.questbook.configs.QuestBookConfig;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(PreChatEvent e)  {
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:"))
            QuestManager.requestQuestBookReading();
        else if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]"))
            QuestManager.requestQuestBookReading();
        else if(e.getMessage().getFormattedText().contains("§6[Quest Completed]"))
            QuestManager.requestQuestBookReading();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookItem(PlayerInteractEvent.RightClickItem e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookBlock(PlayerInteractEvent.RightClickBlock e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBookEntity(PlayerInteractEvent.EntityInteract e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

}
