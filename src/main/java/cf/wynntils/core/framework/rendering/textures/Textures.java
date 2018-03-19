package cf.wynntils.core.framework.rendering.textures;

import cf.wynntils.Reference;
import net.minecraft.util.ResourceLocation;

public class Textures {
    //public static AssetsTexture exampleTexture = new AssetsTexture(new ResourceLocation("resource location"));
    public static void loadTextures() {
        Bars.health = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_health.png"));
        Bars.mana = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_mana.png"));
        Bars.exp = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_exp.png"));

        Masks.empty = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/masks/empty.png"));
        Masks.full = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/masks/full.png"));
        Masks.circle = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/masks/circle.png"));
    }
    public static class Masks {
        public static AssetsTexture empty,full,circle;
    }
    public static class Bars {
        public static AssetsTexture health;
        public static AssetsTexture mana;
        public static AssetsTexture exp;
    }
}
