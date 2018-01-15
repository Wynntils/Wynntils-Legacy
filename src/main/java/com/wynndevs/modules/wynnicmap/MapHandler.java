package com.wynndevs.modules.wynnicmap;

import com.wynndevs.core.Utils.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.*;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;

public class MapHandler {
    private static int mapId;                                                                                                                   /* Texture id of the map in OpenGL */
    private static boolean mapLoaded              = false; public static boolean isMapLoaded() {return mapLoaded;}                              /* Indication of map texture being loaded */
    private static final File fileMapTexture      = new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maptexture.png"); /* Map texture file */
    private static final File fileMapInfo         = new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/map.mapinfo");    /* Map information */
    private static int mapVersion                 = -1; public static int getMapVersion() {return mapVersion;}                                  /* Version of the loaded map */
    public static Map<String,MapInformation> maps = new HashMap<String, MapInformation>();                                                                           /* All map parts */
    public static Point mapTextureSize = new Point(0,0);                                                                                  /* Size of map texture */

    /**
     * Loads the map into memory
     *
     * @throws IOException when either of the map files has failed to load
     */
    public static void LoadMap() throws IOException {
        Unload();
        {
            mapId = GlStateManager.generateTexture();
            BufferedImage image = ImageIO.read(fileMapTexture);
            GlStateManager.bindTexture(mapId);

            GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, GetByteBuffer(image));
            GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            mapTextureSize = new Point(image.getWidth(),image.getHeight());
        }{
            Scanner scanner = new Scanner(fileMapInfo);
            mapVersion = Integer.parseInt(scanner.nextLine());

            while(scanner.hasNext())
                maps.put(scanner.nextLine(),new MapInformation(scanner));

        }
        mapLoaded = true;

    }

    /**
     * Unloads the map and information from memory
     */
    public static void UnloadMap() {
        if(!mapLoaded) return;

        Unload();

        mapLoaded = false;
    }
    private static void Unload() {
        GlStateManager.deleteTexture(mapId);
        maps.clear();
    }

    /**
     * Binds the map texture to be drawn
     *
     * @return boolean indicating succession
     */
    public static boolean BindMapTexture() {
        if(mapLoaded)
            GlStateManager.bindTexture(mapId);
        return mapLoaded;
    }

    /**
     * GetUV will get you a the map uv start point and size of the spesified location and size
     *
     * @param centerX   center point of map in real coordinates
     * @param centerY   center point of map in real coordinates
     * @param drawSizeX on screen drawing dimensions of the map
     * @param drawSizeY on screen drawing dimensions of the map
     * @param zoom zoom in pixels on the map(accepts minus values)
     * @return Pair of Points, a is uv start position and b is uv width and height
     */
    @Nullable
    public static Pair<Point,Point> GetUV(int centerX, int centerY, int drawSizeX, int drawSizeY, int zoom) {
        for (String key:maps.keySet()) {
            MapInformation cur = maps.get(key);
            if(cur.IsOnMap(centerX,centerY))
            {
                Point startPos = new Point(cur.GetUV(centerX - (drawSizeX/2),centerY - (drawSizeY/2)));
                Point size = new Point(drawSizeX,drawSizeY);
                startPos.x += zoom; startPos.y += zoom;
                size.x -= 2*zoom; size.y -= 2*zoom;
                return new Pair<>(startPos,size);

            }
        }
        return null;
    }

    /**
     * GetUVTexCoord will get the openGL uv equivalents of uvPointAndSize that can be drawn on a texture
     *
     * @param uvPointAndSize Pair of a Point and a size(x,y,width,height) of the uv in texture pixels
     * @return Pair of 2 Float Pairs representing 2 uv points(0.0f,0.0f -> 1.0f,1.0f) on the texture
     */
    public static Pair<Pair<Float,Float>,Pair<Float,Float>> GetUVTexCoords(Pair<Point,Point> uvPointAndSize) {
        Pair<Float,Float> uvPoint = new Pair<>((float)uvPointAndSize.a.x/(float)mapTextureSize.x,(float)uvPointAndSize.a.y/(float)mapTextureSize.y);
        Pair<Float,Float> uvEnd = new Pair<>((float)uvPointAndSize.b.x/(float)mapTextureSize.x + uvPoint.a,(float)uvPointAndSize.b.y/(float)mapTextureSize.y + uvPoint.b);
        return new Pair<>(uvPoint,uvEnd);
    }

    /**
     * Clamps the UV and Draw points to deal with the outside of the map
     *
     * @param draw     on-screen drawing start point
     * @param drawSize on-screen drawing width and height
     * @param uv       uv texture start point
     * @param uvSize   uv texture width and height
     * @return indications if clamping has happened
     */
    public static boolean ClampUVAndDrawPoints(Point draw, Point drawSize, Point uv, Point uvSize) {
        boolean clamped = false;
        for (String key : maps.keySet()) {
            MapInformation cur = maps.get(key);
            if(cur.IsUVOnMap(uv.x+uvSize.x/2,uv.y+uvSize.y/2)) {
                if(!cur.IsUVOnMap(uv.x,uv.y)) {
                    clamped = true;
                    draw.translate(cur.getUvStartX()-uv.x,cur.getUvStartY()-uv.y);
                    uv.x = cur.getUvStartX();
                    uv.y = cur.getUvStartY();
                }
                if(!cur.IsUVOnMap(uv.x+uvSize.x,uv.y+uvSize.y)) {
                    clamped = true;
                    drawSize.translate(uv.x+uvSize.x-cur.getUvEndX(),uv.y+uvSize.y-cur.getUvEndY());
                    uvSize.x = cur.getUvEndX() - cur.getUvStartX();
                    uvSize.y = cur.getUvEndY() - cur.getUvStartY();
                }
            }
        }
        return clamped;
    }

    /**
     * Converts a BufferedImage type(from ImageIO.read(File)) into ByteBuffer
     * which can be loaded into OpenGL
     *
     * @param image BufferImage to be converted
     * @return ByteBuffer equivalent of image
     */
    private static ByteBuffer GetByteBuffer(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for(int y = 0; y < image.getHeight(); y++)
            for(int x = 0; x < image.getWidth(); x++)
            {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) &  0xFF));
                buffer.put((byte)  (pixel &        0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        buffer.flip();
        return buffer;
    }

    /**
     * Holds information about a part of the world
     */
    public static class MapInformation {
        private int startX,   /* Start of map part in world */
                    startY,   /* Start of map part in world */
                    uvStartX, /* Start of map part in the texture */
                    uvStartY, /* Start of map part in the texture */
                    uvEndX,   /* End of map part in the texture */
                    uvEndY;   /* End of map part in the texture */

        public int getStartX() {return startX;}
        public int getStartY() {return startY;}
        public int getUvStartX() {return uvStartX;}
        public int getUvStartY() {return uvStartY;}
        public int getUvEndX() {return uvEndX;}
        public int getUvEndY() {return uvEndY;}

        public MapInformation(int startX, int startY, int uvStartX, int uvStartY, int uvEndX, int uvEndY) {
            this.startX = startX;
            this.startY = startY;
            this.uvStartX = uvStartX;
            this.uvStartY = uvStartY;
            this.uvEndX = uvEndX;
            this.uvEndY = uvEndY;
        }

        public MapInformation(Scanner scanner) {
            this(Integer.parseInt(scanner.nextLine()),
                 Integer.parseInt(scanner.nextLine()),
                 Integer.parseInt(scanner.nextLine()),
                 Integer.parseInt(scanner.nextLine()),
                 Integer.parseInt(scanner.nextLine()),
                 Integer.parseInt(scanner.nextLine()));
        }

        /**
         * Gets the texture uv point on the map part for a real position
         *
         * @param x real world x coordinate
         * @param y real world y coordinate
         * @return point of uv on the map texture
         */
        public Point GetUV(int x, int y) {
            return new Point((x-startX)+uvStartX,(y-startY)+uvStartY);
        }

        /**
         * Checks if the real world coordinate is on the map part
         *
         * @param x real world x coordinate
         * @param y real world y coordinate
         * @return Indication if the point is in this map part
         */
        public boolean IsOnMap(int x, int y) {
            Point uv = GetUV(x,y);
            return IsUVOnMap(uv.x,uv.y);
        }

        /**
         * Checks if the uv coordinate is on the map part
         *
         * @param x uv x coordinate
         * @param y uv y coordinate
         * @return indication if the point is in this map part
         */
        public boolean IsUVOnMap(int x, int y) {
            return x >= uvStartX && x <= uvEndX && y >= uvStartY && y <= uvEndY;
        }
    }
}
