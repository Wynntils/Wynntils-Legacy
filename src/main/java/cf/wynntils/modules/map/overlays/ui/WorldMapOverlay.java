package cf.wynntils.modules.map.overlays.ui;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.map.MapModule;
import cf.wynntils.modules.map.instances.MapProfile;
import cf.wynntils.modules.map.overlays.objects.MapIcon;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.MapMarkerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class WorldMapOverlay extends GuiScreen {

    ScreenRenderer renderer = new ScreenRenderer();

    float centerPositionX;
    float centerPositionZ;
    int zoom = 0;

    ArrayList<MapIcon> mapIcons = new ArrayList<>();

    public WorldMapOverlay() {
        mc = Minecraft.getMinecraft();

        for(MapMarkerProfile mmp : WebManager.getMapMarkers()) {

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
                    texPosX = 112; texPosZ = 0;
                    texSizeX = 148; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Scroll":
                    texPosX = 130; texPosZ = 0;
                    texSizeX = 148; texSizeZ = 17;
                    zoomNeeded = 0;
                    break;
                case "Merchant_Seasail":
                    texPosX = 113; texPosZ = 18;
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
                    texPosX = -100;
                    break;
                case "Special_Rune":
                    texPosX = -100;
                    break;
                case "Content_UltimateDiscovery":
                    texPosX = -100;
                    break;
                case "Content_Cave":
                    texPosX = -100;
                    break;
                case "Content_GrindSpot":
                    texPosX = -100;
                    break;
                case "Merchant_Other":
                    texPosX = -100;
                    break;
                case "Special_LightRealm":
                    texPosX = -100;
                    break;
                case "Special_RootsOfCorruption":
                    texPosX = -100;
                    break;
                case "Merchant_Emerald":
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
        updateCenterPosition(centerPositionX, centerPositionZ);
    }

    float minX = 0; float maxX = 0;
    float minZ = 0; float maxZ = 0;

    private void updateCenterPosition(float centerPositionX, float centerPositionZ) {
        this.centerPositionX = centerPositionX; this.centerPositionZ = centerPositionZ;

        MapProfile map = MapModule.getModule().getMainMap();
        minX = map.getTextureXPosition(centerPositionX) - ((width)/2) - (width*zoom/100); // <--- min texture x point
        minZ = map.getTextureZPosition(centerPositionZ) - ((height)/2) - (height*zoom/100); // <--- min texture z point

        maxX = map.getTextureXPosition(centerPositionX) + ((width)/2) + (width*zoom/100); // <--- max texture x point
        maxZ = map.getTextureZPosition(centerPositionZ) + ((height)/2) + (height*zoom/100); // <--- max texture z point

        mapIcons.forEach(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ, zoom));
    }

    int lastMouseX = -Integer.MAX_VALUE;
    int lastMouseY = -Integer.MAX_VALUE;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!Reference.onWorld || !MapModule.getModule().getMainMap().isReadyToUse()) return;

        //draging
        if(clicking)
            updateCenterPosition(centerPositionX+= (lastMouseX - mouseX), centerPositionZ+= (lastMouseY - mouseY));
        lastMouseX = mouseX; lastMouseY = mouseY;

        //start rendering
        ScreenRenderer.beginGL(0, 0);
        drawDefaultBackground();

        MapProfile map = MapModule.getModule().getMainMap();
        minX /= (float)map.getImageWidth(); maxX /= (float)map.getImageWidth();
        minZ /= (float)map.getImageHeight(); maxZ /= (float)map.getImageHeight();

        try{
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();

            map.bindTexture(); // <--- binds the texture
            GlStateManager.color(1, 1, 1, 1f);

            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager.glBegin(GL11.GL_QUADS); // <--- starts gl_quads
            {
                GlStateManager.glTexCoord2f(maxX,maxZ);
                GlStateManager.glVertex3f(width, height, 0);
                GlStateManager.glTexCoord2f(maxX,minZ);
                GlStateManager.glVertex3f(width, 0, 0);
                GlStateManager.glTexCoord2f(minX,minZ);
                GlStateManager.glVertex3f(0, 0, 0);
                GlStateManager.glTexCoord2f(minX,maxZ);
                GlStateManager.glVertex3f(0 , height, 0);
            }
            GlStateManager.glEnd(); // <--- ends gl_quads
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
            renderer.drawRectF(Textures.Map.pointer, playerPostionX - 5f, playerPostionZ - 5f, playerPostionX + 5f, playerPostionZ + 5f, 0f, 0f, 5f, 5f);
            ScreenRenderer.resetRotation();
        }

        mapIcons.forEach(c -> c.drawHovering(mouseX, mouseY, partialTicks));

        ScreenRenderer.endGL();
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 1) {
            updateCenterPosition((float)mc.player.posX, (float)mc.player.posZ);
            return;
        }

        mapIcons.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

}
