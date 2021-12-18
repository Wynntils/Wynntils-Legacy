/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.overlays.ui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

public class ForgeConflictScreen extends CustomModLoadingErrorDisplayException {

    private final int reqMajorVersion, reqMinorVersion, reqRevisionVersion, reqBuildVersion;

    public ForgeConflictScreen(int reqMajorVersion, int reqMinorVersion, int reqRevisionVersion, int reqBuildVersion) {
        this.reqMajorVersion = reqMajorVersion;
        this.reqMinorVersion = reqMinorVersion;
        this.reqRevisionVersion = reqRevisionVersion;
        this.reqBuildVersion = reqBuildVersion;
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {

    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        errorScreen.drawDefaultBackground();

        errorScreen.drawCenteredString(
                fontRenderer,
                String.format(
                    "Wynntils has detected that you are using Forge version: %s.",
                    ForgeVersion.getVersion()
                ),
                errorScreen.width / 2,
                errorScreen.height / 2 - 10,
                0xFFFFFF);
        errorScreen.drawCenteredString(fontRenderer,
                String.format(
                        "However, Wynntils requires Forge to be of %d.%d.%d.%d or higher.",
                    reqMajorVersion,
                    reqMinorVersion,
                    reqRevisionVersion,
                    reqBuildVersion
                ),
                errorScreen.width / 2,
                errorScreen.height / 2,
                0xFFFFFF);
        errorScreen.drawCenteredString(fontRenderer,
                "Please update your Forge version to continue using Wynntils.",
                errorScreen.width / 2,
                errorScreen.height / 2 + 10,
                0xFFFFFF);
    }

}
