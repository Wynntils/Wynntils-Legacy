/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.managers;

import com.google.gson.*;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.rendering.PointRenderer;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LootRunManager {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LootRunPath.class, new LootRunPathSerializer())
        .create();
    private static final File STORAGE_FOLDER = new File(Reference.MOD_STORAGE_ROOT, "lootruns");

    private static LootRunPath activePath = null;
    private static LootRunPath recordingPath = null;

    public static boolean loadFromFile(String lootRunName) {
        if(!STORAGE_FOLDER.exists()) return false;

        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if(!file.exists()) return false;

        try{
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            activePath = GSON.fromJson(reader, LootRunPath.class);

            reader.close();
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveToFile(String lootRunName) {
        if(!STORAGE_FOLDER.exists()) STORAGE_FOLDER.mkdirs();

        try{
            File file = new File(STORAGE_FOLDER, lootRunName + ".json");
            if(!file.exists()) file.createNewFile();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            GSON.toJson(activePath, writer);

            writer.close();
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static void clear() {
        activePath = null;
        recordingPath = null;
    }

    public static LootRunPath getActivePath() {
        return activePath;
    }

    public static LootRunPath getRecordingPath() {
        return recordingPath;
    }

    public static boolean isRecording() {
        return recordingPath != null;
    }

    public static void stopRecording() {
        activePath = recordingPath;
    }

    public static void startRecording() {
        recordingPath = new LootRunPath();
    }

    public static void recordMovement(int x, int y, int z) {
        if(!isRecording()) return;

        Location to = new Location(x + .5d, y + .1d, z + .5d);
        if (recordingPath.isEmpty()) {
            recordingPath.addPoint(to);
            return;
        }

        if(recordingPath.getPoints().get(recordingPath.getPoints().size() - 1).distance(to) < 5d) return;

        recordingPath.addPoint(to);
    }

    public static void addChest(Location pos) {
        if(!isRecording()) return;

        recordingPath.addChest(pos);
    }

    public static void renderActivePaths() {
        if(activePath != null) {
            PointRenderer.drawLines(activePath.getNormalizedPoints(), MinecraftChatColors.AQUA);
            activePath.getChests().forEach(c -> PointRenderer.drawCube(c, MinecraftChatColors.AQUA));
        }
        if(recordingPath != null) {
            PointRenderer.drawLines(recordingPath.getNormalizedPoints(), CommonColors.RED);
            recordingPath.getChests().forEach(c -> PointRenderer.drawCube(c, CommonColors.RED));
        }
    }

    public static class LootRunPathSerializer implements JsonDeserializer<LootRunPath>, JsonSerializer<LootRunPath> {

        private static class Intermediary {
            public List<Location> points;
            public ArrayList<Location> chests;

            Intermediary(LootRunPath fromPath) {
                this.points = fromPath.getPoints();
                this.chests = fromPath.getChests();
            }

            LootRunPath toPath() {
                return new LootRunPath(points, chests);
            }
        }

        @Override
        public LootRunPath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return context.<Intermediary>deserialize(json, Intermediary.class).toPath();
        }

        @Override
        public JsonElement serialize(LootRunPath src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(new Intermediary(src), Intermediary.class);
        }

    }

}
