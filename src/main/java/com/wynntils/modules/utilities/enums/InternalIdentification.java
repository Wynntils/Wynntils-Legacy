package com.wynntils.modules.utilities.enums;

public enum InternalIdentification {
    
    STRENGTHPOINTS("rawStrength"),
    DEXTERITYPOINTS("rawDexterity"),
    INTELLIGENCEPOINTS("rawIntelligence"),
    DEFENSEPOINTS("rawDefence"),
    AGILITYPOINTS("rawAgility"),
    DAMAGEBONUS("mainAttackDamage"),
    DAMAGEBONUSRAW("rawMainAttackNeutralDamage"),
    SPELLDAMAGE("spellDamage"),
    SPELLDAMAGERAW("rawNeutralSpellDamage"),
    HEALTHREGEN("healthRegen"),
    HEALTHREGENRAW("rawHealthRegen"),
    HEALTHBONUS("rawHealth"),
    POISON("poison"),
    LIFESTEAL("lifeSteal"),
    MANAREGEN("manaRegen"),
    MANASTEAL("manaSteal"),
    SPELL_COST_PCT_1("1stSpellCost"),
    SPELL_COST_RAW_1("raw1stSpellCost"),
    SPELL_COST_PCT_2("2ndSpellCost"),
    SPELL_COST_RAW_2("raw2ndSpellCost"),
    SPELL_COST_PCT_3("3rdSpellCost"),
    SPELL_COST_RAW_3("raw3rdSpellCost"),
    SPELL_COST_PCT_4("4thSpellCost"),
    SPELL_COST_RAW_4("raw4thSpellCost"),
    THORNS("thorns"),
    REFLECTION("reflection"),
    ATTACKSPEED("attackSpeed"),
    SPEED("walkSpeed"),
    EXPLODING("exploding"),
    SOULPOINTS("soulPointRegen"),
    STAMINA("sprint"),
    STAMINA_REGEN("sprintRegen"),
    JUMP_HEIGHT("rawJumpHeight"),
    XPBONUS("xpBonus"),
    LOOTBONUS("lootBonus"),
    EMERALDSTEALING("stealing"),
    EARTHDAMAGEBONUS("earthDamage"),
    THUNDERDAMAGEBONUS("thunderDamage"),
    WATERDAMAGEBONUS("waterDamage"),
    FIREDAMAGEBONUS("fireDamage"),
    AIRDAMAGEBONUS("airDamage"),
    EARTHDEFENSE("earthDefence"),
    THUNDERDEFENSE("thunderDefence"),
    WATERDEFENSE("waterDefence"),
    FIREDEFENSE("fireDefence"),
    AIRDEFENSE("airDefence");
    
    private String name;
    
    private InternalIdentification(String n) {
        name = n;
    }
    
    public String getName() {
        return name;
    }
    
    public static String getNameFromInternal(String internal) {
        for (InternalIdentification id : InternalIdentification.values()) {
            if(id.name().equalsIgnoreCase(internal)) {
                return id.getName();
            }
        }
        return null;
    }

}
