/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.events;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.questbook.QuestBookModule;
import cf.wynntils.modules.questbook.configs.QuestBookConfig;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ClientEvents implements Listener {

    @EventHandler(priority = Priority.HIGHEST)
    public void onChat(ClientChatReceivedEvent e)  {
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:")) {
            QuestManager.requestQuestBookReading();
            return;
        }
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]")) {
            QuestManager.requestQuestBookReading();
            return;
        }
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("                            [Quest Completed]")) {
            QuestManager.requestQuestBookReading();
        }
    }

    @EventHandler(priority = Priority.HIGHEST)
    public void onClickOnQuestBook(PlayerInteractEvent.RightClickItem e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook) {
                e.setCanceled(true);
                QuestBookModule.gui.open();
            }
        }
    }

}
