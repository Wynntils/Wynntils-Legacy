package cf.wynntils.core.framework.settings;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Modifier;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SettingsManager {

    private static Gson gson = null;

    static {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .addSerializationExclusionStrategy(new Exclude())
            .create();
    }

    public static void saveSettings(ModuleContainer m, SettingsHolder obj) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null)
            if(!(obj instanceof Overlay))
                return;


        File f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs");
        f.mkdirs();

        f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs",
                m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config");
        if(!f.exists())
            f.createNewFile();
        
        FileWriter fileWriter = new FileWriter(f);
        gson.toJson(obj, fileWriter);
        fileWriter.close();
    }

    public static SettingsHolder getSettings(ModuleContainer m, SettingsHolder obj) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null)
            if(!(obj instanceof Overlay))
                return obj;

        File f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs");
        f.mkdirs();

        f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs",
                m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config");
        if(!f.exists()) {
            f.createNewFile();
            saveSettings(m, obj);
            return obj;
        }

        return gson.fromJson(new JsonReader(new FileReader(f)), obj.getClass());
    }
    
    private static class Exclude implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return !f.hasModifier(Modifier.PUBLIC);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
        
    }

}
