package com.wynntils.modules.map.managers;

import com.wynntils.McIf;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class NametagManager {

  public static void renderWaypointName(String name, double x, double y, double z) {
    GlStateManager.color(1f, 1f, 1f, 1f);
    GlStateManager.disableBlend();
    GlStateManager.enableCull();

    GlStateManager.alphaFunc(516, 0.1f);
    GlStateManager.pushMatrix();
    Entity viewer = McIf.mc().getRenderViewEntity();
    if (viewer == null) return;

    double newX = x - McIf.mc().getRenderManager().viewerPosX;
    double newY = y - McIf.mc().getRenderManager().viewerPosY;
    double newZ = z - McIf.mc().getRenderManager().viewerPosZ;

    double distSq = newX * newX + newY * newY + newZ * newZ;
    double dist = Math.sqrt(distSq);

    if (distSq > 144) {
      newX *= 12 / dist;
      newY *= 12 / dist;
      newZ *= 12 / dist;
    }

    GlStateManager.translate(newX, newY, newZ);
    GlStateManager.translate(0f, viewer.getEyeHeight(), 0f);

    drawNametag(name);

    GlStateManager.rotate(-McIf.mc().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
    GlStateManager.rotate(McIf.mc().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);

    GlStateManager.translate(0f, -0.25f, 0f);

    GlStateManager.rotate(-McIf.mc().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
    GlStateManager.rotate(McIf.mc().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);

    drawNametag(TextFormatting.YELLOW.toString() + Math.round(dist) + "m");

    GlStateManager.popMatrix();
    GlStateManager.disableLighting();
  }

  private static void drawNametag(String text) { // Draws a nametag on the given position, visible to the player at all times
    FontRenderer fontRenderer = McIf.mc().fontRenderer;

    double f = 1.6f;
    double f1 = 0.016666668f * f;

    GlStateManager.pushMatrix();
    GL11.glNormal3f(0.0f, 1.0f, 0.0f);

    GlStateManager.rotate(-McIf.mc().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
    GlStateManager.rotate(McIf.mc().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);

    GlStateManager.scale(-f1, -f1, f1);
    GlStateManager.disableLighting();
    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder worldrenderer = tessellator.getBuffer();

    int i = 0;
    double j = fontRenderer.getStringWidth(text) / 2.0;

    GlStateManager.disableTexture2D();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);

    worldrenderer.pos((-j - 1), (-1 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
    worldrenderer.pos((-j - 1), (8 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
    worldrenderer.pos((j + 1), (8 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
    worldrenderer.pos((j + 1), (-1 + i), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();

    tessellator.draw();

    GlStateManager.enableTexture2D();
    fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, i, 553648127);

    GlStateManager.depthMask(true);
    fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, i, -1);

    GlStateManager.enableDepth();
    GlStateManager.enableBlend();
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    GlStateManager.popMatrix();
  }

}
