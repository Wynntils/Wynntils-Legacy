/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.questbook.QuestBookModule;
import cf.wynntils.modules.questbook.configs.QuestBookConfig;
import cf.wynntils.modules.questbook.enums.QuestStatus;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import cf.wynntils.modules.questbook.managers.QuestManager;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientEvents implements Listener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(ClientChatReceivedEvent e)  {
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[New Quest Started:")) {
            QuestManager.requestQuestBookReading();
            return;
        }
        if(Utils.stripColor(e.getMessage().getFormattedText()).startsWith("[Quest Book Updated]")) {
            QuestManager.requestQuestBookReading();
            return;
        }
        if(e.getMessage().getFormattedText().contains("§6[Quest Completed]")) {
            QuestManager.requestQuestBookReading();
        }
    }

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Specials.Pre e) {
        if (!Reference.onWorld) return;

        if (QuestBookConfig.INSTANCE.questGiverIcons && e.getEntity() instanceof EntityArmorStand && e.getEntity().hasCustomName() && e.getEntity().getAlwaysRenderNameTagForRender()) {
            if (e.getEntity().getDisplayName().getUnformattedText().contains("NPC")) {
                EntityArmorStand npcTagStand = (EntityArmorStand) e.getEntity();
                if (ModCore.mc().world.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(npcTagStand.getPosition())).isEmpty())
                    return;
                EntityArmorStand npcNameStand = ModCore.mc().world.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(npcTagStand.getPosition())).get(0);
                List<QuestInfo> infoList = new ArrayList(QuestManager.getCurrentQuestsData());
                for (QuestInfo info : infoList) {
                    if (info.getStatus() == QuestStatus.COMPLETED || info.getStatus() == QuestStatus.CANNOT_START)
                        continue;
                    if (info.getCurrentDescription().toLowerCase().contains(Utils.stripColor(npcNameStand.getDisplayName().getUnformattedText().toLowerCase()))) {
                        if (info.getStatus() == QuestStatus.CAN_START) {
                            drawQuestNameplate(e.getRenderer().getRenderManager().getFontRenderer(), "§e!", (float) e.getX(), (float) e.getY(), (float) e.getZ(), e.getRenderer().getRenderManager().playerViewY, e.getRenderer().getRenderManager().playerViewX, e.getRenderer().getRenderManager().options.thirdPersonView == 2);
                        } else {
                            drawQuestNameplate(e.getRenderer().getRenderManager().getFontRenderer(), "§e?", (float) e.getX(), (float) e.getY(), (float) e.getZ(), e.getRenderer().getRenderManager().playerViewY, e.getRenderer().getRenderManager().playerViewX, e.getRenderer().getRenderManager().options.thirdPersonView == 2);
                        }
                    }
                }
            }
        }
    }

    private static void drawQuestNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal)
    {
        int VERTICAL_SHIFT = -20;
        float SCALE = 3f;
        float RED = 1f;
        float GREEN = 1f;
        float BLUE = 0f;
        GlStateManager.pushMatrix();
        GlStateManager.scale(SCALE, SCALE, SCALE);
        GlStateManager.translate(x/SCALE, y/SCALE, z/SCALE);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        if (!UtilitiesConfig.INSTANCE.hideNametags) {
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
            vertexbuffer.pos((double) (-i - 1), (double) (-21 + VERTICAL_SHIFT), 0.0D).color(RED, GREEN, BLUE, 0.25F).endVertex();
            vertexbuffer.pos((double) (-i - 1), (double) (8 + VERTICAL_SHIFT), 0.0D).color(RED, GREEN, BLUE, 0.25F).endVertex();
            vertexbuffer.pos((double) (i + 1), (double) (8 + VERTICAL_SHIFT), 0.0D).color(RED, GREEN, BLUE, 0.25F).endVertex();
            vertexbuffer.pos((double) (i + 1), (double) (-1 + VERTICAL_SHIFT), 0.0D).color(RED, GREEN, BLUE, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.depthMask(true);
        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, VERTICAL_SHIFT, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClickOnQuestBook(PlayerInteractEvent.RightClickItem e) {
        if(e.getItemStack().hasDisplayName() && e.getItemStack().getDisplayName().contains("Quest Book")) {
            if(QuestBookConfig.INSTANCE.allowCustomQuestbook) {
                QuestManager.requestLessIntrusiveQuestBookReading();
                QuestBookModule.gui.open();
            }
        }
    }

}
