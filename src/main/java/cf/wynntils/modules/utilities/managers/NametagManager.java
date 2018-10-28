/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.managers;

import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.client.event.RenderLivingEvent;

public class NametagManager {

    public static boolean checkForNametag(RenderLivingEvent.Specials.Pre e) {
        if(!(e.getEntity() instanceof EntityPlayer)) return false;
        EntityPlayer entity = (EntityPlayer) e.getEntity();

        //TODO add this for guild, party and friends
        float r = 0; float g = 0; float b = 0;
        if(WebManager.isModerator(entity.getUniqueID())) {
            r = 0.75f; g = 0; b = 0.75f;
        }else if(WebManager.isHelper(entity.getUniqueID())) {
            r = 1f; g = 1; b = 0.25f;
        }

        if(r == 0 && g == 0 && b == 0) return false;

        if(canRenderName((EntityPlayer)e.getEntity(), e.getRenderer().getRenderManager())) {
            double d0 = entity.getDistanceSqToEntity(e.getRenderer().getRenderManager().renderViewEntity);
            float f = entity.isSneaking() ? 32.0f : 64;

            if (d0 < (double)(f * f))
            {
                String s = entity.getDisplayName().getFormattedText();
                GlStateManager.alphaFunc(516, 0.1F);
                renderLivingLabel(entity, s, e.getX(), e.getY(), e.getZ(), 64, e.getRenderer().getRenderManager(), r, g, b);
            }
        }

        return true;
    }

    private static boolean canRenderName(EntityPlayer entity, RenderManager renderManager) {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
        boolean flag = !entity.isInvisibleToPlayer(entityplayersp);

        if (entity != entityplayersp)
        {
            Team team = entity.getTeam();
            Team team1 = entityplayersp.getTeam();

            if (team != null)
            {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible)
                {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled() && entity != renderManager.renderViewEntity && flag && !entity.isBeingRidden();
    }

    private static void renderLivingLabel(EntityPlayer entityIn, String str, double x, double y, double z, int maxDistance, RenderManager renderManager, float r, float g, float b)
    {
        double d0 = entityIn.getDistanceSqToEntity(renderManager.renderViewEntity);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            boolean flag = entityIn.isSneaking();
            float f = renderManager.playerViewY;
            float f1 = renderManager.playerViewX;
            boolean flag1 = renderManager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
            int i = "deadmau5".equals(str) ? -10 : 0;
            drawNameplate(renderManager.getFontRenderer(), str, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag, r, g, b);
        }
    }

    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, float r, float g, float b)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking)
        {
            GlStateManager.disableDepth();
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int i = fontRendererIn.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexbuffer.pos((double)(-i - 1), (double)(-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
        vertexbuffer.pos((double)(-i - 1), (double)(8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
        vertexbuffer.pos((double)(i + 1), (double)(8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
        vertexbuffer.pos((double)(i + 1), (double)(-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();

        if (!isSneaking)
        {
            fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
            GlStateManager.enableDepth();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, isSneaking ? 553648127 : -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

}
