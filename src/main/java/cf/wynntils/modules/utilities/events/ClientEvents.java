package cf.wynntils.modules.utilities.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.utils.LimitedList;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.utilities.managers.ChatManager;
import cf.wynntils.modules.utilities.managers.DailyReminderManager;
import cf.wynntils.modules.utilities.managers.GlowManager;
import cf.wynntils.modules.utilities.managers.TPSManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ClientEvents implements Listener {

    private static LimitedList<Integer> ids = new LimitedList<>(200);

    @EventHandler
    public void clientTick(TickEvent.ClientTickEvent e) {
        if(Reference.onWorld) {
            TPSManager.updateTPS();
            DailyReminderManager.checkDailyReminder(ModCore.mc().player);
        }

        if(ids.size() > 0 && ModCore.mc().player.world != null) {
            for(int i = 0; i < ids.size(); i++) {
                Entity ent = ModCore.mc().player.world.getEntityByID(ids.get(i));
                if(ent != null) {
                    GlowManager.verifyEntity(ent);
                }
            }
        }
    }

    @EventHandler(priority = Priority.HIGH)
    public void chatHandler(ClientChatReceivedEvent e) {
        if(e.isCanceled() || e.getType() != 1) {
            return;
        }
        if(e.getMessage().getUnformattedText().startsWith("[Daily Rewards:")) {
            DailyReminderManager.openedDaily();
        }
        if(Reference.onWorld) {
            Pair<String, Boolean> message = ChatManager.applyUpdates(e.getMessage().getFormattedText());
            e.setMessage(new TextComponentString(message.a));
            if(message.b) {
                e.setCanceled(true);
            }
        }
    }

    @EventHandler
    public void inventoryOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        DailyReminderManager.openedDailyInventory(e);
    }

    @EventHandler
    public void entitySpawned(EntityJoinWorldEvent e) {
        if(e.getEntity() instanceof EntityItem || e.getEntity() instanceof EntityPlayer) {
            long now = System.nanoTime();
            ids.add(e.getEntity().getEntityId());
        }
    }

}
