/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.core.events.custom;

import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class InventoryClickEvent extends Event {
    
    ItemStack clickedItem;
    int slotdId, usedButton;
    ClickType clickType; String screenTitle;
    
    public InventoryClickEvent(ItemStack clickedItem, int slotId, ClickType clickType, int usedButton, String screenTitle) {
        this.clickedItem = clickedItem; this.slotdId = slotId; this.clickType = clickType; this.usedButton = usedButton; this.screenTitle = screenTitle;
    }

    public int getSlotdId() {
        return slotdId;
    }

    public int getUsedButton() {
        return usedButton;
    }

    public ItemStack getClickedItem() {
        return clickedItem;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public String getScreenTitle() {
        return screenTitle;
    }

}
