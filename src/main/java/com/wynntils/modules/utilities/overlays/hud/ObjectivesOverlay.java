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

    private static final Pattern OBJECTIVE_PATTERN = Pattern.compile("^[- ] (.*): *([0-9]+)/([0-9]+)$");
    private static final int WIDTH = 130;
    private static final int HEIGHT = 52;
    private static final int MAX_OBJECTIVES = 3;

    private static final Objective[] objectives = new Objective[MAX_OBJECTIVES];
    private static String sidebarObjectiveName;
    private static long keepVisibleTimestamp;

    public ObjectivesOverlay() {
        super("Objectives", WIDTH, HEIGHT, true, 1f, 1f, -1, -1, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    public static void checkForSidebar(SPacketDisplayObjective displayObjective) {
        // Find the objective that is displayed in the sidebar (slot 1)
        // We're basically looking for "sb" + username. Ignore the "fb" + username scoreboard.
        if (displayObjective.getPosition() != 1) return;
        String name = displayObjective.getName();
        if (!name.startsWith("sb")) return;

        sidebarObjectiveName = name;
    }

    public static void checkSidebarRemoved(SPacketScoreboardObjective scoreboardObjective) {
        if (scoreboardObjective.getAction() != 1 || !scoreboardObjective.getObjectiveName().equals(sidebarObjectiveName)) {
            return;
        }

        // Sidebar scoreboard is removed
        for (int i = 0; i < MAX_OBJECTIVES; i++) {
            objectives[i] = null;
        }
    }

    private static Objective parseObjectiveLine(String objectiveLine) {
        Matcher matcher = OBJECTIVE_PATTERN.matcher(objectiveLine);
        String goal = null;
        int score = 0;
        int maxScore = 0;

        // Match objective strings like "- Slay Lv. 20+ Mobs: 8/140" or "- Craft Items: 0/6"
        // Could also be multi line like:
        // "- Trade 24E with"
        // "  lvl 10 players: 0/24"
        if (matcher.find()) {
            goal = matcher.group(1);
            try {
                score = Integer.parseInt(matcher.group(2));
                maxScore = Integer.parseInt(matcher.group(3));
            } catch (NumberFormatException e) {
                // Ignored, goal is already null
            }
        }

        if (goal == null) {
            if (objectiveLine.startsWith("- ")) {
                goal = objectiveLine.substring(2);
            } else {
                goal = objectiveLine;
            }
        }

        return new Objective(goal, score, maxScore);
    }

    private static void removeObjective(Objective objective) {
        String goalToRemove = objective.getGoal();

        for (int i = 0; i < objectives.length; i++) {
            if (objectives[i] != null && objectives[i].getGoal().equals(goalToRemove)) {
                objectives[i] = null;
            }
        }
    }

    public static void checkObjectiveUpdate(SPacketUpdateScore updateScore) {
        if (updateScore.getObjectiveName().equals(sidebarObjectiveName)) {
            if (updateScore.getScoreAction() == SPacketUpdateScore.Action.REMOVE) {
                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                Objective objective = parseObjectiveLine(objectiveLine);
                removeObjective(objective);
                return;
            }

            if (updateScore.getScoreValue() <= 7) {
                int pos = 7 - updateScore.getScoreValue();
                if (pos > MAX_OBJECTIVES) {
                    Reference.LOGGER.warn("Too many objectives to handle, can only store " + MAX_OBJECTIVES);
                    return;
                }

                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                objectives[pos] = parseObjectiveLine(objectiveLine);
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
        for (Objective objective : objectives) {
            if (objective != null) objective.refreshTimestamp();
        }
    }

    public static void checkObjectiveReached(ClientChatReceivedEvent e) {
        if (e.getType() != ChatType.CHAT) return;

        String msg = e.getMessage().getUnformattedText();
        if (msg.contains("Click here to claim your rewards")) {
            objectives[0] = new Objective("Claim your reward");
        }
    }

    public static void checkRewardsClaimed(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!e.getGui().getLowerInv().getName().equals("Objective Rewards")) return;

        // When opening reward, remove reminder
        objectives[0] = null;
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
        color.setA(OverlayConfig.Objectives.INSTANCE.objectivesAlpha * fadeAlpha);
        return color;
    }

    private void renderObjective(Objective objective, int height) {
        float fadeAlpha = 1.0f;

        if (OverlayConfig.Objectives.INSTANCE.hideOnInactivity) {
            long currentTime = System.currentTimeMillis();
            fadeAlpha = Math.min(Math.max(objective.getUpdatedAt() + 8000, keepVisibleTimestamp + 7000) - currentTime, 2000) / 2000f;
            if (fadeAlpha <= 0.0f) {
                return;
            }
        }

        drawString(objective.toString(), -WIDTH, -HEIGHT + height + 1, getAlphaAdjustedColor(fadeAlpha),
                SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.Objectives.INSTANCE.textShadow);

        if (OverlayConfig.Objectives.INSTANCE.enableProgressBar && objective.hasProgress()) {
            drawProgressBar(Textures.Overlays.bars_exp, -WIDTH, -HEIGHT + height + 11,
                    -WIDTH / 3, -HEIGHT + height + 11 + 5, getTextureOffset(), getTextureOffset() + 9,
                    objective.getProgress(),OverlayConfig.Objectives.INSTANCE.objectivesAlpha * fadeAlpha);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || !OverlayConfig.Objectives.INSTANCE.enableObjectives ||
                event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        int height = 36;

        if (objectives[2] != null) {
            renderObjective(objectives[2], height);
            height -= 18;
        } else if (!OverlayConfig.Objectives.INSTANCE.growFromBottom) {
            height -= 18;
        }

        if (objectives[1] != null) {
            renderObjective(objectives[1], height);
            height -= 18;
        } else if (!OverlayConfig.Objectives.INSTANCE.growFromBottom) {
            height -= 18;
        }

        if (objectives[0] != null) {
            renderObjective(objectives[0], height);
        }
    }

    public static class Objective {
        private final String goal;
        private final int score;
        private final int maxScore;
        private long updatedAt;

        public Objective(String goal) {
            this(goal, 0, 0);
        }

        public Objective(String goal, int score, int maxScore) {
            this.goal = goal;
            this.score = score;
            this.maxScore = maxScore;
            this.updatedAt = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            if (hasProgress()) {
                return goal + " [" + score + "/" + maxScore + "]";
            }

            return goal;
        }

        public boolean hasProgress() {
            return this.maxScore > 0;
        }

        public float getProgress() {
            return (float) this.score / (float) this.maxScore;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void refreshTimestamp() {
            this.updatedAt = System.currentTimeMillis();
        }

        public String getGoal() {
            return this.goal;
        }

    }

}
