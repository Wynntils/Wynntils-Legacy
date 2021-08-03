package com.wynntils.modules.map.overlays;

/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.webapi.WebManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.wynntils.modules.map.overlays.ui.SeaskipperWorldMapUI;

import java.io.IOException;

public class OverlayEvents implements Listener {

    private SeaskipperWorldMapUI seaskipperWorldMapUI;

    //MouseClicked and MouseClickMove are obsolete since HandleMouseInput is canceled, and mouseClicked is handled by MovementScreen

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void initSeaskipperMenu(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!VisualConfig.CustomSelector.INSTANCE.seaskipperSelector) return;
        if (!e.getGui().getLowerInv().getName().contains("V.S.S. Seaskipper")) return;
        //Return if api has no response
        if (WebManager.getSeaskipperLocations().isEmpty()) return;

        seaskipperWorldMapUI = new SeaskipperWorldMapUI(e.getGui());
        seaskipperWorldMapUI.setWorldAndResolution(McIf.mc(), e.getGui().width, e.getGui().height);
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

    @SubscribeEvent
    public void replaceSeaskipperMouseInput(GuiOverlapEvent.ChestOverlap.HandleMouseInput e) throws IOException {
        if (seaskipperWorldMapUI == null) return;

        seaskipperWorldMapUI.handleMouseInput();
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void replaceKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) throws IOException {
        if (seaskipperWorldMapUI == null) return;

        //1 is the keycode escape, meaning that if seaskipperWorldMapUI runs it, it closes the screen,
        // but the chestreplacer can remain open, leading to a blank screen client side but the chest
        // is still open.

        //So instead escapes are passed through to the chestreplacer, allowing an actual chest close.
        if (e.getKeyCode() != 1) {
            seaskipperWorldMapUI.keyTyped(e.getTypedChar(), e.getKeyCode());
            e.setCanceled(true);
        }
    }
}
