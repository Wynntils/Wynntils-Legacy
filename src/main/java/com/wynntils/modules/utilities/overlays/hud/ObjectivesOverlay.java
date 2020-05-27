/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectivesOverlay extends Overlay {
    private static final Pattern OBJECTIVE_PATTERN = Pattern.compile("^- (.*): *([0-9]+)/([0-9]+)$");
    private static final int WIDTH = 140;
    private static final int HEIGHT = 20;

    private static String sidebarObjectiveName;
    private static String objectiveGoal;
    private static int objectiveScore;
    private static int objectiveMax;

    public ObjectivesOverlay() {
        super("Objectives", WIDTH, HEIGHT, true, 1f, 1f, -1, -1, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    public static boolean handleDisplayObjective(SPacketDisplayObjective displayObjective) {
        if (displayObjective.getPosition() == 1) {
            // This is the objective that is displayed in the sidebar (slot 1)
            if (sidebarObjectiveName != null) {
                Reference.LOGGER.warn("ObjectivesOverlay got a new sidebar objective, changing from " + sidebarObjectiveName + " to " + displayObjective.getName());
            }
            sidebarObjectiveName = displayObjective.getName();
        }

        return false;
    }

    public static boolean handleUpdateScore(SPacketUpdateScore updateScore) {
        if (updateScore.getObjectiveName().equals(sidebarObjectiveName)) {
            if (updateScore.getScoreValue() == 7) {
                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                // Match objectives like "- Slay Lv. 20+ Mobs: 8/140" or "- Craft Items: 0/6"
                Matcher matcher = OBJECTIVE_PATTERN.matcher(objectiveLine);

                objectiveGoal = null;
                if (matcher.find()) {
                    objectiveGoal = matcher.group(1);
                    try {
                        objectiveScore = Integer.parseInt(matcher.group(2));
                        objectiveMax = Integer.parseInt(matcher.group(3));
                    } catch (NumberFormatException e) {
                        objectiveGoal = null;
                    }
                }

                if (objectiveGoal == null) {
                    // Use the entire string as fallback; not sure if it can happen
                    objectiveGoal = objectiveLine;
                    objectiveScore = 0;
                    objectiveMax = 1;
                }
            }
            // Drop packet if overlay is enabled, otherwise let it pass
            return OverlayConfig.Objectives.INSTANCE.enableObjectives;
        }

        return false;
    }

    private int getTextureOffset() {
        switch (OverlayConfig.Objectives.INSTANCE.objectivesTexture) {
            default:
            case Wynn:
                return 0;
            case a:
                return 10;
            case b:
                return 20;
            case c:
                return 30;
            case Liquid:
                return 40;
            case Emerald:
                return 50;
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || !OverlayConfig.Objectives.INSTANCE.enableObjectives ||
                event.getType() != RenderGameOverlayEvent.ElementType.ALL || objectiveGoal == null) return;

        drawString(objectiveGoal + " [" + objectiveScore + "/" + objectiveMax + "]", -WIDTH, -HEIGHT + 8, OverlayConfig.Objectives.INSTANCE.textColour, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.Objectives.INSTANCE.textShadow);
        if (OverlayConfig.Objectives.INSTANCE.enableProgressBar) {
            drawProgressBar(Textures.Overlays.bars_exp, -WIDTH, -HEIGHT, 0, -HEIGHT + 5, getTextureOffset(), getTextureOffset() + 9, (float) objectiveScore / (float) objectiveMax);
        }
    }
}
