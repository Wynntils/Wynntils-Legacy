/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.textures;

import com.wynntils.core.framework.enums.ActionResult;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class RemoteTexture extends Texture {
    //TODO check if this is working
    public int glID;
    public URL url;

    public RemoteTexture(URL url, boolean load) {
        this.url = url;
        if(load) load();
    }

    @Override
    public ActionResult load() {
        if (loaded) return ActionResult.ISSUE;

        try {
            BufferedImage img = ImageIO.read(url);
            this.glID = TextureUtil.glGenTextures();
            width = img.getWidth();
            height = img.getHeight();
            TextureUtil.uploadTextureImageAllocate(glID,img,false,false);
            loaded = true;
            return ActionResult.SUCCESS;
        } catch (Exception e) {
            width = -1;
            height = -1;
            glID = -1;
            loaded = false;
            return ActionResult.ERROR;
        }
    }

    @Override
    public ActionResult unload() {
        if(!loaded) return ActionResult.ISSUE;
        TextureUtil.deleteTexture(glID);
        loaded = false;
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult bind() {
        if(!loaded) return ActionResult.ERROR;
        GlStateManager.bindTexture(glID);
        return ActionResult.SUCCESS;
    }
}
