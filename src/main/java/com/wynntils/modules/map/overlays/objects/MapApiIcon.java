/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.overlays.objects;

import com.google.gson.JsonObject;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;

import java.util.*;

public class MapApiIcon extends MapTextureIcon {

    private MapMarkerProfile mmp;
    private int texPosX, texPosZ, texSizeX, texSizeZ;
    private float sizeX, sizeZ;
    private int zoomNeeded;

    MapApiIcon(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        JsonObject iconMapping = Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIEVAL").getAsJsonObject().get(mmp.getIcon()).getAsJsonObject();

        this.mmp = mmp;

        texPosX = iconMapping.get("texPosX").getAsInt();
        texPosZ = iconMapping.get("texPosZ").getAsInt();
        texSizeX = iconMapping.get("texSizeX").getAsInt();
        texSizeZ = iconMapping.get("texSizeZ").getAsInt();
        float size = iconMapping.get("size").getAsFloat();
        sizeX = (texSizeX - texPosX) / size;
        sizeZ = (texSizeZ - texPosZ) / size;
        zoomNeeded = iconMapping.get("zoomNeeded").getAsInt();
    }

    @Override
    public AssetsTexture getTexture() {
        return Textures.Map.map_icons;
    }

    @Override
    public int getPosX() {
        return mmp.getX();
    }

    @Override
    public int getPosZ() {
        return mmp.getZ();
    }

    @Override
    public String getName() {
        return mmp.getName();
    }

    @Override
    public int getTexPosX() {
        return texPosX;
    }

    @Override
    public int getTexPosZ() {
        return texPosZ;
    }

    @Override
    public int getTexSizeX() {
        return texSizeX;
    }

    @Override
    public int getTexSizeZ() {
        return texSizeZ;
    }

    @Override
    public float getSizeX() {
        return sizeX;
    }

    @Override
    public float getSizeZ() {
        return sizeZ;
    }

    @Override
    public int getZoomNeeded() {
        return zoomNeeded;
    }

    @Override
    public boolean isEnabled(boolean forMinimap) {
        if (MapConfig.INSTANCE.hideCompletedQuests && (mmp.getIcon().equals("Content_Quest") || mmp.getIcon().equals("Content_Miniquest"))) {
            QuestInfo questData = QuestManager.getQuest(mmp.getName());
            if (questData != null && questData.getStatus() == QuestStatus.COMPLETED) {
                return false;
            }
        }

        Boolean enabled = (forMinimap ? MapConfig.INSTANCE.enabledMinimapIcons : MapConfig.INSTANCE.enabledMapIcons).get(mmp.getTranslatedName());

        if (enabled == null) {
            // Missing some keys; Add them all
            HashMap<String, Boolean> defaulted = MapConfig.resetMapIcons(false);
            for (Map.Entry<String, Boolean> e : defaulted.entrySet()) {
                String icon = e.getKey();

                MapConfig.INSTANCE.enabledMapIcons.computeIfAbsent(icon, k -> e.getValue());
            }

            defaulted = MapConfig.resetMapIcons(true);
            for (Map.Entry<String, Boolean> e : defaulted.entrySet()) {
                String icon = e.getKey();

                MapConfig.INSTANCE.enabledMinimapIcons.computeIfAbsent(icon, k -> e.getValue());
            }

            MapConfig.INSTANCE.saveSettings(MapModule.getModule());

            enabled = (forMinimap ? MapConfig.INSTANCE.enabledMinimapIcons : MapConfig.INSTANCE.enabledMapIcons).get(mmp.getTranslatedName());

            // In case the map icon is not present, set it to false and display a console warning!
            if (enabled == null) {
                MapConfig.INSTANCE.enabledMapIcons.put(mmp.getTranslatedName(), false);
                MapConfig.INSTANCE.enabledMinimapIcons.put(mmp.getTranslatedName(), false);
                enabled = false;

                Reference.LOGGER.warn("Missing default map icon state (" + mmp.getTranslatedName() + ").");
            }
        }

        return enabled;
    }

    public MapMarkerProfile getMapMarkerProfile() {
        return mmp;
    }

    // As an optimisation, so methods don't need to be called all the time
    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        float sizeX = this.sizeX * sizeMultiplier;
        float sizeZ = this.sizeZ * sizeMultiplier;
        renderer.drawRectF(
                Textures.Map.map_icons,
                centreX - sizeX, centreZ - sizeZ,
                centreX + sizeX, centreZ + sizeZ,
                texPosX, texPosZ, texSizeX, texSizeZ
        );
    }

    private static List<MapIcon> classicApiMarkers = null;
    private static List<MapIcon> medievalApiMarkers = null;

    public static void resetApiMarkers() {
        if (Mappings.Map.map_icons_mappings == null) return;
        if (classicApiMarkers == null) {
            classicApiMarkers = new ArrayList<>();
        } else {
            classicApiMarkers.clear();
        }
        if (medievalApiMarkers == null) {
            medievalApiMarkers = new ArrayList<>();
        } else {
            medievalApiMarkers.clear();
        }
        for (MapMarkerProfile mmp : WebManager.getNonIgnoredApiMarkers()) {
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Classic)) classicApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Classic));
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Medieval)) medievalApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Medieval));
        }
    }

    public static void resetApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Mappings.Map.map_icons_mappings == null) return;
        List<MapIcon> markers;
        switch (iconTexture) {
            case Classic:
                if (classicApiMarkers == null) {
                    markers = classicApiMarkers = new ArrayList<>();
                } else {
                    markers = classicApiMarkers;
                    markers.clear();
                }
                break;
            case Medieval:
                if (medievalApiMarkers == null) {
                    markers = medievalApiMarkers = new ArrayList<>();
                } else {
                    markers = medievalApiMarkers;
                    markers.clear();
                }
                break;
            default:
                return;
        }
        for (MapMarkerProfile mmp : WebManager.getNonIgnoredApiMarkers()) {
            if (isApiMarkerValid(mmp, iconTexture)) markers.add(new MapApiIcon(mmp, iconTexture));
        }
    }

    public static List<MapIcon> getApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Mappings.Map.map_icons_mappings == null) return Collections.emptyList();
        if (classicApiMarkers == null && medievalApiMarkers == null) resetApiMarkers();
        switch (iconTexture) {
            case Classic:
                if (classicApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Classic);
                return classicApiMarkers;
            case Medieval:
                if (medievalApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Medieval);
                return medievalApiMarkers;
            default:
                return null;
        }
    }

    private static boolean isApiMarkerValid(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        boolean valid = Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIEVAL").getAsJsonObject().has(mmp.getIcon());
        if (!valid && Reference.developmentEnvironment) {
            Reference.LOGGER.warn("No " + iconTexture + " texture for \"" + mmp.getIcon() + "\"");
        }
        return valid;
    }

    /**
     * Return a MapApiIcon that can render a map marker being free from position information
     */
    public static MapApiIcon getFree(String icon, MapConfig.IconTexture iconTexture) {
        String reverseTranslation = MapMarkerProfile.getReverseTranslation(icon);
        return new MapApiIcon(new MapMarkerProfile(null, NO_LOCATION, NO_LOCATION, NO_LOCATION, reverseTranslation), iconTexture);
    }

}
