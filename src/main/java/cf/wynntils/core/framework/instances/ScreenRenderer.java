package cf.wynntils.core.framework.instances;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.opengl.GL11;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ScreenRenderer extends GuiIngame {

    Minecraft mc;

    public ScreenRenderer(Minecraft mc) {
        super(mc);

        this.mc = mc;
    }

    public void drawString(String text, int x, int y, int color) {
        mc.fontRenderer.drawString(text, x, y, color);
    }

    public void drawString(String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(mc.fontRenderer, text, Math.round(x / size),Math.round(y / size), color);
        GL11.glScalef(mSize,mSize,mSize);
    }

}
