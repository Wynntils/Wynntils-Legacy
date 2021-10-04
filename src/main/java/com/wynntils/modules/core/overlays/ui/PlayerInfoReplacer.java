/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.overlays.ui;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import javax.annotation.Nullable;

public class PlayerInfoReplacer extends GuiPlayerTabOverlay {

    public PlayerInfoReplacer(Minecraft mcIn, GuiIngame guiIngameIn) {
        super(mcIn, guiIngameIn);
    }

    @Override
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, @Nullable ScoreObjective scoreObjectiveIn) {
        if (FrameworkManager.getEventBus().post(new GuiOverlapEvent.PlayerInfoOverlap.RenderList(this))) return;

        super.renderPlayerlist(width, scoreboardIn, scoreObjectiveIn);
    }

}
