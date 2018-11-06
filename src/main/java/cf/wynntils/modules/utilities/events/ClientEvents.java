package cf.wynntils.modules.utilities.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.InventoryClickEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.utilities.managers.ChatManager;
import cf.wynntils.modules.utilities.managers.DailyReminderManager;
import cf.wynntils.modules.utilities.managers.NametagManager;
import cf.wynntils.modules.utilities.managers.TPSManager;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ClientEvents implements Listener {

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent e) {
        if(Reference.onWorld) {
            TPSManager.updateTPS();
            DailyReminderManager.checkDailyReminder(ModCore.mc().player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatHandler(ClientChatReceivedEvent e) {
        if(e.isCanceled() || e.getType() != 1) {
            return;
        }
        if(e.getMessage().getUnformattedText().startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
        if(Reference.onWorld) {
            boolean message = ChatManager.applyUpdatesToClient(e.getMessage());
            if(message) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void changeNametagColors(RenderLivingEvent.Specials.Pre e) {
        if(NametagManager.checkForNametag(e)) e.setCanceled(true);
    }

    @SubscribeEvent
    public void inventoryOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        DailyReminderManager.openedDailyInventory(e);
    }

    @SubscribeEvent
    public void changeClass(InventoryClickEvent e) {
        if(e.getScreenTitle().contains("Select a Class")) {
            if(e.getUsedButton() == 0 && e.getClickedItem().hasDisplayName() && e.getClickedItem().getDisplayName().contains("[>] Select")) {
                PlayerInfo.getPlayerInfo().setClassId(e.getSlotdId());
            }
        }
    }

}
