/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public abstract class CachingTranslationService implements TranslationService {
    public static final File TRANSLATION_CACHE_ROOT = new File(Reference.MOD_STORAGE_ROOT, "translationcache");

    // Map language code (String) to a translation map (String -> String)
    private static Map<String, ConcurrentHashMap<String, String>> translationCaches = new HashMap<>();
    private static int counter;

    protected abstract void translateNew(String message, String toLanguage, Consumer<String> handleTranslation);

    protected void saveTranslation(String toLanguage, String message, String translatedMessage) {
        Map<String, String> translationCache = translationCaches.get(toLanguage);
        translationCache.put(message, translatedMessage);
        if (++counter % 16 == 0) {
            // Persist translation cache in background
            Utils.runAsync(CachingTranslationService::saveTranslationCache);
        }
    }

    @Override
    public void translate(String message, String toLanguage, Consumer<String> handleTranslation) {
        if (message == null || message.isEmpty()) {
            Utils.runAsync(() -> handleTranslation.accept(""));
            return;
        }

        Map<String, String> translationCache = translationCaches.computeIfAbsent(toLanguage, k -> new ConcurrentHashMap<>());
        String cachedTranslation = translationCache.get(message);
        if (cachedTranslation != null) {
            Utils.runAsync(() -> handleTranslation.accept(cachedTranslation));
            return;
        }

        translateNew(message, toLanguage, handleTranslation);
    }

    public static synchronized void saveTranslationCache() {
        try {
            File f = new File(TRANSLATION_CACHE_ROOT, "translations.json");
            Gson gson = new Gson();
            String json = gson.toJson(translationCaches);
            FileUtils.writeStringToFile(f, json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void loadTranslationCache() {
        File f = new File(TRANSLATION_CACHE_ROOT, "translations.json");

        if (!f.exists()) {
            translationCaches = new HashMap<>();
            return;
        }

        try {
            String json = FileUtils.readFileToString(f, "UTF-8");

            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, ConcurrentHashMap<String, String>>>(){}.getType();
            translationCaches = gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
