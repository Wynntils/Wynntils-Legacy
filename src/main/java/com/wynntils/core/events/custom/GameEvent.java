/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.enums.professions.GatheringMaterial;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

/**
 * Called when something related to the game happens
 * The actions are = Level Up, Resource Gather, Quest Started, Quest Updated and Quest Complete
 */
public class GameEvent extends Event {

    /**
     * Called when the user dies
     */
    public static class PlayerDeath extends GameEvent {
    }

    /**
     * Called when the user Level Up
     */
    public static class LevelUp extends GameEvent {

        int newLevel;

        public LevelUp(int newLevel) {
            this.newLevel = newLevel;
        }

        public int getNewLevel() {
            return newLevel;
        }

        public static class Profession extends LevelUp {

            ProfessionType profession;

            public Profession(ProfessionType profession, int newLevel) {
                super(newLevel);

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
     * Called whenever a resource is gathered by the player
     */
    public static class ResourceGather extends GameEvent {

        ProfessionType type;
        GatheringMaterial material;

        int materialAmount;
        double xpAmount;
        double xpPercentage;

        Location location;

        public ResourceGather(ProfessionType type, GatheringMaterial material, int materialAmount, double xpAmount, double xpPercentage, Location location) {
            this.type = type;
            this.material = material;
            this.materialAmount = materialAmount;
            this.xpAmount = xpAmount;
            this.xpPercentage = xpPercentage;
            this.location = location;
        }

        public ProfessionType getType() {
            return type;
        }

        public int getMaterialAmount() {
            return materialAmount;
        }

        public GatheringMaterial getMaterial() {
            return material;
        }

        public double getXpPercentage() {
            return xpPercentage;
        }

        public double getXpAmount() {
            return xpAmount;
        }

        public Location getLocation() {
            return location;
        }

    }

    /**
     * Called whenever an entity damage tag is received by the client
     */
    public static class DamageEntity extends GameEvent {

        Map<DamageType, Integer> damageTypes;
        Entity entity;

        public DamageEntity(Map<DamageType, Integer> damageTypes, Entity entity) {
            this.damageTypes = damageTypes;
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }

        public Map<DamageType, Integer> getDamageTypes() {
            return damageTypes;
        }

    }

    /**
     * Called whenever an entity kill tag is received by the client
     */
    public static class KillEntity extends GameEvent {
        Entity entity;

        public KillEntity(Entity entity) {
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }

    }

    /**
     * Called when a discovery is found
     */
    public static class DiscoveryFound extends GameEvent {
        public static class Secret extends DiscoveryFound { }
        public static class World extends DiscoveryFound { }
    }

}
