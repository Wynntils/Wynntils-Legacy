package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * GuiMap like a GuiButton can be added to a Gui/GuiScreen to be used/drawn
 */
public class GuiMap extends Gui {
    private static final ResourceLocation circle_mask = new ResourceLocation(Reference.MOD_ID + ":textures/wynnicmap/mask_circle.png");
    private static final ResourceLocation square_mask = new ResourceLocation(Reference.MOD_ID + ":textures/wynnicmap/mask_square.png");

    public int x,             /* X in screen to draw the map at */
               y,             /* Y in screen to draw the map at */
               width,         /* Width for the map to be drawn upon */
               height,        /* Height for the map to be drawn upon */
               zoom,          /* Zoom on the map in pixels */
               rotation;      /* Map rotation(-180 < rotation > 180) */
    public double centerX,    /* Center of the map real world X */
                  centerY;    /* Center of the map real world Y */
    public boolean circular,  /* Should the map texture be circular instead of a rectangular */
                   visible;   /* Should the map be visible */

    public GuiMap(int x, int y, int width, int height, int zoom, float centerX, float centerY, boolean circular, boolean visible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        this.centerX = centerX;
        this.centerY = centerY;
        this.circular = circular;
        this.visible = visible;
        this.rotation = 0;
    }

    /**
     * Draws the map on the screen, Call from the drawScreen(override) in your GUI or from RenderGameOverlayEvent
     *
     * @return indication of what happened in the drawing process
     */

    public DrawMapResult drawMap() {
        if(!visible)              return DrawMapResult.NOT_VISIBLE;

        try {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 0);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            {
                beginStencil();
                if(!MapHandler.BindMap()) return DrawMapResult.MAP_NOT_LOADED;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if(rotation != 0) {
                    GlStateManager.translate(x+width/2,y+height/2,0);
                    GlStateManager.rotate(180-rotation,0,0,1);
                    GlStateManager.translate(-(x+width/2),-(y+height/2),0);
                }
                GlStateManager.glBegin(GL_QUADS);
                {
                    float uvSX = (float)(centerX-MapHandler.getMap().getStartX()-width/2 + zoom),
                          uvSY = (float)(centerY-MapHandler.getMap().getStartY()-height/2 + zoom),
                          uvEX = (uvSX+width - 2*zoom),
                          uvEY = (uvSY+height - 2*zoom);
                    uvSX /= (float)MapHandler.getMap().getWidth();
                    uvEX /= (float)MapHandler.getMap().getWidth();
                    uvSY /= (float)MapHandler.getMap().getHeight();
                    uvEY /= (float)MapHandler.getMap().getHeight();
                    GlStateManager.glTexCoord2f(uvSX,uvSY);
                    GlStateManager.glVertex3f(x-width*0.5f,y-height*0.5f, 0);
                    GlStateManager.glTexCoord2f(uvSX,uvEY);
                    GlStateManager.glVertex3f(x-width*0.5f,y+height*1.5f, 0);
                    GlStateManager.glTexCoord2f(uvEX,uvEY);
                    GlStateManager.glVertex3f(x+1.5f*width,y+height*1.5f, 0);
                    GlStateManager.glTexCoord2f(uvEX,uvSY);
                    GlStateManager.glVertex3f(x+width*1.5f,y-height*0.5f, 0);
                }
                GlStateManager.glEnd();
            }
            if(rotation != 0) {
                GlStateManager.translate(x+width/2,y+height/2,0);
                GlStateManager.rotate(180-rotation,0,0,-1);
                GlStateManager.translate(-(x+width/2),-(y+height/2),0);
            }
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            drawRect(x + width / 2, y + height / 2, x + width / 2 + 1, y + height / 2 + 1, new Color(255, 0, 0).getRGB());
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

            cleanup();
        } catch (Exception e) {
            e.printStackTrace();
            return DrawMapResult.DRAW_ERROR;
        }
        return DrawMapResult.SUCCESS;
    }

    private void beginStencil() {
        try {
            this.cleanup();
            GlStateManager.colorMask(false, false, false, false);
            Minecraft.getMinecraft().getTextureManager().bindTexture(circular ? circle_mask : square_mask);
            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(0,0);
            GlStateManager.glVertex3f(x,y, 1000.0F);
            GlStateManager.glTexCoord2f(0,1);
            GlStateManager.glVertex3f(x,y+height, 1000.0F);
            GlStateManager.glTexCoord2f(1,1);
            GlStateManager.glVertex3f(x+width,y+height, 1000.0F);
            GlStateManager.glTexCoord2f(1,0);
            GlStateManager.glVertex3f(x+width,y, 1000.0F);
            GlStateManager.glEnd();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.depthMask(false);
            GlStateManager.depthFunc(516);
        } catch (Throwable var2) {}
    }

    private void cleanup() {
        try {
            GlStateManager.depthMask(true);
            GL11.glClear(256);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable var2) {}
    }

    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR,NOT_VISIBLE
    }
}
