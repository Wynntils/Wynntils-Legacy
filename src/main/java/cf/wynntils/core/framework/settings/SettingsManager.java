package cf.wynntils.core.framework.settings;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SettingsManager {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
            );
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

        mapper.writeValue(f, obj);
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

        return mapper.readValue(f, obj.getClass());
    }

}
