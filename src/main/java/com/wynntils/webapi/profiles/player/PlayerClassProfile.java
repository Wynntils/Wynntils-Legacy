package com.wynntils.webapi.profiles.player;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerClassProfile {

    private String name;
    private int level;
    private HashMap<String, Integer> dungeonsCompleted;
    private ArrayList<String> questsCompleted;
    private int itemsIdentified;
    private int mobsKilled;
    private int pvpKills;
    private int pvpDeaths;
    private int chestsFound;
    private long blocksWalked;
    private int logins;
    private int deaths;
    private int playtime;
    private int skillStrength;
    private int skillDexterity;
    private int skillIntelligence;
    private int skillDefense;
    private int skillAgility;
    private int alchemismLevel;
    private int alchemismXp;
    private int armouringLevel;
    private int armouringXp;
    private int combatLevel;
    private int combatXp;
    private int cookingLevel;
    private int cookingXp;
    private int farmingLevel;
    private int farmingXp;
    private int fishingLevel;
    private int fishingXp;
    private int jewelingLevel;
    private int jewelingXp;
    private int miningLevel;
    private int miningXp;
    private int scribingLevel;
    private int scribingXp;
    private int tailoringLevel;
    private int tailoringXp;
    private int weaponsmithingLevel;
    private int weaponsmithingXp;
    private int woodcuttingLevel;
    private int woodcuttingXp;
    private int woodworkingLevel;
    private int woodworkingXp;
    private int discoveries;
    private int eventsWon;
    private boolean preEconomyUpdate;

    public PlayerClassProfile(String name, int level, HashMap<String, Integer> dungeonsCompleted, ArrayList<String> questsCompleted, int itemsIdentified, int mobsKilled, int pvpKills, int pvpDeaths, int chestsFound, long blocksWalked, int logins, int deaths, int playtime, int skillStrength, int skillDexterity, int skillIntelligence, int skillDefense, int skillAgility, int alchemismLevel, int alchemismXp, int armouringLevel, int armouringXp, int combatLevel, int combatXp, int cookingLevel, int cookingXp, int farmingLevel, int farmingXp, int fishingLevel, int fishingXp, int jewelingLevel, int jewelingXp, int miningLevel, int miningXp, int scribingLevel, int scribingXp, int tailoringLevel, int tailoringXp, int weaponsmithingLevel, int weaponsmithingXp, int woodcuttingLevel, int woodcuttingXp, int woodworkingLevel, int woodworkingXp, int discoveries, int eventsWon, boolean preEconomyUpdate) {
        this.name = name;
        this.level = level;
        this.dungeonsCompleted = dungeonsCompleted;
        this.questsCompleted = questsCompleted;
        this.itemsIdentified = itemsIdentified;
        this.mobsKilled = mobsKilled;
        this.pvpKills = pvpKills;
        this.pvpDeaths = pvpDeaths;
        this.chestsFound = chestsFound;
        this.blocksWalked = blocksWalked;
        this.logins = logins;
        this.deaths = deaths;
        this.playtime = playtime;
        this.skillStrength = skillStrength;
        this.skillDexterity = skillDexterity;
        this.skillIntelligence = skillIntelligence;
        this.skillDefense = skillDefense;
        this.skillAgility = skillAgility;
        this.alchemismLevel = alchemismLevel;
        this.alchemismXp = alchemismXp;
        this.armouringLevel = armouringLevel;
        this.armouringXp = armouringXp;
        this.combatLevel = combatLevel;
        this.combatXp = combatXp;
        this.cookingLevel = cookingLevel;
        this.cookingXp = cookingXp;
        this.farmingLevel = farmingLevel;
        this.farmingXp = farmingXp;
        this.fishingLevel = fishingLevel;
        this.fishingXp = fishingXp;
        this.jewelingLevel = jewelingLevel;
        this.jewelingXp = jewelingXp;
        this.miningLevel = miningLevel;
        this.miningXp = miningXp;
        this.scribingLevel = scribingLevel;
        this.scribingXp = scribingXp;
        this.tailoringLevel = tailoringLevel;
        this.tailoringXp = tailoringXp;
        this.weaponsmithingLevel = weaponsmithingLevel;
        this.weaponsmithingXp = weaponsmithingXp;
        this.woodcuttingLevel = woodcuttingLevel;
        this.woodcuttingXp = woodcuttingXp;
        this.woodworkingLevel = woodworkingLevel;
        this.woodworkingXp = woodworkingXp;
        this.discoveries = discoveries;
        this.eventsWon = eventsWon;
        this.preEconomyUpdate = preEconomyUpdate;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public HashMap<String, Integer> getDungeonsCompleted() {
        return dungeonsCompleted;
    }

    public ArrayList<String> getQuestsCompleted() {
        return questsCompleted;
    }

    public int getItemsIdentified() {
        return itemsIdentified;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getPvpKills() {
        return pvpKills;
    }

    public int getPvpDeaths() {
        return pvpDeaths;
    }

    public int getChestsFound() {
        return chestsFound;
    }

    public long getBlocksWalked() {
        return blocksWalked;
    }

    public int getLogins() {
        return logins;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getPlaytime() {
        return playtime;
    }

    public int getSkillStrength() {
        return skillStrength;
    }

    public int getSkillDexterity() {
        return skillDexterity;
    }

    public int getSkillIntelligence() {
        return skillIntelligence;
    }

    public int getSkillDefense() {
        return skillDefense;
    }

    public int getSkillAgility() {
        return skillAgility;
    }

    public int getAlchemismLevel() {
        return alchemismLevel;
    }

    public int getAlchemismXp() {
        return alchemismXp;
    }

    public int getArmouringLevel() {
        return armouringLevel;
    }

    public int getArmouringXp() {
        return armouringXp;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int getCombatXp() {
        return combatXp;
    }

    public int getCookingLevel() {
        return cookingLevel;
    }

    public int getCookingXp() {
        return cookingXp;
    }

    public int getFarmingLevel() {
        return farmingLevel;
    }

    public int getFarmingXp() {
        return farmingXp;
    }

    public int getFishingLevel() {
        return fishingLevel;
    }

    public int getFishingXp() {
        return fishingXp;
    }

    public int getJewelingLevel() {
        return jewelingLevel;
    }

    public int getJewelingXp() {
        return jewelingXp;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public int getMiningXp() {
        return miningXp;
    }

    public int getScribingLevel() {
        return scribingLevel;
    }

    public int getScribingXp() {
        return scribingXp;
    }

    public int getTailoringLevel() {
        return tailoringLevel;
    }

    public int getTailoringXp() {
        return tailoringXp;
    }

    public int getWeaponsmithingLevel() {
        return weaponsmithingLevel;
    }

    public int getWeaponsmithingXp() {
        return weaponsmithingXp;
    }

    public int getWoodcuttingLevel() {
        return woodcuttingLevel;
    }

    public int getWoodcuttingXp() {
        return woodcuttingXp;
    }

    public int getWoodworkingLevel() {
        return woodworkingLevel;
    }

    public int getWoodworkingXp() {
        return woodworkingXp;
    }

    public int getDiscoveries() {
        return discoveries;
    }

    public int getEventsWon() {
        return eventsWon;
    }

    public boolean isPreEconomyUpdate() {
        return preEconomyUpdate;
    }

    public static class PlayerClassProfileDeserializer implements JsonDeserializer<PlayerClassProfile> {

        @Override
        public PlayerClassProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject playerClass = json.getAsJsonObject();
            String name = playerClass.get("name").getAsString();
            int level = playerClass.get("level").getAsInt();

            HashMap<String, Integer> dungeonsCompleted = new HashMap<>();
            JsonArray dungeonsJson = playerClass.get("dungeons").getAsJsonObject().get("list").getAsJsonArray();
            for (JsonElement dungeonJson : dungeonsJson) {
                JsonObject object = dungeonJson.getAsJsonObject();
                dungeonsCompleted.put(object.get("name").getAsString(), object.get("completed").getAsInt());
            }

            ArrayList<String> questsCompleted = new ArrayList<>();
            JsonArray questsJson = playerClass.get("quests").getAsJsonObject().get("list").getAsJsonArray();
            for (JsonElement element : questsJson)
                questsCompleted.add(element.getAsString());

            int itemsIdentified = playerClass.get("itemsIdentified").getAsInt();
            int mobsKilled = playerClass.get("mobsKilled").getAsInt();

            JsonObject pvpStats = playerClass.get("pvp").getAsJsonObject();
            int pvpKills = pvpStats.get("kills").getAsInt();
            int pvpDeaths = pvpStats.get("deaths").getAsInt();

            int chestsFound = playerClass.get("chestsFound").getAsInt();
            long blocksWalked = playerClass.get("blocksWalked").getAsLong();
            int logins = playerClass.get("logins").getAsInt();
            int deaths = playerClass.get("deaths").getAsInt();
            int playtime = playerClass.get("playtime").getAsInt();

            JsonObject skills = playerClass.get("skills").getAsJsonObject();
            int skillStrength = skills.get("strength").getAsInt();
            int skillDexterity = skills.get("dexterity").getAsInt();
            int skillIntelligence = skills.get("intelligence").getAsInt();
            int skillDefense = skills.get("defense").getAsInt();
            int skillAgility = skills.get("agility").getAsInt();

            JsonObject professions = playerClass.get("professions").getAsJsonObject();
            JsonObject alchemism = professions.get("alchemism").getAsJsonObject();
            int alchemismLevel = alchemism.get("level").getAsInt();
            int alchemismXp = alchemism.get("xp").getAsInt();
            JsonObject armouring = professions.get("armouring").getAsJsonObject();
            int armouringLevel = armouring.get("level").getAsInt();
            int armouringXp = armouring.get("xp").getAsInt();
            JsonObject combat = professions.get("combat").getAsJsonObject();
            int combatLevel = combat.get("level").getAsInt();
            int combatXp = combat.get("xp").getAsInt();
            JsonObject cooking = professions.get("cooking").getAsJsonObject();
            int cookingLevel = cooking.get("level").getAsInt();
            int cookingXp = cooking.get("xp").getAsInt();
            JsonObject farming = professions.get("farming").getAsJsonObject();
            int farmingLevel = farming.get("level").getAsInt();
            int farmingXp = farming.get("xp").getAsInt();
            JsonObject fishing = professions.get("fishing").getAsJsonObject();
            int fishingLevel = fishing.get("level").getAsInt();
            int fishingXp = fishing.get("xp").getAsInt();
            JsonObject jeweling = professions.get("jeweling").getAsJsonObject();
            int jewelingLevel = jeweling.get("level").getAsInt();
            int jewelingXp = jeweling.get("xp").getAsInt();
            JsonObject mining = professions.get("mining").getAsJsonObject();
            int miningLevel = mining.get("level").getAsInt();
            int miningXp = mining.get("xp").getAsInt();
            JsonObject scribing = professions.get("scribing").getAsJsonObject();
            int scribingLevel = scribing.get("level").getAsInt();
            int scribingXp = scribing.get("xp").getAsInt();
            JsonObject tailoring = professions.get("tailoring").getAsJsonObject();
            int tailoringLevel = tailoring.get("level").getAsInt();
            int tailoringXp = tailoring.get("xp").getAsInt();
            JsonObject weaponsmithing = professions.get("weaponsmithing").getAsJsonObject();
            int weaponsmithingLevel = weaponsmithing.get("level").getAsInt();
            int weaponsmithingXp = weaponsmithing.get("xp").getAsInt();
            JsonObject woodcutting = professions.get("woodcutting").getAsJsonObject();
            int woodcuttingLevel = woodcutting.get("level").getAsInt();
            int woodcuttingXp = woodcutting.get("xp").getAsInt();
            JsonObject woodworking = professions.get("woodworking").getAsJsonObject();
            int woodworkingLevel = woodworking.get("level").getAsInt();
            int woodworkingXp = woodworking.get("xp").getAsInt();

            int discoveries = playerClass.get("discoveries").getAsInt();
            int eventsWon = playerClass.get("eventsWon").getAsInt();
            boolean preEconomyUpdate = playerClass.get("preEconomyUpdate").getAsBoolean();
            return new PlayerClassProfile(name, level, dungeonsCompleted, questsCompleted, itemsIdentified, mobsKilled, pvpKills, pvpDeaths, chestsFound, blocksWalked, logins, deaths, playtime, skillStrength, skillDexterity, skillIntelligence, skillDefense, skillAgility, alchemismLevel, alchemismXp, armouringLevel, armouringXp, combatLevel, combatXp, cookingLevel, cookingXp, farmingLevel, farmingXp, fishingLevel, fishingXp, jewelingLevel, jewelingXp, miningLevel, miningXp, scribingLevel, scribingXp, tailoringLevel, tailoringXp, weaponsmithingLevel, weaponsmithingXp, woodcuttingLevel, woodcuttingXp, woodworkingLevel, woodworkingXp, discoveries, eventsWon, preEconomyUpdate);
        }
    }
}
