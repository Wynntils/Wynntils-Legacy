package com.wynndevs.expansion.WebAPI;

import com.wynndevs.expansion.ExpReference;
import com.wynndevs.expansion.ItemGuide.WynnItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItemDB {
	
	// StackA = StackA + ((StackB - StackA) / StackAcount)
	// Average = Average + ((New - Average) / Count)
	
	
	public static List<WynnItem> ItemDB = new CopyOnWriteArrayList<WynnItem>();
	private static String[] ItemAttributeList = { "name", "category", "type", "accessoryType", "tier", "level", "classRequirement",
			"material", "armorType", "armorColor", "set", "addedLore", "restrictions", "quest", "dropType", "identified",
			"attackSpeed", "damage", "earthDamage", "thunderDamage", "waterDamage", "fireDamage", "airDamage", "health",
			"earthDefense", "thunderDefense", "waterDefense", "fireDefense", "airDefense", "strength", "dexterity",
			"intelligence", "defense", "agility", "attackSpeedBonus", "damageBonus", "damageBonusRaw", "spellDamage",
			"spellDamageRaw", "healthBonus", "healthRegen", "healthRegenRaw", "lifeSteal", "manaRegen", "manaSteal",
			"bonusEarthDamage", "bonusThunderDamage", "bonusWaterDamage", "bonusFireDamage", "bonusAirDamage",
			"bonusEarthDefense", "bonusThunderDefense", "bonusWaterDefense", "bonusFireDefense", "bonusAirDefense",
			"strengthPoints", "dexterityPoints", "intelligencePoints", "defensePoints", "agilityPoints", "speed",
			"exploding", "poison", "thorns", "reflection", "soulPoints", "emeraldStealing", "lootBonus", "xpBonus", "sockets" };
	
	public static void GenerateItemDB() {
		try {
			ExpReference.ConsoleOut("Generating Item database [1]");
			ItemDB.clear();
			List<String> ItemDBTmp = new ArrayList<String>();
			// String ItemDBRawURL = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/kQ4tz12Y").openConnection().getInputStream())).readLine();
			BufferedReader ItemDBRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.ItemDBURL).openConnection().getInputStream()));
			String ItemDBRaw = ItemDBRawURL.readLine();
			ItemDBRawURL.close();
			
			for (int LastLocation = 1; ItemDBRaw.indexOf("{", LastLocation) != -1; LastLocation++) {
				ItemDBTmp.add(ItemDBRaw.substring(ItemDBRaw.indexOf('{', LastLocation), ItemDBRaw.indexOf('}', ItemDBRaw.indexOf('{', LastLocation))) + ",\"");
				LastLocation = ItemDBRaw.indexOf("}", LastLocation);
			}
			
			Collections.sort(ItemDBTmp, String.CASE_INSENSITIVE_ORDER);
			
			for (int i = 0; i < ItemDBTmp.size(); i++) {
				WynnItem Item = new WynnItem();
				
				for (int j = 0; j < ItemAttributeList.length; j++) {
					if (ItemDBTmp.get(i).contains("\"" + ItemAttributeList[j].toString() + "\":")) {
						Item = ParseItemData(Item, j, ItemDBTmp.get(i).substring(ItemDBTmp.get(i).indexOf("\"" + ItemAttributeList[j].toString() + "\":") + ItemAttributeList[j].length() + 3, ItemDBTmp.get(i).indexOf(",\"", ItemDBTmp.get(i).indexOf("\"" + ItemAttributeList[j].toString() + "\":") + ItemAttributeList[j].length() + 3)).replace("\"", ""));
					}
				}
				if (!Item.Name.contentEquals("YAMLException") && !Item.Name.equals("null")) {
					boolean NewEntry = true;
					for (WynnItem ItemTest : ItemDB) {
						if (ItemTest.Name.equals(Item.Name)) {
							ExpReference.ConsoleOut("Duplicate Item Detected: " + Item.Name);
							NewEntry = false;
							break;
						}
					}
					if (NewEntry) ItemDB.add(Item);
				}
				
			}
			ItemDBTmp.clear();
			ExpReference.ConsoleOut("Item database [1] generated, contianing " + ItemDB.size() + " items");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static WynnItem ParseItemData(WynnItem Item, int ItemAttributeID, String ItemData) {
		if (ItemData == null || ItemData.equals("null")) return Item;
		
		switch (ItemAttributeID) {
		case 0: Item.Name = ItemData; break;
		case 2: Item.SetCagegoryTypeRarity(ItemData, "0"); break;
		case 3: Item.SetCagegoryTypeRarity(ItemData, "0"); break;
		case 4: Item.SetCagegoryTypeRarity(String.valueOf(Item.GetType()), ItemData); break;
		case 5: Item.SetLevel((ItemData.matches("[0-9]+") ? Integer.parseInt(ItemData) : 0)); break;
		case 6: Item.SetClass(ItemData); break;
		case 7: Item.Material = ItemData; break;
		case 8: Item.SetArmourMaterial(ItemData); break;
		case 9: Item.SetArmourColour(ItemData); break;
		case 10: Item.Set = ItemData; break;
		case 11: Item.ParseLore(ItemData); break;
		case 12: Item.SetRestrictions(ItemData); break;
		case 13: Item.SetQuest(ItemData); break;
		case 14: Item.SetDropType(ItemData); break;
		case 15: Item.Identified = Boolean.parseBoolean(ItemData); break;
		
		case 16: Item.SetAttackSpeed(ItemData); break;
		case 17: Item.SetDamage(ItemData); break;
		case 18: Item.SetEarthDamage(ItemData); break;
		case 19: Item.SetThunderDamage(ItemData); break;
		case 20: Item.SetWaterDamage(ItemData); break;
		case 21: Item.SetFireDamage(ItemData); break;
		case 22: Item.SetAirDamage(ItemData); break;
		
		case 23: Item.Health = Short.parseShort(ItemData); break;
		case 24: Item.EarthDefence = Short.parseShort(ItemData); break;
		case 25: Item.ThunderDefence = Short.parseShort(ItemData); break;
		case 26: Item.WaterDefence = Short.parseShort(ItemData); break;
		case 27: Item.FireDefence = Short.parseShort(ItemData); break;
		case 28: Item.AirDefence = Short.parseShort(ItemData); break;
		
		case 29: Item.SetStrengthRequirement(Integer.parseInt(ItemData)); break;
		case 30: Item.SetDexterityRequirement(Integer.parseInt(ItemData)); break;
		case 31: Item.SetIntelligenceRequirement(Integer.parseInt(ItemData)); break;
		case 32: Item.SetDefenceRequirement(Integer.parseInt(ItemData)); break;
		case 33: Item.SetAgilityRequirement(Integer.parseInt(ItemData)); break;
		
		
		case 34: Item.SetAttackSpeedBonus(Integer.parseInt(ItemData)); break;
		case 35: Item.SetDamageBonus(Integer.parseInt(ItemData)); break;
		case 36: Item.SetDamageRawBonus(Integer.parseInt(ItemData)); break;
		case 37: Item.SetSpellDamageBonus(Integer.parseInt(ItemData)); break;
		case 38: Item.SetSpellDamageRawBonus(Integer.parseInt(ItemData)); break;
		case 39: Item.SetHealthBonus(Integer.parseInt(ItemData)); break;
		case 40: Item.SetHealthRegenBonus(Integer.parseInt(ItemData)); break;
		case 41: Item.SetHealthRegenRawBonus(Integer.parseInt(ItemData)); break;
		case 42: Item.SetLifeStealBonus(Integer.parseInt(ItemData)); break;
		case 43: Item.SetManaRegenBonus(Integer.parseInt(ItemData)); break;
		case 44: Item.SetManaStealBonus(Integer.parseInt(ItemData)); break;
		
		case 45: Item.SetEarthDamageBonus(Integer.parseInt(ItemData)); break;
		case 46: Item.SetThunderDamageBonus(Integer.parseInt(ItemData)); break;
		case 47: Item.SetWaterDamageBonus(Integer.parseInt(ItemData)); break;
		case 48: Item.SetFireDamageBonus(Integer.parseInt(ItemData)); break;
		case 49: Item.SetAirDamageBonus(Integer.parseInt(ItemData)); break;
		case 50: Item.SetEarthDefenceBonus(Integer.parseInt(ItemData)); break;
		case 51: Item.SetThunderDefenceBonus(Integer.parseInt(ItemData)); break;
		case 52: Item.SetWaterDefenceBonus(Integer.parseInt(ItemData)); break;
		case 53: Item.SetFireDefenceBonus(Integer.parseInt(ItemData)); break;
		case 54: Item.SetAirDefenceBonus(Integer.parseInt(ItemData)); break;
		
		case 55: Item.SetStrengthBonus(Integer.parseInt(ItemData)); break;
		case 56: Item.SetDexterityBonus(Integer.parseInt(ItemData)); break;
		case 57: Item.SetIntelligenceBonus(Integer.parseInt(ItemData)); break;
		case 58: Item.SetDefenceBonus(Integer.parseInt(ItemData)); break;
		case 59: Item.SetAgilityBonus(Integer.parseInt(ItemData)); break;
		
		case 60: Item.SetSpeedBonus(Integer.parseInt(ItemData)); break;
		case 61: Item.SetExplodingBonus(Integer.parseInt(ItemData)); break;
		case 62: Item.SetPoisonBonus(Integer.parseInt(ItemData)); break;
		case 63: Item.SetThornsBonus(Integer.parseInt(ItemData)); break;
		case 64: Item.SetReflectionBonus(Integer.parseInt(ItemData)); break;
		case 65: Item.SetSoulPointBonus(Integer.parseInt(ItemData)); break;
		case 66: Item.SetStealingBonus(Integer.parseInt(ItemData)); break;
		case 67: Item.SetLootBonus(Integer.parseInt(ItemData)); break;
		case 68: Item.SetExpBonus(Integer.parseInt(ItemData)); break;
		
		case 69: Item.Slots = Byte.parseByte(ItemData); break;
		}
		return Item;
	}
}
