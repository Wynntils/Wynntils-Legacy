/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.NametagLabel;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static net.minecraft.client.renderer.GlStateManager.*;

public class NametagManager {

    private static final NametagLabel friendLabel = new NametagLabel(null, TextFormatting.YELLOW + (TextFormatting.BOLD + "Friend"), 0.7f);
    private static final NametagLabel guildLabel = new NametagLabel(MinecraftChatColors.CYAN, "Guild Member", 0.7f);
    private static final NametagLabel moderatorLabel = new NametagLabel(MinecraftChatColors.ORANGE, "Wynncraft Moderator", 0.7f);
    private static final NametagLabel adminLabel = new NametagLabel(MinecraftChatColors.DARK_RED, "Wynncraft Admin", 0.7f);
    private static final NametagLabel developerLabel = new NametagLabel(null, TextFormatting.GOLD + (TextFormatting.BOLD + "Wynntils Developer"), 0.7f);
    private static final NametagLabel helperLabel = new NametagLabel(CommonColors.LIGHT_GREEN, "Wynntils Helper", 0.7f);
    private static final NametagLabel contentTeamLabel = new NametagLabel(CommonColors.RAINBOW, "Wynntils CT", 0.7f);
    private static final NametagLabel donatorLabel = new NametagLabel(CommonColors.RAINBOW, "Wynntils Donator", 0.7f);

    public static final Pattern MOB_LEVEL = Pattern.compile("(" + TextFormatting.GOLD + " \\[Lv\\. (.*?)\\])");
    private static final ScreenRenderer renderer = new ScreenRenderer();

    /**
     * Called at ClientEvents, replaces the vanilla nametags
     * if you want to register a new label, here's the place
     */
    public static boolean checkForNametags(RenderLivingEvent.Specials.Pre e) {
        Entity entity =  e.getEntity();

        if(!canRender(e.getEntity(), e.getRenderer().getRenderManager())) return true;

        List<NametagLabel> customLabels = new ArrayList<>();

        if(entity instanceof EntityPlayer) {
            if(PlayerInfo.getPlayerInfo().getFriendList().contains(entity.getName())) customLabels.add(friendLabel); //friend
            else if(PlayerInfo.getPlayerInfo().getGuildList().contains(entity.getName())) customLabels.add(guildLabel); //guild

            if(entity.getDisplayName().getUnformattedText().startsWith(TextFormatting.GOLD.toString())) customLabels.add(moderatorLabel); //moderator
            if(entity.getDisplayName().getUnformattedText().startsWith(TextFormatting.DARK_RED.toString())) customLabels.add(adminLabel); //admin
            if(WebManager.isModerator(entity.getUniqueID())) customLabels.add(developerLabel); //developer
            if(WebManager.isHelper(entity.getUniqueID())) customLabels.add(helperLabel); //helper
            if(WebManager.isContentTeam(entity.getUniqueID())) customLabels.add(contentTeamLabel); //contentTeam
            if(WebManager.isDonator(entity.getUniqueID())) customLabels.add(donatorLabel); //donator
            if(Reference.onWars && UtilitiesConfig.Wars.INSTANCE.warrerHealthBar) customLabels.add(new NametagLabel(null, Utils.getPlayerHPBar((EntityPlayer)entity), 0.7f)); //war health
            if(UtilitiesConfig.INSTANCE.showArmors) customLabels.addAll(getUserArmorLabels((EntityPlayer)entity)); // armors
        }else if(!UtilitiesConfig.INSTANCE.hideNametags && !UtilitiesConfig.INSTANCE.hideNametagBox) return false;

        double distance = entity.getDistanceSq(e.getRenderer().getRenderManager().renderViewEntity);
        double range = entity.isSneaking() ? 1024.0d : 4096.0d;

        if (distance < range) {
            alphaFunc(516, 0.1F);
            drawLabels(entity, entity.getDisplayName().getFormattedText(), e.getX(), e.getY(), e.getZ(), e.getRenderer().getRenderManager(), customLabels);
        }

        return true;
    }

    /**
     * Check if the nametag should be rendered, used over checkForNametags
     */
    private static boolean canRender(Entity entity, RenderManager manager) {
        if(entity.isBeingRidden()) return false;
        if(!(entity instanceof EntityPlayer)) return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        boolean isVisible = !entity.isInvisibleToPlayer(player);

        //we also need to consider the teams
        if(entity != player) {
            Team entityTeam = entity.getTeam(); Team playerTeam = player.getTeam();

            if(entityTeam != null) {
                Team.EnumVisible visibility = entityTeam.getNameTagVisibility();

                switch (visibility) {
                    case NEVER: return false;
                    case ALWAYS: return isVisible;
                    case HIDE_FOR_OTHER_TEAMS: return playerTeam == null ? isVisible : entityTeam.isSameTeam(playerTeam) && (entityTeam.getSeeFriendlyInvisiblesEnabled() || isVisible);
                    case HIDE_FOR_OWN_TEAM: return playerTeam == null ? isVisible : !entityTeam.isSameTeam(playerTeam) && isVisible;
                }
            }
        }

        return Minecraft.isGuiEnabled() && entity != manager.renderViewEntity && !entity.isBeingRidden() && isVisible;
    }

