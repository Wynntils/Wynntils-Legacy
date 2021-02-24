/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerNumberOverlay implements Listener {

    private static final Pattern WORLD_PATTERN = Pattern.compile("World (\\d+)(?: \\(Recommended\\))?");

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.DrawItemOverlay event) {

        ItemStack serverStack = event.getStack();
        String serverName = TextFormatting.getTextWithoutFormattingCodes(serverStack.getDisplayName());

        if (serverName == null) return;

        // Check if item follows pattern "World #" or "World # (Recommended)"
        Matcher m = WORLD_PATTERN.matcher(serverName);
        if (!m.matches()) return;

        event.setOverlayText(m.group(1));
    }

}
