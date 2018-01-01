package com.wynndevs.webapi.profiles.item;

import java.util.HashMap;

public class ItemGuessProfile {

    String range;
    HashMap<String, HashMap<String, String>> items = new HashMap<>();

    public ItemGuessProfile(String range) {
        this.range = range;
    }

    public String getRange() {
        return range;
    }

    public HashMap<String, HashMap<String, String>> getItems() {
        return items;
    }

    public void addItems(String part, HashMap<String, String> rarity) {
        items.put(part, rarity);
    }

}
