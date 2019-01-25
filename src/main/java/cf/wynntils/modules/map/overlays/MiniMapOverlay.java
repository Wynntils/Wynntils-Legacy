package cf.wynntils.modules.map.overlays;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.map.MapModule;
import cf.wynntils.modules.map.configs.MapConfig;
import cf.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MiniMapOverlay extends Overlay {

    public MiniMapOverlay() {
        super("Mini Map", 100, 100, true, 0, 0, 10, 10, OverlayGrowFrom.TOP_LEFT);
    }

    private static int zoom = 100;

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if(!Reference.onWorld || e.getType() != RenderGameOverlayEvent.ElementType.ALL || !MapConfig.INSTANCE.enabled) return;
        if(!MapModule.getModule().getMainMap().isReadyToUse()) return;

        MapProfile map = MapModule.getModule().getMainMap();

        //calculates the extra size to avoid rotation overpass
        int extraSize = 0;
        if(MapConfig.INSTANCE.followPlayerRotation && MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) extraSize = 100;

        //updates the map size
        int mapSize = MapConfig.INSTANCE.mapSize;
        staticSize = new Point(mapSize, mapSize);

        zoom = MapConfig.INSTANCE.mapZoom;

        //texture position
        float minX = map.getTextureXPosition(mc.player.posX) - ((mapSize + extraSize)/2) - zoom; // <--- min texture x point
        float minZ = map.getTextureZPosition(mc.player.posZ) - ((mapSize + extraSize)/2) - zoom; // <--- min texture z point

        float maxX = map.getTextureXPosition(mc.player.posX) + ((mapSize + extraSize)/2) + zoom; // <--- max texture x point
        float maxZ = map.getTextureZPosition(mc.player.posZ) + ((mapSize + extraSize)/2) + zoom; // <--- max texture z point

        minX /= (float)map.getImageWidth(); maxX /= (float)map.getImageWidth();
        minZ /= (float)map.getImageHeight(); maxZ /= (float)map.getImageHeight();

        try{
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();

            //textures & masks
            if(MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.CIRCLE) {
                createMask(Textures.Masks.circle, 0, 0, mapSize, mapSize);
            }else{
                createMask(Textures.Masks.full, 0, 0, mapSize, mapSize);
            }

            //map texture
            map.bindTexture();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            //rotation axis
            transformationOrigin(mapSize/2, mapSize/2);
            if(MapConfig.INSTANCE.followPlayerRotation)
                rotate(180 - MathHelper.fastFloor(mc.player.rotationYaw));

            //map quad
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GlStateManager.glBegin(GL11.GL_QUADS);
            {
                GlStateManager.glTexCoord2f(maxX,maxZ);
                GlStateManager.glVertex3f(position.getDrawingX() + mapSize + extraSize/2, position.getDrawingY() + mapSize + extraSize/2, 0);
                GlStateManager.glTexCoord2f(maxX,minZ);
                GlStateManager.glVertex3f(position.getDrawingX() + mapSize + extraSize/2, position.getDrawingY() - extraSize/2, 0);
                GlStateManager.glTexCoord2f(minX,minZ);
                GlStateManager.glVertex3f(position.getDrawingX() - extraSize/2,position.getDrawingY() - extraSize/2, 0);
                GlStateManager.glTexCoord2f(minX,maxZ);
                GlStateManager.glVertex3f(position.getDrawingX() - extraSize/2 ,position.getDrawingY() + mapSize + extraSize/2, 0);
            }
            GlStateManager.glEnd();
            clearMask();

            //cursor & cursor rotation
            rotate(180 + MathHelper.fastFloor(mc.player.rotationYaw));
            drawRectF(Textures.Map.pointer, mapSize/2 - 2.5f, mapSize/2 - 2.5f, mapSize/2 + 2.5f, mapSize/2 + 2.5f, 0f, 0f, 5f, 5f);
            resetRotation();

            if(MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) {
                if(MapConfig.INSTANCE.textureType == MapConfig.TextureType.Paper)
                    drawRect(Textures.Map.paper_map_textures, -3, -3, mapSize + 3, mapSize + 3, 0, 0, 217, 217);
                else if(MapConfig.INSTANCE.textureType == MapConfig.TextureType.Wynn)
                    drawRect(Textures.Map.wynn_map_textures, -3, -3, mapSize + 3, mapSize + 3, 0, 0, 112, 112);
            }else if(MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.CIRCLE) {
                if(MapConfig.INSTANCE.textureType == MapConfig.TextureType.Paper)
                    drawRect(Textures.Map.paper_map_textures, -3, -3, mapSize + 3, mapSize + 3, 217, 217, 434, 438);
                else if(MapConfig.INSTANCE.textureType == MapConfig.TextureType.Wynn) {
                    //todo texture
                }
            }

            //GlStateManager.disableTexture2D();

        }catch (Exception ex) { ex.printStackTrace(); }
    }

}
