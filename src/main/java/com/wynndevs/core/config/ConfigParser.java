package com.wynndevs.core.config;

import com.wynndevs.core.Reference;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ConfigParser {

    public static ArrayList<ConfigCategory> getMappedConfig(Class cl) {
        try{
            return categoryLoop(cl, "main", null);
        }catch (Exception ex) { return new ArrayList<>(); }
    }

    private static ArrayList<ConfigCategory> categoryLoop(Class cl, String main, Object instance) throws Exception {
        ArrayList<ConfigCategory> categories = new ArrayList<>();
        ConfigCategory category = new ConfigCategory(main);
        for(Field f : cl.getFields()) {
            GuiConfig a = f.getAnnotation(GuiConfig.class);
            if(a == null) {
                continue;
            }

            if(a.isInstance()) {
                categories.addAll(categoryLoop(f.get(null).getClass(), a.title(), f.get(null)));
                continue;
            }

            if(f.get(instance).getClass() == boolean.class || f.get(instance).getClass() == Boolean.class) {
                category.addValue(a.title(), f.getBoolean(instance));
                continue;
            }
        }
        categories.add(category);

        return categories;
    }

}
