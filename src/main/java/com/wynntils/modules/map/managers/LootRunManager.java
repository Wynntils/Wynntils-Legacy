/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.managers;

import com.google.gson.*;
import com.wynntils.Reference;
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

    public static void hide() {
        activePath = null;
    }

    // Fix srg name in lootrun paths. Can still be read without
    // but makes the file more readable
    // TODO: remove before stable.
    private static boolean fixed = false;
    private static void fixBlockPos() {
        if (fixed) return;
        String srg_x = "field_177962_a";
        String srg_y = "field_177960_b";
        String srg_z = "field_177961_c";
        for (String fileName : getStoredLootruns()) {
            File f = new File(STORAGE_FOLDER, fileName + ".json");
            JsonParser parser = new JsonParser();
            JsonObject replacingWith = null;
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                JsonObject obj = parser.parse(reader).getAsJsonObject();
                JsonArray chests = obj.getAsJsonArray("chests");
                if (chests.size() > 0 && chests.get(0).getAsJsonObject().has(srg_x)) {
                    for (JsonElement j : chests) {
                        JsonObject o = j.getAsJsonObject();
                        o.addProperty("x", o.get(srg_x).getAsInt());
                        o.addProperty("y", o.get(srg_y).getAsInt());
                        o.addProperty("z", o.get(srg_z).getAsInt());
                        o.remove(srg_x);
                        o.remove(srg_y);
                        o.remove(srg_z);
                    }
                    replacingWith = obj;
                }
            } catch (Exception ex) { ex.printStackTrace(); }

            if (replacingWith == null) continue;

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                GSON.toJson(replacingWith, writer);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        fixed = true;
    }

    static {
        // TODO: remove soon so this isn't called every load
        fixBlockPos();
    }

    public static boolean loadFromFile(String lootRunName) {
        if (!STORAGE_FOLDER.exists()) return false;

        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if (!file.exists()) return false;

        fixBlockPos();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            LootRunPath path = GSON.fromJson(reader, LootRunPathIntermediary.class).toPath();
            if (path.getChests().size() == 0) return false;

            activePath = path;

            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveToFile(String lootRunName) {
        if (!STORAGE_FOLDER.exists()) STORAGE_FOLDER.mkdirs();

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
        if (!STORAGE_FOLDER.exists()) return false;

        try {
            File f = new File(STORAGE_FOLDER, lootRunName + ".json");
            if (!f.exists()) return false;

            f.delete();
        } catch (Exception ex) {
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
            if (MapConfig.LootRun.INSTANCE.pathType == MapConfig.LootRun.PathType.TEXTURED) {
                PointRenderer.drawTexturedLines(Textures.World.path_arrow, activePath.getRoughPoints(),
                        activePath.getRoughDirections(), MapConfig.LootRun.INSTANCE.activePathColour, .5f);
            } else {
                PointRenderer.drawLines(activePath.getSmoothPoints(), MapConfig.LootRun.INSTANCE.activePathColour);
            }

            activePath.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.activePathColour));
        }

        if (recordingPath != null) {
            PointRenderer.drawLines(recordingPath.getSmoothPoints(), MapConfig.LootRun.INSTANCE.recordingPathColour);
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
