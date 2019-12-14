package com.wynntils.core.utils;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    /**
     * Get the lore NBT tag from an item
     */
    public static NBTTagList getLoreTag(ItemStack item) {
        if (item.isEmpty()) return null;
        NBTTagCompound display = item.getSubCompound("display");
        if (display == null || !display.hasKey("Lore")) return null;

        NBTBase loreBase = display.getTag("Lore");
        NBTTagList lore;
        if (loreBase.getId() != 9) return null;

        lore = (NBTTagList) loreBase;
        if (lore.getTagType() != 8) return null;

        return lore;
    }

    /**
     * Get the lore from an item
     *
     * @return an {@link List} containing all item lore
     */
    public static List<String> getLore(ItemStack item) {
        NBTTagList loreTag = getLoreTag(item);

        List<String> lore = new ArrayList<>();
        if (loreTag == null) return lore;

        for (int i = 0; i < loreTag.tagCount(); ++i) {
            lore.add(loreTag.getStringTagAt(i));
        }

        return lore;
    }

    public static String getStringLore(ItemStack is){
        StringBuilder toReturn = new StringBuilder();
        for (String x : getLore(is)) {
            toReturn.append(x);
        }
        return toReturn.toString();
    }


    public static String getFieldName(String key) {
        if (key == null) return null;

        switch (key) {
            case "Mana Regen": return "manaRegen";
            case "Health Regen": return "healthRegen";
            case "rawHealth Regen": return "healthRegenRaw";

            case "Life Steal": return "lifeSteal";
            case "Mana Steal": return "manaSteal";
            case "XP Bonus": return "xpBonus";
            case "Loot Bonus": return "lootBonus";
            case "Stealing": return "emeraldStealing";
            case "Strength": return "strengthPoints";
            case "Dexterity": return "dexterityPoints";
            case "Intelligence": return "intelligencePoints";
            case "Agility": return "agilityPoints";
            case "Defence": return "defensePoints";
            case "Thorns": return "thorns";
            case "Exploding": return "exploding";
            case "Walk Speed": return "speed";
            case "Attack Speed": return "attackSpeedBonus";
            case "tier Attack Speed": return "attackSpeedBonus";
            case "Poison": return "poison";
            case "Health": return "healthBonus";
            case "Soul Point Regen": return "soulPoints";
            case "Reflection": return "reflection";
            case "Spell Damage": return "spellDamage";
            case "rawSpell Damage": return "spellDamageRaw";
            case "Melee Damage": return "damageBonus";
            case "rawMelee Damage": return "damageBonusRaw";
            case "Main Attack Damage": return "damageBonus";  // Name changed on Beta
            case "rawMain Attack Damage": return "damageBonusRaw";    // Name change on Beta
            case "Main Attack Neutral Damage": return "damageBonusRaw";    // Name change in case name consistency is brought to melee%
            case "rawMain Attack Neutral Damage": return "damageBonusRaw";    // Name change on 1.19 Release
            case "Neutral Spell Damage": return "spellDamage";    // Name Change on Beta
            case "rawNeutral Spell Damage": return "spellDamageRaw";  // Name Change on Beta

            case "Fire Damage": return "bonusFireDamage";
            case "Water Damage": return "bonusWaterDamage";
            case "Air Damage": return "bonusAirDamage";
            case "Thunder Damage": return "bonusThunderDamage";
            case "Earth Damage": return "bonusEarthDamage";
            case "Fire Defence": return "bonusFireDefense";
            case "Water Defence": return "bonusWaterDefense";
            case "Air Defence": return "bonusAirDefense";
            case "Thunder Defence": return "bonusThunderDefense";
            case "Earth Defence": return "bonusEarthDefense";

            default: return null;
        }
    }

    public static String getFieldName(String key, String suffix) {
        if (suffix == null) {
            String rawResult = getFieldName("raw" + key);
            return (rawResult == null ? getFieldName(key) : rawResult);
        }

        return getFieldName(key);
    }

    public static int getFieldRank(String key) {
        if (key == null) return 10000;
        switch (key) {
            case "attackSpeedBonus": return 101;

            case "damageBonusRaw": return 102;
            case "damageBonus": return 103;

            case "spellDamageRaw": return 104;
            case "spellDamage": return 105;

            case "healthBonus": return 306;
            case "healthRegenRaw": return 307;
            case "healthRegen": return 308;
            case "lifeSteal": return 309;

            case "manaRegen": return 410;
            case "manaSteal": return 411;

            case "bonusEarthDamage": return 512;
            case "bonusThunderDamage": return 513;
            case "bonusWaterDamage": return 514;
            case "bonusFireDamage": return 515;
            case "bonusAirDamage": return 516;

            case "bonusEarthDefense": return 617;
            case "bonusThunderDefense": return 618;
            case "bonusWaterDefense": return 619;
            case "bonusFireDefense": return 620;
            case "bonusAirDefense": return 621;

            case "strengthPoints": return 722;
            case "dexterityPoints": return 723;
            case "intelligencePoints": return 724;
            case "defensePoints": return 725;
            case "agilityPoints": return 726;

            case "exploding": return 827;
            case "poison": return 828;
            case "thorns": return 829;
            case "reflection": return 830;

            case "speed": return 831;
            case "sprint": return 832;       // Not properly implemented yet
            case "sprintRegen": return 833;  // Not properly implemented yet
            case "jump": return 834;         // Not properly implemented yet

            case "soulPoints": return 1035;
            case "emeraldStealing": return 1036;
            case "lootBonus": return 1037;
            case "lootQuality": return 1038;   // Not properly implemented yet

            case "xpBonus": return 1039;
            case "gatherXp": return 1040;      // Not properly implemented yet
            case "gatherSpeed": return 1041;   // Not properly implemented yet

            case "firstSpellCost": return 1242; // Not properly implemented yet
            case "secondSpellCost": return 1243; // Not properly implemented yet
            case "thirdSpellCost": return 1244; // Not properly implemented yet
            case "forthSpellCost": return 1245; // Not properly implemented yet

            default: return 10000;
        }
    }

    private static final Item EMERALD_BLOCK = Item.getItemFromBlock(Blocks.EMERALD_BLOCK);

    /**
     * @return the total amount of emeralds in an inventory, including blocks and le
     */
    public static int countMoney(IInventory inv) {
        if (inv == null) return 0;

        int money = 0;

        for (int i = 0, len = inv.getSizeInventory(); i < len; i++) {
            ItemStack it = inv.getStackInSlot(i);
            if (it.isEmpty()) continue;

            if (it.getItem() == Items.EMERALD) {
                money += it.getCount();
            } else if (it.getItem() == EMERALD_BLOCK) {
                money += it.getCount() * 64;
            } else if (it.getItem() == Items.EXPERIENCE_BOTTLE) {
                money += it.getCount() * (64 * 64);
            }
        }

        return money;
    }

}
