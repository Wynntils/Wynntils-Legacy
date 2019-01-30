package cf.wynntils.core.framework.rendering.textures;

import cf.wynntils.Reference;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Textures {
    public static void loadTextures() {
        List<Class<?>> textureClasses = new ArrayList<>();


        textureClasses.add(Masks.class);
        textureClasses.add(Overlays.class);
        textureClasses.add(UIs.class);
        textureClasses.add(Map.class);


        for(Class<?> clazz : textureClasses) {
            String path = Reference.MOD_ID + ":textures/" + clazz.getName().split("\\$")[1].toLowerCase() + "/";
            for(Field f : clazz.getDeclaredFields()) {
                try {
                    if (f.get(null) == null && f.getType().isAssignableFrom(AssetsTexture.class)) {
                        String file = path + f.getName() + ".png";
                        f.set(null, new AssetsTexture(new ResourceLocation(file)));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Masks {
        public static AssetsTexture full;
        public static AssetsTexture circle;
        public static AssetsTexture vignette;
    }

    public static class Overlays {
        public static AssetsTexture bars_health;
        public static AssetsTexture bars_mana;
        public static AssetsTexture bars_exp;
        public static AssetsTexture bars_bubbles;
        public static AssetsTexture toast;
    }

    public static class Map {
        public static AssetsTexture wynn_map_textures;
        public static AssetsTexture paper_map_textures;
        public static AssetsTexture pointer;
        public static AssetsTexture map_icons;
        public static AssetsTexture gilded_map_textures;
    }

    public static class UIs {
        public static AssetsTexture book;
        public static AssetsTexture book_scrollarea_settings;

        public static AssetsTexture button_a;
        public static AssetsTexture button_b;
        public static AssetsTexture button_red_x;
        public static AssetsTexture button_scrollbar;

        public static AssetsTexture hud_overlays;

        public static AssetsTexture rarity;

        public static AssetsTexture quest_book;
    }
}
