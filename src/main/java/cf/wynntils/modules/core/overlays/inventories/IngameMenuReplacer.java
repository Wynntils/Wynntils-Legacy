/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.overlays.inventories;

import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.FrameworkManager;
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

}
