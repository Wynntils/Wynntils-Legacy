/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.textures;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Mappings {

    public static void loadMappings() {
        List<Class<?>> mappingClasses = new ArrayList<>();

        //mapping class to be registered
        mappingClasses.add(Mappings.Map.class);

        for(Class<?> clazz : mappingClasses) {
            String mainPath = Reference.MOD_ID + ":textures/" + clazz.getName().split("\\$")[1].toLowerCase() + "/data/";

            for(Field f : clazz.getDeclaredFields()) {
                try{
                    if(f.get(null) != null || !f.getType().isAssignableFrom(JsonObject.class)) continue;

                    ResourceLocation rc = new ResourceLocation(mainPath + f.getName() + ".json");
                    f.set(null, new JsonParser().parse(IOUtils.toString(Minecraft.getMinecraft().getResourceManager().getResource(rc).getInputStream())));

                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static class Map {

        public static JsonObject map_icons_mappings;

    }

}
