package cf.wynntils.core.framework.rendering.textures;

import cf.wynntils.Reference;
import net.minecraft.util.ResourceLocation;

public class Textures {
    /** Textures
     * In here, you can organize all the mod's textures.
     * Do NOT forget to initialize them in {loadTextures()}!
     *
     */
    public static void loadTextures() {
        Masks.full = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/masks/full.png"));
        Masks.circle = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/masks/circle.png"));

        Bars.health = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_health.png"));
        Bars.mana = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_mana.png"));
        Bars.exp = new AssetsTexture(new ResourceLocation(Reference.MOD_ID + ":textures/overlays/bars_exp.png"));
    }
    public static class Masks {
        public static AssetsTexture full;
        public static AssetsTexture circle;
    }
    public static class Bars {
        public static AssetsTexture health;
        public static AssetsTexture mana;
        public static AssetsTexture exp;
    }
}
