package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

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
            pushMatrix();
            pushAttrib();
            {
                enableAlpha();
                enableBlend();
                enableTexture2D();
                color(1.0F, 1.0F, 1.0F, 1F);

                Pair<Float,Float> xy1 = getTexCoord(uv.a);
                uv.b.translate(uv.a.x,uv.a.y);
                Pair<Float,Float> xy2 = getTexCoord(uv.b);
                GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                GlStateManager.glBegin(GL11.GL_QUADS);
                GlStateManager.glTexCoord2f(xy1.a, xy1.b); GlStateManager.glVertex3f(draw.x,               draw.y, 0);
                GlStateManager.glTexCoord2f(xy1.a, xy2.b); GlStateManager.glVertex3f(draw.x,               draw.y+drawSize.y, 0);
                GlStateManager.glTexCoord2f(xy2.a, xy2.b); GlStateManager.glVertex3f(draw.x+drawSize.x, draw.y+drawSize.y, 0);
                GlStateManager.glTexCoord2f(xy2.a, xy1.b); GlStateManager.glVertex3f(draw.x+drawSize.x, draw.y, 0);
                GlStateManager.glEnd();

                disableTexture2D();
                disableBlend();
            }
            popMatrix();
            popAttrib();
        } catch (Exception e) {
            return DrawMapResult.DRAW_ERROR;
        }
        return DrawMapResult.SUCCESS;
    }

    private static Pair<Float,Float> getTexCoord(int textureX, int textureY) {
        return new Pair<>((float)textureX/(float)MapHandler.mapTextureSize.x,(float)textureY/(float)MapHandler.mapTextureSize.y);
    }
    private static Pair<Float,Float> getTexCoord(Point textureXY) {
        return getTexCoord(textureXY.x,textureXY.y);
    }

    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR
    }
}
