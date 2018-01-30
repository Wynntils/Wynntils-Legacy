package com.wynndevs.modules.wynnicmap;


import com.wynndevs.core.Reference;
import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.opengl.GL11;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * GuiMap like a GuiButton can be added to a Gui/GuiScreen to be used/drawn
 */
public class GuiMap extends Gui {
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
    private Point[] DrawingCut = null;

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
        RecreateDrawingCut();
    }

    public void RecreateDrawingCut() {
        DrawingCut = new Point[circular ? 360 : 4];
        if(circular) {
            int radX = width/2, radY = height/2;
            float rot = (float)rotation*0.0174532925f;
            DrawingCut[0] = new Point(x+radX,y+radY);
            for(int a = 359; a >= 0; a--)
                DrawingCut[a] = new Point(
                        x + radX + MathHelper.fastFloor(radX*MathHelper.cos(a * 0.0174532925f+rot)),
                        y + radY + MathHelper.fastFloor(radY*MathHelper.sin(a * 0.0174532925f+rot)));
        } else {
            DrawingCut[0] = new Point(x,y);                    // 0,0
            DrawingCut[1] = new Point(x,y+height);          // 0,1
            DrawingCut[2] = new Point(x+width,y+height); // 1,1
            DrawingCut[3] = new Point(x+width,y);           // 1,0
        }
    }

    /**
     * Draws the map on the screen, Call from the drawScreen(override) in your GUI or from RenderGameOverlayEvent
     *
     * @return indication of what happened in the drawing process
     */

    public DrawMapResult drawMap() {
        if(!visible)              return DrawMapResult.NOT_VISIBLE;
        if(!MapHandler.BindMap()) return DrawMapResult.MAP_NOT_LOADED;

        try {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();
            {
                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.glBegin(circular ? GL_POLYGON : GL_QUADS);
                {
                    GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    Vec2f[] texCoords = MapHandler.getMap().GenerateTextureCut(centerX, centerY, width, height, rotation, zoom, circular);
                    //WynnicMap.logger.info("Listing points on circles: ");
                    for (int i = 0; i < DrawingCut.length; i++) {
                        GlStateManager.glTexCoord2f(texCoords[i].x, texCoords[i].y);
                        GlStateManager.glVertex3f(DrawingCut[i].x, DrawingCut[i].y, 0);
                        //WynnicMap.logger.info("(" + texCoords[i].x + "," + texCoords[i].y + ") <-> (" + DrawingCut[i].x + "," + DrawingCut[i].y + ")");
                    }
                }
                GlStateManager.glEnd();

            }
            GlStateManager.disableTexture2D();
            drawRect(x + width / 2, y + height / 2, x + width / 2 + 1, y + height / 2 + 1, new Color(255, 0, 0).getRGB());
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        } catch (Exception e) {
            e.printStackTrace();
            return DrawMapResult.DRAW_ERROR;
        }
        return DrawMapResult.SUCCESS;
    }



    public enum DrawMapResult {
        SUCCESS,MAP_NOT_LOADED,DRAW_ERROR,NOT_VISIBLE
    }
}
