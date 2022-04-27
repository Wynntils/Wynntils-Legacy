/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.rendering.textures;

import com.wynntils.Reference;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Textures {

    public static void loadTextures() {
        List<Class<?>> textureClasses = new ArrayList<>();

        textureClasses.add(Masks.class);
        textureClasses.add(Overlays.class);
        textureClasses.add(Particles.class);
        textureClasses.add(UIs.class);
        textureClasses.add(Map.class);
        textureClasses.add(World.class);

        for (Class<?> clazz : textureClasses) {
            String path = Reference.MOD_ID + ":textures/" + clazz.getName().split("\\$")[1].toLowerCase(Locale.ROOT) + "/";
            for (Field f : clazz.getDeclaredFields()) {
                try {
                    if (f.getType().isAssignableFrom(AssetsTexture.class)) {
                        String file = path + f.getName() + ".png";
                        f.set(null, new AssetsTexture(new ResourceLocation(file)));
                    }
                } catch (Exception e) {
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
        public static AssetsTexture hotbar;
        public static AssetsTexture toast;
        public static AssetsTexture professions;
    }

    public static class Particles {
        public static AssetsTexture snow;
        public static AssetsTexture flame;
    }

    public static class Map {
        public static AssetsTexture wynn_map_textures;
        public static AssetsTexture paper_map_textures;
        public static AssetsTexture map_icons;
        public static AssetsTexture gilded_map_textures;
        public static AssetsTexture map_pointers;
        public static AssetsTexture map_options;
        public static AssetsTexture full_map;
        public static AssetsTexture map_territory_info;
        public static AssetsTexture map_buttons;
    }

    public static class UIs {
        public static AssetsTexture main_menu;

        public static AssetsTexture book;
        public static AssetsTexture book_scrollarea_settings;

        public static AssetsTexture changelog;

        public static AssetsTexture button_a;
        public static AssetsTexture button_b;
        public static AssetsTexture button_red_x;
        public static AssetsTexture button_scrollbar;

        public static AssetsTexture color_wheel;

        public static AssetsTexture hud_overlays;

        public static AssetsTexture rarity;

        public static AssetsTexture quest_book;

        public static AssetsTexture tab_overlay;

        public static AssetsTexture character_selection;

        public static AssetsTexture profession_icons;
    }

    public static class World {
        public static AssetsTexture path_arrow;
        public static AssetsTexture leaderboard_badges;
        public static AssetsTexture solid_color;
    }
}
