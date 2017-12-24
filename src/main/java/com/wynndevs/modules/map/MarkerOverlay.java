package com.wynndevs.modules.map;

import com.wynndevs.core.Reference;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.util.UIState;
import net.minecraft.block.material.MapColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sample factory that generates a list of MarkerOverlays.
 */
class MarkerOverlayFactory {
    /**
     * Generate a bunch of randomly-placed markers on the map, just to show how MarkerOverlays fit together.
     *
     * @param center   center position
     * @param quantity how many markers
     * @return list of MarkerOverlays (already shown in the API)
     */
    static List <MarkerOverlay> create(IClientAPI jmAPI, BlockPos center, int quantity, int maxDistance) {
        // Use a sprite sheet to vary the icons
        ResourceLocation sprites = new ResourceLocation(Reference.MOD_ID, "textures/map/sprites.png");
        int spriteX = 0, spriteY = 0;
        int iconSize = 64;
        int iconColumns = 8;
        int iconRows = 4;

        List <MarkerOverlay> list = new ArrayList <MarkerOverlay>();
        Random random = new Random();
        int minX = center.getX() - maxDistance;
        int minZ = center.getZ() - maxDistance;

        int colorIndex = 0;
        for (int i = 0; i < quantity; i++) {
            BlockPos pos = new BlockPos(minX + random.nextInt(maxDistance), 70, minZ + random.nextInt(maxDistance));

            // Lets tint the icon using one Minecraft's map colors (usable range is 1-35)
            colorIndex++;
            if (colorIndex > 35) {
                colorIndex = 1;
            }
            int color = MapColor.COLORS[colorIndex].colorValue;

            MapImage icon = new MapImage(sprites, spriteX, spriteY, iconSize, iconSize, color, 1f);

            // Build the overlay
            MarkerOverlay markerOverlay = new MarkerOverlay(Reference.MOD_ID, "sampleMarker" + i, pos, icon);
            markerOverlay.setDimension(0).setTitle("Marker Overlay").setLabel("" + i);

            // Add a listener to it
            markerOverlay.setOverlayListener(new MarkerListener(jmAPI, markerOverlay));

            // Add to list
            try {
                jmAPI.show(markerOverlay);
                list.add(markerOverlay);
            } catch (Exception e) {
                Reference.LOGGER.error("Can't add marker overlay", e);
            }

            // Set next sprite coords
            spriteX += iconSize;
            if (spriteX >= (iconSize * iconColumns)) {
                spriteX = 0;
                spriteY += iconSize;
            }
            if (spriteY >= (iconSize * iconRows)) {
                spriteY = 0;
            }

        }

        return list;
    }

    /**
     * Listener for events on a MarkerOverlay instance.
     */
    static class MarkerListener implements IOverlayListener {
        final IClientAPI jmAPI;
        final MarkerOverlay overlay;
        final int color;
        final float opacity;

        MarkerListener(IClientAPI jmAPI, final MarkerOverlay overlay) {
            this.jmAPI = jmAPI;
            this.overlay = overlay;
            this.color = overlay.getIcon().getColor();
            this.opacity = overlay.getIcon().getOpacity();
        }

        @Override
        public void onActivate(UIState uiState) {
            refresh(uiState);
        }

        @Override
        public void onDeactivate(UIState uiState) {
            refresh(uiState);
        }

        @Override
        public void onMouseMove(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            // Scale the icon larger and change color and opacity
            double size = uiState.blockSize * 10;

            if (overlay.getIcon().getDisplayWidth() != size) {
                // Update the display dimensions and re-center the anchors so the icon
                // will be centered over it's BlockPos
                overlay.getIcon()
                        .setDisplayWidth(size)
                        .setDisplayHeight(size)
                        .setAnchorX(size / 2)
                        .setAnchorY(size);

                // Use sparingly
                overlay.flagForRerender();
            }
        }

        @Override
        public void onMouseOut(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition) {
            refresh(uiState);
        }

        @Override
        public boolean onMouseClick(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
            // Remove it on click
            jmAPI.remove(overlay);

            // Returning true will allow the click event to be used by other overlays
            return true;
        }

        /**
         * Reset properties back to originals, scale display size to zoom level
         */
        private void refresh(UIState uiState) {
            // Lets scale the icon to only span 8 blocks, regardless of zoom level
            double size = uiState.blockSize * 8;

            // Update the display dimensions and re-center the anchors so the icon
            // will be centered over it's BlockPos
            overlay.getIcon()
                    .setColor(color)
                    .setOpacity(opacity)
                    .setDisplayWidth(size)
                    .setDisplayHeight(size)
                    .setAnchorX(size / 2)
                    .setAnchorY(size);

            // Use sparingly
            overlay.flagForRerender();
        }
    }
}
