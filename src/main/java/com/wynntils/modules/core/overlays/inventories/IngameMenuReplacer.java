/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.FrameworkManager;
import net.minecraft.client.gui.GuiIngameMenu;

import java.io.IOException;

public class IngameMenuReplacer extends GuiIngameMenu {

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.IngameMenuOverlap.DrawScreen(this, mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        FrameworkManager.getEventBus().post(new GuiOverlapEvent.IngameMenuOverlap.MouseClicked(this, mouseX, mouseY, mouseButton));
    }

    @Override
    public void drawHoveringText(String text, int x, int y) {
        super.drawHoveringText(text, x, y);
    }

}
