package com.wynndevs.modules.expansion.itemguide;

import com.wynndevs.modules.expansion.webapi.ItemDB;

import java.util.ArrayList;
import java.util.List;

public class ItemSE {
	
	static List<Short> ItemSE = new ArrayList<Short>();
	
	static long SortCritteria = 0; //
	static byte SortOrder = 0; // (0=Lowest 1=Average 2=Highest) + (0=Highest to lowest 3=Lowest to highest)
	static boolean[] ItemTypeFilter = {false, false, false, false, false, false, false, false, false, false, false, false}; // Bitfield Item type filter
	static short[] Level = {0, 1000};
	static boolean[] ItemClassFilter = {false, false, false, false};
	static byte FilterRestrictions = 0; // 1 = Untradable, 2 = Quest
	static byte DropType = 0;
	static byte Slots = 0;
	
	// Damadge Stats
	static byte AttackSpeed = Byte.MIN_VALUE;
	static int[] Damage = {0, 0};
	static int[] EarthDamage = {0, 0};
	static int[] ThunderDamage = {0, 0};
	static int[] WaterDamage = {0, 0};
	static int[] FireDamage = {0, 0};
	static int[] AirDamage = {0, 0};
	
	// Defence Stats
	static short Health = Short.MIN_VALUE;
	static short EarthDefence = Short.MIN_VALUE;
	static short ThunderDefence = Short.MIN_VALUE;
	static short WaterDefence = Short.MIN_VALUE;
	static short FireDefence = Short.MIN_VALUE;
	static short AirDefence = Short.MIN_VALUE;
	
	// Skill Requirements
	static byte Strength = 0;
	static byte Dexterity = 0;
	static byte Intelligence = 0;
	static byte Defence = 0;
	static byte Agility = 0;
	
	
	// BONUS STATS
	static byte AttackSpeedBonus = Byte.MIN_VALUE;
	
	static short DamageBonus = Short.MIN_VALUE;
	static short DamageRawBonus = Short.MIN_VALUE;
	
	static short SpellDamageBonus = Short.MIN_VALUE;
	static short SpellDamageRawBonus = Short.MIN_VALUE;
	
	static short HealthBonus = Short.MIN_VALUE;
	static short HealthRegenBonus = Short.MIN_VALUE;
	static short HealthRegenRawBonus = Short.MIN_VALUE;
	
	static short LifeStealBonus = Short.MIN_VALUE;
	static byte ManaRegenBonus = Byte.MIN_VALUE;
	static byte ManaStealBonus = Byte.MIN_VALUE;
	
	static short EarthDamageBonus = Short.MIN_VALUE;
	static short ThunderDamageBonus = Short.MIN_VALUE;
	static short WaterDamageBonus = Short.MIN_VALUE;
	static short FireDamageBonus = Short.MIN_VALUE;
	static short AirDamageBonus = Short.MIN_VALUE;
	
	static short EarthDefenceBonus = Short.MIN_VALUE;
	static short ThunderDefenceBonus = Short.MIN_VALUE;
	static short WaterDefenceBonus = Short.MIN_VALUE;
	static short FireDefenceBonus = Short.MIN_VALUE;
	static short AirDefenceBonus = Short.MIN_VALUE;
	
	static byte StrengthBonus = Byte.MIN_VALUE;
	static byte DexterityBonus = Byte.MIN_VALUE;
	static byte IntelligenceBonus = Byte.MIN_VALUE;
	static byte DefenceBonus = Byte.MIN_VALUE;
	static byte AgilityBonus = Byte.MIN_VALUE;
	
	static short SpeedBonus = Short.MIN_VALUE;
	static short ExplodingBonus = Short.MIN_VALUE;
	static short PoisonBonus = Short.MIN_VALUE;
	static short ThornsBonus = Short.MIN_VALUE;
	static short ReflectionBonus = Short.MIN_VALUE;
	static short SoulPointBonus = Short.MIN_VALUE;
	static short StealingBonus = Short.MIN_VALUE;
	static short LootBonus = Short.MIN_VALUE;
	static short ExpBonus = Short.MIN_VALUE;
	
	public static void CreateDB() {
		ItemSE.clear();
		for (int i=0;i<ItemDB.ItemDB.size();i++) {
			if (SortOrder >= 3) {
				ItemSE.add(0, (short) i);
			}else{
				ItemSE.add((short) i);
			}
		}
	}
	