    /**
     * Handles the nametags drawing, if you want to add more tags use checkForNametags
     */
    private static void drawLabels(Entity entity, String entityName, double x, double y, double z, RenderManager renderManager, List<NametagLabel> labels) {
        double distance = entity.getDistanceSq(renderManager.renderViewEntity);

        if(distance >= 4096.0d || entityName.isEmpty() || entityName.contains("\u0001")) return;

        boolean isSneaking = entity.isSneaking();
        float playerViewX = renderManager.playerViewX;
        float playerViewY = renderManager.playerViewY;
        boolean thirdPerson = renderManager.options.thirdPersonView == 2;
        float position = entity.height + 0.5F - (isSneaking ? 0.25F : 0);
        int offsetY = +10;

        float lastScale = 0;
        //player labels
        if(!labels.isEmpty() && entity instanceof EntityPlayer) {
            for(NametagLabel label : labels) {
                offsetY-=10 * label.scale;
                drawNametag(label.text, label.color, (float)x, (float) y + position, (float) z, offsetY, playerViewY, playerViewX, thirdPerson, isSneaking, label.scale);
            }
        }

        //default label
        drawNametag(entityName, null, (float) x, (float) y + position, (float) z, offsetY-10, playerViewY, playerViewX, thirdPerson, isSneaking, 1);
    }

    /**
     * Draws the nametag, don't call this, use checkForNametags to add more nametags
     */
    private static void drawNametag(String input, CustomColor color, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, float scale) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer; //since our fontrender ignores bold or italic texts we need to use the mc one

        pushMatrix();
        {
            if(scale != 1) scale(scale, scale, scale);
            verticalShift = (int)(verticalShift/scale);

            renderer.beginGL(0, 0); //we set to 0 because we don't want the ScreenRender to handle this thing
            {
                //positions
                translate(x / scale, y / scale, z / scale); //translates to the correct postion
                glNormal3f(0.0F, 1.0F, 0.0F);
                rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
                rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
                scale(-0.025F, -0.025F, 0.025F);
                disableLighting();
                depthMask(false);

                //disable depth == will be visible through walls
                if(!isSneaking && !UtilitiesConfig.INSTANCE.hideNametags) {
                    if(Math.abs(x) <= 7.5f && Math.abs(y) <= 7.5f && Math.abs(z) <= 7.5f) disableDepth(); //this limit this feature to 7.5 blocks
                }

                int middlePos = color != null ? (int) renderer.getStringWidth(input) / 2 : fontRenderer.getStringWidth(input) / 2;

                //Nametag Box
                if(!UtilitiesConfig.INSTANCE.hideNametagBox) {
                    enableBlend();
                    tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    disableTexture2D();
                    Tessellator tesselator = Tessellator.getInstance();

                    float r = color == null ? 0 : color.r; //red
                    float g = color == null ? 0 : color.g; //green
                    float b = color == null ? 0 : color.b; //blue

                    //draws the box
                    BufferBuilder vertexBuffer = tesselator.getBuffer();
                    {
                        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                        vertexBuffer.pos((double) (-middlePos - 1), (double) (-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos((double) (-middlePos - 1), (double) (8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos((double) (middlePos + 1), (double) (8 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos((double) (middlePos + 1), (double) (-1 + verticalShift), 0.0D).color(r, g, b, 0.25F).endVertex();
                    }
                    tesselator.draw();
                    enableTexture2D();
                }

                depthMask(true);

                //draws the label
                if(!isSneaking && color != null) {
                    if(!UtilitiesConfig.INSTANCE.hideNametags)
                        renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    //renders twice to replace the areas that are overlaped by tile entities
                    enableDepth();
                    renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {
                    if(!UtilitiesConfig.INSTANCE.hideNametags)
                        fontRenderer.drawString(input, -middlePos, verticalShift, isSneaking ? 553648127 : -1);

                    //renders twice to replace the areas that are overlaped by tile entities
                    enableDepth();
                    fontRenderer.drawString(input, -middlePos, verticalShift, isSneaking ? 553648127 : -1);
                }

                //returns back to normal
                enableDepth();
                enableLighting();
                disableBlend();
                color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            renderer.endGL();
        }
        popMatrix();
    }

    /**
     * Grabs the current player armor and equipment as labels
     *
     * @param player The player
     * @return the list with the labels
     */
    private static List<NametagLabel> getUserArmorLabels(EntityPlayer player) {
        List<NametagLabel> labels = new ArrayList<>();

        //detects if the user is looking into the player
        if(Minecraft.getMinecraft().objectMouseOver == null || Minecraft.getMinecraft().objectMouseOver.entityHit == null || Minecraft.getMinecraft().objectMouseOver.entityHit != player) return labels;

        for(ItemStack is : player.getEquipmentAndArmor()) {
            if(!is.hasDisplayName() || !WebManager.getItems().containsKey(Utils.stripColor(is.getDisplayName()))) continue;

            ItemProfile itemProfile = WebManager.getItems().get(Utils.stripColor(is.getDisplayName()));
            CustomColor color;
            switch (itemProfile.getTier()) {
                case MYTHIC: color = MinecraftChatColors.PURPLE; break;
                case LEGENDARY: color = MinecraftChatColors.CYAN; break;
                case RARE: color = MinecraftChatColors.PINK; break;
                case UNIQUE: color = MinecraftChatColors.YELLOW; break;
                case SET: color = MinecraftChatColors.GREEN; break;
                case NORMAL: color = MinecraftChatColors.WHITE; break;
                default: color = CommonColors.RAINBOW;
            }

            labels.add(new NametagLabel(color, Utils.stripColor(is.getDisplayName()), 0.4f));
        }

        return labels;
    }

}
