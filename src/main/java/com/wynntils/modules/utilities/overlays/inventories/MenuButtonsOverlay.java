/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class MenuButtonsOverlay implements Listener {

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.IngameMenuOverlap.InitGui e) {
        if (UtilitiesConfig.INSTANCE.addClassServer) {
            ArrayList<GuiButton> toBeRemoved = new ArrayList<>();
            e.getButtonList().forEach(b -> {
                if (b.id >= 5 && b.id <= 7) {
                    toBeRemoved.add(b);
                } else if (b.id == 1) {
                    b.displayString = TextFormatting.RED + b.displayString;
                } else if (b.id == 12 || b.id == 0) {
                    b.displayString = TextFormatting.GRAY + b.displayString;
                }
            });
            e.getButtonList().removeAll(toBeRemoved);
            e.getButtonList().add(new GuiButton(753, e.getGui().width / 2 - 100, e.getGui().height / 4 + 48 + -16, "Class selection"));
            e.getButtonList().add(new GuiButton(754, e.getGui().width / 2 - 100, e.getGui().height / 4 + 72 + -16, "Back to Hub"));
        }
    }

    @SubscribeEvent
    public void actionPerformed(GuiOverlapEvent.IngameMenuOverlap.ActionPerformed e) {
        if (e.getButton().id == 753) {
            Minecraft.getMinecraft().player.sendChatMessage("/class");
            e.setCanceled(true);
        } else if (e.getButton().id == 754) {
            Minecraft.getMinecraft().player.sendChatMessage("/hub");
            e.setCanceled(true);
        }
    }

}
