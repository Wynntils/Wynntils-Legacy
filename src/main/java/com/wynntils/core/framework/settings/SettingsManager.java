/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.ModuleContainer;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SettingsManager {

    private static Gson gson = null;

    static {
        gson = new GsonBuilder()
            .setPrettyPrinting()
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

        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
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

        InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        return gson.fromJson(new JsonReader(reader), obj.getClass());
    }

}
