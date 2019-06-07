/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.ui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

import java.util.HashMap;

public class ModConflictScreen extends CustomModLoadingErrorDisplayException {

    public HashMap<String, String> conflictMods;

    public ModConflictScreen(HashMap<String, String> conflictMods) {
        this.conflictMods = conflictMods;
    }


    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {

    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        errorScreen.drawDefaultBackground();

        int offset = (errorScreen.height/2) - (40 + 10*conflictMods.size());
        errorScreen.drawCenteredString(fontRenderer, "Wynntils has detected the following mods:", errorScreen.width/2, offset, 0xFFFFFF);
        offset+=10;
        for(String x : conflictMods.keySet()) {
            offset+=10;
            errorScreen.drawCenteredString(fontRenderer, "§f§l" + x + "§f§o (" + conflictMods.get(x) + ")", errorScreen.width/2, offset, 0xFFFFFF);
        }
        offset+=20;
        errorScreen.drawCenteredString(fontRenderer, "§fThese mods causes §f§lmassives conflicts", errorScreen.width/2, offset, 0xFFFFFF);
        offset+=10;
        errorScreen.drawCenteredString(fontRenderer, "§cRemove these mods and restart your game to Continue", errorScreen.width/2, offset, 0xFFFFFF);
    }

}
