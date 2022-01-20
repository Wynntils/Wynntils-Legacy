/*
 *  * Copyright © Wynntils - 2018 - 2022.
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
                    "Wynntils has detected that you are using version %s of Forge for 1.12.2.",
                    ForgeVersion.getVersion()
                ),
                errorScreen.width / 2,
                errorScreen.height / 2 - 30,
                0xFFFFFF);
        errorScreen.drawCenteredString(fontRenderer,
                "However, Wynntils requires Forge to be version 14.23.5.2856 or newer.",
                errorScreen.width / 2,
                errorScreen.height / 2 - 20,
                0xFFFFFF);
        errorScreen.drawCenteredString(
                fontRenderer,
                "Previous versions of Forge are vulnerable to a severe exploit.",
                errorScreen.width / 2,
                errorScreen.height / 2 - 10,
                0xFFFFFF
        );
        errorScreen.drawCenteredString(
                fontRenderer,
                "For your safety, we have prevented your game from loading with vulnerable versions of Forge.",
                errorScreen.width / 2,
                errorScreen.height / 2,
                0xFFFFFF
        );
        errorScreen.drawCenteredString(fontRenderer,
                "§cPlease update your version of Forge for 1.12.2 to be protected against this vulnerability.",
                errorScreen.width / 2,
                errorScreen.height / 2 + 20,
                0xFFFFFF);
    }

}
