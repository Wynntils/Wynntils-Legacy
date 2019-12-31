/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.rendering.PointRenderer;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LootRunManager {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
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

    public static boolean loadFromFile(String lootRunName) {
        if (!STORAGE_FOLDER.exists()) return false;

        File file = new File(STORAGE_FOLDER, lootRunName + ".json");
        if (!file.exists()) return false;

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            LootRunPath path = GSON.fromJson(reader, LootRunPathIntermediary.class).toPath();
            if(path.getChests().size() == 0) return false;

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
            GSON.toJson(new LootRunPathIntermediary(recordingPath), writer);

            writer.close();
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
                // MapConfig.LootRun.INSTANCE.activePathColour.applyColor();
                PointRenderer.drawTexturedLines(Textures.World.path_arrow, activePath.getRoughPoints(), activePath.getRoughDirections(), .5f);
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

}
