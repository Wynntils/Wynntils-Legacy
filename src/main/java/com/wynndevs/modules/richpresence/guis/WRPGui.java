package com.wynndevs.modules.richpresence.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
public class WRPGui extends Gui {

    public Minecraft mc;

    public WRPGui(Minecraft mc) {
        super();
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
        this.drawString(mc.fontRenderer, text, x, y, color);
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

    /**
     * Override the renderSelectedItem
     *
     * @param scaledRes
     */
    public void renderSelectedItem(ScaledResolution scaledRes){

        this.mc.mcProfiler.startSection("selectedItemName");
        try {
            int remainingHighlightTicks = (int) ReflectionHelper.findField(GuiIngame.class, "remainingHighlightTicks").get(Minecraft.getMinecraft().ingameGUI);
            ItemStack highlightingItemStack = (ItemStack) ReflectionHelper.findField(GuiIngame.class, "highlightingItemStack").get(Minecraft.getMinecraft().ingameGUI);

            if (remainingHighlightTicks > 0 && !highlightingItemStack.isEmpty()) {
                String s = highlightingItemStack.getDisplayName();

                if (highlightingItemStack.hasDisplayName()) {
                    s = TextFormatting.ITALIC + s;
                }

                int i = (scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(s)) / 2;
                int j = scaledRes.getScaledHeight() - 83;

                if (!this.mc.playerController.shouldDrawHUD()) {
                    j += 14;
                }

                int k = (int) ((float) remainingHighlightTicks * 256.0F / 10.0F);

                if (k > 255) {
                    k = 255;
                }

                if (k > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    mc.fontRenderer.drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.mc.mcProfiler.endSection();
    }

}
