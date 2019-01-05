package cf.wynntils.core.framework.settings;

import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 23/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SettingsContainer {

    String displayPath;
    SettingsHolder holder;
    ArrayList<Field> fields = new ArrayList<>();
    ModuleContainer m;

    public SettingsContainer(ModuleContainer m, SettingsHolder holder) {
        this.holder = holder;
        this.m = m;
        this.displayPath = holder instanceof Overlay ? m.getInfo().displayName() + "/" + ((Overlay) holder).displayName : holder.getClass().getAnnotation(SettingsInfo.class).displayPath().replaceFirst("^Main",m.getInfo().displayName());


        for(Field f : holder.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                fields.add(f);
            }
        }

        try {
            tryToLoad();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tryToLoad() throws Exception {
        updateValues(SettingsManager.getSettings(m, holder));
    }

    public HashMap<Field, Object> getValues() throws Exception {
        HashMap<Field, Object> values = new HashMap<>();

        for(Field f : fields) {
            values.put(f, f.get(holder));
        }

        return values;
    }

    public void saveSettings() throws Exception {
        SettingsManager.saveSettings(m, holder);
    }

    public void setValue(Field f, Object value) throws Exception {
        setValue(f,value,true);
    }

    public void setValue(Field f, Object value, boolean save) throws Exception {
        f.set(holder, value);
        holder.onSettingChanged(f.getName());

        if(save)
            SettingsManager.saveSettings(m, holder);
    }

    public void updateValues(SettingsHolder newH) throws Exception {
        boolean save = false;

        ArrayList<String> fieldsName = new ArrayList<>();
        for(Field f2 : fields) {
            fieldsName.add(f2.getName());
        }

        for(Field f : newH.getClass().getDeclaredFields()) {
            if(!fieldsName.contains(f.getName())) {
                save = true;
                continue;
            }
            System.out.println("Load " + newH.getClass().toString() + ", " + f.getName());
            setValue(f, f.get(newH), false);
        }

        if(save)
            saveSettings();
    }

    public void resetValue(Field field) throws Exception {
        field.set(holder,field.get(holder.getClass().getConstructor().newInstance()));
    }

    public void resetValues() throws Exception {
        for(Field field : fields)
            field.set(holder,field.get(holder.getClass().getConstructor().newInstance()));
    }

    public String getDisplayPath() {
        return this.displayPath;
    }

    public SettingsHolder getHolder() { return holder; }
}
