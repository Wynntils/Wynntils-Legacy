package com.wynndevs.core.config;

import com.wynndevs.modules.expansion.options.Config;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigCategory {

    String name;
    HashMap<String, Boolean> values = new HashMap<>();
    ArrayList<ConfigCategory> subCategories = new ArrayList<>();
    ConfigCategory inheritance;

    public ConfigCategory(String name) {
        this.name = name; this.values = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Boolean> getValues() {
        return values;
    }

    public ArrayList<ConfigCategory> getSubCategories() { return subCategories; }

    public void addValue(String key, Boolean value) {
        values.put(key, value);
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


}