/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.enums.ProfessionType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when something related to the game happens
 * The actions are = Level Up, Quest Started, Quest Updated and Quest Complete
 */
public class GameEvent extends Event {

    /**
     * Called when the user Level Up
     */
    public static class LevelUp extends GameEvent {

        int oldLevel, currentLevel;

        public LevelUp(int oldLevel, int currentLevel) {
            this.oldLevel = oldLevel;
            this.currentLevel = currentLevel;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }

        public int getOldLevel() {
            return oldLevel;
        }

        public static class Profession extends LevelUp {

            ProfessionType profession;

            public Profession(ProfessionType profession, int oldLevel, int currentLevel) {
                super(oldLevel, currentLevel);

                this.profession = profession;
            }

            public ProfessionType getProfession() {
                return profession;
            }

        }

    }

    /**
     * Called when the user start a quest
     */
    public static class QuestStarted extends GameEvent {

        String quest;

        public QuestStarted(String quest) {
            this.quest = quest;
        }

        public String getQuest() {
            return quest;
        }

        public static class MiniQuest extends QuestStarted {

            public MiniQuest(String quest) {
                super(quest);
            }

        }

    }

    /**
     * Called when the quest update it status
     */
    public static class QuestUpdated extends GameEvent {

    }

    /**
     * Called when a quest is completed
     */
    public static class QuestCompleted extends GameEvent {

        String questName;

        public QuestCompleted(String questName) {
            this.questName = questName;
        }

        public String getQuestName() {
            return questName;
        }

        public static class MiniQuest extends QuestCompleted {

            public MiniQuest(String questName) {
                super(questName);
            }

        }
    }

    /**
     * Called when a discovery is found
     */
    public static class DiscoveryFound extends GameEvent {
        public static class Secrect extends DiscoveryFound { }
        public static class World extends DiscoveryFound { }
    }

}
