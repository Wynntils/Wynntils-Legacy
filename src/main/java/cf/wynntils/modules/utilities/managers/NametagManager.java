/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.managers;

import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.client.event.RenderLivingEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NametagManager {

    public static final Pattern MOB_LEVEL = Pattern.compile("(\u00A7\\d \\[Lv\\. (.*?)\\])");

    public static boolean checkForNametag(RenderLivingEvent.Specials.Pre e) {
        Entity entity =  e.getEntity();

        //TODO add this for guild, party and friends
        float r = 0; float g = 0; float b = 0;

        if(entity instanceof EntityPlayer) {
            if(WebManager.isModerator(entity.getUniqueID())) {
                r = 0.75f; g = 0; b = 0.75f;
            }else if(WebManager.isHelper(entity.getUniqueID())) {
                r = 1f; g = 1; b = 0.25f;
            }
        }else if(!UtilitiesConfig.INSTANCE.hideNametags && !UtilitiesConfig.INSTANCE.hideNametagBox) {
            return false;
        }

        if(canRenderName(e.getEntity(), e.getRenderer().getRenderManager())) {
            double d0 = entity.getDistanceSq(e.getRenderer().getRenderManager().renderViewEntity);
            float f = entity.isSneaking() ? 32.0f : 64;

            if (d0 < (double)(f * f)) {
                String s = entity.getDisplayName().getFormattedText();
                GlStateManager.alphaFunc(516, 0.1F);
                renderLivingLabel(entity, s, e.getX(), e.getY(), e.getZ(), 64, e.getRenderer().getRenderManager(), r, g, b);
            }
        }

        return true;
    }

    private static boolean canRenderName(Entity entity, RenderManager renderManager) {
        if(!(entity instanceof EntityPlayer)) {
            return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
        }

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

    private static void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, RenderManager renderManager, float r, float g, float b)
    {
        double d0 = entityIn.getDistanceSq(renderManager.renderViewEntity);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            boolean flag = entityIn.isSneaking();
            float f = renderManager.playerViewY;
            float f1 = renderManager.playerViewX;
            boolean flag1 = renderManager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
            int i = "deadmau5".equals(str) ? -10 : 0;
            if (!str.isEmpty() && !str.contains("\u0001")) {
                if (entityIn instanceof EntityPlayer) {
                    if(PlayerInfo.getPlayerInfo().getFriendList().contains(entityIn.getName())) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A7e\u00A7lFriend", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    } else if (PlayerInfo.getPlayerInfo().getGuildList().contains(entityIn.getName())) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A7b\u00A7lGuild Member", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    }
                    if (entityIn.getDisplayName().getUnformattedText().startsWith("\u00A76")) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A76\u00A7lModerator", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    }
                    else if (entityIn.getDisplayName().getUnformattedText().startsWith("\u00A74")) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A74\u00A7lAdmin", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    }
                    if (WebManager.isModerator(entityIn.getUniqueID())) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A76\u00A7lWynntils Developer", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    } else if (WebManager.isHelper(entityIn.getUniqueID())) {
                        drawNameplate(renderManager.getFontRenderer(), "\u00A74Wynntils Helper", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                        i -= 10;
                    }
                    if (UtilitiesConfig.INSTANCE.showArmors) {
                        for (ItemStack is : entityIn.getEquipmentAndArmor()) {
                            if(!is.hasDisplayName() || !WebManager.getItems().containsKey(Utils.stripColor(is.getDisplayName()))) continue;
                            ItemProfile wItem = WebManager.getItems().get(Utils.stripColor(is.getDisplayName()));
                            String prefix;
                            switch (wItem.getTier()) {
                                case MYTHIC:
                                    prefix = "\u00A75";
                                    break;
                                case LEGENDARY:
                                    prefix = "\u00A7b";
                                    break;
                                case RARE:
                                    prefix = "\u00A7d";
                                    break;
                                case UNIQUE:
                                    prefix = "\u00A7e";
                                    break;
                                case SET:
                                    prefix = "\u00A7a";
                                    break;
                                case NORMAL:
                                    prefix = "\u00A7f";
                                    break;
                                default:
                                    prefix = "";
                            }
                            drawNameplate(renderManager.getFontRenderer(), prefix + Utils.stripColor(is.getDisplayName()), (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 0.7f);
                            i -= 10;
                        }
                        i = (int) (i / 1.2);
                    }
                } else {
                    if(!(entityIn instanceof EntityArmorStand)) {
                        i = 0;
                        Matcher m = MOB_LEVEL.matcher(str);
                        while (m.find()) {
                            String s = m.group(1);
                            str = str.replace(s, "");
                            drawNameplate(renderManager.getFontRenderer(), s, (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 1);
                            i -= 10;
                        }
                        if (entityIn.getDisplayName().getUnformattedText().contains("Disguised")) {
                            drawNameplate(renderManager.getFontRenderer(), "\u00A77[Disguised]", (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 1);
                            i -= 10;
                            str = str.replace("\u00A77 [Disguised]\u00A7r", "");
                        }
                    }
                }
                drawNameplate(renderManager.getFontRenderer(), str, (float) x, (float) y + f2, (float) z, i, f, f1, flag1, flag, r, g, b, 1);
            }
        }
    }

    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, float r, float g, float b, float scale)
    {
        GlStateManager.pushMatrix();
        if(scale != 1) GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(x/scale, y/scale, z/scale);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!isSneaking && !UtilitiesConfig.INSTANCE.hideNametags) {
            GlStateManager.disableDepth();
        }

        if (!UtilitiesConfig.INSTANCE.hideNametagBox) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int i = fontRendererIn.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            vertexbuffer.pos((double) (-i - 1), (double) (-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
            vertexbuffer.pos((double) (-i - 1), (double) (8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
            vertexbuffer.pos((double) (i + 1), (double) (8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
            vertexbuffer.pos((double) (i + 1), (double) (-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

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
