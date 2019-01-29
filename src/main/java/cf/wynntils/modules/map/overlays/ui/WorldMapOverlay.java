package cf.wynntils.modules.map.overlays.ui;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.map.MapModule;
import cf.wynntils.modules.map.instances.MapProfile;
import cf.wynntils.modules.map.overlays.objects.MapIcon;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.MapMarkerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
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

            switch (mmp.getIcon()) {
                case "Content_CorruptedDungeon":
                    texPosX = 0; texPosZ = 0;
                    texSizeX = 15; texSizeZ = 16;
                    break;
                case "Content_Dungeon":
                    texPosX = 16; texPosZ = 0;
                    texSizeX = 31; texSizeZ = 16;
                    break;
                case "Merchant_Accessory":
                    texPosX = 48; texPosZ = 0;
                    texSizeX = 60; texSizeZ = 12;
                    break;
                case "Merchant_Armour":
                    texPosX = 48; texPosZ = 13;
                    texSizeX = 62; texSizeZ = 26;
                    break;
                case "Merchant_Dungeon":
                    texPosX = 61; texPosZ = 0;
                    texSizeX = 75; texSizeZ = 12;
                    break;
                case "Merchant_Emerald":
                    texPosX = 63; texPosZ = 13;
                    texSizeX = 76; texSizeZ = 26;
                    break;
                case "Merchant_Horse":
                    texPosX = 48; texPosZ = 27;
                    texSizeX = 66; texSizeZ = 46;
                    break;
                case "Merchant_KeyForge":
                    texPosX = 0; texPosZ = 17;
                    texSizeX = 14; texSizeZ = 32;
                    break;
                case "Merchant_Liquid":
                    texPosX = 15; texPosZ = 17;
                    texSizeX = 23; texSizeZ = 31;
                    break;
                case "Merchant_Other":
                    texPosX = 0; texPosZ = 33;
                    texSizeX = 10; texSizeZ = 44;
                    break;
                case "Merchant_Potion":
                    texPosX = 67; texPosZ = 27;
                    texSizeX = 81; texSizeZ = 44;
                    break;
                case "Merchant_Powder":
                    texPosX = 35; texPosZ = 34;
                    texSizeX = 47; texSizeZ = 48;
                    break;
                case "Merchant_Scroll":
                    texPosX = 11; texPosZ = 33;
                    texSizeX = 25; texSizeZ = 44;
                    break;
                case "Merchant_Seasail":
                    texPosX = 82; texPosZ = 28;
                    texSizeX = 107; texSizeZ = 44;
                    break;
                case "Merchant_Weapon":
                    texPosX = 76; texPosZ = 0;
                    texSizeX = 88; texSizeZ = 12;
                    break;
                case "NPC_Blacksmith":
                    texPosX = 63; texPosZ = 13;
                    texSizeX = 76; texSizeZ = 26;
                    break;
                case "NPC_GuildMaster":
                    texPosX = 89; texPosZ = 0;
                    texSizeX = 101; texSizeZ = 19;
                    break;
                case "NPC_ItemIdentifier":
                    texPosX = 82; texPosZ = 20;
                    texSizeX = 96; texSizeZ = 27;
                    break;
                case "NPC_PowderMaster":
                    texPosX = 102; texPosZ = 0;
                    texSizeX = 114; texSizeZ = 14;
                    break;
                case "Special_FastTravel":
                    texPosX = 108; texPosZ = 16;
                    texSizeX = 126; texSizeZ = 28;
                    break;
                case "Special_LightRealm":
                    texPosX = 108; texPosZ = 29;
                    texSizeX = 120; texSizeZ = 45;
                    break;
                case "Special_RootsOfCorruption":
                    texPosX = 121; texPosZ = 29;
                    texSizeX = 133; texSizeZ = 40;
                    break;
                case "painting":
                    texPosX = 115; texPosZ = 0;
                    texSizeX = 129; texSizeZ = 15;
                    break;
                case "tnt":
                    texPosX = 134; texPosZ = 17;
                    texSizeX = 148; texSizeZ = 34;
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
            }

            if(texPosX == -100) continue;

            mapIcons.add(new MapIcon(Textures.Map.map_icons, mmp.getName(), mmp.getX(), mmp.getZ(), 7, texPosX, texPosZ, texSizeX, texSizeZ).setRenderer(renderer));
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

        mapIcons.forEach(c -> c.updateAxis(map, width, height, maxX, minX, maxZ, minZ));
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
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

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
        for(MapIcon c : mapIcons) c.drawScreen(mouseX, mouseY, partialTicks);

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

        ScreenRenderer.endGL();
    }

    boolean clicking = false;

    @Override
    public void handleMouseInput() throws IOException {
        clicking = Mouse.isButtonDown(0);

        int mDwehll = Mouse.getEventDWheel();
        if(mDwehll >= 1) {
            if(zoom -5 <= 0) return;
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
