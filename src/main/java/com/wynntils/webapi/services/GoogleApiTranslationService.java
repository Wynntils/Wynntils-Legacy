package com.wynntils.webapi.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A TranslationService that uses the free googleapi translation API. This service is free but is severly
 * restricted. There is a rate limit of about 100 messages per hour and IP address. This is typically
 * sufficient for NPCs translation, but not for general chat messages, at least not in chatty areas like Detlas.
 */
public class GoogleApiTranslationService implements TranslationService {
    private static final AtomicInteger requestNumber = new AtomicInteger();

    @Override
    public void translate(String message, String toLanguage, Consumer<String> handleTranslation) {
        try {
            String encodedMessage = URLEncoder.encode(message, "UTF-8");
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=" + toLanguage +
                    "&dt=t&q=" + encodedMessage;

            RequestHandler handler = new RequestHandler();
            handler.addAndDispatch(new Request(url, "translate-" + requestNumber.getAndIncrement())
                    .handleJsonArray(json -> {
                        StringBuilder builder = new StringBuilder();
                        JsonArray array = json.get(0).getAsJsonArray();
                        for (JsonElement elem : array) {
                            String part = elem.getAsJsonArray().get(0).getAsString();
                            builder.append(part);
                        }
                        handleTranslation.accept(builder.toString());
                        return true;
                    }), true);
        } catch (UnsupportedEncodingException e) {
            // ignore; cannot happen
        }
    }

}
