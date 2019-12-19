/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.rendering;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class PointRenderer {

    public static void drawLines(List<Location> locations, CustomColor color) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        GlStateManager.pushMatrix();

        GlStateManager.glLineWidth(3f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Location toCompare = locations.get(0);
        for(Location loc : locations) {

            Vec3d start1 = new Vec3d(loc.toBlockPos());
            Vec3d end1 = new Vec3d(toCompare.toBlockPos());

            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR); {
                buffer.pos(
                        (loc.getX()) - renderManager.viewerPosX,
                        (loc.getY()) - renderManager.viewerPosY,
                        (loc.getZ()) - renderManager.viewerPosZ)
                        .color(color.r, color.g, color.b, color.a).endVertex();
                buffer.pos(
                        (toCompare.getX()) - renderManager.viewerPosX,
                        (toCompare.getY()) - renderManager.viewerPosY,
                        (toCompare.getZ()) - renderManager.viewerPosZ)
                        .color(color.r, color.g, color.b, color.a).endVertex();
            } tess.draw();

            toCompare = loc;
        }

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

    public static void drawCube(BlockPos point, CustomColor color) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Location pointLocation = new Location(point);
        Location c = new Location(
            pointLocation.x - renderManager.viewerPosX,
            pointLocation.y - renderManager.viewerPosY,
            pointLocation.z - renderManager.viewerPosZ
        );

        GlStateManager.pushMatrix();

        GlStateManager.glLineWidth(3f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        RenderGlobal.drawBoundingBox(c.getX(), c.getY(), c.getZ(), c.getX()+1, c.getY()+1, c.getZ()+1, color.r, color.g, color.b, color.a);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

}
