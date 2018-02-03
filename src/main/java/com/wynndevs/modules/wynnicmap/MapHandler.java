package com.wynndevs.modules.wynnicmap;

import com.wynndevs.modules.wynnicmap.utils.Pair;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.*;
import java.util.List;

import java.util.Scanner;

public class MapHandler {
    private static int mapId = -1; /* Texture id of the map in OpenGL */
    private static Map map = null; public static Map getMap() {return map;} /* Current map */
    private static int mapVersion = -1; public static int getMapVersion() {return mapVersion;} /* Version of the loaded map */
    public static final int mapFormat = 1; /* Format used by the mod to load the map */
    private static List<Map> maps = new ArrayList<>(); /* Maps list */
    private static final File fileMapInfo = new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + mapFormat + "/map.mapinfo"); /* Map information */

    public static void LoadMap() throws IOException {
        UnloadMap();
        Scanner scanner = new Scanner(fileMapInfo);
        mapVersion = Integer.parseInt(scanner.nextLine());

        while(scanner.hasNext()) {
            maps.add(new Map(scanner));
            WynnicMap.logger.info("Loaded \"" + maps.get(maps.size()-1).ID + "\" successfully");
        }
        scanner.close();
    }

    public static void UnloadMap(){
        maps.clear();
        if(mapId != -1)
            GlStateManager.deleteTexture(mapId);
        mapId = -1;
        map = null;
    }

    public static void ChangeMap(double x, double y) {
        try {
            for (Map map : maps)
                if (map.isInside(x, y)) {
                    WynnicMap.logger.info("Trying to load map \"" + map.ID + "\"");
                    map.loadTexture();
                    WynnicMap.logger.info("Changed active map to \"" + map.ID + "\" successfully!");
                    return;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //WynnicMap.logger.info("ERROR, NO MAP HAS MANAGED TO LOAD FOR (" + x + "," + y + ")");
        mapId = -1;
        map = null;
    }

    public static boolean BindMap() {
        if(mapId != -1)
            GlStateManager.bindTexture(mapId);
        return mapId != -1;
    }

    /**
     * Holds information about a part of the world
     */
    public static class Map {
        private String ID;    /* Map ID */
        private int startX,   /* Start of map part in world */
                    startY,   /* Start of map part in world */
                    endX,     /* End of map part in world */
                    endY,     /* End of map part in world */
                    width,    /* Width of the map part */
                    height;   /* Height of the map part */

        public String getID() {return ID;}
        public int getStartX() {return startX;}
        public int getStartY() {return startY;}
        public int getEndX() {return endX;}
        public int getEndY() {return endY;}
        public int getWidth() {return width;}
        public int getHeight() {return height;}

        public Map(Scanner scanner) throws IOException{
            ID = scanner.nextLine();
            startX = Integer.parseInt(scanner.nextLine());
            startY = Integer.parseInt(scanner.nextLine());
            BufferedImage image = ImageIO.read(new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + mapFormat + "/" + ID + ".png"));
            width = image.getWidth();
            height = image.getHeight();
            endX = startX + width;
            endY = startY + height;
        }

        public void loadTexture() throws Exception {
            TextureUtil.deleteTexture(mapId);

            mapId = TextureUtil.uploadTextureImageAllocate(
                        TextureUtil.glGenTextures(),
                        ImageIO.read(new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + mapFormat + "/" + ID + ".png")),
                    false,
                    false);

            map = this;
        }

        public boolean isInside(int x, int y) {
            return (startX < x && x < endX) && (startY < y && y < endY);
        }

        public boolean isInside(double x, double y) {
            return (startX < x && x < endX) && (startY < y && y < endY);
        }

        public Pair<Float,Float> GetUV(float x, float y) {
            return new Pair<>(x-this.startX,y-this.startY);
        }
        public Pair<Double,Double> GetUV(double x, double y) {
            return new Pair<>(x-this.startX,y-this.startY);
        }
    }
}
