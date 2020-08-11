/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.managers;

import com.google.gson.*;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.rendering.PointRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LootRunManager {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Vec3i.class, new BlockPosSerializer())
        .create();
    public static final File STORAGE_FOLDER = new File(Reference.MOD_STORAGE_ROOT, "lootruns");

    private static LootRunPath activePath = null;
    private static LootRunPath recordingPath = null;

    public static void setup() {
        // Make sure lootrun folder exists at startup to simplify for users wanting to import lootruns
        if (!LootRunManager.STORAGE_FOLDER.exists()) {
            LootRunManager.STORAGE_FOLDER.mkdirs();
        }
    }

    public static List<String> getStoredLootruns() {
        String[] files = STORAGE_FOLDER.list();
        if (files == null) return Collections.emptyList();
        List<String> result = new ArrayList<>(files.length);
        for (String file : files) {
            if (!file.endsWith(".json")) continue;
            result.add(file.substring(0, file.length() - 5));
        }
        return result;
    }

    /**
     * Returns whether a lootrun can be loaded (Respects case-insensitive file systems)
     */
    public static boolean hasLootrun(String name) {
        String[] files = STORAGE_FOLDER.list();
        if (files == null) return false;
        File expectedFile = new File(STORAGE_FOLDER, name + ".json");
        for (String file : files) {
            if (new File(STORAGE_FOLDER, file).equals(expectedFile)) return true;
        }
        return false;
    }

    public static void hide() {
        activePath = null;
    }

    public static boolean loadFromFile(String lootRunName) {
        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if (!file.exists()) return false;

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            activePath = GSON.fromJson(reader, LootRunPathIntermediary.class).toPath();

            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveToFile(String lootRunName) {
        try {
            File file = new File(STORAGE_FOLDER, lootRunName + ".json");
            if (!file.exists()) file.createNewFile();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            GSON.toJson(new LootRunPathIntermediary(activePath), writer);

            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean delete(String lootRunName) {
        try {
            File f = new File(STORAGE_FOLDER, lootRunName + ".json");
            if (!f.exists()) return false;

            return f.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public static boolean rename(String oldName, String newName) {
        try {
            File f = new File(STORAGE_FOLDER, oldName + ".json");
            if (!f.exists()) return false;

            return f.renameTo(new File(STORAGE_FOLDER, newName + ".json"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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
        recordingPath = null;
    }

    public static void startRecording() {
        recordingPath = new LootRunPath();
    }

    public static void recordMovement(double x, double y, double z) {
        if (!isRecording()) return;

        Location to = new Location(x, y + .25d, z);
        if (recordingPath.isEmpty()) {
            recordingPath.addPoint(to);
            return;
        }

        if (recordingPath.getLastPoint().distanceSquared(to) < 4d) return;

        recordingPath.addPoint(to);
    }

    public static void addChest(BlockPos pos) {
        if (!isRecording()) return;

        recordingPath.addChest(pos);
    }

    public static void renderActivePaths() {
        if (activePath != null) {
            CustomColor color = MapConfig.LootRun.INSTANCE.rainbowLootRun ? CommonColors.RAINBOW : MapConfig.LootRun.INSTANCE.activePathColour;
            if (MapConfig.LootRun.INSTANCE.pathType == MapConfig.LootRun.PathType.TEXTURED) {
                PointRenderer.drawTexturedLines(Textures.World.path_arrow, activePath.getRoughPointsByChunk(),
                        activePath.getRoughDirectionsByChunk(), color, .5f);
            } else {
                PointRenderer.drawLines(activePath.getSmoothPointsByChunk(), color);
            }

            activePath.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.activePathColour));
        }

        if (recordingPath != null) {
            PointRenderer.drawLines(recordingPath.getSmoothPointsByChunk(), MapConfig.LootRun.INSTANCE.recordingPathColour);
            recordingPath.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.recordingPathColour));
        }
    }

    private static class LootRunPathIntermediary {
        public List<Location> points;
        public List<BlockPos> chests;
        public Date date;

        LootRunPathIntermediary(LootRunPath fromPath) {
            this.points = fromPath.getPoints();
            this.chests = new ArrayList<>(fromPath.getChests());
            date = new Date();
        }

        LootRunPath toPath() {
            return new LootRunPath(points, chests);
        }
    }

    private static class BlockPosSerializer implements JsonSerializer<Vec3i>, JsonDeserializer<Vec3i> {
        private static final String srg_x = "field_177962_a";
        private static final String srg_y = "field_177960_b";
        private static final String srg_z = "field_177961_c";

        @Override
        public BlockPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = json.getAsJsonObject();
            if (o.has(srg_x)) {
                return new BlockPos(o.get(srg_x).getAsInt(), o.get(srg_y).getAsInt(), o.get(srg_z).getAsInt());
            }
            return new BlockPos(o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt());
        }

        @Override
        public JsonElement serialize(Vec3i src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject o = new JsonObject();
            o.addProperty("x", src.getX());
            o.addProperty("y", src.getY());
            o.addProperty("z", src.getZ());
            return o;
        }
    }

}
