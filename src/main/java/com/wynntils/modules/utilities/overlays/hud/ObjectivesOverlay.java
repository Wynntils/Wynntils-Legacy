/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
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
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectivesOverlay extends Overlay {

    public static final Pattern OBJECTIVE_PATTERN = Pattern.compile("^[- ] (.*): *([0-9]+)/([0-9]+)$");
    public static final Pattern HEADER_PATTERN = Pattern.compile("(★ )?(Daily )?Objectives?:");

    private static final int WIDTH = 130;
    private static final int HEIGHT = 52;
    private static final int MAX_OBJECTIVES = 3;

    private static final Objective[] objectives = new Objective[MAX_OBJECTIVES];
    private static String sidebarObjectiveName;
    private static long keepVisibleTimestamp;
    private static int objectivesPosition;
    private static int objectivesEnd;

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
        objectivesPosition = 0;
        objectivesEnd = 0;

        // Sidebar scoreboard is removed
        removeAllObjectives();
    }

    private static Objective parseObjectiveLine(String objectiveLine) {
        Matcher matcher = OBJECTIVE_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(objectiveLine));
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

        return new Objective(goal, score, maxScore, objectiveLine);
    }

    private static void removeObjective(Objective objective) {
        String goalToRemove = objective.getGoal();

        for (int i = 0; i < objectives.length; i++) {
            if (objectives[i] != null && objectives[i].getGoal().equals(goalToRemove)) {
                objectives[i] = null;
            }
        }
    }

    public static void removeAllObjectives() {
        for (int i = 0; i < objectives.length; i++) {
            objectives[i] = null;
        }
    }

    public static boolean checkObjectiveUpdate(SPacketUpdateScore updateScore) {
        if (updateScore.getObjectiveName().equals(sidebarObjectiveName)) {
            if (updateScore.getScoreAction() == SPacketUpdateScore.Action.REMOVE) {
                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                if (HEADER_PATTERN.matcher(objectiveLine).matches()) {
                    objectivesPosition = 0;
                    return true;
                }
                Objective objective = parseObjectiveLine(objectiveLine);
                removeObjective(objective);
                return true;
            }

            String text = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
            if (HEADER_PATTERN.matcher(text).matches()) {
                objectivesPosition = updateScore.getScoreValue();
                return true;
            } else if (updateScore.getPlayerName().equals(TextFormatting.BLACK.toString())) {
                objectivesEnd = updateScore.getScoreValue();
                return true;
            }

            if (updateScore.getScoreValue() < objectivesPosition && (objectivesEnd == 0 || updateScore.getScoreValue() > objectivesEnd)) {
                int pos = objectivesPosition - 1 - updateScore.getScoreValue();
                if (pos + 1 > MAX_OBJECTIVES) {
                    Reference.LOGGER.warn("Too many objectives to handle, can only store " + MAX_OBJECTIVES);
                    return true;
                }

                String objectiveLine = TextFormatting.getTextWithoutFormattingCodes(updateScore.getPlayerName());
                if (OBJECTIVE_PATTERN.matcher(objectiveLine).find()) // only create objective if it matches the format
                    objectives[pos] = parseObjectiveLine(objectiveLine);
                return true;
            }
        }
        return false;
    }

    public static void resetObjectives() {
        objectivesPosition = 0;
        objectivesEnd = 0;
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

        String msg = McIf.getUnformattedText(e.getMessage());
        if (msg.contains("Click here to claim your rewards")) {
            objectives[0] = new Objective("Claim your reward with /daily");
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
                    -WIDTH / 3, -HEIGHT + height + 11 + 5, 0, getTextureOffset(), 182, getTextureOffset() + 9,
                    objective.getProgress(),OverlayConfig.Objectives.INSTANCE.objectivesAlpha * fadeAlpha);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || !OverlayConfig.Objectives.INSTANCE.enabled ||
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
        private final String original;

        public Objective(String goal) {
            this(goal, 0, 0, "");
        }

        public Objective(String goal, int score, int maxScore, String original) {
            this.goal = goal;
            this.score = score;
            this.maxScore = maxScore;
            this.updatedAt = System.currentTimeMillis();
            this.original = original;
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

        public String getOriginal() {
            return this.original;
        }

    }

}
