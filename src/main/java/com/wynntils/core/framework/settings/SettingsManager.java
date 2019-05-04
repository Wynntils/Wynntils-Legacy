/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class SettingsManager {

    private static Gson gson = null;
    private static final File configFolder = new File(Reference.MOD_STORAGE_ROOT, "configs");

    static {
        gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(CustomColor.class, new CommonColorsDeserialiser())
            .create();

        configFolder.mkdirs(); //if the config folder doesn't exists create the directory
    }

    public static void saveSettings(ModuleContainer m, SettingsHolder obj) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null)
            if(!(obj instanceof Overlay))
                return;

        File f = new File(configFolder, Minecraft.getMinecraft().getSession().getPlayerID());
        if(!f.exists()) f.mkdirs(); // check if the users folder exists

        f = new File(f, m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config");
        if(!f.exists()) f.createNewFile(); // create the config file if it doesn't exists

        //HeyZeer0: Writting to file
        OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8);
        gson.toJson(obj, fileWriter);
        fileWriter.close();

        //HeyZeer0: Uploading file
        if(WebManager.getAccount() != null)
            WebManager.getAccount().uploadConfig(f.getName(), new String(Base64.getEncoder().encode(Files.readAllBytes(f.toPath())), StandardCharsets.UTF_8));
    }

    public static SettingsHolder getSettings(ModuleContainer m, SettingsHolder obj, SettingsContainer container) throws Exception {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null)
            if(!(obj instanceof Overlay))
                return obj;

        File f = new File(configFolder, Minecraft.getMinecraft().getSession().getPlayerID());
        if(!f.exists()) f.mkdirs(); // check if the users folder exists

        String configFile = m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config";
        f = new File(f, configFile);

        //HeyZeer0: converts the old format to the new format
        File conversionFile = new File(configFolder, configFile);
        boolean delete = false;
        if(conversionFile.exists()) {
            f = conversionFile;
            delete = true;

            Reference.LOGGER.warn("Converting old config " + configFile + " to the new format.");
        }

        if(!f.exists()) {
            f.createNewFile();
            container.onCreateConfig();
            saveSettings(m, container.getHolder());
            return obj;
        }

        InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        SettingsHolder holder = gson.fromJson(new JsonReader(reader), obj.getClass());
        reader.close();

        //HeyZeer0: deletes all files from the old format
        if(delete) f.delete();

        return holder;
    }

    public static SettingsHolder getCloudSettings(ModuleContainer m, SettingsHolder obj) {
        SettingsInfo info = obj.getClass().getAnnotation(SettingsInfo.class);
        if(info == null)
            if(!(obj instanceof Overlay))
                return obj;

        String name = m.getInfo().name() + "-" + (obj instanceof Overlay ? "overlay_" + ((Overlay)obj).displayName.toLowerCase().replace(" ", "_") : info.name()) + ".config";

        if(WebManager.getAccount() == null) return null;
        if(!WebManager.getAccount().getEncondedConfigs().containsKey(name)) return null;

        String jsonDecoded = new String(Base64.getDecoder().decode(WebManager.getAccount().getEncondedConfigs().get(name)), StandardCharsets.UTF_8);
        return gson.fromJson(jsonDecoded, obj.getClass());
    }

    /**
     * HeyZeer0: This interpretates the common colors class, into/from the 'rgba(r,g,b,a)' format
     */
    private static class CommonColorsDeserialiser implements JsonDeserializer<CustomColor>, JsonSerializer<CustomColor> {

        @Override
        public CustomColor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json.isJsonObject()) { /* HeyZeer0: this is just to convert old values to the new ones */
                JsonObject obj = json.getAsJsonObject();
                return new CustomColor(obj.get("r").getAsFloat(), obj.get("g").getAsFloat(), obj.get("b").getAsFloat(), obj.get("a").getAsFloat());
            }

            String rgba[] = json.getAsString().replace("rgba(", "").replace(")", "").split(",");

            return new CustomColor(Float.valueOf(rgba[0]), Float.valueOf(rgba[1]), Float.valueOf(rgba[2]), Float.valueOf(rgba[3]));
        }

        @Override
        public JsonElement serialize(CustomColor src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.toString());
        }

    }
}
