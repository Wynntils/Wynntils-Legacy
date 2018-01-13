package com.wynndevs.modules.wynnicmap;

import net.minecraft.client.gui.Gui;

/**
 * GuiMap like a GuiButton can be added to a Gui/GuiScreen to be used/drawn
 */
public class GuiMap extends Gui {
    public int x,      /* X in screen to draw the map at */
               y,      /* Y in screen to draw the map at */
               width,  /* Width for the map to be drawn upon */
               height, /* Width for the map to be drawn upon */
               mapX,   /* X in the map to start drawing from */
               mapY;   /* Y in the map to start drawing from */
    public float zoom; /* Zoom on the map */

    public GuiMap(int x, int y, int width, int height, int mapX, int mapY, float zoom) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mapX = mapX;
        this.mapY = mapY;
        this.zoom = zoom;
    }

    public void drawMap() {

    }
}
