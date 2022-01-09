/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.overlays.ui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class ForgeConflictScreen extends CustomModLoadingErrorDisplayException {

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {

    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        errorScreen.drawDefaultBackground();

        errorScreen.drawCenteredString(
                fontRenderer,
                String.format(
                    "Wynntils has detected that you are using Forge 1.12.2 version: %s.",
                    ForgeVersion.getVersion()
                ),
                errorScreen.width / 2,
                errorScreen.height / 2 - 30,
                0xFFFFFF);
        errorScreen.drawCenteredString(fontRenderer,
                "However, Wynntils requires Forge 1.12.2 to be of 14.23.5.2856 or higher.",
                errorScreen.width / 2,
                errorScreen.height / 2 - 20,
                0xFFFFFF);
        errorScreen.drawCenteredString(
                fontRenderer,
                "As lower versions of Forge are vulnerable to a severe exploit (log4shell).",
                errorScreen.width / 2,
                errorScreen.height / 2 - 10,
                0xFFFFFF
        );
        errorScreen.drawCenteredString(
                fontRenderer,
                "For your safety, we prevent loading with vulnerable versions of Forge.",
                errorScreen.width / 2,
                errorScreen.height / 2,
                0xFFFFFF
        );
        errorScreen.drawCenteredString(fontRenderer,
                "§cPlease update your Forge 1.12.2 version.",
                errorScreen.width / 2,
                errorScreen.height / 2 + 20,
                0xFFFFFF);
    }

}
