package cf.wynntils.core.framework.instances;

import net.minecraft.client.Minecraft;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ScreenRenderer {

    Minecraft mc;

    public ScreenRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void drawString(String text, int x, int y, int color) {
        mc.fontRenderer.drawString(text, x, y, color);
    }

}
