package cf.wynntils.core.framework.rendering.Textures;

import cf.wynntils.core.utils.GenericActionResult;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class AssetsTexture extends Texture {
    public ResourceLocation resourceLocation;

    public AssetsTexture(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        load();
    }

    @Override
    public GenericActionResult load() {
        if(loaded) return GenericActionResult.ISSUE;

        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
            BufferedImage img = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream());
            width = img.getWidth();
            height = img.getHeight();
            loaded = true;
        } catch(Exception e) {
            width = -1; height = -1;
            loaded = false;
            return GenericActionResult.ERROR;
        }
        return loaded ? GenericActionResult.SUCCESS : GenericActionResult.ERROR;
    }

    @Override
    public GenericActionResult unload() {
        if(!loaded) return GenericActionResult.ISSUE;
        Minecraft.getMinecraft().getTextureManager().deleteTexture(resourceLocation);
        loaded = false;
        return GenericActionResult.SUCCESS;
    }

    @Override
    public GenericActionResult bind() {
        if(!loaded) return GenericActionResult.ERROR;
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        return GenericActionResult.SUCCESS;
    }
}
