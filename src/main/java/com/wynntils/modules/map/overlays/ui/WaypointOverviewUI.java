/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.ui.elements.GuiButtonImageBetter;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;

public class WaypointOverviewUI extends GuiScreen {

    private GuiButton nextPageBtn;
    private GuiButton previousPageBtn;
    private GuiButton nextGroupBtn;
    private GuiButton previousGroupBtn;
    private List<GuiButton> groupBtns = new ArrayList<>();
    private GuiButton exitBtn;
    private GuiButton exportBtn;
    private GuiButton importBtn;
    private GuiButton clearBtn;
    private GuiButton editGroupsBtn;
    private List<GuiButton> editButtons = new ArrayList<>();

    private List<String> exportText;
    private List<String> importText;

    private static final int ungroupedIndex = WaypointProfile.WaypointType.values().length;

    private ScreenRenderer renderer = new ScreenRenderer();
    private List<WaypointProfile> waypoints;
    @SuppressWarnings("unchecked")
    private List<WaypointProfile>[] groupedWaypoints = (ArrayList<WaypointProfile>[]) new ArrayList[ungroupedIndex + 1];
    private boolean[] enabledGroups;
    private int page;
    private int pageHeight;
    private int group = ungroupedIndex;
    private int groupWidth;
    private int clearCount = 0;
    private final int clearCountMax = 3;
    private int groupScroll = 0;
    private int spacingMultiplier = MapConfig.Waypoints.INSTANCE.waypointSpacing.getSpacingMultiplier();
    private boolean decreasedSize = (spacingMultiplier == 14);

