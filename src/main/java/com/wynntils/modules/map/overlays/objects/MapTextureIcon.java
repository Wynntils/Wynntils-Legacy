package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;

/**
 * For {@link MapIcon}s which are represented by a sprite sheet texture
 */
public abstract class MapTextureIcon extends MapIcon {
    public abstract AssetsTexture getTexture();

    /**
     * @return The x position of the top-left of this icon (px)
     */
    public abstract int getTexPosX();

    /**
     * @return The z (y) position of the top-left of the icon (px)
     */
    public abstract int getTexPosZ();

    /**
     * @return One more than the x position of the bottom-right of the icon (px)
     */
    public abstract int getTexSizeX();

    /**
     * @return One more than the z (y) position of the bottom-right of the icon (px)
     */
    public abstract int getTexSizeZ();

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        float sizeX = getSizeX() * sizeMultiplier;
        float sizeZ = getSizeZ() * sizeMultiplier;
        renderer.drawRectF(
                getTexture(),
                centreX - sizeX, centreZ - sizeZ,
                centreX + sizeX, centreZ + sizeZ,
                getTexPosX(), getTexPosZ(), getTexSizeX(), getTexSizeZ()
        );
    }

    @Override
    public boolean followRotation() {
        return false;
    }
}
