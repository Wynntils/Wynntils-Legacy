package cf.wynntils.modules.map.overlays.objects;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.AssetsTexture;
import cf.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.Consumer;

public class MapIcon extends GuiScreen {

    ScreenRenderer renderer;

    AssetsTexture texture;
    int posX, posZ, size;
    String name;
    int texPosX, texPosZ, texSizeX, texSizeZ;

    float axisX = 0; float axisZ = 0;
    boolean shouldRender = false;

    Consumer<Integer> onClick;

    public MapIcon(AssetsTexture texture, String name, int posX, int posZ, int size, int texPosX, int texPosZ, int texSizeX, int texSizeZ) {
        this.texture = texture; this.name = name;
        this.posX = posX; this.posZ = posZ; this.texPosX = texPosX; this.texPosZ = texPosZ; this.texSizeX = texSizeX; this.texSizeZ = texSizeZ;
        this.size = size;

        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
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
        return mouseX >= (axisX - size) && mouseX <= (axisX + size) && mouseY >= (axisZ - size) && mouseY <= (axisZ + size);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(!shouldRender || renderer == null) return;

        renderer.drawRectF(texture, axisX - size, axisZ - size, axisX + size, axisZ + size, texPosX, texPosZ, texSizeX, texSizeZ);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(onClick != null && mouseOver(mouseX, mouseY))
            onClick.accept(mouseButton);
    }


}
