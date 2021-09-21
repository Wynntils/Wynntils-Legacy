/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.rendering.textures;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.ActionResult;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class AssetsTexture extends Texture {

    public ResourceLocation resourceLocation;

    public AssetsTexture(ResourceLocation resourceLocation) { this(resourceLocation, true); }

    public AssetsTexture(ResourceLocation resourceLocation, boolean load) {
        this.resourceLocation = resourceLocation;
        if (load) load();
    }

    @Override
    public ActionResult load() {
        if (loaded) return ActionResult.ISSUE;

        try {
            McIf.mc().getTextureManager().bindTexture(resourceLocation);
            BufferedImage img = ImageIO.read(McIf.mc().getResourceManager().getResource(resourceLocation).getInputStream());
            width = img.getWidth();
            height = img.getHeight();
            loaded = true;
            return ActionResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            width = -1; height = -1;
            loaded = false;
            return ActionResult.ERROR;
        }
    }

    @Override
    public ActionResult unload() {
        if (!loaded) return ActionResult.ISSUE;

        McIf.mc().getTextureManager().deleteTexture(resourceLocation);
        loaded = false;
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult bind() {
        if (!loaded) return ActionResult.ERROR;

        McIf.mc().getTextureManager().bindTexture(resourceLocation);
        return ActionResult.SUCCESS;
    }

}
