package cf.wynntils.core.framework.settings;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import cf.wynntils.core.utils.ObjectHelper;

import java.io.*;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SettingsManager {

    public static void saveSettings(ModuleContainer m, SettingsHolder obj) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null) {
            return;
        }

        File f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs");
        f.mkdirs();

        f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs", m.getInfo().name() + "-" + info.name() + ".bcfg");
        if(!f.exists()) {
            f.createNewFile();
        }

        ObjectHelper.objToFile(f, obj);
    }

    public static SettingsHolder getSettings(ModuleContainer m, SettingsHolder obj) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null) {
            return obj;
        }

        File f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs");
        f.mkdirs();

        f = new File(Reference.MOD_STORAGE_ROOT + File.separator + "configs", m.getInfo().name() + "-" + info.name() + ".bcfg");
        if(!f.exists()) {
            f.createNewFile();
            saveSettings(m, obj);
            return obj;
        }

        return (SettingsHolder) ObjectHelper.fileToObj(f);
    }

}
