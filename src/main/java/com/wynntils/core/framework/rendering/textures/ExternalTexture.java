/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.textures;

import com.wynntils.core.framework.enums.ActionResult;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class ExternalTexture extends Texture {

    public int glID;
    public File file;

    public ExternalTexture(File file, boolean load) {
        this.file = file;
        if(load) load();
    }

    @Override
    public ActionResult load() {
        if (loaded) return ActionResult.ISSUE;
        if (!Files.exists(file.toPath())) return ActionResult.ERROR;

        try {
            BufferedImage img = ImageIO.read(file);
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
