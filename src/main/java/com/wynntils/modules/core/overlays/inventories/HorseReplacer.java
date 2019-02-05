/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class HorseReplacer extends GuiScreenHorseInventory  {

    IInventory lowerInv, upperInv;

    public HorseReplacer(IInventory playerInv, IInventory horseInv, AbstractHorse horse) {
        super(playerInv, horseInv, horse);

        this.lowerInv = playerInv; this.upperInv = horseInv;
    }

    public IInventory getUpperInv() {
        return upperInv;
    }

    public IInventory getLowerInv() {
        return lowerInv;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.HorseOverlap.DrawScreen(this, mouseX, mouseY, partialTicks));
    }

    @Override
    public void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.HorseOverlap.HandleMouseClick(this, slotIn, slotId, mouseButton, type)))
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.HorseOverlap.DrawGuiContainerForegroundLayer(this, mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.HorseOverlap.KeyTyped(this, typedChar, keyCode)))
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void renderToolTip(ItemStack stack, int x, int y) {
        super.renderToolTip(stack, x, y);
    }

}
