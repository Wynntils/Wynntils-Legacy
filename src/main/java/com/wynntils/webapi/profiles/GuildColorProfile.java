package com.wynntils.webapi.profiles;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GuildColorProfile {
    String name;
    String prefix;
    String guildColor;

    public GuildColorProfile(String name, String prefix, String guildColor) {
        this.name = name;
        this.prefix = prefix;
        this.guildColor = guildColor;

    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getGuildColor() {
        return guildColor;
    }

    public static class GuildColorDeserializer implements JsonDeserializer<GuildColorProfile> {
        @Override
        public GuildColorProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject guildColorObject = json.getAsJsonObject();

            String name;
            if (guildColorObject.get("_id").isJsonNull()) name = "Unknown";
            else name = guildColorObject.get("_id").getAsString();

            String prefix;
            if (guildColorObject.get("prefix").isJsonNull()) prefix = "UNK";
            else prefix = guildColorObject.get("prefix").getAsString();

            String guildColor;
            if (guildColorObject.get("color").getAsString().isEmpty()) guildColor = null;
            else guildColor = guildColorObject.get("color").getAsString();

            return new GuildColorProfile(name, prefix, guildColor);
        }
    }
}
