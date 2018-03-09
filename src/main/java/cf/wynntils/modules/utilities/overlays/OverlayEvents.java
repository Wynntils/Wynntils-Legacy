package cf.wynntils.modules.utilities.overlays;

import cf.wynntils.ModCore;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.utilities.overlays.inventories.ChestOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.InventoryOverlay;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;

import java.lang.reflect.Field;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OverlayEvents implements Listener {

    @EventHandler
    public void onGuiOpened(GuiOpenEvent e) {
        if(e.getGui() instanceof GuiInventory) {
            if(e.getGui() instanceof InventoryOverlay) {
                return;
            }
            e.setGui(new InventoryOverlay(ModCore.mc().player));
            return;
        }
        if(e.getGui() instanceof GuiChest) {
            if(e.getGui() instanceof ChestOverlay) {
                return;
            }
            Field f = e.getGui().getClass().getDeclaredFields()[2];
            f.setAccessible(true);

            try{
                e.setGui(new ChestOverlay(ModCore.mc().player.inventory, (IInventory)f.get(e.getGui())));
            }catch (Exception ex) { ex.printStackTrace(); }
        }
    }

}
