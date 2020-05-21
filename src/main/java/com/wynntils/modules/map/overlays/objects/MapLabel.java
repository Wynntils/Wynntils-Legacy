/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.map.configs.MapConfig;
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

    public String getLevel() {
        return mlp.getLevel();
    }

    public int getLayer() {
        return mlp.getLayer();
    }

    @Override public float getSizeX() {
        return ScreenRenderer.fontRenderer.getStringWidth(getName()) / 2;
    }

    @Override public float getSizeZ() {
        return 4;
    }

    @Override public int getZoomNeeded() {
        throw new UnsupportedOperationException("Not valid for MapLabel");
    }

    @Override public boolean isEnabled(boolean forMinimap) {
        return !forMinimap && MapConfig.WorldMap.INSTANCE.showLabels;
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
        throw new UnsupportedOperationException("Not valid for MapLabel");
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