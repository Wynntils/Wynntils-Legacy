/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class ChestReplacer extends GuiChest {

    IInventory lowerInv;
    IInventory upperInv;

    public ChestReplacer(IInventory upperInv, IInventory lowerInv){
        super(upperInv, lowerInv);

        this.lowerInv = lowerInv;
        this.upperInv = upperInv;
    }

    public IInventory getLowerInv() {
        return lowerInv;
    }

    public IInventory getUpperInv() {
        return upperInv;
    }

    @Override
    public void initGui() {
        super.initGui();
        FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.InitGui(this, this.buttonList));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.DrawScreen(this, mouseX, mouseY, partialTicks));
    }

    @Override
    public void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.HandleMouseClick(this, slotIn, slotId, mouseButton, type)))
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer(this, mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.KeyTyped(this, typedChar, keyCode)))
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void renderToolTip(ItemStack stack, int x, int y) {
        super.renderToolTip(stack, x, y);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        FrameworkManager.getEventBus().post(new GuiOverlapEvent.ChestOverlap.MouseClicked(this, mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public List<GuiButton> getButtonList() {
        return this.buttonList;
    }
}
