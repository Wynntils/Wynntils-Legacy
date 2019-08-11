package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;

public class WaypointOverviewUI extends GuiScreen {

    private GuiButton nextPageBtn;
    private GuiButton previousPageBtn;
    private GuiButton nextGroupBtn;
    private GuiButton previousGroupBtn;
    private ArrayList<GuiButton> groupBtns = new ArrayList<>();
    // If a GuiButton is rendered before a GuiImageButton, the greyed-out-ness carries over, so this makes fake buttons
    // to grey out the next button properly
    private ArrayList<GuiButton> groupHighlightFixBtns = new ArrayList<>();
    private GuiButton exitBtn;
    private ArrayList<GuiButton> editButtons = new ArrayList<>();


    private static final int ungroupedIndex = WaypointProfile.WaypointType.values().length;

    private ScreenRenderer renderer = new ScreenRenderer();
    private ArrayList<WaypointProfile> waypoints;
    @SuppressWarnings("unchecked")
    private ArrayList<WaypointProfile>[] groupedWaypoints = (ArrayList<WaypointProfile>[]) new ArrayList[ungroupedIndex + 1];
    private boolean[] enabledGroups;
    private int page;
    private int pageHeight;
    private int group = ungroupedIndex;
    private int groupWidth;
    private int groupScroll = 0;

