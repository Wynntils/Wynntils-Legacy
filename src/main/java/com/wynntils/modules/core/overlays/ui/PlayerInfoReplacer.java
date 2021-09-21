/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.overlays.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import javax.annotation.Nullable;

public class PlayerInfoReplacer extends GuiPlayerTabOverlay {
    public static boolean guiOpen = false;

    public PlayerInfoReplacer(Minecraft mcIn, GuiIngame guiIngameIn) {
        super(mcIn, guiIngameIn);
    }

    @Override
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, @Nullable ScoreObjective scoreObjectiveIn) {
        guiOpen = true;
        super.renderPlayerlist(width, scoreboardIn, scoreObjectiveIn);
    }

}
