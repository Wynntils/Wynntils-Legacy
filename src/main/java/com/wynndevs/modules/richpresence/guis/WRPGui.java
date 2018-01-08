package com.wynndevs.modules.richpresence.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class WRPGui extends GuiIngame {

    public Minecraft mc;

    public WRPGui(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    /**
     * A simple shorter method to render a scaled string
     *
     * @param text
     * @param x
     * @param y
     * @param color
     */
    public void drawString(String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(mc.fontRenderer, text, Math.round(x / size),Math.round(y / size), color);
        GL11.glScalef(mSize,mSize,mSize);
    }

    /**
     * A simple shorter method to render a string
     *
     * @param text
     * @param x
     * @param y
     * @param color
     */
    public void drawString(String text, int x, int y, int color) {
        this.drawString(mc.fontRenderer, text, x, y, color);
    }

}
