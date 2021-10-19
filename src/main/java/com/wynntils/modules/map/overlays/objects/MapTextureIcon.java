/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Texture;

/**
 * For {@link MapIcon}s which are represented by a sprite sheet texture
 */
public abstract class MapTextureIcon extends MapIcon {

    public abstract Texture getTexture();

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
    public void renderAt(float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        float sizeX = getSizeX() * sizeMultiplier;
        float sizeZ = getSizeZ() * sizeMultiplier;
        beginGL(0, 0);
        drawRectF(
                getTexture(),
                centreX - sizeX, centreZ - sizeZ,
                centreX + sizeX, centreZ + sizeZ,
                getTexPosX(), getTexPosZ(), getTexSizeX(), getTexSizeZ()
        );
        endGL();
    }

    @Override
    public boolean followRotation() {
        return false;
    }

    @Override
    public boolean hasDynamicLocation() {
        return false;
    }

    public static MapTextureIcon createStaticIcon(
            Texture texture,
            int texPosX, int texPosZ, int texSizeX, int texSizeZ
    ) {
        return createStaticIcon(texture, texPosX, texPosZ, texSizeX, texSizeZ, texSizeX - texPosX, texSizeZ - texPosZ);
    }

    /**
     * @return A `MapTextureIcon` instance with the given parameters that can be drawn with `renderAt`
     */
    public static MapTextureIcon createStaticIcon(
            Texture texture,
            int texPosX, int texPosZ, int texSizeX, int texSizeZ,
            int sizeX, int sizeZ
    ) {
        return new MapTextureIcon() {
            @Override public Texture getTexture() { return texture; }
            @Override public int getTexPosX() { return texPosX; }
            @Override public int getTexPosZ() { return texPosZ; }
            @Override public int getTexSizeX() { return texSizeX; }
            @Override public int getTexSizeZ() { return texSizeZ; }
            @Override public int getPosX() { throw new UnsupportedOperationException("Cannot getPosX() on a static icon"); }
            @Override public int getPosZ() { throw new UnsupportedOperationException("Cannot getPosZ() on a static icon"); }
            @Override public String getName() { throw new UnsupportedOperationException("Cannot getName() on a static icon"); }
            @Override public float getSizeX() { return sizeX; }
            @Override public float getSizeZ() { return sizeZ; }
            @Override public int getZoomNeeded() { throw new UnsupportedOperationException("Cannot getZoomNeeded() on a static icon"); }
            @Override public boolean isEnabled(boolean forMinimap) { throw new UnsupportedOperationException("Cannot isEnabled() on a static icon"); }
            @Override public boolean hasDynamicLocation() { throw new UnsupportedOperationException("Cannot hasDynamicLocation() on a static icon"); }
            @Override
            public void renderAt(float centreX, float centreZ, float sizeMultiplier, float blockScale) {
                float ssizeX = sizeX * sizeMultiplier;
                float ssizeZ = sizeZ * sizeMultiplier;
                beginGL(0, 0);
                drawRectF(
                        texture,
                        centreX - ssizeX, centreZ - ssizeZ,
                        centreX + ssizeX, centreZ + ssizeZ,
                        texPosX, texPosZ, texSizeX, texSizeZ
                );
                endGL();
            }
        };
    }

}
