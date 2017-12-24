package com.wynndevs.modules.expansion.Misc;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class ModGui extends Gui {

    // Shadow
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawCenteredString(fontRendererIn,text,(int)Math.floor(x / size),(int)Math.floor(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    
    public void drawSplitString(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, float size, float padding, int textColor)
    {
        //GL11.glScalef(size,size,size);
        //float mSize = (float)Math.pow(size,-1);
        
        int i = 0;
        for (String string:fontRenderer.listFormattedStringToWidth(str,wrapWidth)) {
            drawString(fontRenderer,string,x,y + Math.round(i * size * fontRenderer.FONT_HEIGHT * padding),size,textColor);
            i++;
        }
    
        //GL11.glScalef(mSize,mSize,mSize);
    }
    
    
    // Without Shadow
    public void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawStringPlain(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    public void drawCenteredStringPlain(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawCenteredStringPlain(fontRendererIn,text,(int)Math.floor(x / size),(int)Math.floor(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }

    public void drawSplitStringPlain(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, float size, float padding, int textColor)
    {
        //GL11.glScalef(size,size,size);
        //float mSize = (float)Math.pow(size,-1);
        
        int i = 0;
        for (String string:fontRenderer.listFormattedStringToWidth(str,wrapWidth)) {
            drawStringPlain(fontRenderer,string,x,y + Math.round(i * size * fontRenderer.FONT_HEIGHT * padding),size,textColor);
            i++;
        }
        
        //GL11.glScalef(mSize,mSize,mSize);
    }
    
    
    // Without Shadow Renderer
    public void drawCenteredStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
    }
    
    public void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawString(text, x, y, color);
    }
}
