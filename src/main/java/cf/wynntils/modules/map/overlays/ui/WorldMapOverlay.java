package cf.wynntils.modules.map.overlays.ui;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.map.MapModule;
import cf.wynntils.modules.map.instances.MapProfile;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.MapMarkerProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class WorldMapOverlay extends GuiScreen {

    ScreenRenderer renderer = new ScreenRenderer();

    float centerPositionX;
    float centerPositionZ;

    int zoom = 0;

    public WorldMapOverlay() {
        mc = Minecraft.getMinecraft();

        centerPositionX = (float)mc.player.posX;
        centerPositionZ = (float)mc.player.posZ;
    }

    int lastMouseX = -Integer.MAX_VALUE;
    int lastMouseY = -Integer.MAX_VALUE;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!Reference.onWorld || !MapModule.getModule().getMainMap().isReadyToUse()) return;

        //map drag
        if(lastMouseX == -Integer.MAX_VALUE) {
            lastMouseX = mouseX; lastMouseY = mouseY;
        }

        if(clicking) {
            centerPositionX+= lastMouseX - mouseX;
            centerPositionZ+= lastMouseY - mouseY;
        }

        lastMouseX = mouseX; lastMouseY = mouseY;

        //start rendering
        ScreenRenderer.beginGL(0, 0);
        drawDefaultBackground();

        MapProfile map = MapModule.getModule().getMainMap();

        float minX = map.getTextureXPosition(centerPositionX) - ((width)/2) - (width*zoom/100); // <--- min texture x point
        float minZ = map.getTextureZPosition(centerPositionZ) - ((height)/2) - (height*zoom/100); // <--- min texture z point

        float maxX = map.getTextureXPosition(centerPositionX) + ((width)/2) + (width*zoom/100); // <--- max texture x point
        float maxZ = map.getTextureZPosition(centerPositionZ) + ((height)/2) + (height*zoom/100); // <--- max texture z point

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

        for(MapMarkerProfile c : WebManager.getMapMarkers()) { // <--- starting the render of waypoints
            float x = ((map.getTextureXPosition(c.getX()) - minX) / (maxX - minX));
            float z = ((map.getTextureZPosition(c.getZ()) - minZ) / (maxZ - minZ));

            if(x > 0 && x < 1 && z > 0 && z < 1) {
                renderer.drawString(c.getIcon(), width * x, height * z, CommonColors.WHITE);
            }
        }

        ScreenRenderer.endGL();
    }

    boolean clicking = false;

    @Override
    public void handleMouseInput() throws IOException {
        int mDwehll = Mouse.getEventDWheel();
        if(mDwehll >= 1) {
            if(zoom -5 <= 0) zoom = 0;
            else zoom-=5;
        }else if(mDwehll <= -1) {
            if(zoom+5 > 130) zoom = 130;
            else zoom+=5;
        }

        clicking = Mouse.isButtonDown(0);

        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton == 1) {
            centerPositionX = (float)mc.player.posX; centerPositionZ = (float)mc.player.posZ;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

}
