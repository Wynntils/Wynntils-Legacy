/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.overlays.inventories;

import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.questbook.QuestBookModule;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class InventoryReplacer extends GuiInventory {

    EntityPlayer player;

    public InventoryReplacer(EntityPlayer player) {
        super(player);

        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.InventoryOverlap.DrawScreen(this, mouseX, mouseY, partialTicks));
    }

    @Override
    public void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.InventoryOverlap.HandleMouseClick(this, slotIn, slotId, mouseButton, type)))
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.InventoryOverlap.DrawGuiContainerForegroundLayer(this, mouseX, mouseY));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(!FrameworkManager.getEventBus().post(new GuiOverlapEvent.InventoryOverlap.KeyTyped(this, typedChar, keyCode)))
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == 10) {
            QuestBookModule.gui.open();
            return;
        }
        
        super.actionPerformed(guiButton);
    }

    @Override
    public void renderToolTip(ItemStack stack, int x, int y) {
        super.renderToolTip(stack, x, y);
    }
}
