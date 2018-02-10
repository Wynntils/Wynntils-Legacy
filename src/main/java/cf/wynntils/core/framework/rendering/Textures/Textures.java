package cf.wynntils.core.framework.rendering.Textures;

import cf.wynntils.Reference;
import net.minecraft.util.ResourceLocation;

public class Textures {
    //public static AssetsTexture exampleTexture = new AssetsTexture(new ResourceLocation("resource location with a texture part at [0,0 => 25,50]"),0,0,25,50);
    public static void loadTextures() {
        overlay_bars = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/gui/overlay_bars.png"));
    }
    public static AssetsTexture overlay_bars;
}
