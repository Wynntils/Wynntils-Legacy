package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;

public class WaypointOverviewUI extends GuiScreen {

    private GuiButton nextPageBtn;
    private GuiButton previousPageBtn;
    private GuiButton exitBtn;
    private ArrayList<GuiButton> editButtons = new ArrayList<>();

    private ScreenRenderer renderer = new ScreenRenderer();
    private ArrayList<WaypointProfile> waypoints;
    private int page;
    private int pageHeight;

    @Override
    public void initGui() {
        super.initGui();
        waypoints = MapConfig.Waypoints.INSTANCE.waypoints;

        pageHeight = (this.height - 100) / 25;
        setEditButtons();
        this.buttonList.add(nextPageBtn = new GuiButton(0, this.width/2 + 2, this.height - 45, 20, 20, ">"));
        this.buttonList.add(previousPageBtn = new GuiButton(1, this.width/2 - 22, this.height - 45, 20, 20, "<"));
        this.buttonList.add(exitBtn = new GuiButton(2, this.width - 40, 20, 20, 20, TextFormatting.RED + "X"));
        checkAvailablePages();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRenderer.drawString(TextFormatting.BOLD + "Icon", this.width/2 - 185, 39, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.BOLD + "Name", this.width/2 - 150, 39, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "X", this.width/2 - 35, 39, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "Z", this.width/2 + 20, 39, 0xFFFFFF);
        drawCenteredString(fontRenderer,TextFormatting.BOLD + "Y", this.width/2 + 60, 39, 0xFFFFFF);
        drawRect(this.width/2 - 185, 48,this.width/2 + 170,49, 0xFFFFFFFF);
        
        ScreenRenderer.beginGL(0,0);
        for (int i = 0; i < Math.min(pageHeight, waypoints.size() - pageHeight * page); i++) {
            WaypointProfile wp = waypoints.get(page * pageHeight + i);
            int tx1 = 0; int tx2 = 0; int ty1 = 0; int ty2 = 0;
            switch (wp.getType()) {
                case LOOTCHEST_T1:
                    tx1 = 136; ty1 = 35;
                    tx2 = 154; ty2 = 53;
                    break;
                case LOOTCHEST_T2:
                    tx1 = 118; ty1 = 35;
                    tx2 = 136; ty2 = 53;
                    break;
                case LOOTCHEST_T3:
                    tx1 = 82; ty1 = 35;
                    tx2 = 100; ty2 = 53;
                    break;
                case LOOTCHEST_T4:
                    tx1 = 100; ty1 = 35;
                    tx2 = 118; ty2 = 53;
                    break;
                case DIAMOND:
                    tx1 = 172; ty1 = 37;
                    tx2 = 190; ty2 = 55;
                    break;
                case FLAG:
                    //TODO handle colours
                    tx1 = 154; ty1 = 36;
                    tx2 = 172; ty2 = 54;
                    break;
                case SIGN:
                    tx1 = 190; ty1 = 36;
                    tx2 = 208; ty2 = 54;
                    break;
                case STAR:
                    tx1 = 208; ty1 = 36;
                    tx2 = 226; ty2 = 54;
                    break;
                case TURRET:
                    tx1 = 226; ty1 = 36;
                    tx2 = 244; ty2 = 54;
                    break;
            }
            renderer.drawRect(Textures.Map.map_icons, this.width/2 - 180, 51 + 25 * i, this.width/2 - 162,69 + 25 * i, tx1, ty1, tx2, ty2);
            fontRenderer.drawString(wp.getName(), this.width/2 - 150, 56 + 25 * i, 0xFFFFFF);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getX()), this.width/2 - 35, 56 + 25 * i, 0xFFFFFF);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getZ()), this.width/2 + 20, 56 + 25 * i, 0xFFFFFF);
            drawCenteredString(fontRenderer, Integer.toString((int) wp.getY()), this.width/2 + 60, 56 + 25 * i, 0xFFFFFF);
        }
        ScreenRenderer.endGL();
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
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapOverlay());
        } else if (b.id % 10 == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(waypoints.get(b.id / 10 + page * pageHeight), this));
        } else if (b.id %10 == 5) {
            MapConfig.Waypoints.INSTANCE.waypoints.remove(waypoints.get(b.id / 10 + page * pageHeight));
            MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());
            Minecraft.getMinecraft().displayGuiScreen(new WaypointOverviewUI());
        }
    }

    private void checkAvailablePages() {
        nextPageBtn.enabled = waypoints.size() - page * pageHeight > pageHeight;
        previousPageBtn.enabled = page > 0;
    }

    private void setEditButtons() {
        this.buttonList.removeAll(editButtons);
        editButtons.clear();
        for (int i = 0; i < Math.min(pageHeight, waypoints.size() - pageHeight * page); i++) {
            editButtons.add(new GuiButton(3 + 10 * i, this.width/2 + 85,50 + 25 * i,40,20,"Edit..."));
            editButtons.add(new GuiButton(5 + 10 * i, this.width/2 + 130, 50 + 25 * i, 40, 20, "Delete"));
        }
        this.buttonList.addAll(editButtons);
    }
}
