package cf.wynntils.modules.capes.managers;

import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CapeManager {

    public static ArrayList<String> downloaded = new ArrayList<>();

    public static void downloadCape(UUID uuid) {
        if (!WebManager.isPremium(uuid) && !WebManager.isUser(uuid)) {
            return;
        }

        String url; ResourceLocation rl;
        if (uuid != null && WebManager.isPremium(uuid)) {
            url = WebManager.apiUrls.get("Capes") + "/user/" + uuid.toString().replace("-", "");
            rl = new ResourceLocation("wynntils:capes/" + uuid.toString().replace("-", ""));
        } else if(uuid != null && WebManager.isUser(uuid)) {
            url = WebManager.apiUrls.get("Capes") + "/user/default";
            rl = new ResourceLocation("wynntils:capes/default");
        } else {
            url = WebManager.apiUrls.get("Capes") + "/user/default";
            rl = new ResourceLocation("wynntils:capes/default");
        }

        //avoid extra downloading
        if(downloaded.contains(rl.getResourcePath())) { return; }
        downloaded.add(rl.getResourcePath());

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
