/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerNumberOverlay implements Listener {

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.DrawItemOverlay event) {
        if (!UtilitiesConfig.Items.INSTANCE.itemLevelOverlayOutsideGui && Minecraft.getMinecraft().currentScreen == null) return;

        ItemStack serverStack = event.getStack();
        String serverName = TextFormatting.getTextWithoutFormattingCodes(serverStack.getDisplayName());

        assert serverName != null;
        if (!serverName.startsWith("World")) return;
        // If the "items" don't start with "World", assume we aren't in the server selector and just don't play with the numbers
        // There is probably a better way to do this, but I can't get the container title "Wynncraft Servers" in this event, so here we are

        // server number replacements
        try {
            int serverNumber = Integer.parseInt(serverName.replaceAll("[^0-9]+", ""));
            event.setOverlayText(String.valueOf(serverNumber));
        } catch (NumberFormatException ignored) {
            // I know ignoring exceptions is probably not good, but I don't see a reason to throw some error or two into the user's logs
            // This is only triggered when it scans for server names, looking for integers
        }
    }

}
