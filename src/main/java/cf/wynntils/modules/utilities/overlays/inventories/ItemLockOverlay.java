/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.inventories;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemLockOverlay implements Listener {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onInventoryGui(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        if(!Reference.onWorld) return;

        for(Slot s : e.getGuiInventory().inventorySlots.inventorySlots) {
            if(s.slotNumber <= 4) continue;
            renderItemLock(s, e.getGuiInventory().getGuiLeft(), e.getGuiInventory().getGuiTop());
        }
        if(e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) e.getGuiInventory().renderToolTip(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getMouseX(), e.getMouseY());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChestGui(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if(!Reference.onWorld) return;

        for(Slot s : e.getGuiInventory().inventorySlots.inventorySlots) {
            if(s.slotNumber < e.getGuiInventory().getUpperInv().getSizeInventory()) continue;
            renderItemLock(s, e.getGuiInventory().getGuiLeft(), e.getGuiInventory().getGuiTop());
        }
        if(e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) e.getGuiInventory().renderToolTip(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getMouseX(), e.getMouseY());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHorseGui(GuiOverlapEvent.HorseOverlap.DrawScreen e) {
        if(!Reference.onWorld) return;

        for(Slot s : e.getGuiInventory().inventorySlots.inventorySlots) {
            if(s.slotNumber < e.getGuiInventory().getUpperInv().getSizeInventory()) continue;
            renderItemLock(s, e.getGuiInventory().getGuiLeft(), e.getGuiInventory().getGuiTop());
        }
        if(e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) e.getGuiInventory().renderToolTip(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getMouseX(), e.getMouseY());
    }

    private void renderItemLock(Slot s, int guiLeft, int guiTop) {
        if(UtilitiesConfig.INSTANCE.locked_slots.containsKey(PlayerInfo.getPlayerInfo().getClassId()) && UtilitiesConfig.INSTANCE.locked_slots.get(PlayerInfo.getPlayerInfo().getClassId()).contains(s.getSlotIndex())) {
            ScreenRenderer.beginGL(0, 0);

            //HeyZeer0: this will make the lock appear over the item
            GlStateManager.translate(0, 0, 260);

            ScreenRenderer r = new ScreenRenderer();
            RenderHelper.disableStandardItemLighting();
            r.scale(0.5f);
            r.drawRect(Textures.UIs.hud_overlays, (int)((guiLeft + s.xPos) / 0.5) + 25, (int)((guiTop + s.yPos) / 0.5) - 8, 0, 0, 16, 16);
            ScreenRenderer.endGL();
        }
    }

}
