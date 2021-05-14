/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.visual.instances;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SplashProfile {

    private static final File SPLASHES_LOCATION = new File(Reference.MOD_STORAGE_ROOT, "splashes");

    // data
    String url, name;
    File splashFile;

    // status
    boolean readyToUse = false;

    // properties
    int textureId = -1;
    int imageWidth = 0;
    int imageHeight = 0;

    public SplashProfile(String url) {
        this.url = url;

        String[] urlSplitted = url.split("/");
        this.name = urlSplitted[urlSplitted.length-1];

        this.splashFile = new File(SPLASHES_LOCATION, name);

        downloadSplash();
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
            Reference.LOGGER.info("Successfully loaded splash " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTexture() throws Exception {
        BufferedImage img = ImageIO.read(splashFile);
        imageHeight = img.getHeight(); imageWidth = img.getWidth();

        textureId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), img, false, false);
    }

    public void bindTexture() {
        if (!readyToUse) return;
        if (textureId == -1) {
            try {
                setTexture();
            }catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        GlStateManager.bindTexture(textureId);
    }

    public void downloadSplash() {
        if (splashFile.exists()) {
            setReadyToUse();
            return;
        }

        DownloaderManager.queueDownload("Class Selection Splash", url, SPLASHES_LOCATION, DownloadAction.SAVE, (c) -> {
            if (c) setReadyToUse();
        });
    }

}
