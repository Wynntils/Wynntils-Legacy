/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.SocialData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.enums.AccountType;
import com.wynntils.modules.core.managers.UserManager;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.NametagLabel;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.LeaderboardProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderLivingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static net.minecraft.client.renderer.GlStateManager.*;

public class NametagManager {

    private static final NametagLabel friendLabel = new NametagLabel(null, TextFormatting.YELLOW + (TextFormatting.BOLD + "Friend"), 0.7f);
    private static final NametagLabel guildLabel = new NametagLabel(MinecraftChatColors.AQUA, "Guild Member", 0.7f);
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

        if (!canRender(e.getEntity(), e.getRenderer().getRenderManager())) return true;

        List<NametagLabel> customLabels = new ArrayList<>();

        if (entity instanceof EntityPlayer) {
            if (PlayerInfo.get(SocialData.class).isFriend(entity.getName())) customLabels.add(friendLabel);  // friend
            else if (PlayerInfo.get(SocialData.class).isGuildMember(entity.getName())) customLabels.add(guildLabel);  // guild

            if (UserManager.isAccountType(entity.getUniqueID(), AccountType.MODERATOR)) customLabels.add(developerLabel);  // developer
            if (UserManager.isAccountType(entity.getUniqueID(), AccountType.HELPER)) customLabels.add(helperLabel);  // helper
            if (UserManager.isAccountType(entity.getUniqueID(), AccountType.CONTENT_TEAM)) customLabels.add(contentTeamLabel);  // contentTeam
            if (UserManager.isAccountType(entity.getUniqueID(), AccountType.DONATOR)) customLabels.add(donatorLabel);  // donator
            if (Reference.onWars && UtilitiesConfig.Wars.INSTANCE.warrerHealthBar) customLabels.add(new NametagLabel(null, Utils.getPlayerHPBar((EntityPlayer)entity), 0.7f));  // war health
            if (UtilitiesConfig.INSTANCE.showArmors) customLabels.addAll(getUserArmorLabels((EntityPlayer)entity));  // armors
        } else if (!UtilitiesConfig.INSTANCE.hideNametags && !UtilitiesConfig.INSTANCE.hideNametagBox) return false;

        double distance = entity.getDistanceSq(e.getRenderer().getRenderManager().renderViewEntity);
        double range = entity.isSneaking() ? 1024.0d : 4096.0d;

        if (distance > range) return true;

        alphaFunc(516, 0.1F);
        drawLabels(entity, McIf.getFormattedText(entity.getDisplayName()), e.getX(), e.getY(), e.getZ(), e.getRenderer().getRenderManager(), customLabels);

