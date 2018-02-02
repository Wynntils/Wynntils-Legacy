package com.wynndevs.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

    public static HashMap<String, String> getItemFieldName = new HashMap<>();

    public static String arrayWithCommas(ArrayList<String> values) {
        StringBuilder total = new StringBuilder();

        for (String value : values) {
            if(total.toString().equals("")) {
                total = new StringBuilder(value);
                continue;
            }
            total.append(", ").append(value);
        }

        return total.toString().endsWith(", ") ? total.substring(0, total.length() - 2) + "." : total + ".";
    }

    public static String firstCharToUpper(String[] array) {
        StringBuilder result = new StringBuilder();

        result.append(array[0].toLowerCase());

        for(int i = 1; i < array.length; i++) {
            result.append(StringUtils.capitalize(array[i]));
        }

        return result.toString();
    }

    public static String getFieldName(String key) {
        if(getItemFieldName.size() <= 0) {
            getItemFieldName.put("Mana Regen", "manaRegen");
            getItemFieldName.put("Health Regen", "healthRegen");
            getItemFieldName.put("rawHealth Regen", "healthRegenRaw");

            getItemFieldName.put("Life Steal", "lifeSteal");
            getItemFieldName.put("Mana Steal", "manaSteal");
            getItemFieldName.put("XP Bonus", "xpBonus");
            getItemFieldName.put("Loot Bonus", "lootBonus");
            getItemFieldName.put("Stealing", "emeraldStealing");
            getItemFieldName.put("Strength", "strengthPoints");
            getItemFieldName.put("Dexterity", "dexterityPoints");
            getItemFieldName.put("Intelligence", "intelligencePoints");
            getItemFieldName.put("Agility", "agilityPoints");
            getItemFieldName.put("Defense", "defensePoints");
            getItemFieldName.put("Thorns", "thorns");
            getItemFieldName.put("Exploding", "exploding");
            getItemFieldName.put("Walk Speed", "speed");
            getItemFieldName.put("Attack Speed", "attackSpeedBonus");
            getItemFieldName.put("tier Attack Speed", "attackSpeedBonus");
            getItemFieldName.put("Poison", "poison");
            getItemFieldName.put("Health", "healthBonus");
            getItemFieldName.put("Soul Point Regen", "soulPoints");
            getItemFieldName.put("Reflection", "reflection");
            getItemFieldName.put("Spell Damage", "spellDamage");
            getItemFieldName.put("rawSpell Damage", "spellDamageRaw");
            getItemFieldName.put("Melee Damage", "damageBonus");
            getItemFieldName.put("rawMelee Damage", "damageBonusRaw");

            getItemFieldName.put("Fire Damage", "bonusFireDamage");
            getItemFieldName.put("Water Damage", "bonusWaterDamage");
            getItemFieldName.put("Air Damage", "bonusAirDamage");
            getItemFieldName.put("Thunder Damage", "bonusThunderDamage");
            getItemFieldName.put("Earth Damage", "bonusEarthDamage");
            getItemFieldName.put("Fire Defense", "bonusFireDefense");
            getItemFieldName.put("Water Defense", "bonusWaterDefense");
            getItemFieldName.put("Air Defense", "bonusAirDefense");
            getItemFieldName.put("Thunder Defense", "bonusThunderDefense");
            getItemFieldName.put("Earth Defense", "bonusEarthDefense");
        }

        return getItemFieldName.getOrDefault(key, null);
    }

    /**
     * Updates the Yggdrasil Authentication Token
     * Used at Wynntils Account Handler
     *
     * @param newToken The new provided token
     */
    public static void updateAuthToken(String newToken) {
        try{
            Field f = Session.class.getDeclaredFields()[2];
            f.setAccessible(true);
            f.set(Minecraft.getMinecraft().getSession(), newToken);
        }catch (Exception ex) { ex.printStackTrace(); }
    }

}