    @Override
    public void initGui() {
        super.initGui();
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        pageHeight = (this.height - 100) / spacingMultiplier;

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

        String clearButtonText = "CLEAR (Press " + clearCountMax + " times)";
        this.buttonList.add(exportBtn = new GuiButton(8, this.width/2 + 26, this.height - 45, 50, 20, "EXPORT"));
        this.buttonList.add(importBtn = new GuiButton(9, this.width/2 - 76, this.height - 45, 50, 20, "IMPORT"));
        this.buttonList.add(clearBtn = new GuiButton(10, 25, this.height - 45, fontRenderer.getStringWidth(clearButtonText) + 15, 20, clearButtonText));

        String text = "EDIT BEACON GROUPS";
        this.buttonList.add(editGroupsBtn = new GuiButton(10, 10, 10, fontRenderer.getStringWidth(text) + 25, 20, text));

        onWaypointChange();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        fontRenderer.drawString(TextFormatting.BOLD + "Group", this.width/2 - 205, 43, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width / 2 - 165, 43, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width / 2 - 130, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "X", this.width/2 - 15, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Z", this.width/2 + 40, 43, 0xFFFFFF);
        drawCenteredString(fontRenderer, TextFormatting.BOLD + "Y", this.width/2 + 80, 43, 0xFFFFFF);
        drawRect(this.width/2 - 205, 52, this.width/2 + 190, 53, 0xFFFFFFFF);

        ScreenRenderer.beginGL(0, 0);
        List<WaypointProfile> waypoints = getWaypoints();
        int hovered = getHoveredWaypoint(mouseX, mouseY);
        for (int i = 0, lim = Math.min(pageHeight, waypoints.size() - pageHeight * page); i < lim; i++) {
            WaypointProfile wp = waypoints.get(page * pageHeight + i);
            if (wp == null || wp.getType() == null) continue;

            int colour = 0xFFFFFF;
            boolean hidden = wp.getZoomNeeded() == MapWaypointIcon.HIDDEN_ZOOM;
            if (hidden) {
                colour = 0x636363;
            }

            MapWaypointIcon wpIcon = new MapWaypointIcon(wp);
            float centreZ = 64 + spacingMultiplier * i;
            float multiplier = 9f / Math.max(wpIcon.getSizeX(), wpIcon.getSizeZ());
            wpIcon.renderAt(renderer, this.width / 2f - 151, centreZ, multiplier, 1);

            if (i == hovered) {
                GuiButtonImageBetter.setColour(true, true);
            }
            if (wp.getGroup() == null) {
                String text = "NONE";
                fontRenderer.drawString(text, (int) (this.width / 2f - 191 - fontRenderer.getStringWidth(text) / 2f), (int) centreZ, 0xFFFFFFFF);
            } else {
                MapWaypointIcon groupIcon = MapWaypointIcon.getFree(wp.getGroup());
                float groupIconMultiplier = 9f / Math.max(groupIcon.getSizeX(), groupIcon.getSizeZ());
                groupIcon.renderAt(renderer, this.width / 2f - 191, centreZ, groupIconMultiplier, 1);
            }
            if (i == hovered) {
                GuiButtonImageBetter.setColour(false, true);
            }

            fontRenderer.drawString(wp.getName(), this.width/2 - 130, 60 + spacingMultiplier * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getX()), this.width/2 - 15, 60 + spacingMultiplier * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getZ()), this.width/2 + 40, 60 + spacingMultiplier * i, colour);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getY()), this.width/2 + 80, 60 + spacingMultiplier * i, colour);

            if (hidden) {
                drawHorizontalLine(this.width / 2 - 135, this.width / 2 + 95, (int) centreZ - 1, colour | 0xFF000000);
                GlStateManager.color(1, 1, 1, 1);
            }
        }
        ScreenRenderer.endGL();

        if (exportBtn.isMouseOver()) {
            drawHoveringText(exportText, mouseX, mouseY, fontRenderer);
        } else if (importBtn.isMouseOver()) {
            drawHoveringText(importText, mouseX, mouseY, fontRenderer);
        }
    }

    // Returns the index of the waypoint (on the current page) that is being hovered over (Or -1 if no hover)
    private int getHoveredWaypoint(int mouseX, int mouseY) {
        if (this.width / 2f - 205 <= mouseX && mouseX <= this.width / 2f - 170) {
            int i = Math.round((mouseY - 64) / (float)spacingMultiplier);
            int offset = mouseY - spacingMultiplier * i - 64;
            if (i >= 0 && -10 <= offset && offset <= 10 && i < Math.min(pageHeight, getWaypoints().size() - pageHeight * page)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 || mouseButton == 1) {
            int i = getHoveredWaypoint(mouseX, mouseY);
            if (i >= 0) {
                // Clicked on group button of ith waypoint on this page
                WaypointProfile wp = getWaypoints().get(page * pageHeight + i);
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
        } else if (b == exportBtn) {
            Utils.copyToClipboard(WaypointProfile.encode(getWaypoints(), WaypointProfile.currentFormat));
            exportText = Arrays.asList(
                "Export  ==  SUCCESS",
                "Copied to clipboard!"
            );
        } else if (b == importBtn) {
            String data = Utils.pasteFromClipboard();
            if (data != null) data = data.replaceAll("\\s+", "");
            if (data == null || data.isEmpty()) {
                importText = Arrays.asList(
                    "Import  ==  ERROR",
                    "Clipboard is empty"
                );
                return;
            }
            List<WaypointProfile> imported;
            try {
                imported = WaypointProfile.decode(data);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                String[] lines = e.getMessage().split("\\\\n|\\n");
                importText = new ArrayList<>(1 + lines.length);
                importText.add("Import  ==  ERROR");
                Collections.addAll(importText, lines);
                return;
            }
            int newWaypoints = 0;
            Set<Location> existing = new HashSet<>(waypoints.size());
            for (WaypointProfile wp : waypoints) {
                existing.add(new Location(wp.getX(), wp.getY(), wp.getZ()));
            }
            for (WaypointProfile wp : imported) {
                if (!existing.contains(new Location(wp.getX(), wp.getY(), wp.getZ()))) {
                    waypoints.add(wp);
                    ++newWaypoints;
                }
            }
            if (newWaypoints > 0) {
                onWaypointChange();
            }
            importText = Arrays.asList(
                "Import  ==  SUCCESS",
                String.format("Imported %d waypoints", newWaypoints)
            );
        } else if (b == clearBtn) {
            clearCount++;
            if (clearCount == clearCountMax) {
                waypoints.clear();
                onWaypointChange();
                clearCount = 0;
                clearBtn.displayString = "CLEARED!";
            } else {
                clearBtn.displayString = "CLEAR (Press " + (clearCountMax - clearCount) + " times)";
            }
        } else if (b == editGroupsBtn) {
            McIf.mc().displayGuiScreen(new WaypointBeaconGroupMenu(this));
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
            McIf.mc().displayGuiScreen(new WaypointCreationMenu(getWaypoints().get(b.id / 10 + page * pageHeight), this));
        } else if (b.id % 10 == 5) {
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

        checkAvailablePages();
        setEditButtons();
        resetGroupButtons();

        exportText = Arrays.asList(
            "Export",
            "Copy importable text to the clipboard",
            "Will export current group only"
        );
        importText = Arrays.asList(
            "Import",
            "Import waypoints from text in the clipboard",
            "Will not import waypoints with the same coordinates"
        );

        if (this.group != ungroupedIndex && !enabledGroups[this.group]) {
            this.group = ungroupedIndex;
            onWaypointChange();
        }
    }

    private void checkAvailablePages() {
        nextPageBtn.enabled = getWaypoints().size() - page * pageHeight > pageHeight;
        previousPageBtn.enabled = page > 0;
    }

    private void resetGroupButtons() {
        buttonList.removeAll(groupBtns);
        groupBtns = new ArrayList<>();
        int buttonX = this.width / 2 - groupWidth * 11 + 2;
        final int buttonY = 20;
        int group = 0;
        nextGroupBtn.enabled = true;
        previousGroupBtn.enabled = groupScroll > 0;
        for (int i = groupScroll - 1; i < groupScroll + groupWidth - 1; ++i) {
            if (i == -1) {
                GuiButton btn = new GuiButton(-(ungroupedIndex + 1), buttonX, buttonY, 18, 18, "*");
                groupBtns.add(btn);
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
                GuiButtonImage btn = new GuiButtonImageBetter(-(group + 1), buttonX, buttonY, icon.getTexSizeX() - texPosX, icon.getTexSizeZ() - texPosZ, texPosX, texPosZ, 0, icon.getTexture().resourceLocation);
                groupBtns.add(btn);
                if (this.group == group) {
                    btn.enabled = false;
                }
                ++group;
            }
            buttonX += 22;
        }
        this.buttonList.addAll(groupBtns);
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
            editButtons.add(new GuiButton(3 + 10 * i, this.width/2 + 85 + groupShift, 54 + spacingMultiplier * i, (int)Math.round(40.0 * (decreasedSize ? 0.7 : 1.0)), (int)Math.round(20.0 * (decreasedSize ? 0.6 : 1.0)),"Edit"));
            editButtons.add(new GuiButton(5 + 10 * i, this.width/2 + 130 + groupShift, 54 + spacingMultiplier * i, (int)Math.round(40.0 * (decreasedSize ? 0.9 : 1.0)), (int)Math.round(20.0 * (decreasedSize ? 0.6 : 1.0)), "Delete"));
            GuiButton up = new GuiButton(6 + 10 * i, this.width/2 + 172 + groupShift, 54 + spacingMultiplier * i, (int)Math.round(9 * (decreasedSize ? 0.75 : 1.0)), (int)Math.round(9 * (decreasedSize ? 0.75 : 1.0)), "\u028C");
            GuiButton down = new GuiButton(7 + 10 * i, this.width/2 + 172 + groupShift, 54 + spacingMultiplier * i + (int)Math.round(9 * (decreasedSize ? 0.75 : 1.0)), (int)Math.round(9 * (decreasedSize ? 0.75 : 1.0)), (int)Math.round(9 * (decreasedSize ? 0.75 : 1.0)), "\u1D5B"); // This is the ᵛ character
            up.enabled = i != 0 || previousPageBtn.enabled;
            down.enabled = i == pageHeight - 1 ? nextPageBtn.enabled : i != lim - 1;
            editButtons.add(up);
            editButtons.add(down);
        }
        this.buttonList.addAll(editButtons);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();
        if (mDWheel < 0 && nextPageBtn.enabled) {
            ++page;
            checkAvailablePages();
            setEditButtons();
        } else if (mDWheel > 0 && previousPageBtn.enabled) {
            --page;
            checkAvailablePages();
            setEditButtons();
        }
    }

}
