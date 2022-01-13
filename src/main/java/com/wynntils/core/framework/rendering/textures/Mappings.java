/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.rendering.textures;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.McIf;
import com.wynntils.Reference;

import net.minecraft.util.ResourceLocation;

public class Mappings {

    public static void loadMappings() {
        List<Class<?>> mappingClasses = new ArrayList<>();

        // mapping class to be registered
        mappingClasses.add(Mappings.Map.class);

        for (Class<?> clazz : mappingClasses) {
            String mainPath = Reference.MOD_ID + ":textures/" + clazz.getName().split("\\$")[1].toLowerCase() + "/data/";

            for (Field f : clazz.getDeclaredFields()) {
                try {
                    if (!f.getType().isAssignableFrom(JsonObject.class)) continue;

                    ResourceLocation rc = new ResourceLocation(mainPath + f.getName() + ".json");
                    f.set(null, new JsonParser().parse(IOUtils.toString(McIf.mc().getResourceManager().getResource(rc).getInputStream(), StandardCharsets.UTF_8)));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static class Map {

        public static JsonObject map_icons_mappings;

    }

}
