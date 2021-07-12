package com.wynntils.modules.map.overlays;

/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.visual.configs.VisualConfig;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.wynntils.modules.map.overlays.ui.SeaskipperWorldMapUI;

import java.io.IOException;

public class OverlayEvents implements Listener {

    private SeaskipperWorldMapUI seaskipperWorldMapUI;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initSeaskipperMenu(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!VisualConfig.CustomSelector.INSTANCE.seaskipperSelector) return;
        if (!e.getGui().getLowerInv().getName().contains("V.S.S. Seaskipper")) return;

        seaskipperWorldMapUI = new SeaskipperWorldMapUI(e.getGui());
        Utils.displayGuiScreen(seaskipperWorldMapUI);
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void closeSeaskipperMenu(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        if (!e.getGui().getLowerInv().getName().contains("V.S.S. Seaskipper")) return;

        seaskipperWorldMapUI = null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceSeaskipperMenuDraw(GuiOverlapEvent.ChestOverlap.DrawScreen.Pre e) {
        if (seaskipperWorldMapUI == null) return;

        seaskipperWorldMapUI.drawScreen(e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceSeaskipperMenuClick(GuiOverlapEvent.ChestOverlap.MouseClicked e) throws IOException {
        if (seaskipperWorldMapUI == null) return;

        seaskipperWorldMapUI.mouseClicked(e.getMouseX(), e.getMouseY(), e.getMouseButton());
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void replaceSeaskipperMouseClickMove(GuiOverlapEvent.ChestOverlap.MouseClickMove e) {
        if (seaskipperWorldMapUI == null) return;

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void replaceSeaskipperMouseInput(GuiOverlapEvent.ChestOverlap.HandleMouseInput e) throws IOException {
        if (seaskipperWorldMapUI == null) return;

        seaskipperWorldMapUI.handleMouseInput();
    }

    @SubscribeEvent
    public void replaceKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) throws IOException {
        if (seaskipperWorldMapUI == null) return;

        seaskipperWorldMapUI.keyTyped(e.getTypedChar(), e.getKeyCode());
        e.setCanceled(true);
    }
}
