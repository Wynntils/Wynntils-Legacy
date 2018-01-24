package com.wynndevs.core.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigCategory {

    String name;
    HashMap <String, AdvancedField> values;
    ArrayList<ConfigCategory> subCategories = new ArrayList<>();
    ConfigCategory inheritance;

    public ConfigCategory(String name) {
        this.name = name; this.values = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, AdvancedField> getValues() {
        return values;
    }

    public ArrayList<ConfigCategory> getSubCategories() { return subCategories; }

    public void addValue(String key, boolean value, Field f, Object instance) {
        values.put(key, new AdvancedField(value, f, instance));
    }

    public void addSubCategory(ConfigCategory category) {
        subCategories.add(category);
    }

    public ConfigCategory setInheritance(ConfigCategory cfg) {
        inheritance = cfg;

        return this;
    }

    public ConfigCategory getInheritance() {
        return inheritance;
    }

    public static class AdvancedField {

        boolean value; Field f; Object instance;

        public AdvancedField(boolean value, Field f, Object instance) {
            this.value = value;
            this.f = f;
            this.instance = instance;
        }

        public boolean getValue() {
            return !value;
        }

        public Field getField() {
            return f;
        }

        public Object getInstance() {
            return instance;
        }

    }

}