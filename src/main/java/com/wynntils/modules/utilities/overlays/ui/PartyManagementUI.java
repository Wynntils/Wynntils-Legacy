/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartyManagementUI extends GuiScreen {

    private GuiButton exitBtn;
    private List<GuiButton> buttons = new ArrayList<>();


    private static final int ungroupedIndex = WaypointProfile.WaypointType.values().length;

    private final ScreenRenderer renderer = new ScreenRenderer();
    private List<WaypointProfile> waypoints;
    @SuppressWarnings("unchecked")
    private List<WaypointProfile>[] groupedWaypoints = (ArrayList<WaypointProfile>[]) new ArrayList[ungroupedIndex + 1];
    private boolean[] enabledGroups;
    private int page;
    private int pageHeight;
    private int group = ungroupedIndex;

    @Override
    public void initGui() {
        super.initGui();
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        pageHeight = (this.height - 100) / 25;
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));

        onWaypointChange();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width/2 - 205, 43, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 165, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Promote", this.width/2 + 120, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Kick", this.width/2 + 179, 43, 0xFFFFFF);
        drawRect(this.width/2 - 205, 52, this.width/2 + 190, 53, 0xFFFFFFFF); // Underline

        List<String> partyMembers = new ArrayList<>(PlayerInfo.get(SocialData.class).getPlayerParty().getPartyMembers());
        if (partyMembers.size() < 1) return;

        ScreenRenderer.beginGL(0, 0);
        for (int i = 0, lim = Math.min(pageHeight, partyMembers.size() - pageHeight * page); i < lim; i++) {
            String playerName = partyMembers.get(page * pageHeight + i);
            if (playerName == null) continue;

            int colour = 0xFFFFFF;

//            MapWaypointIcon wpIcon = new MapWaypointIcon(wp);
//            float centreZ = 64 + 25 * i;
//            float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
//            wpIcon.renderAt(renderer, this.width / 2f - 151, centreZ, multiplier, 1);

            fontRenderer.drawString(playerName, this.width/2 - 130, 60 + 25 * i, colour);
//            drawCenteredString(fontRenderer, Integer.toString((int) wp.getX()), this.width/2 + 120, 60 + 25 * i, colour);
        }
        ScreenRenderer.endGL();
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id % 10 == 5) {
            MapConfig.Waypoints.INSTANCE.waypoints.remove(getWaypoints().get(b.id / 10 + page * pageHeight));
            onWaypointChange();
        } else if (b.id % 10 == 6 || b.id % 10 == 7) {
            int i = b.id / 10 + page * pageHeight;
            int j = i + (b.id % 10 == 6 ? -1 : +1);
            // Swap waypoints at i and j
            WaypointProfile iWp = getWaypoints().get(i);
            WaypointProfile jWp = getWaypoints().get(j);
            // ii and jj will be different if this is a grouped view
            int ii = MapConfig.Waypoints.INSTANCE.waypoints.indexOf(iWp);
            int jj = MapConfig.Waypoints.INSTANCE.waypoints.indexOf(jWp);
            MapConfig.Waypoints.INSTANCE.waypoints.set(ii, jWp);
            MapConfig.Waypoints.INSTANCE.waypoints.set(jj, iWp);
            onWaypointChange();
        }
    }

    private void setButtons() {
        this.buttonList.removeAll(buttons);
        buttons.clear();
        for (int i = 0, lim = Math.min(pageHeight, getWaypoints().size() - pageHeight * page); i < lim; i++) {
            buttons.add(new GuiButton(3 + 10 * i))
        }
    }

    private List<WaypointProfile> getWaypoints() {
        if (group == ungroupedIndex) {
            return waypoints;
        }
        return groupedWaypoints[group];
    }

    private void onWaypointChange() {
        MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        if (getWaypoints().size() > 0 && page * pageHeight > getWaypoints().size() - 1) {
            page = (getWaypoints().size() - 1) / pageHeight;
        }

        enabledGroups = new boolean[ungroupedIndex];
        for (int i = 0; i <= ungroupedIndex; ++i) {
            groupedWaypoints[i] = new ArrayList<>();
        }

        for (WaypointProfile wp : waypoints) {
            if (wp.getGroup() == null) {
                groupedWaypoints[ungroupedIndex].add(wp);
            } else {
                int index = wp.getGroup().ordinal();
                groupedWaypoints[index].add(wp);
                enabledGroups[index] = true;
            }
        }

        if (this.group != ungroupedIndex && !enabledGroups[this.group]) {
            this.group = ungroupedIndex;
            onWaypointChange();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
    }

}
