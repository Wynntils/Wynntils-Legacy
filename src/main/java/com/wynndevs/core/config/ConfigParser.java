package com.wynndevs.core.config;

import java.lang.reflect.Field;

public class ConfigParser {

    public static ConfigCategory getMappedConfig(Class cl) {
        try{
            return categoryLoop(cl, "main", null);
        }catch (Exception ex) { return null; }
    }

    private static ConfigCategory categoryLoop(Class cl, String main, Object instance) throws Exception {
        ConfigCategory category = new ConfigCategory(main);
        for(Field f : cl.getFields()) {
            GuiConfig a = f.getAnnotation(GuiConfig.class);
            if(a == null) {
                continue;
            }

            if(a.isInstance()) {
                category.addSubCategory(categoryLoop(f.get(null).getClass(), a.title(), f.get(null)));
                continue;
            }

            if(f.get(instance).getClass() == boolean.class || f.get(instance).getClass() == Boolean.class) {
                category.addValue(a.title(), f.getBoolean(instance));
                continue;
            }
        }

        return category;
    }

}
