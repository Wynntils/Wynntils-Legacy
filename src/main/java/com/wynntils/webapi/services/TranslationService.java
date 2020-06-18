package com.wynntils.webapi.services;

import java.util.function.Consumer;

public interface TranslationService {

    /**
     * Translate a message from English to the target language, as described by a two-letter 639-1
     * language code. The response can be executed asynchronously. The message can contain html,
     * but no Minecraft formatting.
     *
     * @param message           The message to translate.
     * @param toLanguage        The target language code
     * @param handleTranslation Handler for the translation. The argument is the translated string.
     */
    void translate(String message, String toLanguage, Consumer<String> handleTranslation);
}
