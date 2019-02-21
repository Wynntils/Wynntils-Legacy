/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.overlays.inventories.RarityColorOverlay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChestReplacer extends GuiChest {

    IInventory lowerInv;
    IInventory upperInv;
    GuiButton professionsButton;
    private final static ArrayList<String> professionArray = new ArrayList<String>(Arrays.asList("-", "None", "Ⓐ", "Cooking", "Ⓓ", "Jeweling", "Ⓔ", "Scribing", "Ⓕ", "Tailoring", "Ⓖ", "Weapon smithing", "Ⓗ", "Armouring", "Ⓘ", "Woodworking", "Ⓛ", "Alchemism"));

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
        if (UtilitiesConfig.Items.INSTANCE.filterEnabled) {
            this.professionsButton = new GuiButton(11, this.guiLeft - 20, this.guiTop + 15, 18, 18, RarityColorOverlay.getProfessionFilter());
            this.buttonList.add(this.professionsButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (professionsButton.isMouseOver()) {
            drawHoveringText(professionArray.get(professionArray.indexOf(professionsButton.displayString) + 1), mouseX, mouseY);
        }
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
        if (mouseButton == 1 && professionsButton.isMouseOver()) {
            char c = professionArray.get((professionArray.indexOf(professionsButton.displayString) + 14) % 16).charAt(0);
            professionsButton.displayString = Character.toString(c);
            RarityColorOverlay.setProfessionFilter(professionsButton.displayString);
            professionsButton.playPressSound(this.mc.getSoundHandler());
            return;
        } else if (mouseButton == 2 && professionsButton.isMouseOver()) {
            RarityColorOverlay.setProfessionFilter("-");
            professionsButton.displayString = "-";
            professionsButton.playPressSound(this.mc.getSoundHandler());
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void actionPerformed(GuiButton btn) throws IOException {
        if (btn.id == 11) {
            char c = professionArray.get((professionArray.indexOf(btn.displayString) + 2) % 16).charAt(0);
            btn.displayString = Character.toString(c);
            RarityColorOverlay.setProfessionFilter(btn.displayString);
            return;
        }
        super.actionPerformed(btn);
    }
}
