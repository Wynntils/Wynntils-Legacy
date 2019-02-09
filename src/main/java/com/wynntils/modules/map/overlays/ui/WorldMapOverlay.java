/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.objects.MapIcon;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class WorldMapOverlay extends GuiScreen {

    private ScreenRenderer renderer = new ScreenRenderer();

    private GuiButton settingsBtn = new GuiButton(1,5,6,60,18, "Settings...");
    private GuiButton wayPointsBtn = new GuiButton(2,5,27,60,18, "Waypoints");
    private float centerPositionX;
    private float centerPositionZ;
    private int zoom = 0;

    private ArrayList<MapIcon> mapIcons = new ArrayList<>();

    public WorldMapOverlay() {
        mc = Minecraft.getMinecraft();

        for(MapMarkerProfile mmp : WebManager.getMapMarkers()) {
            if (MapConfig.INSTANCE.enabledMapIcons.containsKey(mmp.getIcon()) && !MapConfig.INSTANCE.enabledMapIcons.get(mmp.getIcon())) { continue; }

            int texPosX = 0; int texPosZ = 0;
            int texSizeX = 16; int texSizeZ = 16;

            int zoomNeeded = -1000;
            int size = 2;

            switch (mmp.getIcon()) {
                case "Content_CorruptedDungeon":
                    texPosX = 12; texPosZ = 17;
                    texSizeX = 24; texSizeZ = 29;
                    break;
                case "Content_Dungeon":
                    texPosX = 0; texPosZ = 17;
                    texSizeX = 12; texSizeZ = 29;
                    break;
                case "Merchant_Accessory":
                    texPosX = 0; texPosZ = 0;
                    texSizeX = 17; texSizeZ = 16;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Armour":
                    texPosX = 17; texPosZ = 0;
                    texSizeX = 34; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Dungeon":
                    texPosX = 34; texPosZ = 0;
                    texSizeX = 49; texSizeZ = 15;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Horse":
                    texPosX = 49; texPosZ = 0;
                    texSizeX = 65; texSizeZ = 17;
                    break;
                case "Merchant_KeyForge":
                    texPosX = 65; texPosZ = 0;
                    texSizeX = 82; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Liquid":
                    texPosX = 82; texPosZ = 0;
                    texSizeX = 97; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Potion":
                    texPosX = 97; texPosZ = 0;
                    texSizeX = 113; texSizeZ = 18;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Powder":
                    texPosX = 113; texPosZ = 0;
                    texSizeX = 130; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Scroll":
                    texPosX = 130; texPosZ = 0;
                    texSizeX = 148; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Seasail":
                    texPosX = 113; texPosZ = 17;
                    texSizeX = 131; texSizeZ = 35;
                    break;
                case "Merchant_Weapon":
                    texPosX = 148; texPosZ = 0;
                    texSizeX = 166; texSizeZ = 15;
                    zoomNeeded = 0;
                    break;
                case "NPC_Blacksmith":
                    texPosX = 147; texPosZ = 17;
                    texSizeX = 165; texSizeZ = 35;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "NPC_GuildMaster":
                    texPosX = 42; texPosZ = 17;
                    texSizeX = 60; texSizeZ = 35;
                    zoomNeeded = 0;
                    break;
                case "NPC_ItemIdentifier":
                    texPosX = 60; texPosZ = 18;
                    texSizeX = 78; texSizeZ = 35;
                    break;
                case "NPC_PowderMaster":
                    texPosX = 96; texPosZ = 18;
                    texSizeX = 113; texSizeZ = 35;
                    zoomNeeded = 0;
                    break;
                case "Special_FastTravel":
                    texPosX = 24; texPosZ = 17;
                    texSizeX = 42; texSizeZ = 35;
                    zoomNeeded = 0;
                    break;
                case "tnt":
                    texPosX = 131; texPosZ = 17;
                    texSizeX = 147; texSizeZ = 35;
                    zoomNeeded = 0;
                    break;
                case "Ore_Refinery":
                    texPosX = 199; texPosZ = 0;
                    texSizeX = 217; texSizeZ = 16;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Fish_Refinery":
                    texPosX = 182; texPosZ = 0;
                    texSizeX = 199; texSizeZ = 17;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Wood_Refinery":
                    texPosX = 217; texPosZ = 0;
                    texSizeX = 234; texSizeZ = 18;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Crop_Refinery":
                    texPosX = 166; texPosZ = 0;
                    texSizeX = 182; texSizeZ = 18;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "MarketPlace":
                    texPosX = 78; texPosZ = 17;
                    texSizeX = 96; texSizeZ = 35;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Content_Quest":
                    texPosX = 165; texPosZ = 18;
                    texSizeX = 183; texSizeZ = 36;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Special_Rune":
                    texPosX = 64; texPosZ = 35;
                    texSizeX = 82; texSizeZ = 53;
                    zoomNeeded = 0;
                    break;
                case "Content_UltimateDiscovery":
                    texPosX = 201; texPosZ = 18;
                    texSizeX = 219; texSizeZ = 36;
                    zoomNeeded = 0;
                    break;
                case "Content_Cave":
                    texPosX = 234; texPosZ = 0;
                    texSizeX = 250; texSizeZ = 18;
                    zoomNeeded = 0;
                    break;
                case "Content_GrindSpot":
                    texPosX = 221; texPosZ = 20;
                    texSizeX = 233; texSizeZ = 32;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Other":
                    texPosX = 0; texPosZ = 29;
                    texSizeX = 18; texSizeZ = 47;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "Special_LightRealm":
                    texPosX = 52; texPosZ = 35;
                    texSizeX = 64; texSizeZ = 53;
                    zoomNeeded = 0;
                    break;
                case "Special_RootsOfCorruption":
                    texPosX = 38; texPosZ = 35;
                    texSizeX = 50; texSizeZ = 53;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Emerald":
                    texPosX = 233; texPosZ = 18;
                    texSizeX = 250; texSizeZ = 36;
                    zoomNeeded = 0;
                    size = 3;
                    break;
                case "painting":
                    texPosX = 18; texPosZ = 35;
                    texSizeX = 36; texSizeZ = 53;
                    zoomNeeded = 0;
                    break;
                default:
                    texPosX = -100;
                    break;
            }

            if(texPosX == -100) continue;

            MapIcon mp = new MapIcon(Textures.Map.map_icons, mmp.getName(), mmp.getX(), mmp.getZ(), size, texPosX, texPosZ, texSizeX, texSizeZ).setRenderer(renderer).setZoomNeded(zoomNeeded);
            mp.setOnClick(c -> {
                if(c == 0) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f));
                    ModCore.mc().world.setSpawnPoint(new BlockPos(mmp.getX(), 0, mmp.getZ()));
                }
            });

            mapIcons.add(mp);
        }

        updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
    }

    @Override
    public void initGui() {
        super.initGui();
        wayPointsBtn.enabled = Reference.developmentEnvironment;
        this.buttonList.add(settingsBtn);
        this.buttonList.add(wayPointsBtn);
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    float minX = 0; float maxX = 0;
    float minZ = 0; float maxZ = 0;

    private void updateCenterPosition(float centerPositionX, float centerPositionZ) {
        this.centerPositionX = centerPositionX; this.centerPositionZ = centerPositionZ;

        MapProfile map = MapModule.getModule().getMainMap();

        minX = map.getTextureXPosition(centerPositionX) - ((width)/2.0f) - (width*zoom/100.0f); // <--- min texture x point
        minZ = map.getTextureZPosition(centerPositionZ) - ((height)/2.0f) - (height*zoom/100.0f); // <--- min texture z point

        maxX = map.getTextureXPosition(centerPositionX) + ((width)/2.0f) + (width*zoom/100.0f); // <--- max texture x point
        maxZ = map.getTextureZPosition(centerPositionZ) + ((height)/2.0f) + (height*zoom/100.0f); // <--- max texture z point

        mapIcons.forEach(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
    }

    int lastMouseX = -Integer.MAX_VALUE;
    int lastMouseY = -Integer.MAX_VALUE;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!Reference.onWorld || !MapModule.getModule().getMainMap().isReadyToUse()) return;

        //draging
        if(clicking && !(wayPointsBtn.isMouseOver() || settingsBtn.isMouseOver())) {
            float acceleration = (1f + zoom/100f); //<---- this is basically 1.0~10 || Min = 1.0 Max = 2.0
            updateCenterPosition(centerPositionX += (lastMouseX - mouseX) * acceleration, centerPositionZ += (lastMouseY - mouseY) * acceleration);
        }
        lastMouseX = mouseX; lastMouseY = mouseY;

        //start rendering
        ScreenRenderer.beginGL(0, 0);
        drawDefaultBackground();

        MapProfile map = MapModule.getModule().getMainMap();
        minX /= (float)map.getImageWidth(); maxX /= (float)map.getImageWidth();
        minZ /= (float)map.getImageHeight(); maxZ /= (float)map.getImageHeight();

        try{
            GlStateManager.enableAlpha();
            GlStateManager.color(1, 1, 1, 1f);
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            map.bindTexture(); // <--- binds the texture
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

                bufferbuilder.pos(0, height, 0).tex(minX, maxZ).endVertex();
                bufferbuilder.pos(width, height, 0).tex(maxX, maxZ).endVertex();
                bufferbuilder.pos(width, 0, 0).tex(maxX, minZ).endVertex();
                bufferbuilder.pos(0, 0, 0).tex(minX, minZ).endVertex();
                tessellator.draw();

            }

        }catch (Exception ignored) {}

        //draw map icons
        mapIcons.forEach(c -> c.drawScreen(mouseX, mouseY, partialTicks));

        minX = minX*map.getImageWidth(); maxX = maxX*map.getImageWidth();
        minZ = minZ*map.getImageHeight(); maxZ = maxZ*map.getImageHeight();

        float playerPostionX = (map.getTextureXPosition(mc.player.posX) - minX) / (maxX - minX);
        float playerPostionZ = (map.getTextureZPosition(mc.player.posZ) - minZ) / (maxZ - minZ);

        if(playerPostionX > 0 && playerPostionX < 1 && playerPostionZ > 0 && playerPostionZ < 1) { // <--- player position
            playerPostionX = width * playerPostionX;
            playerPostionZ = height * playerPostionZ;

            ScreenRenderer.transformationOrigin((int)playerPostionX, (int)playerPostionZ);
            ScreenRenderer.rotate(180 + MathHelper.fastFloor(mc.player.rotationYaw));
            MapConfig.PointerType type = MapConfig.INSTANCE.pointerStyle;
            MapConfig.PointerColor color = MapConfig.INSTANCE.pointerColor;

            renderer.drawRectF(Textures.Map.map_pointers, playerPostionX - type.dWidth*1.5f, playerPostionZ - type.dHeight*1.5f, playerPostionX + type.dWidth*1.5f, playerPostionZ + type.dHeight*1.5f, 0, type.yStart + (color.index * type.height), type.width, type.yStart + ((color.index+1) * type.height));

            ScreenRenderer.resetRotation();
        }

        mapIcons.forEach(c -> c.drawHovering(mouseX, mouseY, partialTicks));

        ScreenRenderer.endGL();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    boolean clicking = false;

    @Override
    public void handleMouseInput() throws IOException {
        clicking = Mouse.isButtonDown(0);

        int mDwehll = Mouse.getEventDWheel();
        if(mDwehll >= 1) {
            if(zoom -5 < 0) return;
            else {
                zoom-=5;
                updateCenterPosition(centerPositionX, centerPositionZ);
            }
        }else if(mDwehll <= -1) {
            if(zoom+5 > 130) return;
            else{
                zoom+=5;
                updateCenterPosition(centerPositionX, centerPositionZ);
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (wayPointsBtn.isMouseOver() || settingsBtn.isMouseOver()) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (mouseButton == 1) {
            updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
            return;
        }

        mapIcons.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() || keyCode == MapModule.getModule().getMapKey().getKeyBinding().getKeyCode()) {
            Minecraft.getMinecraft().player.closeScreen();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn.id == 1) { //Settings...
            Minecraft.getMinecraft().displayGuiScreen(new WorldMapSettingsUI());
        } else if (btn.id == 2) { //Waypoints
            //TODO waypoints
        }
    }
}
