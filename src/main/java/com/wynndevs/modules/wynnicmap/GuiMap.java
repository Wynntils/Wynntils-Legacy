package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

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
     * Draws the map on the screen, Call from the drawScreen(override) in your GUI
     *
     * @return indication of what happened in the drawing process
     */
    public DrawMapResult drawMap() {
        if(!MapHandler.isMapLoaded()) return DrawMapResult.MAP_NOT_LOADED;
        try {
            MapHandler.BindMapTexture();
            Pair<Point,Point> uv = MapHandler.GetUV(mapX,mapY,width,height,zoom);
            Point draw = new Point(x,y);
            Point drawSize = new Point(width,height);
            MapHandler.ClampUVAndDrawPoints(draw,drawSize,uv.a,uv.b);
            drawModalRectWithCustomSizedTexture(draw.x,draw.y,uv.a.x,uv.a.y,drawSize.x,drawSize.y,uv.b.x,uv.b.y);
        } catch (Exception e) {
            e.printStackTrace();
            return DrawMapResult.DRAW_ERROR;
        }
        return DrawMapResult.SUCCESS;
    }

    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR
    }
}
