/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

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

    }

}
