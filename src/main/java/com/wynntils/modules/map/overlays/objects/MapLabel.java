/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapLabelProfile;

import java.util.ArrayList;
import java.util.List;

public class MapLabel extends MapIcon {
    MapLabelProfile mlp;

    public MapLabel(MapLabelProfile mlp) {
        this.mlp = mlp;
    }

    @Override public int getPosX() {
        return mlp.getX();
    }

    @Override public int getPosZ() {
        return mlp.getZ();
    }

    @Override public String getName() {
        return mlp.getName();
    }

    @Override public float getSizeX() {
        return 8;
    }

    @Override public float getSizeZ() {
        return 8;
    }

    @Override public int getZoomNeeded() {
        return -1000;
    }

    @Override public boolean isEnabled(boolean forMinimap) {
        return !forMinimap;
    }

    @Override
    public boolean followRotation() {
        return false;
    }

    @Override
    public boolean hasDynamicLocation() {
        return false;
    }

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        float sizeX = getSizeX() * sizeMultiplier;
        float sizeZ = getSizeZ() * sizeMultiplier;
        renderer.drawCenteredString(mlp.getName(), centreX - sizeX, centreZ - sizeZ, CommonColors.YELLOW);
    }

    private static List<MapIcon> labels = null;

    public static List<MapIcon> getLabels() {
        if (labels == null) {
            resetLabels();
        }
        return labels;
    }

    public static void resetLabels() {
        if (labels == null) {
            labels = new ArrayList<>();
        } else {
            labels.clear();
        }
        for (MapLabelProfile mmp : WebManager.getMapLabels()) {
            labels.add(new MapLabel(mmp));
        }
    }
}