        return true;
    }

    /**
     * Check if the nametag should be rendered, used over checkForNametags
     */
    private static boolean canRender(Entity entity, RenderManager manager) {
        if (!(entity instanceof EntityPlayer)) return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();

        EntityPlayerSP player = McIf.player();
        boolean isVisible = !entity.isInvisibleToPlayer(player);

        // we also need to consider the teams
        if (entity != player) {
            Team entityTeam = entity.getTeam(); Team playerTeam = player.getTeam();

            if (entityTeam != null) {
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

        if (distance >= 4096.0d || entityName.isEmpty() || entityName.contains("\u0001")) return;

        boolean isSneaking = entity.isSneaking();
        float playerViewX = renderManager.playerViewX;
        float playerViewY = renderManager.playerViewY;
        boolean thirdPerson = renderManager.options.thirdPersonView == 2;
        float position = entity.height + 0.5F - (isSneaking ? 0.25F : 0);
        int offsetY = +10;

        float lastScale = 0;
        // player labels & badges
        if (entity instanceof EntityPlayer) {
            if (!labels.isEmpty()) {
                for (NametagLabel label : labels) {
                    offsetY -= 10 * label.scale;
                    drawNametag(label.text, label.color, (float) x, (float) y + position, (float) z, offsetY, playerViewY, playerViewX, thirdPerson, isSneaking, label.scale);
                }
            }

            LeaderboardProfile leader = LeaderboardManager.getLeader(entity.getUniqueID());
            if (UtilitiesConfig.INSTANCE.renderLeaderboardBadges && leader != null) {
                double horizontalShift = -(((leader.rankSize() - 1) * 21f) / 2);

                // TODO limit max badges to 3 and switch between them by time
                for (Map.Entry<ProfessionType, Integer> badge : leader.getRanks()) {
                    if (badge.getValue() == 10) continue;

                    drawBadge(badge.getKey(), ((badge.getValue()-1) / 3),
                            (float)x, (float)y + position, (float)z, horizontalShift, offsetY - 25, playerViewY, playerViewX, thirdPerson, isSneaking);

                    horizontalShift += 21f;
                }
            }
        }

        // default label
        drawNametag(entityName, null, (float) x, (float) y + position, (float) z, offsetY - 10, playerViewY, playerViewX, thirdPerson, isSneaking, 1);
    }

    private static void drawBadge(ProfessionType profession, int tier, float x, float y, float z, double horizontalShift, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
        pushMatrix();
        {
            ScreenRenderer.beginGL(0, 0);
            {
                translate(x, y, z);
                glNormal3f(0f, 1f, 0f);
                rotate(-viewerYaw, 0f, 1f, 0f);
                rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
                scale(-0.025F, -0.025F, 0.025F);
                disableLighting();
                depthMask(true);
                color(1.0f, 1.0f, 1.0f, 1.0f);

                AssetsTexture texture = Textures.World.leaderboard_badges;
                float texMinX = (profession.ordinal() * 19) / texture.width;
                float texMinY = (tier * 17) / texture.height;
                float texMaxX = ((profession.ordinal() + 1) * 19) / texture.width;
                float texMaxY = ((tier + 1) * 17) / texture.height;

                // draws the box
                texture.bind();
                Tessellator tesselator = Tessellator.getInstance();
                BufferBuilder vertexBuffer = tesselator.getBuffer();
                {
                    vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
                    vertexBuffer.pos(-9.5 - horizontalShift, -8.5 + verticalShift, 0).tex(texMinX, texMinY).endVertex();
                    vertexBuffer.pos(-9.5 - horizontalShift, +8.5 + verticalShift, 0).tex(texMinX, texMaxY).endVertex();
                    vertexBuffer.pos(+9.5 - horizontalShift, +8.5 + verticalShift, 0).tex(texMaxX, texMaxY).endVertex();
                    vertexBuffer.pos(+9.5 - horizontalShift, -8.5 + verticalShift, 0).tex(texMaxX, texMinY).endVertex();
                }
                tesselator.draw();

                enableDepth();
                enableLighting();
                disableBlend();
            }
        }
        popMatrix();
    }

    /**
     * Draws the nametag, don't call this, use checkForNametags to add more nametags
     */
    private static void drawNametag(String input, CustomColor color, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, float scale) {
        FontRenderer fontRenderer = McIf.mc().fontRenderer;  // since our fontrender ignores bold or italic texts we need to use the mc one

        pushMatrix();
        {
            if (scale != 1) scale(scale, scale, scale);
            verticalShift = (int)(verticalShift/scale);

            ScreenRenderer.beginGL(0, 0);  // we set to 0 because we don't want the ScreenRender to handle this thing
            {
                // positions
                translate(x / scale, y / scale, z / scale);  // translates to the correct postion
                glNormal3f(0.0F, 1.0F, 0.0F);
                rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
                rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
                scale(-0.025F, -0.025F, 0.025F);
                disableLighting();
                depthMask(false);

                // disable depth == will be visible through walls
                if (!isSneaking && !UtilitiesConfig.INSTANCE.hideNametags) {
                    if (Math.abs(x) <= 7.5f && Math.abs(y) <= 7.5f && Math.abs(z) <= 7.5f) disableDepth();  // this limit this feature to 7.5 blocks
                }

                int middlePos = color != null ? (int) renderer.getStringWidth(input) / 2 : fontRenderer.getStringWidth(input) / 2;

                // Nametag Box
                if (!UtilitiesConfig.INSTANCE.hideNametagBox) {
                    enableBlend();
                    tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    disableTexture2D();
                    Tessellator tesselator = Tessellator.getInstance();

                    float r = color == null ? 0 : color.r;  // red
                    float g = color == null ? 0 : color.g;  // green
                    float b = color == null ? 0 : color.b;  // blue

                    // draws the box
                    BufferBuilder vertexBuffer = tesselator.getBuffer();
                    {
                        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                        vertexBuffer.pos(-middlePos - 1, -1 + verticalShift, 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos(-middlePos - 1, 8 + verticalShift, 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos(middlePos + 1, 8 + verticalShift, 0.0D).color(r, g, b, 0.25F).endVertex();
                        vertexBuffer.pos(middlePos + 1, -1 + verticalShift, 0.0D).color(r, g, b, 0.25F).endVertex();
                    }
                    tesselator.draw();
                    enableTexture2D();
                }

                depthMask(true);

                // draws the label
                if (!isSneaking && color != null) {
                    if (!UtilitiesConfig.INSTANCE.hideNametags)
                        renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    // renders twice to replace the areas that are overlaped by tile entities
                    enableDepth();
                    renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {
                    if (!UtilitiesConfig.INSTANCE.hideNametags)
                        fontRenderer.drawString(input, -middlePos, verticalShift, isSneaking ? 553648127 : -1);

                    // renders twice to replace the areas that are overlaped by tile entities
                    enableDepth();
                    fontRenderer.drawString(input, -middlePos, verticalShift, isSneaking ? 553648127 : -1);
                }

                // returns back to normal
                enableDepth();
                enableLighting();
                disableBlend();
                color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            ScreenRenderer.endGL();
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

        // detects if the user is looking into the player
        if (McIf.mc().objectMouseOver == null || McIf.mc().objectMouseOver.entityHit == null || McIf.mc().objectMouseOver.entityHit != player) return labels;

        for (ItemStack is : player.getEquipmentAndArmor()) {
            if (!is.hasDisplayName()) continue;
            String itemName = WebManager.getTranslatedItemName(TextFormatting.getTextWithoutFormattingCodes(is.getDisplayName())).replace("֎", "");

            CustomColor color;
            String displayName;
            if (WebManager.getItems().containsKey(itemName)) {

                ItemProfile itemProfile = WebManager.getItems().get(itemName);
                color = itemProfile.getTier().getChatColor();

                // this solves an unidentified item showcase exploit
                // boxes items are STONE_SHOVEL, 1 represents UNIQUE boxes and 6 MYTHIC boxes
                if (is.getItem() == Items.STONE_SHOVEL && is.getItemDamage() >= 1 && is.getItemDamage() <= 6) {
                    displayName = "Unidentified Item";
                } else displayName = itemProfile.getDisplayName();
            } else if (itemName.contains("Crafted")) {
                color = ItemTier.CRAFTED.getChatColor();
                displayName = itemName;
            } else continue;

            labels.add(new NametagLabel(color, TextFormatting.getTextWithoutFormattingCodes(displayName), 0.4f));
        }

        return labels;
    }

}
