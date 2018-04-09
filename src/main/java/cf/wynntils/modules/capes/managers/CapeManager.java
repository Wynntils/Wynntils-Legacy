package cf.wynntils.modules.capes.managers;

import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CapeManager {

    public static ArrayList<String> users = new ArrayList<>();

    public static void updateCapes() {
        if(WebManager.apiUrls == null) return;
        try{
            users = WebManager.getAllUsersWithCapes();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void downloadCape(String uuid) {
        if(users.size() <= 0) {
            return;
        }
        if ((uuid != null) && (!uuid.isEmpty()) && users.contains(uuid.replace("-", ""))) {
            String url = WebManager.apiUrls.get("Capes") + "/user/" + uuid.replace("-", "");
            ResourceLocation rl = new ResourceLocation("wynntils:capes/" + uuid.replace("-", ""));

            IImageBuffer ibuffer = new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    return formatCape(image);
                }

                @Override
                public void skinAvailable() { }
            };

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ImageDownloader textureCape = new ImageDownloader(null, url, ibuffer);

            textureManager.loadTexture(rl, textureCape);
        }
    }

    public static BufferedImage formatCape(BufferedImage img) {
        int imageWidth = 64;
        int imageHeight = 32;

        BufferedImage finalImg = new BufferedImage(imageWidth, imageHeight, 2);
        Graphics g = finalImg.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return finalImg;
    }

}
