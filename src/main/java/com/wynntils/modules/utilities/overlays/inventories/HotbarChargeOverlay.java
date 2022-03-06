package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HotbarChargeOverlay implements Listener {

    @SubscribeEvent
    public void onHotbarOverlay(RenderEvent.DrawItemOverlay event) {
        if (!UtilitiesConfig.INSTANCE.showConsumableChargesHotbar) return;
        ItemStack stack = event.getStack();
        Item item = stack.getItem();

        // We don't care about items inside the inventory, only in the hotbar
        if (McIf.player().inventory.mainInventory.contains(stack) && McIf.player().inventory.mainInventory.indexOf(stack) > 8) return;
        if (item != Items.POTIONITEM && item != Items.DIAMOND_AXE) return; // Consumables are only potions or diamond axes (crafted)

        String name = stack.getDisplayName();
        if (!name.contains("[") || !name.contains("/")) return; // Make sure it's actually some consumable

        String[] consumable = name.split(" ");
        if (consumable.length < 2) return; // Make sure we actually split the consumable name

        String[] charges = consumable[consumable.length - 1].split("/");
        if (charges.length != 2) return; // Make sure we aren't splitting a / from anywhere else; charges always return 2 entries - one for charges remaining and one for total

        String remainingCharges = charges[0].replace("[", "");
        event.setOverlayText(TextFormatting.getTextWithoutFormattingCodes(remainingCharges));

    }

}
