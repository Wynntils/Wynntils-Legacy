/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;

import java.util.ArrayList;
import java.util.List;

public class MapIconInfo {

    AssetsTexture texture;

    int posX, posZ;
    String name;

    int texPosX, texPosZ, texSizeX, texSizeZ;

    float sizeX, sizeZ;

    int zoomNeeded;

    protected MapIconInfo(AssetsTexture texture, String name, int posX, int posZ, float size, int texPosX, int texPosZ, int texSizeX, int texSizeZ, int zoomNeeded) {
        this.texture = texture;
        this.posX = posX;
        this.posZ = posZ;
        this.name = name;
        this.texPosX = texPosX;
        this.texPosZ = texPosZ;
        this.texSizeX = texSizeX;
        this.texSizeZ = texSizeZ;
        this.sizeX = (texSizeX - texPosX) / size;
        this.sizeZ = (texSizeZ - texPosZ) / size;
        this.zoomNeeded = zoomNeeded;
    }

    private MapIconInfo(WaypointProfile wp) {
        texture = Textures.Map.map_icons;

        switch (wp.getType()) {
            case LOOTCHEST_T1:
                texPosX = 136; texPosZ = 35;
                texSizeX = 154; texSizeZ = 53;
                break;
            case LOOTCHEST_T2:
                texPosX = 118; texPosZ = 35;
                texSizeX = 136; texSizeZ = 53;
                break;
            case LOOTCHEST_T3:
                texPosX = 82; texPosZ = 35;
                texSizeX = 100; texSizeZ = 53;
                break;
            case LOOTCHEST_T4:
                texPosX = 100; texPosZ = 35;
                texSizeX = 118; texSizeZ = 53;
                break;
            default:
            case DIAMOND:
                texPosX = 172; texPosZ = 37;
                texSizeX = 190; texSizeZ = 55;
                break;
            case FLAG:
                //TODO handle colours
                texPosX = 154; texPosZ = 36;
                texSizeX = 172; texSizeZ = 54;
                break;
            case SIGN:
                texPosX = 190; texPosZ = 36;
                texSizeX = 208; texSizeZ = 54;
                break;
            case STAR:
                texPosX = 208; texPosZ = 36;
                texSizeX = 226; texSizeZ = 54;
                break;
            case TURRET:
                texPosX = 226; texPosZ = 36;
                texSizeX = 244; texSizeZ = 54;
                break;
        }

        name = wp.getName();
        posX = (int)wp.getX();
        posZ = (int)wp.getZ();
        sizeX = (texSizeX - texPosX) / 2.5f;
        sizeZ = (texSizeZ - texPosZ) / 2.5f;

        zoomNeeded = wp.getZoomNeeded();
    }

    public AssetsTexture getTexture() {
        return texture;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosZ() {
        return posZ;
    }

    public String getName() {
        return name;
    }

    public int getTexPosX() {
        return texPosX;
    }

    public int getTexPosZ() {
        return texPosZ;
    }

    public int getTexSizeX() {
        return texSizeX;
    }

    public int getTexSizeZ() {
        return texSizeZ;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public int getZoomNeeded() {
        return zoomNeeded;
    }

    private static List<MapIconInfo> classicApiMarkers = null;
    private static List<MapIconInfo> medivalApiMarkers = null;
    private static List<MapIconInfo> waypoints = null;

    public static void resetApiMarkers() {
        if (Textures.Map.map_icons == null) return;
        if (classicApiMarkers == null) {
            classicApiMarkers = new ArrayList<>();
        } else {
            classicApiMarkers.clear();
        }
        if (medivalApiMarkers == null) {
            medivalApiMarkers = new ArrayList<>();
        } else {
            medivalApiMarkers.clear();
        }
        for (MapMarkerProfile mmp : WebManager.getMapMarkers()) {
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Classic)) classicApiMarkers.add(new MapApiIconInfo(mmp, MapConfig.IconTexture.Classic));
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Medieval)) medivalApiMarkers.add(new MapApiIconInfo(mmp, MapConfig.IconTexture.Medieval));
        }
    }

    public static void resetApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Textures.Map.map_icons == null) return;
        List<MapIconInfo> markers;
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
                if (medivalApiMarkers == null) {
                    markers = medivalApiMarkers = new ArrayList<>();
                } else {
                    markers = medivalApiMarkers;
                    markers.clear();
                }
                break;
            default:
                return;
        }
        for (MapMarkerProfile mmp : WebManager.getMapMarkers()) {
            if (isApiMarkerValid(mmp, iconTexture)) markers.add(new MapApiIconInfo(mmp, iconTexture));
        }
    }

    public static List<MapIconInfo> getApiMarkers(MapConfig.IconTexture iconTexture) {
        if (classicApiMarkers == null && medivalApiMarkers == null) resetApiMarkers();
        switch (iconTexture) {
            case Classic:
                if (classicApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Classic);
                return classicApiMarkers;
            case Medieval:
                if (medivalApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Medieval);
                return medivalApiMarkers;
            default:
                return null;
        }
    }

    private static boolean isApiMarkerValid(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        return Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIVAL").getAsJsonObject().has(mmp.getIcon());
    }

    public static List<MapIconInfo> getWaypoints() {
        if (waypoints == null && Textures.Map.map_icons != null) {
            resetWaypoints();
        }
        return waypoints;
    }

    public static void resetWaypoints() {
        if (Textures.Map.map_icons == null) return;
        if (waypoints == null) {
            waypoints = new ArrayList<>();
        } else {
            waypoints.clear();
        }
         MapConfig.Waypoints.INSTANCE.waypoints.forEach(c -> waypoints.add(new MapIconInfo(c)));
    }

    public static MapCompassIconInfo getCompass() {
        return MapCompassIconInfo.getInstance();
    }
}
