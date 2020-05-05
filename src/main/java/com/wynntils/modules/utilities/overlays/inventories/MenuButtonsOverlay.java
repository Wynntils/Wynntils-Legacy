/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.modules.core.overlays.inventories.IngameMenuReplacer;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class MenuButtonsOverlay implements Listener {

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.IngameMenuOverlap.InitGui e) {
        if (!Reference.onServer) return;
        if (Reference.onWorld) {
            if (UtilitiesConfig.INSTANCE.addClassServer) {
                initClassServerGui(e.getButtonList(), e.getGui());
            }
            return;
        }
        if (UtilitiesConfig.INSTANCE.addChangeOptions) {
            initChangeServer(e.getButtonList(), e.getGui());
        }
    }

    /**
     * Removes the "Advancements", "Statistics" and "Open to LAN" buttons.
     * Also makes "Options..." and "Mod Options..." grey and "Disconnect" red.
     */
    private static void removeDefaultButtons(List<GuiButton> buttonList) {
        buttonList.removeIf(b -> {
            if (b.id >= 5 && b.id <= 7) return true;
            if (b.id == 1) {
                b.displayString = TextFormatting.RED + b.displayString;
            } else if (b.id == 12 || b.id == 0) {
                b.displayString = TextFormatting.GRAY + b.displayString;
            }
            return false;
        });
    }

    private void initClassServerGui(List<GuiButton> buttonList, IngameMenuReplacer gui) {
        removeDefaultButtons(buttonList);

        buttonList.add(new GuiButton(753, gui.width / 2 - 100, gui.height / 4 + 48 + -16, "Class selection"));
        buttonList.add(new GuiButton(754, gui.width / 2 - 100, gui.height / 4 + 72 + -16, "Back to Hub"));
    }

    private void initChangeServer(List<GuiButton> buttonList, IngameMenuReplacer gui) {
        removeDefaultButtons(buttonList);

        for (GuiButton button : buttonList) {
            if (button.id == 4) {
                button.y = gui.height / 4 + 48 - 16;
            }
        }
        buttonList.add(new GuiButton(755, gui.width / 2 - 100, gui.height / 4 + 72 - 16, "Wynntils Options"));
    }

    @SubscribeEvent
    public void actionPerformed(GuiOverlapEvent.IngameMenuOverlap.ActionPerformed e) {
        int id = e.getButton().id;
        switch (id) {
            case 753:
                Minecraft.getMinecraft().player.sendChatMessage("/class");
                break;
            case 754:
                Minecraft.getMinecraft().player.sendChatMessage("/hub");
                break;
            case 755:
                Minecraft.getMinecraft().displayGuiScreen(SettingsUI.getInstance(Minecraft.getMinecraft().currentScreen));
                break;
            default:
                return;
        }
        e.setCanceled(true);
    }

}
