/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.utils.helpers.MD5Verification;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.WebReader;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapProfile {

    private static final File MAP_LOCATION = new File(Reference.MOD_STORAGE_ROOT, "map");

    String url;
    File mapFile;

    boolean downloadDirect = false;
    boolean readyToUse = false;

    int textureId = -20;

    double centerX = 0; double centerZ = 0;
    int imageWidth = 0; int imageHeight = 0;

    public MapProfile(String url, String name) {
        this.url = url; this.mapFile = new File(MAP_LOCATION, name + ".png");

        if (!mapFile.exists()) downloadDirect = true;
    }

    public void updateMap() {
        if (url == null) {
            WebReader apiUrls = WebManager.getApiUrls();
            if (apiUrls != null) {
                url = apiUrls.get("MainMap");
            }
        }

        RequestHandler handler = new RequestHandler();
        handler.addRequest(new Request(url, "main_map.info")
            .cacheTo(new File(MAP_LOCATION, "main-map.txt"))
            .handleWebReader(reader -> {
                centerX = Double.parseDouble(reader.get("CenterX"));
                centerZ = Double.parseDouble(reader.get("CenterZ"));
                if (!downloadDirect) {
                    if (new MD5Verification(mapFile).equals(reader.get("MD5"))) {
                        setReadyToUse();
                        return true;
                    }
                }

                DownloaderManager.queueDownload("Wynntils Map", reader.get("DownloadLocation"), MAP_LOCATION, DownloadAction.SAVE, success -> {
                    if (success) setReadyToUse();
                });
                return true;
            })
        );
        handler.dispatchAsync();
    }

    private void setReadyToUse() {
        // make sure this is being called from the main thread
        if (!McIf.mc().isCallingFromMinecraftThread()) {
            McIf.mc().addScheduledTask(this::setReadyToUse);
            return;
        }
        readyToUse = true;

        // this allocates the texture to the OpenGL container
        // calling it here avoids the game stuttering while entering the world
        try {
            setTexture();
            Reference.LOGGER.info("Successfully loaded map " + mapFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTexture() throws Exception {
        BufferedImage img = ImageIO.read(mapFile);
        imageHeight = img.getHeight(); imageWidth = img.getWidth();

        textureId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), img, false, false);
    }

    public void bindTexture() throws Exception {
        if (!readyToUse) return;
        if (textureId == -20) setTexture();

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
