/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
            GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            GlStateManager.enableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

                bufferbuilder.pos(position.getDrawingX() - extraSize/2.0f, position.getDrawingY() + mapSize + extraSize/2, 0).tex(minX, maxZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() + mapSize + extraSize/2, position.getDrawingY() + mapSize + extraSize/2, 0).tex(maxX, maxZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() + mapSize + extraSize/2, position.getDrawingY() - extraSize/2.0f, 0).tex(maxX, minZ).endVertex();
                bufferbuilder.pos(position.getDrawingX() - extraSize/2.0f, position.getDrawingY() - extraSize/2.0f, 0).tex(minX, minZ).endVertex();
                tessellator.draw();
            }
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            clearMask();

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
            // TODO: Optimise
            if (MapConfig.INSTANCE.showCompass) {
                if (MapConfig.INSTANCE.followPlayerRotation) {
                    float mapCentre = (float) mapSize / 2f;
                    float mapCentreSquare = mapCentre * MathHelper.SQRT_2;
                    if (MapConfig.INSTANCE.mapFormat == MapConfig.MapFormat.CIRCLE) {
                        drawString("N", mapCentre - 2 + mapCentre * MathHelper.cos((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) - (Math.PI / 2))), mapCentre - 3 + mapCentre * MathHelper.sin((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) - (Math.PI / 2))), CommonColors.WHITE);
                        if (!MapConfig.INSTANCE.northOnly) {
                            drawString("E", mapCentre - 2 + mapCentre * MathHelper.cos((float) Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw))), mapCentre - 3 + mapCentre * MathHelper.sin((float) Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw))), CommonColors.WHITE);
                            drawString("S", mapCentre - 2 + mapCentre * MathHelper.cos((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) + (Math.PI / 2))), mapCentre - 3 + mapCentre * MathHelper.sin((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) + (Math.PI / 2))), CommonColors.WHITE);
                            drawString("W", mapCentre - 2 + mapCentre * MathHelper.cos((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) + Math.PI)), mapCentre - 3 + mapCentre * MathHelper.sin((float) (Math.toRadians(180 - MathHelper.fastFloor(mc.player.rotationYaw)) + Math.PI)), CommonColors.WHITE);
                        }
                    } else {
                        int limitedDeg = (180 - MathHelper.fastFloor(mc.player.rotationYaw)) % 360;
                        if (limitedDeg < 0)
                            limitedDeg += 360;
                        if (limitedDeg <= 45 || limitedDeg > 315) {
                            drawString("N", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) - (Math.PI / 2))), -3, CommonColors.WHITE);
                            if (!MapConfig.INSTANCE.northOnly) {
                                drawString("E", mapSize - 2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) Math.toRadians(limitedDeg)), CommonColors.WHITE);
                                drawString("S", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) + (Math.PI / 2))), mapSize - 3, CommonColors.WHITE);
                                drawString("W", -2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) + Math.PI)), CommonColors.WHITE);
                            }
                        } else if (limitedDeg <= 135) {
                            drawString("N", mapSize - 2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) - (Math.PI / 2))), CommonColors.WHITE);
                            if (!MapConfig.INSTANCE.northOnly) {
                                drawString("E", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) Math.toRadians(limitedDeg)), mapSize - 3, CommonColors.WHITE);
                                drawString("S", -2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) + (Math.PI / 2))), CommonColors.WHITE);
                                drawString("W", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) + Math.PI)), -3, CommonColors.WHITE);
                            }
                        } else if (limitedDeg <= 225) {
                            drawString("N", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) - (Math.PI / 2))), mapSize - 3, CommonColors.WHITE);
                            if (!MapConfig.INSTANCE.northOnly) {
                                drawString("E", -2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) Math.toRadians(limitedDeg)), CommonColors.WHITE);
                                drawString("S", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) + (Math.PI / 2))), -3, CommonColors.WHITE);
                                drawString("W", mapSize - 2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) + Math.PI)), CommonColors.WHITE);
                            }
                        } else {
                            drawString("N", -2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) - (Math.PI / 2))), CommonColors.WHITE);
                            if (!MapConfig.INSTANCE.northOnly) {
                                drawString("E", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) Math.toRadians(limitedDeg)), -3, CommonColors.WHITE);
                                drawString("S", mapSize - 2, mapCentre - 3 + mapCentreSquare * MathHelper.sin((float) (Math.toRadians(limitedDeg) + (Math.PI / 2))), CommonColors.WHITE);
                                drawString("W", mapCentre - 2 + mapCentreSquare * MathHelper.cos((float) (Math.toRadians(limitedDeg) + Math.PI)), mapSize - 3, CommonColors.WHITE);
                            }
                        }
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

            //GlStateManager.disableTexture2D();

        }catch (Exception ex) { ex.printStackTrace(); }
    }

}
