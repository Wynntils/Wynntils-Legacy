/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.Reference;
import com.wynntils.core.utils.MD5Verification;
import com.wynntils.webapi.WebReader;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapProfile {

    private static final File mapLocation = new File(Reference.MOD_STORAGE_ROOT, "map");

    String url;
    File mapFile;

    boolean downloadDirect = false;
    boolean readyToUse = false;

    int textureId = -20;

    double centerX = 0; double centerZ = 0;
    int imageWidth = 0; int imageHeight = 0;

    public MapProfile(String url, String name) {
        this.url = url; this.mapFile = new File(mapLocation, name + ".png");

        if(!mapFile.exists()) downloadDirect = true;
    }

    public void updateMap() {
        try {
            WebReader reader = new WebReader(url);

            centerX = Double.valueOf(reader.get("CenterX"));
            centerZ = Double.valueOf(reader.get("CenterZ"));
            if (!downloadDirect) {
                if (new MD5Verification(mapFile).equals(reader.get("MD5"))) {
                    readyToUse = true;
                    return;
                }
            }

            DownloaderManager.queueDownload("Wynntils Map", reader.get("DownloadLocation"), mapLocation, DownloadAction.SAVE, c -> readyToUse = c);
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    private void setTexture() throws Exception {
        BufferedImage img = ImageIO.read(mapFile);
        imageHeight = img.getHeight(); imageWidth = img.getWidth();

        textureId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), ImageIO.read(mapFile), false, false);
    }

    public void bindTexture() throws Exception {
        if(!readyToUse) return;
        if(textureId == -20) setTexture();

        GlStateManager.bindTexture(textureId);
    }

    public float getTextureXPosition(double posX) {
        return (float)(posX - centerX + imageWidth);
    }

    public float getTextureZPosition(double posZ) {
        return (float)(posZ - centerZ + imageHeight);
    }

    public int getWorldXPosition(double textureX) {
        return (int) Math.round(textureX + centerX - imageWidth);
    }

    public int getWorldZPosition(double textureY) {
        return (int) Math.round(textureY + centerZ - imageHeight);
    }

    public boolean isReadyToUse() {
        return readyToUse;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

}
