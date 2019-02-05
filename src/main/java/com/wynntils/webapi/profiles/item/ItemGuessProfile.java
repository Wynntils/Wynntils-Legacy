/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

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
    
    public static class ItemGuessDeserializer implements JsonDeserializer<HashMap<?, ?>> {

        @Override
        public HashMap<String, ItemGuessProfile> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            HashMap<String, ItemGuessProfile> hashMap = new HashMap<>();
            for (Entry<String, JsonElement> itemGuesses : jsonObject.entrySet()) {
                ItemGuessProfile itemGuessProfile = new ItemGuessProfile(itemGuesses.getKey());
                Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
                }.getType();
                Gson gson = new Gson();
                itemGuessProfile.items.putAll(gson.fromJson(itemGuesses.getValue(), type));
                hashMap.put(itemGuesses.getKey(), itemGuessProfile);
            }
            return hashMap;
        }
        
    }

}
