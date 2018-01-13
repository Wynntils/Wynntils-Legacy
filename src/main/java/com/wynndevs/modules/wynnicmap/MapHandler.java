package com.wynndevs.modules.wynnicmap;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MapHandler {
    private static int mapId;                                                                                                               /* Texture id of the map in OpenGL */
    private static boolean mapLoaded         = false; public static boolean isMapLoaded() {return mapLoaded;}                               /* Indication of map texture being loaded */
    private static final File fileMapTexture = new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maptexture.png");  /* Map texture file */
    private static final File fileMapInfo    = new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/map.mapinfo");     /* Map information */
    private static int mapVersion            = -1; public static int getMapVersion() {return mapVersion;}                                   /* Version of the loaded map */
    private static List<MapInformation> maps = new ArrayList<MapInformation>(); public static List<MapInformation> getMaps() {return maps;} /* All map parts */

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
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, GetByteBuffer(image));
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }{
            Scanner scanner = new Scanner(fileMapInfo);
            mapVersion = Integer.parseInt(scanner.nextLine());

            while(scanner.hasNext())
                maps.add(new MapInformation(scanner));
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


    /*
     * @TODO HERE
     * method - get map cords from texture cords and texture cords from map cords
     * method - draw map at screen cords with params (zoom,centerX,centerY)
     */



    /**
     * Converts a BufferedImage type(from ImageIO.read()) into ByteBuffer
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

    }
}
