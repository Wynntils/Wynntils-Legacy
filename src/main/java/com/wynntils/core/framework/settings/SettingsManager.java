/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.settings;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import com.wynntils.modules.map.instances.PathWaypointProfile;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class SettingsManager {

    private static final Gson gson;
    private static final File configFolder = new File(Reference.MOD_STORAGE_ROOT, "configs");

    static {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(CustomColor.class, new CustomColorSerializer())
            .registerTypeAdapter(PathWaypointProfile.class, new PathWaypointProfile.Serializer())
            .create();

        configFolder.mkdirs();  // if the config folder doesn't exists create the directory
    }

    public static void saveSettings(ModuleContainer m, SettingsHolder obj, boolean localOnly) throws IOException {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if (info == null)
            if (!(obj instanceof Overlay))
                return;

        File f = new File(configFolder, Minecraft.getMinecraft().getSession().getPlayerID());
        if (!f.exists()) f.mkdirs();  // check if the users folder exists

        f = new File(f, m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase(Locale.ROOT).replace(' ', '_') : info.name()) + ".config");
        if (!f.exists()) f.createNewFile();  // create the config file if it doesn't exists

        // HeyZeer0: Writing to file
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
        gson.toJson(obj, fileWriter);
        fileWriter.close();

        // HeyZeer0: Uploading file
        if (!localOnly && WebManager.getAccount() != null) WebManager.getAccount().uploadConfig(f);
    }

    public static SettingsHolder getSettings(ModuleContainer m, SettingsHolder obj, SettingsContainer container) throws IOException {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if (info == null)
            if (!(obj instanceof Overlay))
                return obj;

        File f = new File(configFolder, Minecraft.getMinecraft().getSession().getPlayerID());
        if (!f.exists()) f.mkdirs();  // check if the users folder exists

        String configFile = m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase(Locale.ROOT).replace(' ', '_') : info.name()) + ".config";
        f = new File(f, configFile);

        if (!f.exists()) {
            f.createNewFile();
            container.onCreateConfig();
            saveSettings(m, container.getHolder(), true);
            return obj;
        }

        InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        SettingsHolder holder = gson.fromJson(new JsonReader(reader), obj.getClass());
        reader.close();

        return holder;
    }

    public static SettingsHolder getCloudSettings(ModuleContainer m, SettingsHolder obj) {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if (info == null)
            if (!(obj instanceof Overlay))
                return obj;

        String name = m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config";

        if (WebManager.getAccount() == null) return null;
        if (!WebManager.getAccount().getEncodedConfigs().containsKey(name)) return null;

        String jsonDecoded = WebManager.getAccount().getEncodedConfigs().get(name);
        WebManager.getAccount().dumpEncodedConfig(name);

        return gson.fromJson(jsonDecoded, obj.getClass());
    }

    /**
     * HeyZeer0: This interprets the common colors class, into/from the 'rgba(r,g,b,a)' format
     */
    private static class CustomColorSerializer implements JsonDeserializer<CustomColor>, JsonSerializer<CustomColor> {

        @Override
        public CustomColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) { /* HeyZeer0: this is just to convert old values to the new ones */
                JsonObject obj = json.getAsJsonObject();
                return new CustomColor(obj.get("r").getAsFloat(), obj.get("g").getAsFloat(), obj.get("b").getAsFloat(), obj.get("a").getAsFloat());
            }

            String value = json.getAsString();
            if (value.length() == 2 && (value.charAt(0) == '§' || value.charAt(0) == '&')) {
                // §(minecraft colour code)
                int code = Integer.parseInt(value.substring(1), 16);
                return new CustomColor(MinecraftChatColors.set.fromCode(code));
            }
            CustomColor asCommonColor = CommonColors.set.fromName(value);
            if (asCommonColor != null) {
                return new CustomColor(asCommonColor);
            }
            String[] rgba = value.replace("rgba(", "").replace(")", "").split(",");

            return new CustomColor(Float.parseFloat(rgba[0]), Float.parseFloat(rgba[1]), Float.parseFloat(rgba[2]), Float.parseFloat(rgba[3]));
        }

        @Override
        public JsonElement serialize(CustomColor src, Type typeOfSrc, JsonSerializationContext context) {
            String asCommonColor = CommonColors.set.getName(src);
            if (asCommonColor != null) {
                return context.serialize(asCommonColor);
            }

            int asMinecraftColor = MinecraftChatColors.set.getCode(src);
            if (asMinecraftColor != -1) {
                return context.serialize("&" + Integer.toString(asMinecraftColor, 16));
            }

            return context.serialize(src.toString());
        }

    }
}
