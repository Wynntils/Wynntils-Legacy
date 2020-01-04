/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.rendering;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Texture;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.List;

public class PointRenderer {

    public static void drawTexturedLines(Texture texture, List<Location> points, List<Vector3d> directions, CustomColor color, float width) {
        if (points.size() <= 1) return;

        double maxDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16;
        Point3d player = new Point3d(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ);

        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        color.applyColor();

        texture.bind();

        for (int i = 0; i < points.size(); ++i) {
            Point3d start = new Point3d(points.get(i));
            if (start.distance(player) > maxDistance) continue;

            Vector3d direction = new Vector3d(directions.get(i));
            Point3d end = new Point3d(points.get(i));
            start.y -= .24;

            direction.normalize();
            end.add(direction);
            end.y -= .24;

            drawTexturedLine(start, end, width);
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public static void drawTexturedLine(Texture texture, Point3d start, Point3d end, CommonColors color, float width) {
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        color.applyColor();

        texture.bind();

        drawTexturedLine(start, end, width);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private static void drawTexturedLine(Point3d start, Point3d end, float width) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Vector3d direction = new Vector3d(start);
        direction.sub(end);
        direction.normalize();

        Vector3d rotationAxis = new Vector3d();
        rotationAxis.cross(new Vector3d(direction.x, 0, direction.z), new Vector3d(0, 1, 0));

        Matrix3d transformation = new Matrix3d();
        transformation.set(new AxisAngle4d(rotationAxis, -Math.PI / 2));

        Vector3d normal = new Vector3d(direction);
        transformation.transform(normal);
        normal.cross(new Vector3d(direction.x, 0, direction.z), normal);
        normal.normalize();

        Vec3d scaled = new Vec3d(normal.x, normal.y, normal.z).scale(width);

        // we need 4 points for rendering
        Vec3d startVec = new Vec3d(start.x, start.y, start.z);
        Vec3d endVec = new Vec3d(end.x, end.y, end.z);
        Vec3d p1 = startVec.add(scaled);
        Vec3d p2 = startVec.subtract(scaled);
        Vec3d p3 = endVec.add(scaled);
        Vec3d p4 = endVec.subtract(scaled);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        { buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);

            buffer.pos(p1.x - renderManager.viewerPosX, p1.y - renderManager.viewerPosY, p1.z - renderManager.viewerPosZ)
                    .tex(0f, 0f).endVertex();
            buffer.pos(p3.x - renderManager.viewerPosX, p3.y - renderManager.viewerPosY, p3.z - renderManager.viewerPosZ)
                    .tex(1f, 0f).endVertex();
            buffer.pos(p4.x - renderManager.viewerPosX, p4.y - renderManager.viewerPosY, p4.z - renderManager.viewerPosZ)
                    .tex(1f, 1f).endVertex();
            buffer.pos(p2.x - renderManager.viewerPosX, p2.y - renderManager.viewerPosY, p2.z - renderManager.viewerPosZ)
                    .tex(0f, 1f).endVertex();

        } tess.draw();
    }

    public static void drawLines(List<Location> locations, CustomColor color) {
        if (locations.isEmpty()) return;

        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        GlStateManager.glLineWidth(3f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        color.applyColor();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);

        { buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

            for (Location loc : locations) {
                buffer.pos(loc.x, loc.y, loc.z).endVertex();
            }
        } tess.draw();

        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public static void drawCube(BlockPos point, CustomColor color) {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

        Location c = new Location(
            point.getX() - renderManager.viewerPosX,
            point.getY() - renderManager.viewerPosY,
            point.getZ() - renderManager.viewerPosZ
        );

        GlStateManager.pushMatrix();

        GlStateManager.glLineWidth(3f);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        RenderGlobal.drawBoundingBox(c.getX(), c.getY(), c.getZ(), c.getX()+1, c.getY()+1, c.getZ()+1, color.r, color.g, color.b, color.a);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

}
