/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.managers;

import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

public class CapeManager {

    public static ArrayList<String> downloaded = new ArrayList<>();

    public static void downloadCape(UUID uuid) {
        if (!WebManager.hasCape(uuid) && !WebManager.hasElytra(uuid)) {
            return;
        }

        String url; ResourceLocation rl;
        url = WebManager.apiUrls.get("Capes") + "/user/" + uuid.toString().replace("-", "");
        rl = new ResourceLocation("wynntils:capes/" + uuid.toString().replace("-", ""));

        //avoid extra downloading
        if(downloaded.contains(rl.getPath())) { return; }
        downloaded.add(rl.getPath());

        IImageBuffer ibuffer = new IImageBuffer() {
            public BufferedImage parseUserSkin(BufferedImage image) {
                return formatCape(image);
            }
            public void skinAvailable() { }
        };

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ImageDownloader textureCape = new ImageDownloader(null, url, ibuffer);

        textureManager.loadTexture(rl, textureCape);
    }

    public static BufferedImage formatCape(BufferedImage img) {
        int imageWidth = 128;
        int imageHeight = 64;

        BufferedImage finalImg = new BufferedImage(imageWidth, imageHeight, 2);
        Graphics g = finalImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return finalImg;
    }

}
