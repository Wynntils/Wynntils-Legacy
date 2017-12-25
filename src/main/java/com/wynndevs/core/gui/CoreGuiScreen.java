package com.wynndevs.core.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class CoreGuiScreen extends GuiScreen {

    public Minecraft mc;

    public CoreGuiScreen(Minecraft mc) {
        this.mc = mc;
    }

    public void drawCenteredStringPlain(String text, int x, int y, int color)
    {
        fontRenderer.drawString(text, (x - fontRenderer.getStringWidth(text) / 2), y, color);
    }

}
