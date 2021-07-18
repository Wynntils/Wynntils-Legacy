/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.wynntils.McIf;
import com.wynntils.modules.questbook.enums.AnalysePosition;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.TextFormatting;

public class ScoreboardManager {

    private static List<String> trackedQuest = new ArrayList<>();

    public static void checkScoreboard() {
        List<Score> scores = getScoreboardLines();
        if (scores.isEmpty()) return;

        Collections.reverse(scores); // scores arrive in ascending order
        boolean foundQuest = false;
        List<String> questLines = new ArrayList<>();
        for (Score s : scores) {
            String line = TextFormatting.getTextWithoutFormattingCodes(s.getPlayerName());
            if (line.startsWith("Tracked Quest:")) {
                foundQuest = true;
                continue;
            }

            if (!foundQuest) continue;
            if (line.replace("À", "").isEmpty()) break;

            questLines.add(line);
        }

        if (!trackedQuest.equals(questLines)) {
            // quest info changed (quest started, stopped, or updated)
            trackedQuest = questLines;
            QuestManager.updateAnalysis(AnalysePosition.QUESTS, true, true);
            return;
        }
    }

    private static List<Score> getScoreboardLines() {
        ScoreObjective objective = McIf.world().getScoreboard().getObjectiveInDisplaySlot(1); // sidebar objective
        if (objective == null) return new ArrayList<>();

        // get the 15 highest scores
        Scoreboard scoreboard = objective.getScoreboard();
        return Lists.newArrayList(Iterables.filter(scoreboard.getSortedScores(objective), s -> s.getPlayerName() != null && !s.getPlayerName().startsWith("#")));
    }

}
