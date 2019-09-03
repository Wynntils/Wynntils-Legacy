/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.objects.MapCompassIcon;
import com.wynntils.modules.map.overlays.objects.MapIcon;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.function.Consumer;

public class MiniMapOverlay extends Overlay {

    public MiniMapOverlay() {
        super("Mini Map", 100, 100, true, 0, 0, 10, 10, OverlayGrowFrom.TOP_LEFT);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if(!Reference.onWorld || e.getType() != RenderGameOverlayEvent.ElementType.ALL || !MapConfig.INSTANCE.enabled) return;
        if(!MapModule.getModule().getMainMap().isReadyToUse()) return;

        MapProfile map = MapModule.getModule().getMainMap();

        //calculates the extra size to avoid rotation overpass
        float extraFactor = 1;
        if (MapConfig.INSTANCE.followPlayerRotation && MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) extraFactor = 1.5f;

        //updates the map size
        int mapSize = MapConfig.INSTANCE.mapSize;
        staticSize = new Point(mapSize, mapSize);

        int zoom = MapConfig.INSTANCE.mapZoom;

        //texture position
        float minX = map.getTextureXPosition(mc.player.posX) - extraFactor * (mapSize/2f + zoom); // <--- min texture x point
        float minZ = map.getTextureZPosition(mc.player.posZ) - extraFactor * (mapSize/2f + zoom); // <--- min texture z point

        float maxX = map.getTextureXPosition(mc.player.posX) + extraFactor * (mapSize/2f + zoom); // <--- max texture x point
        float maxZ = map.getTextureZPosition(mc.player.posZ) + extraFactor * (mapSize/2f + zoom); // <--- max texture z point

        minX /= (float)map.getImageWidth(); maxX /= (float)map.getImageWidth();
        minZ /= (float)map.getImageHeight(); maxZ /= (float)map.getImageHeight();

        float centerX = minX + ((maxX - minX)/2);
        float centerZ = minZ + ((maxZ - minZ)/2);

        if(centerX > 1 || centerX < 0 || centerZ > 1 || centerZ < 0) return;

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
            if(MapConfig.INSTANCE.followPlayerRotation) rotate(180 - MathHelper.fastFloor(mc.player.rotationYaw));

            //map quad
            float extraSize = (extraFactor - 1f) * mapSize/2f;  // How many extra pixels multiplying by extraFactor added on each side
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            GlStateManager.enableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

                bufferbuilder.pos(position.getDrawingX() - extraSize, position.getDrawingY() + mapSize + extraSize, 0).tex(minX, maxZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() + mapSize + extraSize, position.getDrawingY() + mapSize + extraSize, 0).tex(maxX, maxZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() + mapSize + extraSize, position.getDrawingY() - extraSize, 0).tex(maxX, minZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() - extraSize, position.getDrawingY() - extraSize, 0).tex(minX, minZ).endVertex();
                tessellator.draw();
            }


            resetRotation();

