package cf.wynntils.modules.utilities.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.managers.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;

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

    //HeyZeer0: Handles the inventory lock, 2 methods below, first one on inventory, other one by dropping the item (without inventory)
    @SubscribeEvent
    public void keyPressOnInventory(GuiOverlapEvent.InventoryOverlap.KeyTyped e) {
        if(!Reference.onWorld) return;

        if(e.getKeyCode() == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode()) {
            if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
                if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

                if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(e.getGuiInventory().getSlotUnderMouse().getSlotIndex())) {
                    e.setCanceled(true);
                }
            }
            return;
        }

        if(e.getKeyCode() == KeyManager.getLockInventoryKey().getKeyBinding().getKeyCode()) {
            if(e.getGuiInventory().getSlotUnderMouse() != null && Minecraft.getMinecraft().player.inventory == e.getGuiInventory().getSlotUnderMouse().inventory) {
                if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) {
                    UtilitiesConfig.INSTANCE.locked_slots.put(PlayerInfo.getPlayerInfo().getClassId(), new HashSet<>());
                }

                if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(e.getGuiInventory().getSlotUnderMouse().getSlotIndex())) {
                    UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).remove(e.getGuiInventory().getSlotUnderMouse().getSlotIndex());
                }else{
                    UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).add(e.getGuiInventory().getSlotUnderMouse().getSlotIndex());
                }

                UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
            }
        }
    }

    @SubscribeEvent
    public void keyPress(PacketEvent.PlayerDropItemEvent e) {
        if(Minecraft.getMinecraft().gameSettings.keyBindDrop.isKeyDown()) {
            if(!UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId())) return;

            if(UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(Minecraft.getMinecraft().player.inventory.currentItem)) {
                e.setCanceled(true);
            }
        }
    }

}
