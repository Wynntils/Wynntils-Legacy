/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.webapi.services;

import com.wynntils.core.utils.Utils;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

public class TranslationManager {

    /**
     * Get a TranslationService.
     *
     * @param service An enum describing which translation service is requested.
     * @return An instance of the selected translation service, or null on failure
     */
    public static TranslationService getService(TranslationServices service) {
        try {
            Constructor<? extends TranslationService> ctor = service.serviceClass.getConstructor();
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void init() {
        CachingTranslationService.loadTranslationCache();
    }

    public static void shutdown() {
        CachingTranslationService.saveTranslationCache();
    }

    public enum TranslationServices {
        GOOGLEAPI(GoogleApiTranslationService.class),
        PIGLATIN(PigLatinTranslationService.class);

        private final Class<? extends TranslationService> serviceClass;

        TranslationServices(Class<? extends TranslationService> serviceClass) {
            this.serviceClass = serviceClass;
        }
    }

    /**
     * A demo "translation" service that ignores the selected language, and always translates
     * to "pig latin". Use for test purposes, or for hours of enjoyment for the simple-minded. ;-)
     */
    public static class PigLatinTranslationService implements TranslationService {
        @Override
        public void translate(String message, String toLanguage, Consumer<String> handleTranslation) {
            StringBuilder latinString = new StringBuilder();
            for (String word : message.split("\\s")) {
                if ("AEIOUaeiou".indexOf(word.charAt(0)) != -1) {
                    latinString.append(word).append("ay ");
                } else {
                    latinString.append(word.substring(1)).append(word.charAt(0)).append("ay ");
                }
            }
            Utils.runAsync(() -> handleTranslation.accept(latinString.toString()));
        }
    }

}
