package com.wynntils.webapi.profiles;

import com.google.gson.*;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.StringUtils;

import java.lang.reflect.Type;

public class GuildColorProfile {
    String name;
    String prefix;
    CustomColor guildColor;

    public GuildColorProfile(String name, String prefix, CustomColor guildColor) {
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

    public CustomColor getGuildColor() {
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

            CustomColor guildColor;
            if (guildColorObject.get("color").getAsString().isEmpty()) guildColor = null;
            else {
                String guildColorString = guildColorObject.get("color").getAsString();
                if (guildColorString.length() == 7) guildColor =  StringUtils.colorFromHex(guildColorString);
                else guildColor = new CustomColor(CommonColors.WHITE);
            }

            return new GuildColorProfile(name, prefix, guildColor);
        }
    }
}
