/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectivesOverlay extends Overlay {
    private static final Pattern OBJECTIVE_PATTERN = Pattern.compile("^- (.*): *([0-9]+)/([0-9]+)$");
    private static final int WIDTH = 130;
    private static final int HEIGHT = 52;
    private static final int MAX_OBJECTIVES = 3;

    private static String sidebarObjectiveName;
    private static String[] objectiveGoal = new String[MAX_OBJECTIVES];
    private static int[] objectiveScore = new int[MAX_OBJECTIVES];
    private static int[] objectiveMax = new int[MAX_OBJECTIVES];
    private static long[] objectiveUpdatedAt = new long[MAX_OBJECTIVES];
    private static long keepVisibleTimestamp;

    public ObjectivesOverlay() {
        super("Objectives", WIDTH, HEIGHT, true, 1f, 1f, -1, -1, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    public static void checkForSidebar(SPacketDisplayObjective displayObjective) {
        // Find the objective that is displayed in the sidebar (slot 1)
        if (displayObjective.getPosition() == 1) {
            // We're basically looking for "sb" + username. Ignore the "fb" + username scoreboard.
            if (displayObjective.getName().startsWith("sb")) {
                sidebarObjectiveName = displayObjective.getName();
            }
        }
    }

    public static void checkObjectiveRemoved(SPacketScoreboardObjective scoreboardObjective) {
        if (scoreboardObjective.getAction() == 1 && scoreboardObjective.getObjectiveName().equals(sidebarObjectiveName)) {
            // Sidebar scoreboard is removed
            for (int i = 0; i < MAX_OBJECTIVES; i++) {
                objectiveGoal[i] = null;
                objectiveScore[i] = 0;
                objectiveMax[i] = 0;
            }
        }
    }

    private static void parseObjectiveLine(String objectiveLine, int pos) {
        Matcher matcher = OBJECTIVE_PATTERN.matcher(objectiveLine);

        objectiveGoal[pos] = null;
        if (matcher.find()) {
            objectiveGoal[pos] = matcher.group(1);
            try {
                objectiveScore[pos] = Integer.parseInt(matcher.group(2));
                objectiveMax[pos] = Integer.parseInt(matcher.group(3));
            } catch (NumberFormatException e) {
                objectiveGoal[pos] = null;
            }
        }

        if (objectiveGoal[pos] == null) {
            if (objectiveLine.equals("- All done")) {
                objectiveGoal[pos] = "All done";
                objectiveScore[pos] = 0;
                objectiveMax[pos] = 0;
            } else {
                if (objectiveLine.startsWith("- ")) {
                    objectiveGoal[pos] = objectiveLine.substring(2);
                } else {
                    objectiveGoal[pos] = objectiveLine;
                }
                objectiveScore[pos] = 0;
                objectiveMax[pos] = 0;
            }
        }
    }

    public static void checkObjectiveUpdate(SPacketUpdateScore updateScore) {
        if (updateScore.getObjectiveName().equals(sidebarObjectiveName)) {
            // We don't care about removals since they always gets replaced at the same score
            if (updateScore.getScoreAction() != SPacketUpdateScore.Action.CHANGE) return;

            if (updateScore.getScoreValue() <= 7) {
                int pos = 7 - updateScore.getScoreValue();
                if (pos > MAX_OBJECTIVES) {
                    Reference.LOGGER.warn("Too many objectives to handle, can only store " + MAX_OBJECTIVES);
                    return;
                }

                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                // Match objectives like "- Slay Lv. 20+ Mobs: 8/140" or "- Craft Items: 0/6"
                parseObjectiveLine(objectiveLine, pos);

                objectiveUpdatedAt[pos] = System.currentTimeMillis();
            }
        }
    }

    public static void updateOverlayActivation() {
        GuiIngameForge.renderObjective = !OverlayConfig.Objectives.INSTANCE.enableObjectives;
    }

    public static void restoreVanillaScoreboard() {
        GuiIngameForge.renderObjective = true;
    }

    public static void refreshVisibility() {
        // Keep updating timestamps as long as chest/inventory is open
        if (OverlayConfig.Objectives.INSTANCE.hideOnInactivity) {
            keepVisibleTimestamp = System.currentTimeMillis();
        }
    }

    public static void refreshAllTimestamps() {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < objectiveUpdatedAt.length; i++) {
            objectiveUpdatedAt[i] = currentTime;
        }
    }

    public static void checkObjectiveReached(ClientChatReceivedEvent e) {
        if (e.getType() != ChatType.CHAT) return;

        String msg = e.getMessage().getUnformattedText();
        if (msg.contains("Click here to claim your rewards")) {
            objectiveGoal[0] = "Claim your reward";
            objectiveScore[0] = 0;
            objectiveMax[0] = 0;
            objectiveUpdatedAt[0] = 253370761200L; // Expire in the year 9999...
        }
    }

    public static void checkRewardsClaimed(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (e.getGui().getLowerInv().getName().equals("Objective Rewards")) {
            // When opening reward, remove reminder
            objectiveGoal[0] = null;
            objectiveScore[0] = 0;
            objectiveMax[0] = 0;
            objectiveUpdatedAt[0] = System.currentTimeMillis();
        }
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

    private CustomColor getAlphaAdjustedColor(float fadeAlpha) {
        CustomColor color = new CustomColor(OverlayConfig.Objectives.INSTANCE.textColour);
        color.setA(OverlayConfig.Objectives.INSTANCE.objectivesAlpha*fadeAlpha);
        return color;
    }

    private void renderObjective(int pos, int height) {
        if (objectiveGoal[pos] == null) return;
        float fadeAlpha = 1.0f;

        if (OverlayConfig.Objectives.INSTANCE.hideOnInactivity) {
            long currentTime = System.currentTimeMillis();
            fadeAlpha = Math.min(Math.max(objectiveUpdatedAt[pos] + 8000, keepVisibleTimestamp + 7000) - currentTime, 2000) / 2000f;
            if (fadeAlpha <= 0.0f) {
                return;
            }
        }
        String objectiveString;
        if (objectiveMax[pos] > 0) {
            objectiveString = objectiveGoal[pos] + " [" + objectiveScore[pos] + "/" + objectiveMax[pos] + "]";
        } else {
            objectiveString = objectiveGoal[pos];
        }
        drawString(objectiveGoal[pos] + " [" + objectiveScore[pos] + "/" + objectiveMax[pos] + "]",
                -WIDTH, -HEIGHT + height + 1, getAlphaAdjustedColor(fadeAlpha), SmartFontRenderer.TextAlignment.LEFT_RIGHT,
                OverlayConfig.Objectives.INSTANCE.textShadow);
        if (OverlayConfig.Objectives.INSTANCE.enableProgressBar && objectiveMax[pos] > 0) {
            drawProgressBar(Textures.Overlays.bars_exp, -WIDTH , -HEIGHT + height + 11,
                     -WIDTH/3, -HEIGHT + height + 11 + 5, getTextureOffset(), getTextureOffset() + 9,
                    (float) objectiveScore[pos] / (float) objectiveMax[pos],
                    OverlayConfig.Objectives.INSTANCE.objectivesAlpha*fadeAlpha);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || !OverlayConfig.Objectives.INSTANCE.enableObjectives ||
                event.getType() != RenderGameOverlayEvent.ElementType.ALL || objectiveGoal[0] == null) return;

        renderObjective(0, 0);
        renderObjective(1, 18);
        renderObjective(2, 36);
    }
}
