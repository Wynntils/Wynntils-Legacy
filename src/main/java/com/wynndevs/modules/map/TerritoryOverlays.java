package com.wynndevs.modules.map;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.TerritoryProfile;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.UIState;
import net.minecraft.util.math.BlockPos;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerritoryOverlays {
    static List <PolygonOverlay> create(IClientAPI jmAPI){
        List <PolygonOverlay> list = new ArrayList <>();
        //Code here
        for (int i = 0; i < WebManager.getTerritories().size(); i++) {
            TerritoryProfile pf = WebManager.getTerritories().get(i);

            String displayId = pf.getName();
            String groupName = "Territories";
            String label = String.format("%s\n%s", pf.getName(), pf.getGuild());

            // Style the polygon
            ShapeProperties shapeProps = new ShapeProperties()
                    .setStrokeWidth(2)
                    .setStrokeColor(0x00ff00).setStrokeOpacity(.7f)
                    .setFillColor(0x00ff00).setFillOpacity(.4f);

            // Style the text
            TextProperties textProps = new TextProperties()
                    .setBackgroundColor(0x000022)
                    .setBackgroundOpacity(.5f)
                    .setColor(0x00ff00)
                    .setOpacity(1f)
                    .setMinZoom(2)
                    .setFontShadow(true);

            // Define the shape
            BlockPos sw = new BlockPos(pf.getEndX(), 0, pf.getStartZ());
            BlockPos se = new BlockPos(pf.getEndX(), 0, pf.getEndZ());
            BlockPos ne = new BlockPos(pf.getStartX(), 0, pf.getEndZ());
            BlockPos nw = new BlockPos(pf.getStartX(), 0, pf.getStartZ());
            MapPolygon polygon = new MapPolygon(sw, se, ne, nw);

            // Create the overlay
            PolygonOverlay territoryOverlay = new PolygonOverlay(Reference.MOD_ID, displayId, 0, shapeProps, polygon);

            // Set the text
            territoryOverlay.setOverlayGroupName(groupName)
                    .setLabel(label)
                    .setTextProperties(textProps);

            // Add a listener for mouse events
            IOverlayListener overlayListener = new TerritoryOverlayListener(territoryOverlay);
            territoryOverlay.setOverlayListener(overlayListener);

            try {
                jmAPI.show(territoryOverlay);
                list.add(territoryOverlay);
            } catch (Throwable t) {
                Reference.LOGGER.error(t.getMessage(), t);
            }
        }
        return list;
    }

    /**
     * Listener for events on a slime chunk overlay instance.
     */
    static class TerritoryOverlayListener implements IOverlayListener {
        final PolygonOverlay overlay;
        final ShapeProperties sp;
        final int fillColor;
        final int strokeColor;
        final float strokeOpacity;

        TerritoryOverlayListener(final PolygonOverlay overlay){
            this.overlay = overlay;
            sp = overlay.getShapeProperties();
            fillColor = sp.getFillColor();
            strokeColor = sp.getStrokeColor();
            strokeOpacity = sp.getStrokeOpacity();
        }


        public void onActivate(UIState uiState){
            // Reset
            resetShapeProperties();
        }


        public void onDeactivate(UIState uiState){
            // Reset
            resetShapeProperties();
        }


        public void onMouseMove(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition){
            // Random stroke and make it opaque just to prove this works
            sp.setStrokeColor(new Random().nextInt(0xffffff));
            sp.setStrokeOpacity(1f);

            // Update title
            String title = "%s blocks away";
            BlockPos playerLoc = ModCore.mc().player.getPosition();
            int distance = (int) Math.sqrt(playerLoc.distanceSq(blockPosition.getX(), playerLoc.getY(), blockPosition.getZ()));
            overlay.setTitle(String.format(title, distance));
        }


        public void onMouseOut(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition){
            // Reset
            resetShapeProperties();
            overlay.setTitle(null);
        }


        public boolean onMouseClick(UIState uiState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick){
            // Random color on click just to prove the event works.
            sp.setFillColor(new Random().nextInt(0xffffff));

            // Returning false will stop the click event from being used by other overlays,
            // including JM's invisible overlay for creating/selecting waypoints
            return false;
        }

        /**
         * Reset properties back to original
         */
        private void resetShapeProperties(){
            sp.setFillColor(fillColor);
            sp.setStrokeColor(strokeColor);
            sp.setStrokeOpacity(strokeOpacity);
        }
    }
}
