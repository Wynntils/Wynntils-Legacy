/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TrackedQuestOverlay extends Overlay {

    public TrackedQuestOverlay() {
        super("Tracked Quest", 215, 70, true, 0.0f, 0.0f, 10, 10, OverlayGrowFrom.TOP_LEFT);
    }


    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR)
            return;

        QuestInfo trackedQuest = QuestManager.getTrackedQuest();

        if (trackedQuest == null || trackedQuest.getSplittedDescription() == null || trackedQuest.getSplittedDescription().size() == 0)
            return;

        String name = trackedQuest.isMiniQuest() ? "Mini-Quest" : "Quest";
        drawString("Tracked " + name + " Info: ", 0, 0, CommonColors.GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);

        int currentY = 0;
        for (String message : trackedQuest.getSplittedDescription()) {
            drawString(message, 0, 10 + currentY, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
            currentY += 10;
        }

        if (QuestManager.hasInterrupted())
            drawString("(Open your book to update)", 0, 20 + currentY, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);

        if (QuestBookConfig.INSTANCE.compassFollowQuests && trackedQuest.hasTargetLocation())
            CompassManager.setCompassLocation(trackedQuest.getTargetLocation());
    }

}
