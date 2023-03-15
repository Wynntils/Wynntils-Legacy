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
import org.apache.commons.lang3.ArrayUtils;

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
    private static LootRunPath latestLootrun = null;
    private static String latestLootrunName = null;
    private static LootRunPath recordingPath = null;
    private static LootRunPath lastRecorded = null;
    private final static List<PathWaypointProfile> mapPath = new ArrayList<>();
    private static int sessionLootrunsAmount = 0;
    private static int sessionLootrunChestsAmount = 0;
    private static boolean isLootrunLoaded = false;


    private final static List<CustomColor> pathColors = Arrays.asList(
        CommonColors.BLUE,
        CommonColors.GREEN,
        CommonColors.RED,
        CommonColors.YELLOW,
        CommonColors.PURPLE,
        CommonColors.CYAN,
        CommonColors.ORANGE,
        CommonColors.PINK,
        CommonColors.LIGHT_BLUE,
        CommonColors.LIGHT_GREEN,
        CommonColors.LIGHT_GRAY
    );

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

    public static Optional<LootRunPath> loadFromFile(String lootRunName) {
        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if (!file.exists()) return Optional.empty();

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            LootRunPath path = GSON.fromJson(reader, LootRunPathIntermediary.class).toPath();
            activePaths.put(lootRunName, path);
            updateMapPath();

            reader.close();
            latestLootrun = path;
            latestLootrunName = lootRunName;
            addLootruntoSession();
            isLootrunLoaded = true;
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
        isLootrunLoaded = false;
        updateMapPath();
    }

    public static Map<String, LootRunPath> getActivePaths() {
        return activePaths;
    }

    public static LootRunPath getRecordingPath() {
        return recordingPath;
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
            Optional<Pair<String, LootRunPath>> lastAdded = getLastLootrun();
            if (!lastAdded.isPresent()) return false;
            lastAdded.get().b.addChest(pos);
            saveToFile(lastAdded.get().a);
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
            Optional<Pair<String, LootRunPath>> lastAdded = getLastLootrun();
            if (!lastAdded.isPresent()) return false;
            lastAdded.get().b.removeChest(pos);
            saveToFile(lastAdded.get().a);
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
            Optional<Pair<String, LootRunPath>> lastAdded = getLastLootrun();
            if (!lastAdded.isPresent()) return false;
            lastAdded.get().b.addNote(note);
            saveToFile(lastAdded.get().a);
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
            Optional<Pair<String, LootRunPath>> lastAdded = getLastLootrun();
            if (!lastAdded.isPresent()) return false;
            lastAdded.get().b.removeNote(location);
            saveToFile(lastAdded.get().a);
            return true;
        }

        return false;
    }

    public static Optional<Pair<String, LootRunPath>> getLastLootrun() {
        if(latestLootrun == null || latestLootrunName == null) return Optional.empty();
        return Optional.of(new Pair<>(latestLootrunName, latestLootrun));
    }

    public static void renderActivePaths() {
        if (!activePaths.isEmpty()) {
            if (MapConfig.LootRun.INSTANCE.pathType == MapConfig.LootRun.PathType.TEXTURED) {
                for (LootRunPath path : activePaths.values()) {
                    CustomColor color = getPathColor(path);
                    PointRenderer.drawTexturedLines(Textures.World.path_arrow, path.getRoughPointsByChunk(),
                            path.getRoughDirectionsByChunk(), color, .5f);
                }
            } else {
                for (LootRunPath path : activePaths.values()) {
                    CustomColor color = getPathColor(path);
                    PointRenderer.drawLines(path.getSmoothPointsByChunk(), color);
                }
            }

            activePaths.values().forEach(path -> path.getChests().forEach(c -> PointRenderer.drawCube(c, getPathColor(path))));
            if (MapConfig.LootRun.INSTANCE.showNotes)
                activePaths.values().forEach(path -> path.getNotes().forEach(n -> n.drawNote(getPathColor(path))));
        }

        if (recordingPath != null) {
            PointRenderer.drawLines(recordingPath.getSmoothPointsByChunk(), MapConfig.LootRun.INSTANCE.recordingPathColour);
            recordingPath.getChests().forEach(c -> PointRenderer.drawCube(c, MapConfig.LootRun.INSTANCE.recordingPathColour));
            recordingPath.getNotes().forEach(n -> n.drawNote(MapConfig.LootRun.INSTANCE.recordingPathColour));
        }
    }

    private static CustomColor getPathColor(LootRunPath path){
        CustomColor defaultColor = MapConfig.LootRun.INSTANCE.rainbowLootRun ? CommonColors.RAINBOW : MapConfig.LootRun.INSTANCE.activePathColour;
        if (!MapConfig.LootRun.INSTANCE.differentColorsMultipleLootruns || getActivePaths().size() == 1 || MapConfig.LootRun.INSTANCE.rainbowLootRun) return defaultColor;

        int index = ArrayUtils.indexOf(getActivePaths().values().toArray(), path) % pathColors.size();
        return pathColors.get(index);
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

    public static boolean unloadLootrun(String name) {
        if(!activePaths.containsKey(name)) {
            return false;
        }
        if(activePaths.get(name) == latestLootrun)
            latestLootrun = null;
        activePaths.remove(name);
        updateMapPath();
        return true;
    }

    public static int getSessionLootruns() {
        return sessionLootrunsAmount;
    }

    public static int getSessionLootrunChests() {
        return sessionLootrunChestsAmount;
    }

    public static void addLootruntoSession() {
        sessionLootrunsAmount += 1;
    }

    public static void addOpenedChestToSession() {
        sessionLootrunChestsAmount += 1;
    }

    public static boolean isLootrunLoaded() {
        return isLootrunLoaded;
    }

    public static String getLatestLootrunName() {
        return latestLootrunName;
    }

    public static boolean isCheckALootrunChest(BlockPos pos) {
        for (LootRunPath path : activePaths.values()) {
            if (path.getChests().contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public static String getLootrunNames() {
        if (activePaths.size() > 0) {
            String s = "";
            for (String key : activePaths.keySet()) {
                if (key != activePaths.keySet().toArray()[activePaths.keySet().toArray().length - 1]) {
                    s = s + key + ", ";

                }
                else {
                    s = s + key;
                }
            }
            return s;
        }
        return "§c§l✖§r";
    }

    public static int getLootrunChests() {
       if(latestLootrun != null) {
           return latestLootrun.getChests().size();
       }
       return 0;
    }

    public static int getLootrunPoints() {
        if(latestLootrun != null) {
            return latestLootrun.getPoints().size();
        }
        return 0;
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
