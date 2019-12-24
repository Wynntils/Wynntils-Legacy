/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class SettingsContainer {

    String displayPath;
    SettingsHolder holder;
    List<SettingField> fields = new ArrayList<>();
    ModuleContainer m;

    SettingsHolder fromCloud = null;

    public SettingsContainer(ModuleContainer m, SettingsHolder holder) {
        this.holder = holder;
        this.m = m;
        this.displayPath = holder instanceof Overlay ? m.getInfo().displayName() + "/" + ((Overlay) holder).displayName : holder.getClass().getAnnotation(SettingsInfo.class).displayPath().replaceFirst("^Main", m.getInfo().displayName());

        for (Class<?> clazz = holder.getClass(); SettingsHolder.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            for (Field f : (Reference.developmentEnvironment ? clazz.getDeclaredFields() : clazz.getFields())) {
                if (Modifier.isStatic(f.getModifiers())) continue;

                String name = null;

                if (Reference.developmentEnvironment) {
                    name = f.getDeclaringClass().getName() + "$" + f.getName();
                    if (!Modifier.isPublic(f.getModifiers())) {
                        f.setAccessible(true);
                        if (f.getAnnotation(Setting.class) != null) {
                            Reference.LOGGER.error("Field " + name + " is a private @Setting, which will not be recognized");
                        }
                    }
                }

                SettingField sf = new SettingField(f);
                if (sf.info != null) {
                    fields.add(sf);
                }

                if (Reference.developmentEnvironment && !f.getName().equals("INSTANCE")) {
                    if (sf.info == null && !Modifier.isTransient(f.getModifiers())) {
                        Reference.LOGGER.error("Field " + name + " is not a @Setting but is also not transient");
                    } else if (sf.info != null && Modifier.isTransient(f.getModifiers())) {
                        Reference.LOGGER.error("Field " + name + " is a @Setting but is also transient");
                    }
                }
            }
        }

        try {
            fromCloud = SettingsManager.getCloudSettings(m, holder);
        } catch (Exception ex) { ex.printStackTrace(); }

        try {
            tryToLoad();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tryToLoad() throws IOException {
        if (fromCloud == null) {
            updateValues(SettingsManager.getSettings(m, holder, this));
            return;
        }

        updateValues(fromCloud);  // this makes the synchronization
    }

    public Map<SettingField, Object> getValues() {
        Map<SettingField, Object> values = new HashMap<>();

        for (SettingField f : fields) {
            values.put(f, f.get(holder));
        }

        return values;
    }

    public void saveSettings() throws IOException {
        SettingsManager.saveSettings(m, holder, false);
    }

    public String getSaveFile() {
        SettingsInfo info = holder.getClass().getAnnotation(SettingsInfo.class);
        if (info == null) return null;
        return m.getInfo().name() + "-" + (holder instanceof Overlay ? "overlay_" + ((Overlay)holder).displayName.toLowerCase(Locale.ROOT).replace(' ', '_') : info.name()) + ".config";
    }

    public void setValue(Field f, Object value) throws IOException {
        setValueWithoutSaving(f, value);

        SettingsManager.saveSettings(m, holder, false);
    }

    public void setValueWithoutSaving(Field f, Object value) {
        try {
            f.set(holder, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        holder.onSettingChanged(f.getName());
    }

    public void updateValues(SettingsHolder newH) throws IOException {
        for (Class<?> clazz = newH.getClass(); SettingsHolder.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            for (Field f : clazz.getFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;

                SettingField sf = new SettingField(f);
                if (sf.info == null) continue;

                Object newValue = sf.get(newH);
                if (newValue != null) {
                    setValueWithoutSaving(f, newValue);
                }

            }
        }

        SettingsManager.saveSettings(m, holder, true);
    }

    public void onCreateConfig() {
        for (Class<?> clazz = holder.getClass(); SettingsHolder.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
            for (Field f : clazz.getFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;

                if (f.getAnnotation(Setting.class) != null) {
                    getConfigFromCloud(f);
                }
            }
        }
    }

    public boolean getConfigFromCloud(Field f) {
        if (fromCloud == null) return false;

        Setting c = f.getAnnotation(Setting.class);
        if (c == null || !c.upload()) return false;

        Object value;
        try {
            value = f.get(fromCloud);
        } catch (IllegalAccessException e) {
            return false;
        }
        setValueWithoutSaving(f, value);
        Reference.LOGGER.warn("Loaded configuration " + f.getName() + " from cloud!");
        return true;
    }

    private SettingsHolder constructResetInstance() {
        try {
            return holder.getClass().getConstructor().newInstance();
        } catch (Exception e) {
            Reference.LOGGER.error("new SettingsHolder could not be constructed for class " + holder.getClass());
            e.printStackTrace();
            return null;
        }
    }

    public void resetValue(SettingField field) {
        SettingsHolder reset = constructResetInstance();
        if (reset != null) {
            try {
                field.field.set(holder, field.get(reset));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetValues() {
        SettingsHolder defaultInstance = constructResetInstance();
        if (defaultInstance == null) return;
        for (SettingField field : fields) {
            try {
                field.field.set(holder, field.get(defaultInstance));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isReset() {
        SettingsHolder resetInstance = constructResetInstance();
        if (resetInstance == null) return false;
        for (SettingField field : fields) {
            Object resetValue;
            Object heldValue;
            resetValue = field.get(resetInstance);
            heldValue = field.get(holder);
            if (!Objects.deepEquals(resetValue, heldValue)) {
                return false;
            }
        }
        return true;
    }

    public String getDisplayPath() {
        return this.displayPath;
    }

    public SettingsHolder getHolder() { return holder; }

    public static class SettingField {
        public final Field field;
        public transient final Setting info;

        public SettingField(Field field) {
            this.field = field;
            this.info = field == null ? null : field.getAnnotation(Setting.class);
        }

        public Object get(Object settingHolder) {
            try {
                return field.get(settingHolder);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof SettingField)) return false;
            return field.equals(((SettingField) other).field);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(field);
        }
    }

}
