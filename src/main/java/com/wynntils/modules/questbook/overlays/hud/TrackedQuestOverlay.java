/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.questbook.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class TrackedQuestOverlay extends Overlay {

    public TrackedQuestOverlay() {
        super("Tracked Quest", 215, 70, true, true, 0.0f, 0.0f, 120, 10, OverlayGrowFrom.TOP_LEFT);
    }


    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR)
            return;

        QuestInfo trackedQuest = QuestManager.getTrackedQuest();
        OverlayConfig.TrackedQuestInfo config = OverlayConfig.TrackedQuestInfo.INSTANCE;

        if (trackedQuest == null || trackedQuest.getSplittedDescription() == null || trackedQuest.getSplittedDescription().size() == 0)
            return;

        String name = trackedQuest.isMiniQuest() ? "Mini-Quest" : "Quest";
        drawString("Tracked " + name + " Info: ", 0, 0, CommonColors.GREEN, config.textAlignment, config.textShadow);

        int currentY = 0;
        if (config.displayQuestName) {
            drawString(trackedQuest.getName(), 0, 10 + currentY, CommonColors.LIGHT_GREEN, config.textAlignment, config.textShadow);
            currentY += 10;
        }
        for (String message : trackedQuest.getSplittedDescription()) {
            drawString(message, 0, 10 + currentY, CommonColors.WHITE, config.textAlignment, config.textShadow);
            currentY += 10;
        }

        if (!QuestManager.hasInterrupted()) return;

        drawString("(Open your book to update)", 0, 20 + currentY, CommonColors.WHITE, config.textAlignment, config.textShadow);
    }

}
