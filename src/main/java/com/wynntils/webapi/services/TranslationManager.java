package com.wynntils.webapi.services;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

public class TranslationManager {
    /**
     * Get a TranslationService.
     *
     * @param service An enum describing which translation service is requested.
     * @return An instance of the selected translation service, or the dummy (no-op) translation
     * service if no matching service can be created.
     */
    public static TranslationService getService(TranslationServices service) {
        try {
            Constructor<? extends TranslationService> ctor = service.serviceClass.getConstructor();
            return ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return new DummyTranslationService();
    }

    public enum TranslationServices {
        DUMMY(DummyTranslationService.class),
        GOOGLEAPI(GoogleApiTranslationService.class);

        private Class<? extends TranslationService> serviceClass;

        TranslationServices(Class<? extends TranslationService> serviceClass) {
            this.serviceClass = serviceClass;
        }
    }

    public static class DummyTranslationService implements TranslationService {
        @Override
        public void translate(String message, String toLanguage, Consumer<String> handleTranslation) {
            handleTranslation.accept(message);
        }
    }
}
