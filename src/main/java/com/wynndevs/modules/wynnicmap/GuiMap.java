package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;


import java.awt.*;

import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * GuiMap like a GuiButton can be added to a Gui/GuiScreen to be used/drawn
 */
public class GuiMap extends Gui {
    public int x,      /* X in screen to draw the map at */
               y,      /* Y in screen to draw the map at */
               width,  /* Width for the map to be drawn upon */
               height, /* Width for the map to be drawn upon */
               mapX,   /* X in the map to start drawing from */
               mapY,   /* Y in the map to start drawing from */
               zoom;   /* Zoom on the map */

    public GuiMap(int x, int y, int width, int height, int mapX, int mapY, int zoom) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mapX = mapX;
        this.mapY = mapY;
        this.zoom = zoom;
    }

    /**
     * Draws the map on the screen, Call from the drawScreen(override) in your GUI or from RenderGameOverlayEvent
     *
     * @return indication of what happened in the drawing process
     */
    public DrawMapResult drawMap() {
        if(!MapHandler.isMapLoaded()) return DrawMapResult.MAP_NOT_LOADED;
        try {
            drawRect(x,y,x+width,y+height,new Color(15,5,15).getRGB());
            MapHandler.BindMapTexture();
            Pair<Point,Point> uv = MapHandler.GetUV(mapX,mapY,width,height,zoom);
            Point draw = new Point(x,y);
            Point drawSize = new Point(width,height);
            MapHandler.ClampUVAndDrawPoints(draw,drawSize,uv.a,uv.b);
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
            }
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        } catch (Exception e) {
            return DrawMapResult.DRAW_ERROR;
        }
        return DrawMapResult.SUCCESS;
    }



    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR
    }
}
