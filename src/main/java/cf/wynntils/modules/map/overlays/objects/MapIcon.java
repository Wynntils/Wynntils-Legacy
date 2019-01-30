package cf.wynntils.modules.map.overlays.objects;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.AssetsTexture;
import cf.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class MapIcon {

    ScreenRenderer renderer;

    AssetsTexture texture;
    int posX, posZ;
    String name;
    int texPosX, texPosZ, texSizeX, texSizeZ;

    float sizeX, sizeZ;

    float axisX = 0; float axisZ = 0;
    boolean shouldRender = false;

    Consumer<Integer> onClick;

    int zoomNeded = -1000;

    public MapIcon(AssetsTexture texture, String name, int posX, int posZ, float size, int texPosX, int texPosZ, int texSizeX, int texSizeZ) {
        this.texture = texture; this.name = name;
        this.posX = posX; this.posZ = posZ; this.texPosX = texPosX; this.texPosZ = texPosZ; this.texSizeX = texSizeX; this.texSizeZ = texSizeZ;
        this.sizeX = (texSizeX - texPosX) / size;
        this.sizeZ = (texSizeZ - texPosZ) / size;
    }

    public MapIcon setZoomNeded(int zoomNeded) {
        this.zoomNeded = zoomNeded;

        return this;
    }

    public MapIcon setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public MapIcon setOnClick(Consumer<Integer> onClick) {
        this.onClick = onClick;

        return this;
    }

    public void updateAxis(MapProfile mp, int width, int height, float maxX, float minX, float maxZ, float minZ) {
        axisX = ((mp.getTextureXPosition(posX) - minX) / (maxX - minX));
        axisZ = ((mp.getTextureZPosition(posZ) - minZ) / (maxZ - minZ));

        if(axisX > 0 && axisX < 1 && axisZ > 0 && axisZ < 1) {
            shouldRender = true;
            axisX = width * axisX;
            axisZ = height * axisZ;
        }
    }

    public boolean mouseOver(int mouseX, int mouseY) {
        return mouseX >= (axisX - sizeX) && mouseX <= (axisX + sizeX) && mouseY >= (axisZ - sizeZ) && mouseY <= (axisZ + sizeZ);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks, int zoom) {
        if(!shouldRender || renderer == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        float alpha = 0;
        if(zoomNeded != -1000) {
            alpha = 1 - ((zoom - zoomNeded) / 40.0f);

            if(alpha <= 0) {
                GlStateManager.popMatrix();
                return;
            }
        }
        GlStateManager.color(1, 1, 1, alpha);
        renderer.drawRectF(texture, axisX - sizeX, axisZ - sizeZ, axisX + sizeX, axisZ + sizeZ, texPosX, texPosZ, texSizeX, texSizeZ);
        GlStateManager.color(1,1,1,1);
        GlStateManager.popMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(onClick != null && mouseOver(mouseX, mouseY))
            onClick.accept(mouseButton);
    }

}
