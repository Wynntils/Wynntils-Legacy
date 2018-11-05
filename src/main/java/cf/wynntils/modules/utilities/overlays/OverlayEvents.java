package cf.wynntils.modules.utilities.overlays;

import cf.wynntils.ModCore;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.ReflectionFields;
import cf.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.ChestOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.InventoryOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OverlayEvents implements Listener {

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent e) {
        if(e.getGui() instanceof GuiInventory) {
            if(e.getGui() instanceof InventoryOverlay) return;

            e.setGui(new InventoryOverlay(ModCore.mc().player));
            return;
        }
        if(e.getGui() instanceof GuiChest) {
            if(e.getGui() instanceof ChestOverlay) return;

            e.setGui(new ChestOverlay(ModCore.mc().player.inventory, (IInventory)ReflectionFields.GuiChest_lowerChestInventory.getValue(e.getGui())));
        }
    }
    
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent e) {
        WarTimerOverlay.warMessage(e);
    }
    
    @SubscribeEvent
    public void onWorldJoin(WynnWorldJoinEvent e) {
        WarTimerOverlay.onWorldJoin(e);
    }
    
    @SubscribeEvent
    public void onTitle(PacketEvent.TitleEvent e) {
        WarTimerOverlay.onTitle(e);
    }

}
