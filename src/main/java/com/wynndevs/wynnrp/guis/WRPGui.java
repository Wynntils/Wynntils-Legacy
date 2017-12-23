package com.wynndevs.wynnrp.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WRPGui extends Gui {

    Minecraft mc;

    public WRPGui(Minecraft mc) {
        this.mc = mc;
    }

    public void drawString(String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(mc.fontRenderer, text, x, y, color);
        GL11.glScalef(mSize,mSize,mSize);
    }

    public void drawString(String text, int x, int y, int color) {
        this.drawString(mc.fontRenderer, text, x, y, color);
    }

}
