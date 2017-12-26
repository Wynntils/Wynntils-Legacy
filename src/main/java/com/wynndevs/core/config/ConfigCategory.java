package com.wynndevs.core.config;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigCategory {

    String name;
    HashMap<String, Boolean> values = new HashMap<>();
    ArrayList<ConfigCategory> subCategories = new ArrayList<>();

    public ConfigCategory(String name) {
        this.name = name; this.values = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Boolean> getValues() {
        return values;
    }

    public void addValue(String key, Boolean value) {
        values.put(key, value);
    }

    public void addSubCategory(ConfigCategory category) {
        subCategories.add(category);
    }

}