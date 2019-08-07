package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class MainWorldMapUI extends WorldMapUI {
    private GuiButton settingsBtn;
    private GuiButton waypointMenuBtn;
    private GuiButton pathWaypointMenuBtn;
    private GuiButtonImage addWaypointBtn;

    private boolean holdingMapKey = false;
    private long creationTime;

    public MainWorldMapUI() {
        super();

        creationTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(settingsBtn = new GuiButton(1, 22, 23, 60, 18, "Markers"));
        this.buttonList.add(waypointMenuBtn = new GuiButton(3, 22, 46, 60, 18, "Waypoints"));
        this.buttonList.add(pathWaypointMenuBtn = new GuiButton(3, 22, 69, 60, 18, "Paths"));
        this.buttonList.add(addWaypointBtn = new GuiButtonImage(2, 24, 92, 14, 14, 0, 0, 0, Textures.Map.map_options.resourceLocation));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //HeyZeer0: This detects if the user is holding the map key;
        if(!holdingMapKey && (System.currentTimeMillis() - creationTime >= 150) && Keyboard.isKeyDown(MapModule.getModule().getMapKey().getKeyBinding().getKeyCode())) holdingMapKey = true;

        //HeyZeer0: This close the map if the user was pressing the map key and after a moment dropped it
        if(holdingMapKey && !Keyboard.isKeyDown(MapModule.getModule().getMapKey().getKeyBinding().getKeyCode())) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }

        updatePosition(mouseX, mouseY);

        //start rendering
        ScreenRenderer.beginGL(0, 0);

        drawMap(mouseX, mouseY, partialTicks);
        drawIcons(mouseX, mouseY, partialTicks);
        drawCoordinates(mouseX, mouseY, partialTicks);

        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if ((settingsBtn.isMouseOver() || addWaypointBtn.isMouseOver() || waypointMenuBtn.isMouseOver() || pathWaypointMenuBtn.isMouseOver())) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        } else if (mouseButton == 1) {
            updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
            return;
        } else if (mouseButton == 2) {
            // Set compass to middle clicked location
            MapProfile map = MapModule.getModule().getMainMap();
            int worldX = getMouseWorldX(mouseX, map);
            int worldZ = getMouseWorldZ(mouseY, map);
            CompassManager.setCompassLocation(new Location(worldX, 0, worldZ));

            resetCompassMapIcon();
            return;
        }

        if (mouseButton == 0) {
            forEachIcon(c -> {
                if (c.mouseOver(mouseX, mouseY)) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));

                    CompassManager.setCompassLocation(new Location(c.getInfo().getPosX(), 0, c.getInfo().getPosZ()));
                    resetCompassMapIcon();
                }
            });
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!holdingMapKey && keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }


    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn == settingsBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapSettingsUI());
        } else if (btn == addWaypointBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointCreationMenu(null));
        } else if (btn == waypointMenuBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new WaypointOverviewUI());
        } else if (btn == pathWaypointMenuBtn) {
            Minecraft.getMinecraft().displayGuiScreen(new PathWaypointOverwiewUI());
        }
    }
}