	private static void TypeFilterSE() {
		boolean Filtering = false;
		for (boolean Filter : ItemTypeFilter) {
			if (Filter) {
				Filtering = true;
				break;
			}
		}
		if (Filtering) {
			for (int i=0;i<ItemSE.size();i++) {
				if (!ItemTypeFilter[ItemDB.ItemDB.get(ItemSE.get(i)).GetType()]) {
					ItemSE.remove(i);
					i--;
				}
			}
		}
		Filtering = false;
		for (boolean Filter : ItemClassFilter) {
			if (Filter) {
				Filtering = true;
				break;
			}
		}
		if (Filtering) {
			for (int i=0;i<ItemSE.size();i++) {
				if (!ItemDB.ItemDB.get(ItemSE.get(i)).CanClassUse(ItemClassFilter)) {
					ItemSE.remove(i);
					i--;
				}
			}
		}
	}
	
	public static void SortList() {
		for (int i=0;i<ItemSE.size();i++) {
			for (int j=0;j<i;j++) {
				if (CompareItems(i, j)) {
					ItemSE.add(j, ItemSE.get(i));
					ItemSE.remove(i+1);
					break;
				}
			}
		}
	}
	
	private static boolean CompareItems(int Item1, int Item2) {
		int[] Stats1 = {0, 0};
		int[] Stats2 = {0 ,0};
		
		long Bitfield = SortCritteria;
		long BitfieldCheck = 4611686018427387904L; //Long.MAX_VALUE/2;
		
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLevel(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLevel();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLevel(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLevel();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetRarity(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetRarity();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetRarity(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetRarity();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).Slots; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).Slots;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).Slots; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).Slots;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		
		//Attack
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).AttackSpeed; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).AttackSpeed;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).AttackSpeed; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).AttackSpeed;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDamage()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDamage()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDamage()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDamage()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		
		//Defence
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).Health; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).Health;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).Health; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).Health;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).FireDefence; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).FireDefence;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).FireDefence; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).FireDefence;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).WaterDefence; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).WaterDefence;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).WaterDefence; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).WaterDefence;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).AirDefence; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).AirDefence;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).AirDefence; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).AirDefence;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).ThunderDefence; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).ThunderDefence;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).ThunderDefence; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).ThunderDefence;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).EarthDefence; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).EarthDefence;
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).EarthDefence; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).EarthDefence;
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		
		//Skill Requirement
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStrengthRequirement(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStrengthRequirement();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStrengthRequirement(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStrengthRequirement();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDexterityRequirement(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDexterityRequirement();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDexterityRequirement(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDexterityRequirement();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetIntelligenceRequirement(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetIntelligenceRequirement();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetIntelligenceRequirement(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetIntelligenceRequirement();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDefenceRequirement(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDefenceRequirement();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDefenceRequirement(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDefenceRequirement();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAgilityRequirement(); Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAgilityRequirement();
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAgilityRequirement(); Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAgilityRequirement();
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		
		//BONUS STATS
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAttackSpeedBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAttackSpeedBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAttackSpeedBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAttackSpeedBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Damadge
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamageRawBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDamageRawBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamageRawBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDamageRawBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Health
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthRegenBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthRegenBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthRegenBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthRegenBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthRegenRawBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetHealthRegenRawBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthRegenRawBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetHealthRegenRawBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Steals
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLifeStealBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLifeStealBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLifeStealBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLifeStealBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetManaRegenBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetManaRegenBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetManaRegenBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetManaRegenBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetManaStealBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetManaStealBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetManaStealBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetManaStealBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Damage
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDamageBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDamageBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDamageBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDamageBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Defence
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetFireDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetFireDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetWaterDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetWaterDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAirDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAirDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThunderDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThunderDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetEarthDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetEarthDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Bonus Skills
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStrengthBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStrengthBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStrengthBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStrengthBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDexterityBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDexterityBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDexterityBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDexterityBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetIntelligenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetIntelligenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetIntelligenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetIntelligenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDefenceBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetDefenceBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDefenceBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetDefenceBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAgilityBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetAgilityBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAgilityBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetAgilityBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
	// Other Bonus Stats
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetSpeedBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetSpeedBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetSpeedBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetSpeedBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetExplodingBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetExplodingBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetExplodingBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetExplodingBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetPoisonBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetPoisonBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetPoisonBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetPoisonBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThornsBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetThornsBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThornsBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetThornsBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetReflectionBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetReflectionBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetReflectionBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetReflectionBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetSoulPointBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetSoulPointBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetSoulPointBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetSoulPointBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStealingBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetStealingBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStealingBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetStealingBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLootBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetLootBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLootBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetLootBonus()[1];
			Bitfield = Bitfield - BitfieldCheck;
		}
		BitfieldCheck = BitfieldCheck/2;
		if (Bitfield >= BitfieldCheck) {
			Stats1[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetExpBonus()[0]; Stats1[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item1)).GetExpBonus()[1];
			Stats2[0] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetExpBonus()[0]; Stats2[1] =+ ItemDB.ItemDB.get(ItemSE.get(Item2)).GetExpBonus()[1];
		}
		return CompareItemStat(Stats1, Stats2);
	}
	
	private static boolean CompareItemStat(int[] Stat1, int[] Stat2) {
		if (Stat1.length == 1) {
			if (Stat1[0] > Stat2[0]) return (SortOrder >= 3 ? false : true);
		}
		switch (SortOrder%3) {
			case 0: if (Stat1[0] > Stat2[0]) return (SortOrder >= 3 ? false : true);
			case 1: if ((Stat1[0] + Stat1[1])/2 > (Stat2[0] + Stat2[1])/2) return (SortOrder >= 3 ? false : true);
			case 2: if (Stat1[1] > Stat2[1]) return (SortOrder >= 3 ? false : true);
			default: return false;
		}
	}
	
	private static void StatFilterSE() {
		int Bump = 0;
		for (int i=0;i<ItemSE.size();i++) {
			if (!PartialMatchRequirements(ItemDB.ItemDB.get(ItemSE.get(i)))) {
				ItemSE.remove(i);
				i--;
			}else if (MatchRequirements(ItemDB.ItemDB.get(ItemSE.get(i)))) {
				ItemSE.add(Bump, ItemSE.get(i));
				ItemSE.remove(i+1);
				Bump++;
			}
		}
	}
	
	private static boolean MatchRequirements(WynnItem Item) {
		//if (Item.AttackSpeed < AttackSpeed) return false;
		if (Item.GetDamage()[1] < Damage[1]) return false;
		if (Item.GetEarthDamage()[1] < EarthDamage[1]) return false;
		if (Item.GetThunderDamage()[1] < ThunderDamage[1]) return false;
		if (Item.GetWaterDamage()[1] < WaterDamage[1]) return false;
		if (Item.GetFireDamage()[1] < FireDamage[1]) return false;
		if (Item.GetAirDamage()[1] < AirDamage[1]) return false;
		/*
		if (Item.Health < Health) return false;
		if (Item.EarthDefence < EarthDefence) return false;
		if (Item.ThunderDefence < ThunderDefence) return false;
		if (Item.WaterDefence < WaterDefence) return false;
		if (Item.FireDefence < FireDefence) return false;
		if (Item.AirDefence < AirDefence) return false;
		
		if (Item.GetStrengthRequirement() < Strength) return false;
		if (Item.GetDexterityRequirement() < Dexterity) return false;
		if (Item.GetIntelligenceRequirement() < Intelligence) return false;
		if (Item.GetDefenceRequirement() < Defence) return false;
		if (Item.GetAgilityRequirement() < Agility) return false;
		*/
		// BONUS STATS
		if (Item.GetAttackSpeedBonus()[1] < AttackSpeedBonus) return false;
		
		if (Item.GetDamageBonus()[1] < DamageBonus) return false;
		if (Item.GetDamageRawBonus()[1] < DamageRawBonus) return false;
		
		if (Item.GetSpellDamageBonus()[1] < SpellDamageBonus) return false;
		if (Item.GetSpellDamageRawBonus()[1] < SpellDamageRawBonus) return false;
		
		if (Item.GetHealthBonus()[1] < HealthBonus) return false;
		if (Item.GetHealthRegenBonus()[1] < HealthRegenBonus) return false;
		if (Item.GetHealthRegenRawBonus()[1] < HealthRegenRawBonus) return false;
		
		if (Item.GetLifeStealBonus()[1] < LifeStealBonus) return false;
		if (Item.GetManaRegenBonus()[1] < ManaRegenBonus) return false;
		if (Item.GetManaStealBonus()[1] < ManaStealBonus) return false;
		
		if (Item.GetEarthDamageBonus()[1] < EarthDamageBonus) return false;
		if (Item.GetThunderDamageBonus()[1] < ThunderDamageBonus) return false;
		if (Item.GetWaterDamageBonus()[1] < WaterDamageBonus) return false;
		if (Item.GetFireDamageBonus()[1] < FireDamageBonus) return false;
		if (Item.GetAirDamageBonus()[1] < AirDamageBonus) return false;
		
		if (Item.GetEarthDefenceBonus()[1] < EarthDefenceBonus) return false;
		if (Item.GetThunderDefenceBonus()[1] < ThunderDefenceBonus) return false;
		if (Item.GetWaterDefenceBonus()[1] < WaterDefenceBonus) return false;
		if (Item.GetFireDefenceBonus()[1] < FireDefenceBonus) return false;
		if (Item.GetAirDefenceBonus()[1] < AirDefenceBonus) return false;
		
		if (Item.GetStrengthBonus()[1] < StrengthBonus) return false;
		if (Item.GetDexterityBonus()[1] < DexterityBonus) return false;
		if (Item.GetIntelligenceBonus()[1] < IntelligenceBonus) return false;
		if (Item.GetDefenceBonus()[1] < DefenceBonus) return false;
		if (Item.GetAgilityBonus()[1] < AgilityBonus) return false;
		
		if (Item.GetSpeedBonus()[1] < SpeedBonus) return false;
		if (Item.GetExplodingBonus()[1] < ExplodingBonus) return false;
		if (Item.GetPoisonBonus()[1] < PoisonBonus) return false;
		if (Item.GetThornsBonus()[1] < ThornsBonus) return false;
		if (Item.GetReflectionBonus()[1] < ReflectionBonus) return false;
		if (Item.GetSoulPointBonus()[1] < SoulPointBonus) return false;
		if (Item.GetStealingBonus()[1] < StealingBonus) return false;
		if (Item.GetLootBonus()[1] < LootBonus) return false;
		if (Item.GetExpBonus()[1] < ExpBonus) return false;
		return true;
	}
	
	private static boolean PartialMatchRequirements(WynnItem Item) {
		if (!(Item.GetLevel() >= Level[0] && Item.GetLevel() <= Level[1])) return false;
		
		
		//if (Item.AttackSpeed < AttackSpeed) return false;
		if (Item.GetDamage()[0] < Damage[0]) return false;
		if (Item.GetEarthDamage()[0] < EarthDamage[0]) return false;
		if (Item.GetThunderDamage()[0] < ThunderDamage[0]) return false;
		if (Item.GetWaterDamage()[0] < WaterDamage[0]) return false;
		if (Item.GetFireDamage()[0] < FireDamage[0]) return false;
		if (Item.GetAirDamage()[0] < AirDamage[0]) return false;
		
		if (Item.Health < Health) return false;
		if (Item.EarthDefence < EarthDefence) return false;
		if (Item.ThunderDefence < ThunderDefence) return false;
		if (Item.WaterDefence < WaterDefence) return false;
		if (Item.FireDefence < FireDefence) return false;
		if (Item.AirDefence < AirDefence) return false;
		
		if (Item.GetStrengthRequirement() < Strength) return false;
		if (Item.GetDexterityRequirement() < Dexterity) return false;
		if (Item.GetIntelligenceRequirement() < Intelligence) return false;
		if (Item.GetDefenceRequirement() < Defence) return false;
		if (Item.GetAgilityRequirement() < Agility) return false;
		
		// BONUS STATS
		if (Item.GetAttackSpeedBonus()[0] < AttackSpeedBonus) return false;
		
		if (Item.GetDamageBonus()[0] < DamageBonus) return false;
		if (Item.GetDamageRawBonus()[0] < DamageRawBonus) return false;
		
		if (Item.GetSpellDamageBonus()[0] < SpellDamageBonus) return false;
		if (Item.GetSpellDamageRawBonus()[0] < SpellDamageRawBonus) return false;
		
		if (Item.GetHealthBonus()[0] < HealthBonus) return false;
		if (Item.GetHealthRegenBonus()[0] < HealthRegenBonus) return false;
		if (Item.GetHealthRegenRawBonus()[0] < HealthRegenRawBonus) return false;
		
		if (Item.GetLifeStealBonus()[0] < LifeStealBonus) return false;
		if (Item.GetManaRegenBonus()[0] < ManaRegenBonus) return false;
		if (Item.GetManaStealBonus()[0] < ManaStealBonus) return false;
		
		if (Item.GetEarthDamageBonus()[0] < EarthDamageBonus) return false;
		if (Item.GetThunderDamageBonus()[0] < ThunderDamageBonus) return false;
		if (Item.GetWaterDamageBonus()[0] < WaterDamageBonus) return false;
		if (Item.GetFireDamageBonus()[0] < FireDamageBonus) return false;
		if (Item.GetAirDamageBonus()[0] < AirDamageBonus) return false;
		
		if (Item.GetEarthDefenceBonus()[0] < EarthDefenceBonus) return false;
		if (Item.GetThunderDefenceBonus()[0] < ThunderDefenceBonus) return false;
		if (Item.GetWaterDefenceBonus()[0] < WaterDefenceBonus) return false;
		if (Item.GetFireDefenceBonus()[0] < FireDefenceBonus) return false;
		if (Item.GetAirDefenceBonus()[0] < AirDefenceBonus) return false;
		
		if (Item.GetStrengthBonus()[0] < StrengthBonus) return false;
		if (Item.GetDexterityBonus()[0] < DexterityBonus) return false;
		if (Item.GetIntelligenceBonus()[0] < IntelligenceBonus) return false;
		if (Item.GetDefenceBonus()[0] < DefenceBonus) return false;
		if (Item.GetAgilityBonus()[0] < AgilityBonus) return false;
		
		if (Item.GetSpeedBonus()[0] < SpeedBonus) return false;
		if (Item.GetExplodingBonus()[0] < ExplodingBonus) return false;
		if (Item.GetPoisonBonus()[0] < PoisonBonus) return false;
		if (Item.GetThornsBonus()[0] < ThornsBonus) return false;
		if (Item.GetReflectionBonus()[0] < ReflectionBonus) return false;
		if (Item.GetSoulPointBonus()[0] < SoulPointBonus) return false;
		if (Item.GetStealingBonus()[0] < StealingBonus) return false;
		if (Item.GetLootBonus()[0] < LootBonus) return false;
		if (Item.GetExpBonus()[0] < ExpBonus) return false;
		return true;
	}
	
	public static void Search(String Search) {
		
		// Get Fresh Sorted DB
		CreateDB();
		// Filter out by ItemType
		TypeFilterSE();
		// Sort Search Engine
		if (SortCritteria != 0 || SortOrder >= 3) SortList();
		// Cull list and bump matches
		int Bump = 0;
		for (int i=0;i<ItemSE.size();i++) {
			
			if (ItemDB.ItemDB.get(ItemSE.get(i)).Name.length() >= Search.length()) {
				if (!ItemDB.ItemDB.get(ItemSE.get(i)).Name.toLowerCase().contains(Search.toLowerCase())) {
					ItemSE.remove(i);
					i--;
				}else if (ItemDB.ItemDB.get(ItemSE.get(i)).Name.toLowerCase().equals(Search.toLowerCase())) {
					ItemSE.add(0, ItemSE.get(i));
					ItemSE.remove(i+1);
					Bump++;
				}else if (ItemDB.ItemDB.get(ItemSE.get(i)).Name.toLowerCase().startsWith(Search.toLowerCase())) {
					ItemSE.add(Bump, ItemSE.get(i));
					ItemSE.remove(i+1);
					Bump++;
				}
			}else{
				if (!Search.toLowerCase().contains(ItemDB.ItemDB.get(ItemSE.get(i)).Name.toLowerCase())) {
					ItemSE.remove(i);
					i--;
				}
			}
		}
		// Filter by Stats
		StatFilterSE();
		
		
		
		// Advanced Sorting by Match Rating
		/*for (int i=0;i<ItemSE.size();i++) {
			double Test1 = similarity(ItemDB.ItemDB.get(ItemSE.get(i)).Name, Search);
			for (int j=0;j<i;j++) {
				double Test2 = similarity(ItemDB.ItemDB.get(ItemSE.get(j)).Name, Search);
				if (Test1 > Test2) {
					ItemSE.add(j, ItemSE.get(i));
					ItemSE.remove(i+1);
					break;
				}
			}
		}*/
	}
	
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2; shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) {return 1.0;}
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}
	
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		
		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				}else{
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue),
									costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
}
