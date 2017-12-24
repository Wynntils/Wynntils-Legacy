package com.wynndevs.modules.expansion.ItemGuide;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.QuestBook.QuestBook;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class WynnItem {
	
	/**Name of item [name]**/
	public String Name = "null"; // Name of item [name]
	/**Category, Type and Rarity of item stored bitfielded (Normal = 0, Unique = 12, Rare = 24, etc) [category, type, accessoryType, tier]**/
	private byte CategoryTypeRarity = 11; // Category, Type and Rarity of item stored as a bitfield (Weapon = 0, Armour = 12, Accessory = 24) [category, type, accessoryType, tier]
	//public byte Category = 0; // Category of item (Armour, Weapon, Accessory) [category]
	//public byte Type = 0; // Type of item (Wand, Dagger, Bow, Spear) [type, accessoryType]
	//public byte Rarity = 0; // Rarety of item (Unique, Rare, Legendary etc) [tier]
	/**Level of item (34 etc) [level]**/
	private byte Level = 0; // Level of item (34 etc) [level]
	/**Class requirment for item as bitfield (1 = Archer, 2 = Warrior, 4 = Mage, 8 = Assassin) [classRequirement]**/
	private byte Class = 0; // Class requirment for item as bitfield (1 = Archer, 2 = Warrior, 4 = Mage, 8 = Assassin) [classRequirement]
	/**Item Data for the item itself () [material, armorType, armorColor]**/
	public String Material = "19:1"; // Item Data for the item itself () [material, armorType, armorColor]
	/**Armour Colour [armorColor]**/
	public int ArmourColour = 10511680;
	/**set goes here, whatever that is**/
	public String Set = ""; // set goes here, whatever that is
	/**Lore of the item [addedLore]**/
	public String Lore = null; // Lore of the item [addedLore]
	/**Restrictions of the item (Untradable etc) [restrictions]**/
	public boolean Untradable = false; // Restrictions of the item (Untradable etc) [restrictions]
	public boolean QuestItem = false;
	/**Quest requirement (Cosmic set: "???") [quest]**/
	private String Quest = null; // Quest requirement (Cosmic set: "???") [quest]
	/**Where the item gets dropped from (0 = Never, 1 = Mobs, 2 = LootChest) [dropType]**/
	public byte DropType = 0; // Where the item gets dropped from (lootchest, never) [dropType]
	/**Weather the item needs to calculate IDs or just use raw values [identified]**/
	public boolean Identified = false; // Weather the item needs to calculate IDs or just use raw values [identified]
	/**Numder of Powder Slots and item has [sockets]**/
	public byte Slots = 0;
	
	// Damadge Stats
	/**Attack Speed of weapon (FAST, NORMAL, SUPER SLOW) [attackSpeed]**/
	public byte AttackSpeed = 0; // Attack Speed of weapon (FAST, NORMAL, SUPER SLOW) [attackSpeed]
	/**Damage [damage]**/
	private short[] Damage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Damage [damage]
	/**Earth Damage [earthDamage]**/
	private short[] EarthDamage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Earth Damage [earthDamage]
	/**Thunder Damage [thunderDamage]**/
	private short[] ThunderDamage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Thunder Damage [thunderDamage]
	/**Water Damage [waterDamage]**/
	private short[] WaterDamage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Water Damage [waterDamage]
	/**Fire Damage [fireDamage]**/
	private short[] FireDamage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Fire Damage [fireDamage]
	/**Air Damage [airDamage]**/
	private short[] AirDamage = {Short.MIN_VALUE, Short.MIN_VALUE}; // Air Damage [airDamage]
	
	// Defence Stats
	/**Health [health]**/
	public short Health = 0; // Health [health]
	/**Earth defence [earthDefense]**/
	public short EarthDefence = 0; // Earth defence [earthDefense]
	/**Thunder defence [thunderDefense]**/
	public short ThunderDefence = 0; // Thunder defence [thunderDefense]
	/**Water defence [waterDefense]**/
	public short WaterDefence = 0; // Water defence [waterDefense]
	/**Fire defence [fireDefense]**/
	public short FireDefence = 0; // Fire defence [fireDefense]
	/**Air defence [airDefense]**/
	public short AirDefence = 0; // Air defence [airDefense]
	
	// Skill Requirements
	/**Strength Requirement [strength]**/
	private byte Strength = 0; // Strength Requirement [strength]
	/**Dexterity Requirement [dexterity]**/
	private byte Dexterity = 0; // Dexterity Requirement [dexterity]
	/**Intelligence Requirement [intelligence]**/
	private byte Intelligence = 0; // Intelligence Requirement [intelligence]
	/**Defence Requirement [defense]**/
	private byte Defence = 0; // Defence Requirement [defense]
	/**Agility Requirement [agility]**/
	private byte Agility = 0; // Agility Requirement [agility]
	
	
	// BONUS STATS
	/**Bonus Attack Speed [attackSpeedBonus]**/
	private byte AttackSpeedBonus = 0; // Bonus Attack Speed [attackSpeedBonus]
	
	/**Percentage Damage Bonus [damageBonus]**/
	private short DamageBonus = 0; // Percentage Damage Bonus [damageBonus]
	/**Raw Damage Bonus [damageBonusRaw]**/
	private short DamageRawBonus = 0; // Raw Damage Bonus [damageBonusRaw]
	
	/**Percentage SpellDamage Bonus [spellDamage]**/
	private short SpellDamageBonus = 0; // Percentage SpellDamage Bonus [spellDamage]
	/**Raw SpellDamage Bonus [spellDamageRaw]**/
	private short SpellDamageRawBonus = 0; // Raw SpellDamage Bonus [spellDamageRaw]
	
	/**Health [healthBonus]**/
	private short HealthBonus = 0; // Health [healthBonus]
	/**Percentage Health Regen [healthRegen]**/
	private short HealthRegenBonus = 0; // Percentage Health Regen [healthRegen]
	/**Raw Health Regen [healthRegenRaw]**/
	private short HealthRegenRawBonus = 0; // Raw Health Regen [healthRegenRaw]
	
	/**Added Life Steal [lifeSteal]**/
	private short LifeStealBonus = 0; // Added Life Steal [lifeSteal]
	/**Added Life Steal [manaRegen]**/
	private byte ManaRegenBonus = 0; // Added Life Steal [manaRegen]
	/**Added Life Steal [manaSteal]**/
	private byte ManaStealBonus = 0; // Added Life Steal [manaSteal]
	
	/**Earth Damage Bonus [bonusEarthDamage]**/
	private short EarthDamageBonus = 0; // Earth Damage Bonus [bonusEarthDamage]
	/**Thunder Damage Bonus [bonusThunderDamage]**/
	private short ThunderDamageBonus = 0; // Thunder Damage Bonus [bonusThunderDamage]
	/**Water Damage Bonus [bonusWaterDamage]**/
	private short WaterDamageBonus = 0; // Water Damage Bonus [bonusWaterDamage]
	/**Fire Damage Bonus [bonusFireDamage]**/
	private short FireDamageBonus = 0; // Fire Damage Bonus [bonusFireDamage]
	/**Air Damage Bonus [bonusAirDamage]**/
	private short AirDamageBonus = 0; // Air Damage Bonus [bonusAirDamage]
	
	/**Earth defence [bonusEarthDefense]**/
	private short EarthDefenceBonus = 0; // Earth defence [bonusEarthDefense]
	/**Thunder defence [bonusThunderDefense]**/
	private short ThunderDefenceBonus = 0; // Thunder defence [bonusThunderDefense]
	/**Water defence [bonusWaterDefense]**/
	private short WaterDefenceBonus = 0; // Water defence [bonusWaterDefense]
	/**Fire defence [bonusFireDefense]**/
	private short FireDefenceBonus = 0; // Fire defence [bonusFireDefense]
	/**Air defence [bonusAirDefense]**/
	private short AirDefenceBonus = 0; // Air defence [bonusAirDefense]
	
	/**Strength Requirement [strengthPoints]**/
	private byte StrengthBonus = 0; // Strength Requirement [strengthPoints]
	/**Dexterity Requirement [dexterityPoints]**/
	private byte DexterityBonus = 0; // Dexterity Requirement [dexterityPoints]
	/**Intelligence Requirement [intelligencePoints]**/
	private byte IntelligenceBonus = 0; // Intelligence Requirement [intelligencePoints]
	/**Defence Requirement [defensePoints]**/
	private byte DefenceBonus = 0; // Defence Requirement [defensePoints]
	/**Agility Requirement [agilityPoints]**/
	private byte AgilityBonus = 0; // Agility Requirement [agilityPoints]
	
	/**Movment Speed Bonus [speed]**/
	private short SpeedBonus = 0; // Movment Speed Bonus [speed]
	/**Exploding Chance [exploding]**/
	private short ExplodingBonus = 0; // Exploding Chance [exploding]
	/**Poison damage [poison]**/
	private short PoisonBonus = 0; // Poison damage [poison]
	/**Thorns [thorns]**/
	private short ThornsBonus = 0; // Thorns [thorns]
	/**Reflection amount [reflection]**/
	private short ReflectionBonus = 0; // Reflection amount [reflection]
	/**Soulpoint Regen Chance [soulPoints]**/
	private short SoulPointBonus = 0; // Soulpoint Regen Chance [soulPoints]
	/**Stealing Chance [emeraldStealing]**/
	private short StealingBonus = 0; // Stealing Chance [emeraldStealing]
	/**Loot Bonus [lootBonus]**/
	private short LootBonus = 0; // Loot Bonus [lootBonus]
	/**Experience Bonus [xpBonus]**/
	private short ExpBonus = 0; // Experience Bonus [xpBonus]
	
	
	public String GetColouredName() {
		String Name = this.Name;
		switch (this.GetRarity()) {
		case 0: Name = String.valueOf('\u00a7') + 'f' + Name; break;
		case 1: Name = String.valueOf('\u00a7') + 'e' + Name; break;
		case 2: Name = String.valueOf('\u00a7') + 'd' + Name; break;
		case 3: Name = String.valueOf('\u00a7') + 'b' + Name; break;
		case 4: Name = String.valueOf('\u00a7') + '5' + Name; break;
		case 5: Name = String.valueOf('\u00a7') + 'a' + Name; break;
		default: Name = String.valueOf('\u00a7') + '8' + Name; break;
		}
		return Name;
	}
	
	public void SetCagegoryTypeRarity(String Type, String Rarity) {
		this.CategoryTypeRarity = 0;
		
		Type = Type.toLowerCase();
		if (Type.equals("bow") || Type.equals("0")) {
			this.CategoryTypeRarity = 0;
			this.Class = 1;
			this.Material = "261";
		}else if (Type.equals("spear") || Type.equals("1")) {
			this.CategoryTypeRarity = 1;
			this.Class = 2;
			this.Material = "256";
		}else if (Type.equals("wand") || Type.equals("2")) {
			this.CategoryTypeRarity = 2;
			this.Class = 4;
			this.Material = "280";
		}else if (Type.equals("dagger") || Type.equals("3")) {
			this.CategoryTypeRarity = 3;
			this.Class = 8;
			this.Material = "359";
		}else if (Type.equals("helmet") || Type.equals("4")) {
			this.CategoryTypeRarity = 4;
			this.Material = "298";
		}else if (Type.equals("chestplate") || Type.equals("5")) {
			this.CategoryTypeRarity = 5;
			this.Material = "299";
		}else if (Type.equals("leggings") || Type.equals("6")) {
			this.CategoryTypeRarity = 6;
			this.Material = "300";
		}else if (Type.equals("boots") || Type.equals("7")) {
			this.CategoryTypeRarity = 7;
			this.Material = "301";
		}else if (Type.equals("ring") || Type.equals("8")) {
			this.CategoryTypeRarity = 8;
			this.Material = "95";
		}else if (Type.equals("bracelet") || Type.equals("9")) {
			this.CategoryTypeRarity = 9;
			this.Material = "188";
		}else if (Type.equals("necklace") || Type.equals("10")) {
			this.CategoryTypeRarity = 10;
			this.Material = "102";
		}else{
			this.CategoryTypeRarity = 11;
		}
		
		Rarity = Rarity.toLowerCase();
		if (Rarity.equals("normal") || Rarity.equals("0")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 0);
		}else if (Rarity.equals("unique") || Rarity.equals("1")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 12);
		}else if (Rarity.equals("rare") || Rarity.equals("2")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 24);
		}else if (Rarity.equals("legendary") || Rarity.equals("3")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 36);
		}else if (Rarity.equals("mythic") || Rarity.equals("4")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 48);
		}else if (Rarity.equals("set") || Rarity.equals("5")) {
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 60);
		}else{
			this.CategoryTypeRarity = (byte) (this.CategoryTypeRarity + 72);
		}
	}
	
	public int GetCategory() {
		return (int) Math.floor(((int) (this.CategoryTypeRarity) % 12)/4);
	}
	
	public int GetType() {
		return ((int) (this.CategoryTypeRarity) % 12);
	}
	
	public String GetTypeName() {
		switch ((int) (this.CategoryTypeRarity) % 12) {
		case 0: return "Bow";
		case 1: return "Spear";
		case 2: return "Wand";
		case 3: return "Dagger";
		case 4: return "Helmet";
		case 5: return "Chestplate";
		case 6: return "Leggings";
		case 7: return "Boots";
		case 8: return "Ring";
		case 9: return "Bracelet";
		case 10: return "Necklace";
		}
		return "Unknown";
	}
	
	public int GetRarity() {
		return (int) Math.floor(this.CategoryTypeRarity / 12);
	}
	
	public String GetRarityName() {
		String Rarity = "";
		switch (this.GetRarity()) {
		case 0: Rarity = String.valueOf('\u00a7') + "fNormal"; break;
		case 1: Rarity = String.valueOf('\u00a7') + "eUnique"; break;
		case 2: Rarity = String.valueOf('\u00a7') + "dRare"; break;
		case 3: Rarity = String.valueOf('\u00a7') + "bLegendary"; break;
		case 4: Rarity = String.valueOf('\u00a7') + "5Mythic"; break;
		case 5: Rarity = String.valueOf('\u00a7') + "aSet"; break;
		default: Rarity = String.valueOf('\u00a7') + "7Unknown"; break;
		}
		return Rarity;
	}
	
	public void SetLevel(int Level) {
		this.Level = (byte) (Level - 100);
	}
	
	public int GetLevel() {
		return (this.Level + 100);
	}
	
	public void SetClass(String Class) {
		Class = Class.toLowerCase();
		this.Class = 0;
		if (Class.contains("archer")) {
			this.Class =+ 1;
		}
		if (Class.contains("warrior")) {
			this.Class =+ 2;
		}
		if (Class.contains("mage")) {
			this.Class =+ 4;
		}
		if (Class.contains("assassin")) {
			this.Class =+ 8;
		}
	}
	
	public int GetClass() {
		return this.Class;
	}
	
	public boolean CanClassUse(boolean[] Class) {
		if (Class.length != 4) {
			return false;
		}
		int ClassTmp = (int) (this.Class);
		if (ClassTmp >= 8) {if (Class[3]) {return true;} ClassTmp = ClassTmp - 8;}
		if (ClassTmp >= 4) {if (Class[2]) {return true;} ClassTmp = ClassTmp - 4;}
		if (ClassTmp >= 2) {if (Class[1]) {return true;} ClassTmp = ClassTmp - 2;}
		if (ClassTmp >= 1) {if (Class[0]) {return true;}}
		if (this.Class == 0) {return true;}
		return false;
	}
	
	public String GetClassName() {
		int ClassTmp = (int) (this.Class);
		String Class = "";
		if (ClassTmp >= 8) {Class = "Assassin/Ninja " + Class; ClassTmp = ClassTmp - 8;}
		if (ClassTmp >= 4) {Class = "Mage/Dark Wizard " + Class; ClassTmp = ClassTmp - 4;}
		if (ClassTmp >= 2) {Class = "Warrior/Knight " + Class; ClassTmp = ClassTmp - 2;}
		if (ClassTmp >= 1) {Class = "Archer/Hunter " + Class;}
		return Class;
	}
	
	public ItemStack GetMaterial() {
		new Item();
		ItemStack MaterialItem = new ItemStack(Item.getByNameOrId(this.Material));
		if (MaterialItem.getItem() == null) {
			if (this.Material.contains(":")) {
				//System.out.println(this.Name + ": " + this.Material + " 1");
				String Meta = this.Material.substring(this.Material.lastIndexOf(":") +1);
				this.Material = this.Material.substring(0, this.Material.lastIndexOf(":"));
				MaterialItem = new ItemStack(Item.getByNameOrId(this.Material));
				MaterialItem.setItemDamage((Meta.matches("[0-9+]") ? Integer.parseInt(Meta) : 0));
			}else{
				//System.out.println(this.Name + ": " + this.Material + " 2");
				MaterialItem = new ItemStack(Item.getItemById(Integer.parseInt(this.Material)));
			}
		} else if (MaterialItem.isEmpty()) {
			if (this.Material.contains(":")) {
				//System.out.println(this.Name + ": " + this.Material + " 3");
				String Meta = this.Material.substring(this.Material.lastIndexOf(":") +1);
				this.Material = this.Material.substring(0, this.Material.lastIndexOf(":"));
				MaterialItem = new ItemStack(Block.getBlockById(Integer.parseInt(this.Material)));
				MaterialItem.setItemDamage((Meta.matches("[0-9+]") ? Integer.parseInt(Meta) : 0));
			}else{
				//System.out.println(this.Name + ": " + this.Material + " 4");
				MaterialItem = new ItemStack(Block.getBlockById(Integer.parseInt(this.Material)));
			}
		}
		if (MaterialItem.getItem() != null && this.ArmourColour != 10511680) {
			//System.out.println(this.Name + ": " + this.Material + " [" + this.ArmourColour + "]");
			NBTTagCompound Nbt = MaterialItem.getOrCreateSubCompound("display");
			Nbt.setInteger("color", this.ArmourColour);
			MaterialItem.writeToNBT(Nbt);
		}
		return MaterialItem;
	}
	
	private ItemStack Stack = null;
	public ItemStack GetStaticMaterial(){
		if (Stack == null) Stack = this.GetMaterial();
		return Stack;
	}
	
	public void SetArmourMaterial(String Armour) {
		if (Armour.toLowerCase().equals("diamond")) {
			switch (this.GetType()) {
				case 4: this.Material = "minecraft:diamond_helmet"; break;
				case 5: this.Material = "minecraft:diamond_chestplate"; break;
				case 6: this.Material = "minecraft:diamond_leggings"; break;
				case 7: this.Material = "minecraft:diamond_boots"; break;
				default: this.Material = "minecraft:diamond"; break;
			}
		}else if (Armour.toLowerCase().equals("iron")) {
			switch (this.GetType()) {
				case 4: this.Material = "minecraft:iron_helmet"; break;
				case 5: this.Material = "minecraft:iron_chestplate"; break;
				case 6: this.Material = "minecraft:iron_leggings"; break;
				case 7: this.Material = "minecraft:iron_boots"; break;
				default: this.Material = "minecraft:iron_ingot"; break;
			}
		}else if (Armour.toLowerCase().equals("golden")) {
			switch (this.GetType()) {
				case 4: this.Material = "minecraft:golden_helmet"; break;
				case 5: this.Material = "minecraft:golden_chestplate"; break;
				case 6: this.Material = "minecraft:golden_leggings"; break;
				case 7: this.Material = "minecraft:golden_boots"; break;
				default: this.Material = "minecraft:gold_ingot"; break;
			}
		}else if (Armour.toLowerCase().equals("chain")) {
			switch (this.GetType()) {
				case 4: this.Material = "minecraft:chainmail_helmet"; break;
				case 5: this.Material = "minecraft:chainmail_chestplate"; break;
				case 6: this.Material = "minecraft:chainmail_leggings"; break;
				case 7: this.Material = "minecraft:chainmail_boots"; break;
				default: this.Material = "minecraft:minecart"; break;
			}
		}else if (Armour.toLowerCase().equals("leather")) {
			switch (this.GetType()) {
				case 4: this.Material = "minecraft:leather_helmet"; break;
				case 5: this.Material = "minecraft:leather_chestplate"; break;
				case 6: this.Material = "minecraft:leather_leggings"; break;
				case 7: this.Material = "minecraft:leather_boots"; break;
				default: this.Material = "minecraft:leather"; break;
			}
		}else{
			this.Material = "minecraft:sponge";
		}
	}
	
	public void SetArmourColour(String Colour){
		int RGB = 0;
		String Num = "";
		for (char c : Colour.toCharArray()){
			if (c >= '0' && c <= '9'){
				Num += c;
			}else if (Num != ""){
				RGB = (RGB << 8) + Integer.parseInt(Num);
				Num = "";
			}
		}
		if (Num != ""){
			RGB = (RGB << 8) + Integer.parseInt(Num);
		}
		this.ArmourColour = RGB;
	}
	
	public void SetRestrictions(String Restrictions) {
		Restrictions = Restrictions.toLowerCase();
		if (Restrictions.contains("untradable")) {
			this.Untradable = true;
		}else{
			this.Untradable = false;
		}
		if (Restrictions.contains("quest item")) {
			this.QuestItem = true;
		}else{
			this.QuestItem = false;
		}
	}
	
	public String GetRestrictions(){
		String Restrictions = "";
		if (this.Untradable) {
			Restrictions = Restrictions + "Untradable";
		}
		return Restrictions;
	}
	
	public void SetQuest(String Quest){
		if (QuestBook.QuestCorrections.subList(0, (QuestBook.QuestCorrections.size()/2)).contains(Quest)){
			for (int i=0;i<(QuestBook.QuestCorrections.size()/2);i++){
				if (QuestBook.QuestCorrections.get(i).equals(Quest)){
					Quest = QuestBook.QuestCorrections.get(i + (QuestBook.QuestCorrections.size()/2));
					break;
				}
			}
		}
		this.Quest = Quest;
	}
	
	public String GetQuest() {
		return this.Quest;
	}
	
	public void SetDropType(String DropType) {
		DropType = DropType.toLowerCase();
		this.DropType = 1;
		if (DropType.equals("lootchest")) {
			this.DropType = 2;
		}else if (DropType.equals("never")) {
			this.DropType = 0;
		}
	}
	
	public void SetAttackSpeed(String AtkSpd) {
		AtkSpd = AtkSpd.toLowerCase();
		if (AtkSpd.equals("super_fast")) {
			this.AttackSpeed = 1;
		}else if (AtkSpd.equals("very_fast")) {
			this.AttackSpeed = 2;
		}else if (AtkSpd.equals("fast")) {
			this.AttackSpeed = 3;
		}else if (AtkSpd.equals("normal")) {
			this.AttackSpeed = 4;
		}else if (AtkSpd.equals("slow")) {
			this.AttackSpeed = 5;
		}else if (AtkSpd.equals("very_slow")) {
			this.AttackSpeed = 6;
		}else if (AtkSpd.equals("super_slow")) {
			this.AttackSpeed = 7;
		}else{
			this.AttackSpeed = 8;
		}
	}
	
	public String GetAttackSpeedName() {
		switch (this.AttackSpeed) {
		case 1: return "Super Fast";
		case 2: return "Very Fast";
		case 3: return "Fast";
		case 4: return "Normal";
		case 5: return "Slow";
		case 6: return "Very Slow";
		case 7: return "Super Slow";
		case 8: return "Unknown";
		default: return "Not Set";
		}
	}
	
	public void SetDamage(String Dmg) {
		this.Damage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.Damage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetDamage() {
		int[] Damage = {(this.Damage[0] - Short.MIN_VALUE), (this.Damage[1] - Short.MIN_VALUE)};
		return Damage;
	}
	
	public void SetEarthDamage(String Dmg) {
		this.EarthDamage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.EarthDamage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetEarthDamage() {
		int[] EarthDamage = {(this.EarthDamage[0] - Short.MIN_VALUE), (this.EarthDamage[1] - Short.MIN_VALUE)};
		return EarthDamage;
	}
	
	public void SetThunderDamage(String Dmg) {
		this.ThunderDamage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.ThunderDamage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetThunderDamage() {
		int[] ThunderDamage = {(this.ThunderDamage[0] - Short.MIN_VALUE), (this.ThunderDamage[1] - Short.MIN_VALUE)};
		return ThunderDamage;
	}
	
	public void SetWaterDamage(String Dmg) {
		this.WaterDamage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.WaterDamage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetWaterDamage() {
		int[] Damage = {(this.WaterDamage[0] - Short.MIN_VALUE), (this.WaterDamage[1] - Short.MIN_VALUE)};
		return Damage;
	}
	
	public void SetFireDamage(String Dmg) {
		this.FireDamage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.FireDamage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetFireDamage() {
		int[] Damage = {(this.FireDamage[0] - Short.MIN_VALUE), (this.FireDamage[1] - Short.MIN_VALUE)};
		return Damage;
	}
	
	public void SetAirDamage(String Dmg) {
		this.AirDamage[0] = (short) (Short.parseShort(Dmg.substring(0, Dmg.indexOf("-", 1))) + Short.MIN_VALUE);
		this.AirDamage[1] = (short) (Short.parseShort(Dmg.substring(Dmg.indexOf("-", 1) +1)) + Short.MIN_VALUE);
	}
	public int[] GetAirDamage() {
		int[] Damage = {(this.AirDamage[0] - Short.MIN_VALUE), (this.AirDamage[1] - Short.MIN_VALUE)};
		return Damage;
	}
	
	
	public void SetStrengthRequirement(int Strength) {this.Strength = (byte) (Strength + Byte.MIN_VALUE);}
	public int GetStrengthRequirement() {return (this.Strength - Byte.MIN_VALUE);}
	
	public void SetDexterityRequirement(int Dexterity) {this.Dexterity = (byte) (Dexterity + Byte.MIN_VALUE);}
	public int GetDexterityRequirement() {return (this.Dexterity - Byte.MIN_VALUE);}
	
	public void SetIntelligenceRequirement(int Intelligence) {this.Intelligence = (byte) (Intelligence + Byte.MIN_VALUE);}
	public int GetIntelligenceRequirement() {return (this.Intelligence - Byte.MIN_VALUE);}
	
	public void SetDefenceRequirement(int Defence) {this.Defence = (byte) (Defence + Byte.MIN_VALUE);}
	public int GetDefenceRequirement() {return (this.Defence - Byte.MIN_VALUE);}
	
	public void SetAgilityRequirement(int Agility) {this.Agility = (byte) (Agility + Byte.MIN_VALUE);}
	public int GetAgilityRequirement() {return (this.Agility - Byte.MIN_VALUE);}
	
	
	private int[] CalculateIdRange(int ID) {
		if (this.Identified) {
			int[] Output = {ID, ID}; 
			return Output;
		}else{
			if (ID > 0) {
				int[] Output = {Math.max(Math.round(ID * 0.3f), 1), Math.max(Math.round(ID * 1.3f), 1)};
				return Output;
			}else{
				int[] Output = {Math.min(Math.round(ID * 0.7f), -1), Math.min(Math.round(ID * 1.3f), -1)};
				return Output;
			}
		}
	}
	
	public void SetAttackSpeedBonus(int ID) { this.AttackSpeedBonus = (byte) ID;}
	public int[] GetAttackSpeedBonus() {return CalculateIdRange(this.AttackSpeedBonus);}
	public int GetRawAttackSpeedBonus() {return this.AttackSpeedBonus;}
	
	public void SetDamageBonus(int ID) { this.DamageBonus = (short) ID;}
	public int[] GetDamageBonus() {return CalculateIdRange(this.DamageBonus);}
	public int GetRawDamageBonus() {return this.DamageBonus;}
	
	public void SetDamageRawBonus(int ID) { this.DamageRawBonus = (short) ID;}
	public int[] GetDamageRawBonus() {return CalculateIdRange(this.DamageRawBonus);}
	public int GetRawDamageRawBonus() {return this.DamageRawBonus;}
	
	public void SetSpellDamageBonus(int ID) { this.SpellDamageBonus = (short) ID;}
	public int[] GetSpellDamageBonus() {return CalculateIdRange(this.SpellDamageBonus);}
	public int GetRawSpellDamageBonus() {return this.SpellDamageBonus;}
	
	public void SetSpellDamageRawBonus(int ID) { this.SpellDamageRawBonus = (short) ID;}
	public int[] GetSpellDamageRawBonus() {return CalculateIdRange(this.SpellDamageRawBonus);}
	public int GetRawSpellDamageRawBonus() {return this.SpellDamageRawBonus;}
	
	public void SetHealthBonus(int ID) { this.HealthBonus = (short) ID;}
	public int[] GetHealthBonus() {return CalculateIdRange(this.HealthBonus);}
	public int GetRawHealthBonus() {return this.HealthBonus;}
	
	public void SetHealthRegenBonus(int ID) { this.HealthRegenBonus = (short) ID;}
	public int[] GetHealthRegenBonus() {return CalculateIdRange(this.HealthRegenBonus);}
	public int GetRawHealthRegenBonus() {return this.HealthRegenBonus;}
	
	public void SetHealthRegenRawBonus(int ID) { this.HealthRegenRawBonus = (short) ID;}
	public int[] GetHealthRegenRawBonus() {return CalculateIdRange(this.HealthRegenRawBonus);}
	public int GetRawHealthRegenRawBonus() {return this.HealthRegenRawBonus;}
	
	public void SetLifeStealBonus(int ID) { this.LifeStealBonus = (short) ID;}
	public int[] GetLifeStealBonus() {return CalculateIdRange(this.LifeStealBonus);}
	public int GetRawLifeStealBonus() {return this.LifeStealBonus;}
	
	public void SetManaRegenBonus(int ID) { this.ManaRegenBonus = (byte) ID;}
	public int[] GetManaRegenBonus() {return CalculateIdRange(this.ManaRegenBonus);}
	public int GetRawManaRegenBonus() {return this.ManaRegenBonus;}
	
	public void SetManaStealBonus(int ID) { this.ManaStealBonus = (byte) ID;}
	public int[] GetManaStealBonus() {return CalculateIdRange(this.ManaStealBonus);}
	public int GetRawManaStealBonus() {return this.ManaStealBonus;}
	
	public void SetEarthDamageBonus(int ID) { this.EarthDamageBonus = (short) ID;}
	public int[] GetEarthDamageBonus() {return CalculateIdRange(this.EarthDamageBonus);}
	public int GetRawEarthDamageBonus() {return this.EarthDamageBonus;}
	
	public void SetThunderDamageBonus(int ID) { this.ThunderDamageBonus = (short) ID;}
	public int[] GetThunderDamageBonus() {return CalculateIdRange(this.ThunderDamageBonus);}
	public int GetRawThunderDamageBonus() {return this.ThunderDamageBonus;}
	
	public void SetWaterDamageBonus(int ID) { this.WaterDamageBonus = (short) ID;}
	public int[] GetWaterDamageBonus() {return CalculateIdRange(this.WaterDamageBonus);}
	public int GetRawWaterDamageBonus() {return this.WaterDamageBonus;}
	
	public void SetFireDamageBonus(int ID) { this.FireDamageBonus = (short) ID;}
	public int[] GetFireDamageBonus() {return CalculateIdRange(this.FireDamageBonus);}
	public int GetRawFireDamageBonus() {return this.FireDamageBonus;}
	
	public void SetAirDamageBonus(int ID) { this.AirDamageBonus = (short) ID;}
	public int[] GetAirDamageBonus() {return CalculateIdRange(this.AirDamageBonus);}
	public int GetRawAirDamageBonus() {return this.AirDamageBonus;}
	
	public void SetEarthDefenceBonus(int ID) { this.EarthDefenceBonus = (short) ID;}
	public int[] GetEarthDefenceBonus() {return CalculateIdRange(this.EarthDefenceBonus);}
	public int GetRawEarthDefenceBonus() {return this.EarthDefenceBonus;}
	
	public void SetThunderDefenceBonus(int ID) { this.ThunderDefenceBonus = (short) ID;}
	public int[] GetThunderDefenceBonus() {return CalculateIdRange(this.ThunderDefenceBonus);}
	public int GetRawThunderDefenceBonus() {return this.ThunderDefenceBonus;}
	
	public void SetWaterDefenceBonus(int ID) { this.WaterDefenceBonus = (short) ID;}
	public int[] GetWaterDefenceBonus() {return CalculateIdRange(this.WaterDefenceBonus);}
	public int GetRawWaterDefenceBonus() {return this.WaterDefenceBonus;}
	
	public void SetFireDefenceBonus(int ID) { this.FireDefenceBonus = (short) ID;}
	public int[] GetFireDefenceBonus() {return CalculateIdRange(this.FireDefenceBonus);}
	public int GetRawFireDefenceBonus() {return this.FireDefenceBonus;}
	
	public void SetAirDefenceBonus(int ID) { this.AirDefenceBonus = (short) ID;}
	public int[] GetAirDefenceBonus() {return CalculateIdRange(this.AirDefenceBonus);}
	public int GetRawAirDefenceBonus() {return this.AirDefenceBonus;}
	
	public void SetStrengthBonus(int ID) { this.StrengthBonus = (byte) ID;}
	public int[] GetStrengthBonus() {return CalculateIdRange(this.StrengthBonus);}
	public int GetRawStrengthBonus() {return this.StrengthBonus;}
	
	public void SetDexterityBonus(int ID) { this.DexterityBonus = (byte) ID;}
	public int[] GetDexterityBonus() {return CalculateIdRange(this.DexterityBonus);}
	public int GetRawDexterityBonus() {return this.DexterityBonus;}
	
	public void SetIntelligenceBonus(int ID) { this.IntelligenceBonus = (byte) ID;}
	public int[] GetIntelligenceBonus() {return CalculateIdRange(this.IntelligenceBonus);}
	public int GetRawIntelligenceBonus() {return this.IntelligenceBonus;}
	
	public void SetDefenceBonus(int ID) { this.DefenceBonus = (byte) ID;}
	public int[] GetDefenceBonus() {return CalculateIdRange(this.DefenceBonus);}
	public int GetRawDefenceBonus() {return this.DefenceBonus;}
	
	public void SetAgilityBonus(int ID) { this.AgilityBonus = (byte) ID;}
	public int[] GetAgilityBonus() {return CalculateIdRange(this.AgilityBonus);}
	public int GetRawAgilityBonus() {return this.AgilityBonus;}
	
	public void SetSpeedBonus(int ID) { this.SpeedBonus = (short) ID;}
	public int[] GetSpeedBonus() {return CalculateIdRange(this.SpeedBonus);}
	public int GetRawSpeedBonus() {return this.SpeedBonus;}
	
	public void SetExplodingBonus(int ID) { this.ExplodingBonus = (short) ID;}
	public int[] GetExplodingBonus() {return CalculateIdRange(this.ExplodingBonus);}
	public int GetRawExplodingBonus() {return this.ExplodingBonus;}
	
	public void SetPoisonBonus(int ID) { this.PoisonBonus = (short) ID;}
	public int[] GetPoisonBonus() {return CalculateIdRange(this.PoisonBonus);}
	public int GetRawPoisonBonus() {return this.PoisonBonus;}
	
	public void SetThornsBonus(int ID) { this.ThornsBonus = (short) ID;}
	public int[] GetThornsBonus() {return CalculateIdRange(this.ThornsBonus);}
	public int GetRawThornsBonus() {return this.ThornsBonus;}
	
	public void SetReflectionBonus(int ID) { this.ReflectionBonus = (short) ID;}
	public int[] GetReflectionBonus() {return CalculateIdRange(this.ReflectionBonus);}
	public int GetRawReflectionBonus() {return this.ReflectionBonus;}
	
	public void SetSoulPointBonus(int ID) { this.SoulPointBonus = (short) ID;}
	public int[] GetSoulPointBonus() {return CalculateIdRange(this.SoulPointBonus);}
	public int GetRawSoulPointBonus() {return this.SoulPointBonus;}
	
	public void SetStealingBonus(int ID) { this.StealingBonus = (short) ID;}
	public int[] GetStealingBonus() {return CalculateIdRange(this.StealingBonus);}
	public int GetRawStealingBonus() {return this.StealingBonus;}
	
	public void SetLootBonus(int ID) { this.LootBonus = (short) ID;}
	public int[] GetLootBonus() {return CalculateIdRange(this.LootBonus);}
	public int GetRawLootBonus() {return this.LootBonus;}
	
	public void SetExpBonus(int ID) { this.ExpBonus = (short) ID;}
	public int[] GetExpBonus() {return CalculateIdRange(this.ExpBonus);}
	public int GetRawExpBonus() {return this.ExpBonus;}
	
	public void ParseLore(String Lore) {
		Lore = Lore.replace("'", String.valueOf("\u0027"));
		Lore = Lore.replace("\\\\", "");
		//Lore = Lore.replace("�", String.valueOf('\u201C'));
		//Lore = Lore.replace("�", String.valueOf('\u201D'));
		if (this.Name.equals("Aquarius")) Lore = String.valueOf('\u00a7') + "o\"The symbol of the river\"" + String.valueOf('\u00a7') + "r" + String.valueOf('\u00a7') + "8" + Lore.substring(Lore.indexOf(" a name"));
		
		boolean Italic = false;
		while (Lore.contains("\\")) {
			if (Italic) {
				Lore = Lore.replaceFirst("\\\\", "\"" + String.valueOf('\u00a7') + "r" + String.valueOf('\u00a7') + "8");
				Italic = false;
			}else{
				Lore = Lore.replaceFirst("\\\\", String.valueOf('\u00a7') + "o\"");
				Italic = true;
			}
		}
		this.Lore = Lore;
	}
	
	
	public String GetTooltip(boolean ShowLore, boolean ShowSales) {
		String Tooltip = this.GetColouredName() + " &7" + this.GetTypeName() + "/n";
		if (this.AttackSpeed > 0) Tooltip = Tooltip + "&7" + this.GetAttackSpeedName() + " Attack Speed" + "/n";
		
		Tooltip = Tooltip + "/n";
		
		if (this.GetDamage()[1] > 0) Tooltip = Tooltip + "&6" + String.valueOf('\u2723') + " Neutral Damage: " + this.GetDamage()[0] + "-" + this.GetDamage()[1] + "/n";
		if (this.GetFireDamage()[1] > 0) Tooltip = Tooltip + "&c" + String.valueOf('\u2739') + " Fire &7Damage: " + this.GetFireDamage()[0] + "-" + this.GetFireDamage()[1] + "/n";
		if (this.GetWaterDamage()[1] > 0) Tooltip = Tooltip + "&b" + String.valueOf('\u2749') + " Water &7Damage: " + this.GetWaterDamage()[0] + "-" + this.GetWaterDamage()[1] + "/n";
		if (this.GetAirDamage()[1] > 0) Tooltip = Tooltip + "&f" + String.valueOf('\u274B') + " Air &7Damage: " + this.GetAirDamage()[0] + "-" + this.GetAirDamage()[1] + "/n";
		if (this.GetThunderDamage()[1] > 0) Tooltip = Tooltip + "&e" + String.valueOf('\u2726') + " Thunder &7Damage: " + this.GetThunderDamage()[0] + "-" + this.GetThunderDamage()[1] + "/n";
		if (this.GetEarthDamage()[1] > 0) Tooltip = Tooltip + "&2" + String.valueOf('\u2724') + " Earth &7Damage: " + this.GetEarthDamage()[0] + "-" + this.GetEarthDamage()[1] + "/n";
		
		if (this.Health != 0) Tooltip = Tooltip + "&4" + String.valueOf('\u2764') + " Health: " + this.Health + "/n";
		if (this.FireDefence != 0) Tooltip = Tooltip + "&c" + String.valueOf('\u2739') + " Fire &7Defence: " + this.FireDefence + "/n";
		if (this.WaterDefence != 0) Tooltip = Tooltip + "&b" + String.valueOf('\u2749') + " Water &7Defence: " + this.WaterDefence + "/n";
		if (this.AirDefence != 0) Tooltip = Tooltip + "&f" + String.valueOf('\u274B') + " Air &7Defence: " + this.AirDefence + "/n";
		if (this.ThunderDefence != 0) Tooltip = Tooltip + "&e" + String.valueOf('\u2726') + " Thunder &7Defence: " + this.ThunderDefence + "/n";
		if (this.EarthDefence != 0) Tooltip = Tooltip + "&2" + String.valueOf('\u2724') + " Earth &7Defence: " + this.EarthDefence + "/n";
		
		if (!Tooltip.endsWith("/n/n")) Tooltip = Tooltip + "/n";
		
		if (this.Class != 0)Tooltip = Tooltip + "&7- Class Req: " + this.GetClassName() + "/n";
		if (this.Quest != null) Tooltip = Tooltip + "&7- Quest Req: " + this.GetQuest() + "/n";
		Tooltip = Tooltip + "  &7- Lv. Min: " + this.GetLevel() + "/n";
		if (this.GetStrengthRequirement() > 0) Tooltip = Tooltip + "&7- Strength Min: " + this.GetStrengthRequirement() + "/n";
		if (this.GetDexterityRequirement() > 0) Tooltip = Tooltip + "&7- Dexterity Min: " + this.GetDexterityRequirement() + "/n";
		if (this.GetIntelligenceRequirement() > 0) Tooltip = Tooltip + "&7- Intelligence Min: " + this.GetIntelligenceRequirement() + "/n";
		if (this.GetDefenceRequirement() > 0) Tooltip = Tooltip + "&7- Defence Min: " + this.GetDefenceRequirement() + "/n";
		if (this.GetAgilityRequirement() > 0) Tooltip = Tooltip + "&7- Agility Min: " + this.GetAgilityRequirement() + "/n";
		
		if (!Tooltip.endsWith("/n/n")) Tooltip = Tooltip + "/n";
		
		//if (this.GetRawBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawBonus(), this.GetBonus(), "", "");
		
		if (this.GetRawAttackSpeedBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawAttackSpeedBonus(), this.GetAttackSpeedBonus(), "AttackSpeed", "");
		
		if (this.GetRawDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawDamageBonus(), this.GetDamageBonus(), "Melee Damage", "%");
		if (this.GetRawDamageRawBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawDamageRawBonus(), this.GetDamageRawBonus(), "Melee Damage", "");
		
		if (this.GetRawSpellDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawSpellDamageBonus(), this.GetSpellDamageBonus(), "Spell Damage", "%");
		if (this.GetRawSpellDamageRawBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawSpellDamageRawBonus(), this.GetSpellDamageRawBonus(), "Spell Damage", "");
		
		if (this.GetRawHealthBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawHealthBonus(), this.GetHealthBonus(), "Health", "");
		if (this.GetRawHealthRegenBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawHealthRegenBonus(), this.GetHealthRegenBonus(), "Health Regen", "%");
		if (this.GetRawHealthRegenRawBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawHealthRegenRawBonus(), this.GetHealthRegenRawBonus(), "Health Regen", "");
		
		if (this.GetRawLifeStealBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawLifeStealBonus(), this.GetLifeStealBonus(), "Life Steal", "/4s");
		if (this.GetRawManaRegenBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawManaRegenBonus(), this.GetManaRegenBonus(), "Mana Regen", "/4s");
		if (this.GetRawManaStealBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawManaStealBonus(), this.GetManaStealBonus(), "Mana Steal", "/4s");
		
		if (this.GetRawFireDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawFireDamageBonus(), this.GetFireDamageBonus(), "Fire Damage", "%");
		if (this.GetRawWaterDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawWaterDamageBonus(), this.GetWaterDamageBonus(), "Water Damage", "%");
		if (this.GetRawAirDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawAirDamageBonus(), this.GetAirDamageBonus(), "Air Damage", "%");
		if (this.GetRawThunderDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawThunderDamageBonus(), this.GetThunderDamageBonus(), "Thunder Damage", "%");
		if (this.GetRawEarthDamageBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawEarthDamageBonus(), this.GetEarthDamageBonus(), "Earth Damage", "%");
		
		if (this.GetRawFireDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawFireDefenceBonus(), this.GetFireDefenceBonus(), "Fire Defence", "");
		if (this.GetRawWaterDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawWaterDefenceBonus(), this.GetWaterDefenceBonus(), "Water Defence", "");
		if (this.GetRawAirDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawAirDefenceBonus(), this.GetAirDefenceBonus(), "Air Defence", "");
		if (this.GetRawThunderDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawThunderDefenceBonus(), this.GetThunderDefenceBonus(), "Thunder Defence", "");
		if (this.GetRawEarthDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawEarthDefenceBonus(), this.GetEarthDefenceBonus(), "Earth Defence", "");
		
		if (this.GetRawStrengthBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawStrengthBonus(), this.GetStrengthBonus(), "Strength", "");
		if (this.GetRawDexterityBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawDexterityBonus(), this.GetDexterityBonus(), "Dexterity", "");
		if (this.GetRawIntelligenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawIntelligenceBonus(), this.GetIntelligenceBonus(), "Intelligence", "");
		if (this.GetRawDefenceBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawDefenceBonus(), this.GetDefenceBonus(), "Defence", "");
		if (this.GetRawAgilityBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawAgilityBonus(), this.GetAgilityBonus(), "Agility", "");
		
		if (this.GetRawSpeedBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawSpeedBonus(), this.GetSpeedBonus(), "Walk Speed", "%");
		if (this.GetRawExplodingBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawExplodingBonus(), this.GetExplodingBonus(), "Exploding", "%");
		if (this.GetRawPoisonBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawPoisonBonus(), this.GetPoisonBonus(), "Poison", "/3s");
		if (this.GetRawThornsBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawThornsBonus(), this.GetThornsBonus(), "Thorns", "%");
		if (this.GetRawReflectionBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawReflectionBonus(), this.GetReflectionBonus(), "Reflection", "%");
		if (this.GetRawSoulPointBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawSoulPointBonus(), this.GetSoulPointBonus(), "Soul Point Regen", "%");
		if (this.GetRawStealingBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawStealingBonus(), this.GetStealingBonus(), "Stealing", "%");
		if (this.GetRawLootBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawLootBonus(), this.GetLootBonus(), "Loot Bonus", "%");
		if (this.GetRawExpBonus() != 0) Tooltip = GetTooltipID(Tooltip, this.GetRawExpBonus(), this.GetExpBonus(), "XP Bonus", "%");
		
		if (!Tooltip.endsWith("/n/n")) Tooltip = Tooltip + "/n";
		
		if (this.Slots > 0) Tooltip = Tooltip + "&7[" + this.Slots + "] Powder Slot" + (this.Slots == 1 ? "/n" : "s/n");
		Tooltip = Tooltip + this.GetRarityName() + " Item";
		if (this.Untradable) Tooltip = Tooltip + "/n&cUntradable Item";
		if (this.QuestItem) Tooltip = Tooltip + "/n&cQuest Item";
		
		
		if (this.Lore != null && ShowLore) {
			int MaxWidth = 0;
			for (String Line : Tooltip.split("/n")) {
				if (ExpReference.GetMsgLength(Line, 1.0f) > MaxWidth) {
					MaxWidth = ExpReference.GetMsgLength(Line, 1.0f);
				}
			}
			if (MaxWidth < 150) {
				MaxWidth = 150;
			}
			for (String string: ModCore.mc().fontRenderer.listFormattedStringToWidth(this.Lore, MaxWidth)) {
				Tooltip = Tooltip + "/n&8" + string;
			}
		}
		
		return Tooltip;
	}
	
	private String GetTooltipID(String Tooltip, int RawStat, int[] Stats, String StatName, String StatSufix) {
		//if (RawStat > 0) {
		//	Tooltip = Tooltip + "&a+" + (this.Identified ? RawStat + StatSufix : Stats[0] + StatSufix + " to &a+" + Stats[1] + StatSufix) + " &7" + StatName + "/n";
		//}else{
		//	Tooltip = Tooltip + "&c" + (this.Identified ? RawStat + StatSufix : Stats[1] + StatSufix + " to &c" + Stats[0] + StatSufix) + " &7" + StatName + "/n";
		//}
		
		//Tooltip = Tooltip + (RawStat > 0 ? "&a+" : "&c") + (this.Identified ? RawStat + StatSufix : Stats[0] + StatSufix + " to " + (RawStat > 0 ? "&a+" : "&c") + Stats[1] + StatSufix) + " &7" + StatName + "/n";
		
		Tooltip = Tooltip + (this.Identified ? (RawStat > 0 ? "&a+" : "&c") + RawStat + StatSufix : (RawStat > 0 ? "&a+" + Stats[0] + StatSufix + " to +" + Stats[1] + StatSufix : "&c"  + Stats[1] + StatSufix + " to " + Stats[0] + StatSufix)) + " &7" + StatName + "/n";
		return Tooltip;
		
	}
}
