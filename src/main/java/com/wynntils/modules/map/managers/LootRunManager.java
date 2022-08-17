/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.map.managers;

import com.google.gson.*;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.LootRunNote;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.instances.PathWaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapIcon;
import com.wynntils.modules.map.overlays.objects.MapPathWaypointIcon;
import com.wynntils.modules.map.rendering.PointRenderer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LootRunManager {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeHierarchyAdapter(Vec3i.class, new BlockPosSerializer())
        .create();
    public static final File STORAGE_FOLDER = new File(Reference.MOD_STORAGE_ROOT, "lootruns");

    private final static Map<String, LootRunPath> activePaths = new HashMap<>();
    private static LootRunPath recordingPath = null;
    private static LootRunPath lastRecorded = null;
    private final static List<PathWaypointProfile> mapPath = new ArrayList<>();

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
        activePaths.clear();
        updateMapPath();
    }

    public static Optional<LootRunPath> loadFromFile(String lootRunName) {
        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if (!file.exists()) return Optional.empty();

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            LootRunPath path = GSON.fromJson(reader, LootRunPathIntermediary.class).toPath();
            activePaths.put(lootRunName, path);
            updateMapPath();

            reader.close();

            return Optional.of(path);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Optional.empty();
        }
    }

    public static boolean saveToFile(String lootRunName, boolean recording) {
        try {
            File file = new File(STORAGE_FOLDER, lootRunName + ".json");
            if (!file.exists()) file.createNewFile();

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                LootRunPathIntermediary intermediary = new LootRunPathIntermediary(recording ? lastRecorded : activePaths.get(lootRunName));
                GSON.toJson(intermediary, writer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveToFile(String lootRunName) {
        return saveToFile(lootRunName, false);
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
        activePaths.clear();
        recordingPath = null;
        updateMapPath();
    }

    public static void clearOnlyLoaded() {
        clear();
        updateMapPath();
    }

    public static Map<String, LootRunPath> getActivePath() {
        return activePaths;
    }

    public static LootRunPath getRecordingPath() {
        return recordingPath;
    }

    public static List<PathWaypointProfile> getMapPath() {
        return mapPath;
    }

    public static List<MapIcon> getMapPathWaypoints() {
        if (!mapPath.isEmpty() && MapConfig.LootRun.INSTANCE.displayLootrunOnMap)
            return mapPath.stream().map(MapPathWaypointIcon::new).collect(Collectors.toList());
        else
            return new ArrayList<>();
    }

    public static boolean isRecording() {
        return recordingPath != null;
    }

    public static void stopRecording() {
        lastRecorded = recordingPath;
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

    public static boolean undoMovement(double x, double y, double z) {
        if (!isRecording()) return false;

        Location to = new Location(x, y + .25d, z);
        List<Location> recordedPoints = recordingPath.getPoints();
        List<Location> removed = new ArrayList<>();
        boolean pastInitial = false;
        for (int i = recordedPoints.size() - 1; i >= 0; i--) {
            if (i == 0) return false; // never found a point to rewind to

            if (recordedPoints.get(i).distanceSquared(to) < 4d) {
                if (pastInitial) break; // we've reached the player again
            } else {
                if (!pastInitial) pastInitial = true; // we've moved past the end of the path
            }

            removed.add(recordedPoints.get(i));
        }

        recordingPath.removePoints(removed);
        return true;
    }

    public static boolean addChest(BlockPos pos) {
        if (isRecording()) {
            recordingPath.addChest(pos);
            return true;
        }

        if (!activePaths.isEmpty()) {
            Pair<String, LootRunPath> lastAdded = getLastLootrun();
            if(lastAdded == null) return false;
            lastAdded.b.addChest(pos);
            saveToFile(lastAdded.a);
            return true;
        }

        return false;
    }

    public static boolean removeChest(BlockPos pos) {
        if (isRecording()) {
            recordingPath.removeChest(pos);
            return true;
        }

        if (!activePaths.isEmpty()) {
            Pair<String, LootRunPath> lastAdded = getLastLootrun();
            if(lastAdded == null) return false;
            lastAdded.b.removeChest(pos);
            saveToFile(lastAdded.a);
            return true;
        }

        return false;
    }

    public static boolean addNote(LootRunNote note) {
        if (isRecording()) {
            recordingPath.addNote(note);
            return true;
        }

        if (!activePaths.isEmpty()) {
            Pair<String, LootRunPath> lastAdded = getLastLootrun();
            if(lastAdded == null) return false;
            lastAdded.b.addNote(note);
            saveToFile(lastAdded.a);
            return true;
        }

        return false;
    }

    public static boolean removeNote(String location) {
        if (isRecording()) {
            recordingPath.removeNote(location);
            return true;
        }

        if (!activePaths.isEmpty()) {
            Pair<String, LootRunPath> lastAdded = getLastLootrun();
            if(lastAdded == null) return false;
            lastAdded.b.removeNote(location);
            saveToFile(lastAdded.a);
            return true;
        }

        return false;
    }

    public static Pair<String, LootRunPath> getLastLootrun() {
        String[] names = activePaths.keySet().toArray(new String[0]);
        if(names.length == 0) return null;
        String lootrunName = names[names.length - 1];
        if(lootrunName == null) return null;
        LootRunPath lastAdded = activePaths.get(lootrunName);
        return new Pair<>(lootrunName, lastAdded);
    }

    public static void renderActivePaths() {
        if (!activePaths.isEmpty()) {
            CustomColor color = MapConfig.LootRun.INSTANCE.rainbowLootRun ? CommonColors.RAINBOW : MapConfig.LootRun.INSTANCE.activePathColour;
            if (MapConfig.LootRun.INSTANCE.pathType == MapConfig.LootRun.PathType.TEXTURED) {
               for(LootRunPath path : activePaths.values()) {
                   PointRenderer.drawTexturedLines(Textures.World.path_arrow, path.getRoughPointsByChunk(),
                           path.getRoughDirectionsByChunk(), color, .5f);
               }
            } else {
                for(LootRunPath path : activePaths.values()) {
                    PointRenderer.drawLines(path.getSmoothPointsByChunk(), color);
                }
            }

            activePaths.values().forEach(path -> path.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.activePathColour)));
            if (MapConfig.LootRun.INSTANCE.showNotes)
                activePaths.values().forEach(path -> path.getNotes().forEach(n -> n.drawNote(MapConfig.LootRun.INSTANCE.activePathColour)));
        }

        if (recordingPath != null) {
            PointRenderer.drawLines(recordingPath.getSmoothPointsByChunk(), MapConfig.LootRun.INSTANCE.recordingPathColour);
            recordingPath.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.recordingPathColour));
            recordingPath.getNotes().forEach(n -> n.drawNote(MapConfig.LootRun.INSTANCE.recordingPathColour));
        }
    }

    private static void updateMapPath() {
        mapPath.clear();
        if (!activePaths.isEmpty()) {
            mapPath.addAll(activePaths.values().stream().map(PathWaypointProfile::new).collect(Collectors.toList()));
        }
    }

    public static void clearLootrun(String name) {
        activePaths.remove(name);
        recordingPath = null;
        updateMapPath();
    }

    public static void unloadLootrun(String name) {
        activePaths.remove(name);
        updateMapPath();
    }

    private static class LootRunPathIntermediary {
        public List<Location> points;
        public List<BlockPos> chests;
        public List<LootRunNote> notes;
        public Date date;

        LootRunPathIntermediary(LootRunPath fromPath) {
            this.points = fromPath.getPoints();
            this.chests = new ArrayList<>(fromPath.getChests());
            this.notes = new ArrayList<>(fromPath.getNotes());
            date = new Date();
        }

        LootRunPath toPath() {
            return new LootRunPath(points, chests, notes);
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