            // Draw map icons
            if (MapConfig.INSTANCE.minimapIcons) {
                final float halfMapSize = mapSize / 2f;
                final float scaleFactor = mapSize / (mapSize + 2f * zoom);
                final float sizeMultiplier = 0.8f * MapConfig.INSTANCE.minimapIconSizeMultiplier * (1 - (1 - scaleFactor) * (1 - scaleFactor));
                final double rotationRadians = Math.toRadians(mc.player.rotationYaw);
                final float sinRotationRadians = (float) Math.sin(rotationRadians);
                final float cosRotationRadians = (float) -Math.cos(rotationRadians);

                // These two points for a box bigger than the actual minimap so the icons outside
                // can quickly be filtered out
                final int minFastWorldX = (int) (mc.player.posX - extraFactor * (mapSize/2f + zoom));
                final int minFastWorldZ = (int) (mc.player.posZ - extraFactor * (mapSize/2f + zoom));

                final int maxFastWorldX = (int) (mc.player.posX + extraFactor * (mapSize/2f + zoom)) + 1;
                final int maxFastWorldZ = (int) (mc.player.posZ + extraFactor * (mapSize/2f + zoom)) + 1;

                Consumer<MapIcon> consumer = c -> {
                    int posX = c.getPosX();
                    int posZ = c.getPosZ();
                    float sizeX = c.getSizeX();
                    float sizeZ = c.getSizeZ();
                    if (
                        !c.isEnabled() ||
                        !(minFastWorldX <= posX + sizeX && posX - sizeX <= maxFastWorldX) ||
                        !(minFastWorldZ <= posZ + sizeZ && posZ - sizeZ <= maxFastWorldZ)
                    ) {
                        return;
                    }
                    float dx = (float) (posX - mc.player.posX) * scaleFactor;
                    float dz = (float) (posZ - mc.player.posZ) * scaleFactor;

                    boolean followRotation = false;

                    if (MapConfig.INSTANCE.followPlayerRotation) {
                        // Rotate dx and dz
                        if (followRotation = c.followRotation()) {
                            GlStateManager.pushMatrix();
                            Point drawingOrigin = MiniMapOverlay.drawingOrigin();
                            GlStateManager.translate(drawingOrigin.x + halfMapSize, drawingOrigin.y + halfMapSize, 0);
                            GlStateManager.rotate(180 - mc.player.rotationYaw, 0, 0, 1);
                            GlStateManager.translate(-drawingOrigin.x - halfMapSize, -drawingOrigin.y - halfMapSize, 0);
                        } else {
                            float temp = dx * cosRotationRadians - dz * sinRotationRadians;
                            dz = dx * sinRotationRadians + dz * cosRotationRadians;
                            dx = temp;
                        }
                    }

                    c.renderAt(this, dx + halfMapSize, dz + halfMapSize, sizeMultiplier, scaleFactor);
                    if (followRotation) {
                        GlStateManager.popMatrix();
                    }
                };

                MapIcon.getApiMarkers(MapConfig.INSTANCE.iconTexture).forEach(consumer);
                MapIcon.getWaypoints().forEach(consumer);
                MapIcon.getPathWaypoints().forEach(consumer);

                MapIcon compassIcon = MapIcon.getCompass();

                if (compassIcon.isEnabled()) {
                    float dx = (float) (compassIcon.getPosX() - mc.player.posX) * scaleFactor;
                    float dz = (float) (compassIcon.getPosZ() - mc.player.posZ) * scaleFactor;

                    if (MapConfig.INSTANCE.followPlayerRotation) {
                        float temp = dx * cosRotationRadians - dz * sinRotationRadians;
                        dz = dx * sinRotationRadians + dz * cosRotationRadians;
                        dx = temp;
                    }

                    boolean scaled = false;
                    float newDx = 0;
                    float newDz = 0;
                    final float compassSize = Math.max(compassIcon.getSizeX(), compassIcon.getSizeZ()) * 0.8f * MapConfig.INSTANCE.minimapIconSizeMultiplier;
                    boolean rendering = true;

                    float distanceSq = dx * dx + dz * dz;
                    float maxDistance = halfMapSize - compassSize;

                    if (distanceSq > 16000000f) {
                        rendering = false;
                    } if (MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) {
                        if (!(
                                -maxDistance <= dx && dx <= maxDistance &&
                                -maxDistance <= dz && dz <= maxDistance
                        )) {
                            // Scale them in so that `|newDx| <= maxDistance && |newDz| <= maxDistance`
                            scaled = true;
                            float scale = maxDistance / Math.max(Math.abs(dx), Math.abs(dz));
                            newDx = dx * scale;
                            newDz = dz * scale;
                        }
                    } else {
                        if (distanceSq > maxDistance * maxDistance) {
                            // Scale it down back into the circle
                            float multiplier = maxDistance / (float) Math.sqrt(distanceSq);
                            newDx = dx * multiplier;
                            newDz = dz * multiplier;
                            scaled = true;
                        }
                    }

                    if (rendering && scaled) {
                        float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) + 90f;

                        dx = newDx + halfMapSize;
                        dz = newDz + halfMapSize;

                        Point drawingOrigin = MiniMapOverlay.drawingOrigin();

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(drawingOrigin.x + dx, drawingOrigin.y + dz, 0);
                        GlStateManager.rotate(angle,0,0,1);
                        GlStateManager.translate(-drawingOrigin.x - dx, -drawingOrigin.y - dz, 0);

                        MapCompassIcon.pointer.renderAt(this, dx, dz, sizeMultiplier, 1f);

                        GlStateManager.popMatrix();
                    } else if (rendering) {
                        compassIcon.renderAt(this, dx + halfMapSize, dz + halfMapSize, sizeMultiplier, scaleFactor);
                    }
                }
            }

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            clearMask();

            if(MapConfig.INSTANCE.followPlayerRotation) rotate(180 - MathHelper.fastFloor(mc.player.rotationYaw));

            //cursor & cursor rotation
            rotate(180 + MathHelper.fastFloor(mc.player.rotationYaw));

            MapConfig.PointerType type = MapConfig.Textures.INSTANCE.pointerStyle;

            MapConfig.Textures.INSTANCE.pointerColor.applyColor();
            drawRectF(Textures.Map.map_pointers, (mapSize/2f) - type.dWidth, (mapSize/2f) - type.dHeight, (mapSize/2f) + type.dWidth, (mapSize/2f) + type.dHeight, 0, type.yStart, type.width, type.yStart + type.height);
            GlStateManager.color(1, 1, 1, 1);

            resetRotation();

            if (MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) {
                if (MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Paper) {
                    drawRect(Textures.Map.paper_map_textures, -3, -3, mapSize + 3, mapSize + 3, 0, 0, 217, 217);
                } else if (MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Wynn) {
                    drawRect(Textures.Map.wynn_map_textures, -3, -3, mapSize + 3, mapSize + 3, 0, 0, 112, 112);
                } else if (MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Gilded) {
                    drawRect(Textures.Map.gilded_map_textures, -1, -1, mapSize+1, mapSize+1, 0, 263, 262, 524);
                }
            } else if (MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.CIRCLE) {
                if (MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Paper) {
                    drawRect(Textures.Map.paper_map_textures, -3, -3, mapSize + 3, mapSize + 3, 217, 217, 434, 438);
                } else if(MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Wynn) {
                    //todo texture
                } else if (MapConfig.Textures.INSTANCE.textureType == MapConfig.TextureType.Gilded) {
                    drawRect(Textures.Map.gilded_map_textures, -1, -1, mapSize+1, mapSize+1, 0, 0, 262, 262);
                }
            }

            // Direction Text
            if (MapConfig.INSTANCE.showCompass) {
                if (MapConfig.INSTANCE.followPlayerRotation) {
                    float mapCentre = (float) mapSize / 2f;
                    float yawRadians = (float) Math.toRadians(mc.player.rotationYaw);
                    float northDX = mapCentre * MathHelper.sin(yawRadians);
                    float northDY = mapCentre * MathHelper.cos(yawRadians);
                    if (MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.SQUARE) {
                        // Scale by sec((offset from 90 degree angle)) to map to tangent from offset point
                        float circleToSquareScale = MathHelper.cos((float) Math.toRadians((mc.player.rotationYaw % 360f + 405f) % 90f - 45f));
                        northDX /= circleToSquareScale;
                        northDY /= circleToSquareScale;
                    }
                    drawString("N", mapCentre - 2 + northDX, mapCentre - 3 + northDY, CommonColors.WHITE);
                    if (!MapConfig.INSTANCE.northOnly) {
                        drawString("E", mapCentre - 2 - northDY, mapCentre - 3 + northDX, CommonColors.WHITE);
                        drawString("S", mapCentre - 2 - northDX, mapCentre - 3 - northDY, CommonColors.WHITE);
                        drawString("W", mapCentre - 2 + northDY, mapCentre - 3 - northDX, CommonColors.WHITE);
                    }
                } else {
                    float mapCentre = (float) mapSize / 2f;
                    drawString("N", mapCentre - 2, -3, CommonColors.WHITE);
                    if (!MapConfig.INSTANCE.northOnly) {
                        drawString("E", mapSize - 2, mapCentre - 3, CommonColors.WHITE);
                        drawString("S", mapCentre - 2, mapSize - 3, CommonColors.WHITE);
                        drawString("W", -2, mapCentre - 3, CommonColors.WHITE);
                    }
                }
            }

            if (MapConfig.INSTANCE.showCoords) {
                drawString(
                        String.join(", ", Integer.toString((int) mc.player.posX), Integer.toString((int) mc.player.posY), Integer.toString((int) mc.player.posZ)),
                        mapSize / 2f, mapSize + 6, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE
                );
            }

        }catch (Exception ex) { ex.printStackTrace(); }
    }

}
