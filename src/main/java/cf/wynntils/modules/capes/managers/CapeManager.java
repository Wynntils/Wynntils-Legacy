package cf.wynntils.modules.capes.managers;

import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.WebReader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CapeManager {

    public static ArrayList<String> users = new ArrayList<>();
    private static WebReader reader;

    public static void updateCapes() {
        if(WebManager.apiUrls == null) return;
        String url = WebManager.apiUrls.get("Capes");
        try{
            reader = new WebReader(url + "/config");

            users = reader.getList("AllowedUsers");
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void downloadCape(String uuid) {
        if(users.size() <= 0) {
            return;
        }
        if ((uuid != null) && (!uuid.isEmpty()) && users.contains(uuid.replace("-", ""))) {
            String url = WebManager.apiUrls.get("Capes") + "/user/" + uuid.replace("-", "");
            ResourceLocation rl = new ResourceLocation("wynntils:capes/" + uuid.replace("-", ""));

            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ImageDownloader textureCape = new ImageDownloader(null, url);

            textureManager.loadTexture(rl, textureCape);
        }
    }

}