    @Override
    public void initGui() {
        super.initGui();
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        pageHeight = (this.height - 100) / 25;
        this.buttonList.add(nextPageBtn = new GuiButton(0, this.width/2 + 2, this.height - 45, 20, 20, ">"));
        this.buttonList.add(previousPageBtn = new GuiButton(1, this.width/2 - 22, this.height - 45, 20, 20, "<"));
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));

        groupWidth = Math.min(Math.max((this.width - 100) / 22, 2), ungroupedIndex + 1);
        int halfGroupPixelWidth = groupWidth * 11 + 2;
        previousGroupBtn = new GuiButton(-(ungroupedIndex + 2), this.width / 2 - halfGroupPixelWidth - 22, 20, 18, 18, "<");
        nextGroupBtn = new GuiButton(-(ungroupedIndex + 3), this.width / 2 + halfGroupPixelWidth + 4, 20, 18, 18, ">");
        if (groupWidth != ungroupedIndex + 1) {
            this.buttonList.add(previousGroupBtn);
            this.buttonList.add(nextGroupBtn);
        }

        onWaypointChange();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        int groupShift = 0;
        if (group == ungroupedIndex) {
            groupShift = 20;
            fontRenderer.drawString(TextFormatting.BOLD + "Group", this.width/2 - 205, 43, 0xFFFFFF);
        }
        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width / 2 - 185 + groupShift, 43, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 150 + groupShift, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "X", this.width/2 - 35 + groupShift, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "Z", this.width/2 + 20 + groupShift, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "Y", this.width/2 + 60 + groupShift, 43, 0xFFFFFF);
        drawRect(this.width/2 - 185 - groupShift, 52,this.width/2 + 170 + groupShift,53, 0xFFFFFFFF);

        ScreenRenderer.beginGL(0,0);
        ArrayList<WaypointProfile> waypoints = getWaypoints();
        for (int i = 0, lim = Math.min(pageHeight, waypoints.size() - pageHeight * page); i < lim; i++) {
            WaypointProfile wp = waypoints.get(page * pageHeight + i);
            if (wp == null || wp.getType() == null) continue;

            int colour = 0xFFFFFF;
            boolean hidden = wp.getZoomNeeded() == MapWaypointIcon.HIDDEN_ZOOM;
            if (hidden) {
                colour = 0x636363;
            }

            MapWaypointIcon wpIcon = new MapWaypointIcon(wp);
            float centreZ = 64 + 25 * i;
            float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
            wpIcon.renderAt(renderer, this.width / 2f - 171 + groupShift, centreZ, multiplier, 1);

            if (group == ungroupedIndex) {
                if (wp.getGroup() == null) {
                    String text = "NONE";
                    fontRenderer.drawString(text, (int) (this.width / 2f - 191 - fontRenderer.getStringWidth(text) / 2f), (int) centreZ, 0xFFFFFFFF);
                } else {
                    MapWaypointIcon groupIcon = MapWaypointIcon.getFree(wp.getGroup());
                    float groupIconMultiplier = 9f / Math.max(groupIcon.getSizeX(), groupIcon.getSizeZ());
                    groupIcon.renderAt(renderer, this.width / 2f - 191, centreZ, groupIconMultiplier, 1);
                }
            }

            fontRenderer.drawString(wp.getName(), this.width/2 - 150 + groupShift, 60 + 25 * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getX()), this.width/2 - 35 + groupShift, 60 + 25 * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getZ()), this.width/2 + 20 + groupShift, 60 + 25 * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getY()), this.width/2 + 60 + groupShift, 60 + 25 * i, colour);

            if (hidden) {
                drawHorizontalLine(this.width / 2 - 155 + groupShift, this.width / 2 + 75 + groupShift, (int) centreZ - 1, colour | 0xFF000000);
            }
        }
        ScreenRenderer.endGL();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if ((mouseButton == 0 || mouseButton == 1) && group == ungroupedIndex) {
            if (this.width / 2f - 205 <= mouseX && mouseX <= this.width / 2f - 170) {
                int i = Math.round((mouseY - 64) / 25f);
                ArrayList<WaypointProfile> waypoints;
                int offset = mouseY - 25 * i - 64;
                if (i >= 0 && -10 <= offset && offset <= 10 && i < Math.min(pageHeight, (waypoints = getWaypoints()).size() - pageHeight * page)) {
                    // Clicked on group button of ith waypoint on this page
                    WaypointProfile wp = waypoints.get(page * pageHeight + i);
                    if (wp != null) {
                        final int maxWaypointTypeIndex = WaypointProfile.WaypointType.values().length - 1;
                        if (mouseButton == 0) {
                            // Left click; Move group up
                            if (wp.getGroup() == null) {
                                wp.setGroup(WaypointProfile.WaypointType.values()[0]);
                            } else {
                                int g = wp.getGroup().ordinal();
                                if (g == maxWaypointTypeIndex) {
                                    wp.setGroup(null);
                                } else {
                                    wp.setGroup(WaypointProfile.WaypointType.values()[g + 1]);
                                }
                            }
                        } else {
                            // Right click; Move group down
                            if (wp.getGroup() == null) {
                                wp.setGroup(WaypointProfile.WaypointType.values()[maxWaypointTypeIndex]);
                            } else {
                                int g = wp.getGroup().ordinal();
                                if (g == 0) {
                                    wp.setGroup(null);
                                } else {
                                    wp.setGroup(WaypointProfile.WaypointType.values()[g - 1]);
                                }
                            }
                        }
                        onWaypointChange();
                    }
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b == nextPageBtn) {
            page++;
            checkAvailablePages();
            setEditButtons();
        } else if (b == previousPageBtn) {
            page--;
            checkAvailablePages();
            setEditButtons();
        } else if (b == exitBtn) {
            Utils.displayGuiScreen(new MainWorldMapUI());
        } else if (b.id < 0) {
            // A group button
            if (b == nextGroupBtn) {
                ++groupScroll;
                resetGroupButtons();
            } else if (b == previousGroupBtn) {
                --groupScroll;
                resetGroupButtons();
            } else {
                group = -b.id - 1;
                onWaypointChange();
            }
        } else if (b.id % 10 == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(waypoints.get(b.id / 10 + page * pageHeight), this));
        } else if (b.id % 10 == 5) {
            MapConfig.Waypoints.INSTANCE.waypoints.remove(waypoints.get(b.id / 10 + page * pageHeight));
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

    private ArrayList<WaypointProfile> getWaypoints() {
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

        checkAvailablePages();
        setEditButtons();
        resetGroupButtons();
    }

    private void checkAvailablePages() {
        nextPageBtn.enabled = getWaypoints().size() - page * pageHeight > pageHeight;
        previousPageBtn.enabled = page > 0;
    }

    private void resetGroupButtons() {
        buttonList.removeAll(groupBtns);
        buttonList.removeAll(groupHighlightFixBtns);
        groupBtns = new ArrayList<>();
        groupHighlightFixBtns = new ArrayList<>();
        int buttonX = this.width / 2 - groupWidth * 11 + 2;
        final int buttonY = 20;
        int group = 0;
        nextGroupBtn.enabled = true;
        previousGroupBtn.enabled = groupScroll > 0;
        for (int i = groupScroll - 1; i < groupScroll + groupWidth - 1; ++i) {
            if (i == -1) {
                GuiButton btn = new GuiButton(-(ungroupedIndex + 1), buttonX, buttonY, 18, 18, "*");
                groupBtns.add(btn);
                buttonList.add(btn);
                if (this.group == ungroupedIndex) {
                    btn.enabled = false;
                }
            } else {
                while (group < ungroupedIndex && !enabledGroups[group]) ++group;
                if (group == ungroupedIndex) {
                    // No more enabled groups
                    nextGroupBtn.enabled = false;
                    break;
                }
                MapWaypointIcon icon = MapWaypointIcon.getFree(WaypointProfile.WaypointType.values()[group]);
                int texPosX = icon.getTexPosX();
                int texPosZ = icon.getTexPosZ();
                GuiButtonImage btn = new GuiButtonImage(-(group + 1), buttonX, buttonY, icon.getTexSizeX() - texPosX, icon.getTexSizeZ() - texPosZ, texPosX, texPosZ, 0, icon.getTexture().resourceLocation);
                GuiButton fix = new GuiButton(-100 - group, -100, -100, 0, 0, "");
                groupBtns.add(btn);
                groupHighlightFixBtns.add(fix);
                buttonList.add(fix);
                buttonList.add(btn);
                if (this.group == group) {
                    btn.enabled = false;
                    fix.enabled = false;
                }
                ++group;
            }
            buttonX += 22;
        }
        if (nextGroupBtn.enabled) {
            while (group < ungroupedIndex && !enabledGroups[group]) ++group;
            if (group == ungroupedIndex) {
                nextGroupBtn.enabled = false;
            }
        }
    }

    private void setEditButtons() {
        this.buttonList.removeAll(editButtons);
        editButtons.clear();
        int groupShift = group == ungroupedIndex ? 20 : 0;
        for (int i = 0, lim = Math.min(pageHeight, getWaypoints().size() - pageHeight * page); i < lim; i++) {
            editButtons.add(new GuiButton(3 + 10 * i, this.width/2 + 85 + groupShift,54 + 25 * i,40,20,"Edit..."));
            editButtons.add(new GuiButton(5 + 10 * i, this.width/2 + 130 + groupShift, 54 + 25 * i, 40, 20, "Delete"));
            GuiButton up = new GuiButton(6 + 10 * i, this.width/2 + 172 + groupShift, 54 + 25 * i, 9, 9, "\u028C");
            GuiButton down = new GuiButton(7 + 10 * i, this.width/2 + 172 + groupShift, 65 + 25 * i, 9, 9, "v");
            up.enabled = i != 0 || previousPageBtn.enabled;
            down.enabled = i == pageHeight - 1 ? nextPageBtn.enabled : i != lim - 1;
            editButtons.add(up);
            editButtons.add(down);
        }
        this.buttonList.addAll(editButtons);
    }
}
