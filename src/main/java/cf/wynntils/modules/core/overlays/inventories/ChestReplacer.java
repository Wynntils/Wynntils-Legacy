/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.overlays.inventories;

import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.overlays.inventories.RarityColorOverlay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class ChestReplacer extends GuiChest {

    IInventory lowerInv;
    IInventory upperInv;
    GuiButton professionsButton;

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
            if (!UtilitiesConfig.Items.INSTANCE.saveFilter) {
                RarityColorOverlay.setProfessionFilter("-");
            }
            this.professionsButton = new GuiButton(11, this.guiLeft - 20, this.guiTop + 15, 18, 18, RarityColorOverlay.getProfessionFilter());
            this.buttonList.add(this.professionsButton);
        }
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
    public void actionPerformed(GuiButton btn) throws IOException {
        if (btn.id == 11) {
            char c = btn.displayString.charAt(0);
            if (c == '-') {
                c = 'Ⓐ';
            } else if ((c == 'Ⓐ' || c == 'Ⓘ')) {
                c += 3;
            } else if ((c == 'Ⓒ' || c == 'Ⓙ')) {
                c += 2;
            } else if (c == 'Ⓛ'){
                c = '-';
            } else {
                c += 1;
            }
            btn.displayString = Character.toString(c);
            RarityColorOverlay.setProfessionFilter(btn.displayString);
            return;
        }
        super.actionPerformed(btn);
    }
}
