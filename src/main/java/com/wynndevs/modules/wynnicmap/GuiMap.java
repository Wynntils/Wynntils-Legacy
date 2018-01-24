package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * GuiMap like a GuiButton can be added to a Gui/GuiScreen to be used/drawn
 */
public class GuiMap extends Gui {
    public int x,                  /* X in screen to draw the map at */
               y,                  /* Y in screen to draw the map at */
               width,              /* Width for the map to be drawn upon */
               height,             /* Width for the map to be drawn upon */
               zoom,               /* Zoom on the map */
               rotation;           /* Rotation of the map, 0 being NORTH */
    public float centerX,          /* center of the map real world X */
                 centerY;          /* center of the map real world Y */
    public boolean visible = true; /* should the map be visible */


    public GuiMap(int x, int y, int width, int height, int zoom, float centerX, float centerY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    /**
     * Draws the map on the screen, Call from the drawScreen(override) in your GUI or from RenderGameOverlayEvent
     *
     * @return indication of what happened in the drawing process
     */
    public void drawMap(){
        if (!visible) return;
        if (!MapHandler.isMapLoaded()) return;
        try {
            MapHandler.BindMapTexture();
            Pair<Pair<Float,Float>,Point> uv = MapHandler.GetUV(centerX,centerY,width,height,zoom);
            Point draw = new Point(x,y);
            Point drawSize = new Point(width,height);
            MapHandler.ClampUVAndDrawPoints(draw,drawSize,uv.a,uv.b);
            drawRect(x,y,x+width,y+height,new Color(15,5,15).getRGB());
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            {
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                Pair<Pair<Float,Float>,Pair<Float,Float>> texCoords = MapHandler.GetUVTexCoords(uv);
                GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                GlStateManager.glBegin(GL_QUADS);
                {
                    GlStateManager.glTexCoord2f(texCoords.a.a, texCoords.a.b);GlStateManager.glVertex3f(draw.x, draw.y, 0);
                    GlStateManager.glTexCoord2f(texCoords.a.a, texCoords.b.b);GlStateManager.glVertex3f(draw.x, draw.y + drawSize.y, 0);
                    GlStateManager.glTexCoord2f(texCoords.b.a, texCoords.b.b);GlStateManager.glVertex3f(draw.x + drawSize.x, draw.y + drawSize.y, 0);
                    GlStateManager.glTexCoord2f(texCoords.b.a, texCoords.a.b);GlStateManager.glVertex3f(draw.x + drawSize.x, draw.y, 0);
                }
                GlStateManager.glEnd();

                GlStateManager.disableTexture2D();
                GlStateManager.disableBlend();
                drawRect(x+width/2,y+height/2,x+width/2+1,y+height/2+1,new Color(255,0,0).getRGB());
            }
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

        } catch (Exception e) {
        }
    }



    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR,NOT_VISIBLE
    }
}
