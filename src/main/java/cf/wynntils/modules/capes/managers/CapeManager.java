package cf.wynntils.modules.capes.managers;

import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CapeManager {

    public static void downloadCape(UUID uuid) {
        if (!WebManager.isPremium(uuid) && !WebManager.isUser(uuid)) {
            return;
        }

        String url = null;
        ResourceLocation rl = null;
        if (uuid != null) {
            url = WebManager.apiUrls.get("Capes") + "/user/" + uuid.toString().replace("-", "");
            rl = new ResourceLocation("wynntils:capes/" + uuid.toString().replace("-", ""));
        } else {
            url = WebManager.apiUrls.get("Capes") + "/user/default";
            rl = new ResourceLocation("wynntils:capes/default");
        }

        IImageBuffer ibuffer = new IImageBuffer() {
            @Override
            public BufferedImage parseUserSkin(BufferedImage image) {
                return formatCape(image);
            }

            @Override
            public void skinAvailable() {
            }
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
