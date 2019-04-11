/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TrackedQuestOverlay extends Overlay {

    public TrackedQuestOverlay() {
        super("Tracked Quest", I18n.format("wynntils.questbook.overlays.tracked_quest.display_name"), 215, 70, true, 0.0f, 0.0f, 10, 10, OverlayGrowFrom.TOP_LEFT);
    }


    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR)
            return;

        if(QuestManager.getTrackedQuest() == null || QuestManager.getTrackedQuest().getSplittedDescription() == null || QuestManager.getTrackedQuest().getSplittedDescription().size() == 0)
            return;

        try {
            drawString(I18n.format("wynntils.questbook.overlays.tracked_quest.title"), 0, 0, CommonColors.GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);

            int currentY = 0;
            for (String message : QuestManager.getTrackedQuest().getSplittedDescription()) {
                drawString(message, 0, 10 + currentY, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
                currentY += 10;
            }

            if (QuestBookConfig.INSTANCE.compassFollowQuests && QuestManager.getTrackedQuest().getX() != 0 && QuestManager.getTrackedQuest().getZ() != 0)
                ScreenRenderer.mc.world.setSpawnPoint(new BlockPos(QuestManager.getTrackedQuest().getX(), 0, QuestManager.getTrackedQuest().getZ()));
        } catch (NullPointerException ex) {
            // Likely caused by concurrent modification after updating quests
            Reference.LOGGER.warn("NPE caught when rendering tracked quest - this is generally nothing to worry about", ex);
        }
    }

}
