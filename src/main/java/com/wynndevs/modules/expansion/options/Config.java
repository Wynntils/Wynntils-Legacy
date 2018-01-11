package com.wynndevs.modules.expansion.options;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.WynnExpansion;
import com.wynndevs.modules.expansion.chat.ChatManipulator;
import com.wynndevs.modules.expansion.chat.ChatTimeStamp;
import com.wynndevs.modules.expansion.experience.ExperienceUI;
import com.wynndevs.modules.expansion.experience.SkillpointUI;
import com.wynndevs.modules.expansion.experience.SoulpointTime;
import com.wynndevs.modules.expansion.experience.SpellCastingUI;
import com.wynndevs.modules.expansion.itemguide.ItemGuideGUI;
import com.wynndevs.modules.expansion.misc.*;
import com.wynndevs.modules.expansion.partyfriendsguild.*;
import com.wynndevs.modules.expansion.webapi.Territory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;



public class Config {
	
	public static Configuration config;
	
	public static File OPTIONS_FILE = new File("config/Wynn Expansion/Wynn Expansion.cfg");
	
	//private static boolean FirstInstall = true;
	private static boolean ShowUpdateMessages = false;
	
	public static void Refresh() {
		if (new File("config/SHCMWynn").exists()){
			(new File("config/SHCMWynn")).renameTo(new File("config/Wynn Expansion"));
			(new File("config/Wynn Expansion/SHCMWynn.cfg")).renameTo(new File("config/Wynn Expansion/Wynn Expansion.cfg"));
		}
		if (!(new File("config/Wynn Expansion")).exists()) {
			(new File("config/Wynn Expansion")).mkdirs();
		}
		config = new Configuration(OPTIONS_FILE);
		
		try {
			config.load();
			
			if (!config.getString("VERSION", "VERSION", "none", "").equals(Reference.VERSION)){
				//if (config.getString("VERSION", "VERSION", "none", "").equals("none")) {FirstInstall = true;};
				if (config.getString("VERSION", "VERSION", "none", "").equals("0.024")) {
					config.get("WarHornAttack", "Modules", true).set(true);
				}

				config.get("VERSION", "VERSION", "none", "").set(Reference.VERSION);
				ShowUpdateMessages = true;
				config.save();
				config.load();
			}
			
			
			// Sticky Items drop key reloader
			if (StickyItems.SavedDropKeyCode == 0) {
				LoadStickyItems();
				StickyItems.SavedDropKeyCode = config.getInt("SavedDropKeyCode", "StickyItems", 16, -100000, 100000, "");
				if(StickyItems.SavedDropKeyCode == 0){
					config.get("StickyItems", "SavedDropKeyCode", 16).set(16);
					config.save();
					config.load();
				}
			}
			
			
			// Daily Rewards reminder set alarm
			if (DailyChestReminder.Alarm == 0) DailyChestReminder.Alarm = Long.parseLong(config.getString("Alarm", "DailyChest", String.valueOf(Long.MAX_VALUE), ""));
			
			
			// module on/off states \\
			
			WorldItemName.DisplayName = config.getBoolean("DisplayName", "Modules", true, "");
			WynnExpansion.UseLegacyExperience = config.getBoolean("UseLegacyExperience", "Modules", false, "");
			ExperienceUI.EnableSidebar = config.getBoolean("EnableSidebar", "Modules", true, "");
			ExperienceUI.EnableScrollingSidebar = config.getBoolean("EnableScrollingSidebar", "Modules", true, "");
			WynnExpansion.PotionShiftOff = config.getBoolean("PotionShiftOff", "Modules", true, "");
			WynnExpansion.DisableFOV = config.getBoolean("DisableFOV", "Modules", false, "");
			
			ChatManipulator.tipDisabler = config.getBoolean("tipDisabler", "Modules", true, "");
			WynnExpansion.TerritoryNews = config.getBoolean("TerritoryNews", "Modules", true, "");
			Territory.CompactNews = config.getBoolean("CompactNews", "Modules", false, "");
			WynnExpansion.OptimiseWar = config.getBoolean("OptimiseWar", "Modules", false, "");
			CompassUI.Compass = config.getBoolean("Compass", "Modules", true, "");
			CompassUI.CompassType = config.getBoolean("CompassType", "Modules", true, "");
			
			PlayerGlow.NameParty = config.getBoolean("NameParty", "Modules", true, "");
			PlayerGlow.NameFriends = config.getBoolean("NameFriends", "Modules", true, "");
			PlayerGlow.NameGuild = config.getBoolean("NameGuild", "Modules", true, "");
			PlayerGlow.BumpHelperName = config.getBoolean("BumpHelperName", "Modules", true, "");
			PlayerGlow.NameDisguises = config.getBoolean("NameDisguises", "Modules", true, "");
			
			PlayerGlow.GlowParty = config.getBoolean("GlowParty", "Modules", true, "");
			PlayerGlow.GlowFriends = config.getBoolean("GlowFriends", "Modules", false, "");
			PlayerGlow.GlowGuild = config.getBoolean("GlowGuild", "Modules", false, "");
			PlayerGlow.GlowHelpers = config.getBoolean("GlowHelpers", "Modules", true, "");
			PlayerGlow.BumpHelperGlow = config.getBoolean("BumpHelperGlow", "Modules", false, "");
			PlayerGlow.HighlightDisguises = config.getBoolean("HighlightDisguises", "Modules", true, "");
			
			ExperienceUI.ExpAboveHealth = config.getBoolean("ExpAboveHealth", "Modules", false, "");
			ExperienceUI.UseExpInstead = config.getBoolean("UseExpInstead", "Modules", false, "");
			ExperienceUI.ExpFlowPercentage = config.getBoolean("ExpFlowPercentage", "Modules", false, "");
			ExperienceUI.ExpFlowShowNames = config.getBoolean("ExpFlowShowNames", "Modules", true, "");
			ExperienceUI.ExpFlowShowLevel = config.getBoolean("ExpFlowShowLevel", "Modules", true, "");
			ExperienceUI.ExpFlowSlow = config.getBoolean("ExpFlowSlow", "Modules", false, "");
			ExperienceUI.ExpFlowSmall = config.getBoolean("ExpFlowSmall", "Modules", true, "");
			
			SpellCastingUI.showSpellCastingHUD = config.getBoolean("showSpellCastingHUD", "Modules", true, "");
			ExperienceUI.StaticBarShadow = config.getBoolean("StaticBarShadow", "Modules", false, "");
			ExperienceUI.SideBarHeaderShadow = config.getBoolean("SideBarHeaderShadow", "Modules", true, "");
			ExperienceUI.SideBarFeedShadow = config.getBoolean("SideBarFeedShadow", "Modules", true, "");
			
			WorldItemName.SmartDeobfuscate = config.getBoolean("SmartDeobfuscate", "Modules", true, "");
			WorldItemName.ShortEmerald = config.getBoolean("ShortEmerald", "Modules", false, "");
			WorldItemName.ShortPotions = config.getBoolean("ShortPotions", "Modules", false, "");
			WorldItemName.ShortPowders = config.getBoolean("ShortPowders", "Modules", false, "");
			WorldItemName.ShortItems = config.getBoolean("ShortItems", "Modules", false, "");
			
			WorldItemName.NameEmerald = config.getBoolean("NameEmerald", "Modules", false, "");
			WorldItemName.NamePotions = config.getBoolean("NamePotions", "Modules", true, "");
			WorldItemName.NamePowders = config.getBoolean("NamePowders", "Modules", true, "");
			WorldItemName.NameKeyScroll = config.getBoolean("NameKeyScroll", "Modules", true, "");
			WorldItemName.NameMisc = config.getBoolean("NameMisc", "Modules", true, "");
			WorldItemName.NameJunk = config.getBoolean("NameJunk", "Modules", false, "");
			
			WorldItemName.NameMythic = config.getBoolean("NameMythic", "Modules", true, "");
			WorldItemName.NameLegendary = config.getBoolean("NameLegendary", "Modules", true, "");
			WorldItemName.NameRare = config.getBoolean("NameRare", "Modules", true, "");
			WorldItemName.NameUnique = config.getBoolean("NameUnique", "Modules", true, "");
			WorldItemName.NameNormal = config.getBoolean("NameNormal", "Modules", true, "");
			WorldItemName.NameSet = config.getBoolean("NameSet", "Modules", true, "");
			
			WorldItemName.AnnounceMythic = config.getBoolean("AnnounceMythic", "Modules", true, "");
			WorldItemName.MythicSound = config.getBoolean("MythicSound", "Modules", true, "");
			WorldItemName.AnnounceLegendary = config.getBoolean("AnnounceLegendary", "Modules", true, "");
			
			WorldItemName.HighlightMythic = config.getBoolean("HighlightMythic", "Modules", true, "");
			WorldItemName.HighlightLegendary = config.getBoolean("HighlightLegendary", "Modules", true, "");
			WorldItemName.HighlightRare = config.getBoolean("HighlightRare", "Modules", false, "");
			WorldItemName.HighlightUnique = config.getBoolean("HighlightUnique", "Modules", false, "");
			WorldItemName.HighlightSet = config.getBoolean("HighlightSet", "Modules", false, "");
			
			DailyChestReminder.DailyChestReminder = config.getBoolean("DailyChestReminder", "Modules", true, "");
			SkillpointUI.showSkillpoints = config.getBoolean("showSkillpoints", "Modules", true, "");
			SoulpointTime.soulPointTime = config.getBoolean("soulPointTime", "Modules", true, "");
			WynnExpansion.InfoOverrideFind = config.getBoolean("InfoOverrideFind", "Modules", false, "");
			WynnExpansion.HeaderVersion = config.getBoolean("HeaderVersion", "Modules", false, "");
			WynnExpansion.ShowTPS = config.getBoolean("ShowTPS", "Modules", false, "");
			
			GuildAttack.AttackTimer = config.getBoolean("AttackTimer", "Modules", true, "");
			GuildAttack.RemoveExcessWar = config.getBoolean("RemoveExcessWar", "Modules", false, "");
			GuildAttack.GuildWarWaypoints = config.getBoolean("GuildWarWaypoints", "Modules", false, "");
			GuildAttack.WarHornWarmup = config.getBoolean("WarHornWarmup", "Modules", true, "");
			GuildAttack.WarHornAttack = config.getBoolean("WarHornAttack", "Modules", true, "");
			WarTimer.EnableWarTimer = config.getBoolean("EnableWarTimer", "Modules", true, "");
			WarTimer.WarTimerLeft = config.getBoolean("WarTimerLeft", "Modules", true, "");
			
			PlayerGlow.WarHealth = config.getBoolean("WarHealth", "Modules", true, "");
			PlayerGlow.GlowVeryLowHealth = config.getBoolean("GlowVeryLowHealth", "Modules", true, "");
			PlayerGlow.GlowLowHealth = config.getBoolean("GlowLowHealth", "Modules", true, "");
			PlayerGlow.GlowMidHealth = config.getBoolean("GlowMidHealth", "Modules", true, "");
			PlayerGlow.GlowHighHealth = config.getBoolean("GlowHighHealth", "Modules", false, "");
			PlayerGlow.GlowVeryHighHealth = config.getBoolean("GlowVeryHighHealth", "Modules", false, "");
			
			WynnExpansion.PotionEnabled = config.getBoolean("PotionEnabled", "Modules", true, "");
			PotionDisplay.PotionShadow = config.getBoolean("PotionShadow", "Modules", false, "");
			PotionDisplay.PotionHideTimer = config.getBoolean("PotionHideTimer", "Modules", true, "");
			PotionDisplay.PotionAllignRight = config.getBoolean("PotionAllignRight", "Modules", false, "");
			PotionDisplay.PotionCenterVerticaly = config.getBoolean("PotionCenterVerticaly", "Modules", false, "");
			
			PlayerGlow.PlayerCollision = config.getBoolean("PlayerCollision", "Modules", false, "");
			ExperienceUI.KillStreak = config.getBoolean("KillStreak", "Modules", false, "");
			ExperienceUI.KillPerMinute = config.getBoolean("KillPerMinute", "Modules", false, "");
			ItemGuideGUI.SearchBoxActiveDefault = config.getBoolean("SearchBoxActiveDefault", "Modules", false, "");
			ItemGuideGUI.ItemGuideShowLore = config.getBoolean("ItemGuideShowLore", "Modules", false, "");
			ItemGuideGUI.ItemGuideBoxRarity = config.getBoolean("ItemGuideBoxRarity", "Modules", false, "");
			ItemGuideGUI.ItemGuidePurple = config.getBoolean("ItemGuidePurple", "Modules", false, "");
			
			ChatTimeStamp.ShowTimeStamps = config.getBoolean("ShowTimeStamps", "Modules", true, "");
			ChatTimeStamp.ShowSeconds = config.getBoolean("ShowSeconds", "Modules", false, "");
			ChatTimeStamp.PlainTimeStamp = config.getBoolean("PlainTimeStamp", "Modules", false, "");
			ChatTimeStamp.TwelveHourTime = config.getBoolean("TwelveHourTime", "Modules", false, "");
			
			PartyHUD.ShowHealthHUD = config.getBoolean("ShowHealthHUD", "Modules", true, "");
			PartyHUD.ShowOutOfRange = config.getBoolean("ShowOutOfRange", "Modules", false, "");
			PartyHUD.AllignRight = config.getBoolean("AllignRight", "Modules", true, "");
			PartyHUD.ColourHealthBar = config.getBoolean("ColourHealthBar", "Modules", true, "");
			PartyHUD.ColourName = config.getBoolean("ColourName", "Modules", false, "");
			PartyHUD.ShowPercentage = config.getBoolean("ShowPercentage", "Modules", true, "");
			PartyHUD.UseProportionalDisplay = config.getBoolean("UseProportionalDisplay", "Modules", false, "");
			
			GuildAttackTimer.AttackShadowTerritory = config.getBoolean("AttackShadowTerritory", "Modules", false, "");
			GuildAttackTimer.AttackShadowTimer = config.getBoolean("AttackShadowTimer", "Modules", false, "");
			GuildAttackTimer.AttackColourTimer = config.getBoolean("AttackColourTimer", "Modules", false, "");
			//////////// |\\\\\\\\\\\\\
			
			if (config.getBoolean("AttackTimer", "Modules", true, "") == true && config.getBoolean("RemoveExcessWar", "Modules", false, "") == true){
				config.get("RemoveExcessWar", "Modules", false).set(false);
				GuildAttack.RemoveExcessWar = config.getBoolean("RemoveExcessWar", "Modules", false, "");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ExpReference.consoleOut("Error loading config, returning to default variables.");
			
			WorldItemName.DisplayName = true;
			WynnExpansion.UseLegacyExperience = false;
			ExperienceUI.EnableSidebar = true;
			ExperienceUI.EnableScrollingSidebar = true;
			WynnExpansion.PotionShiftOff = true;
			WynnExpansion.DisableFOV = false;
			
			ChatManipulator.tipDisabler = true;
			WynnExpansion.TerritoryNews = true;
			Territory.CompactNews = false;
			WynnExpansion.OptimiseWar = false;
			CompassUI.Compass = true;
			CompassUI.CompassType = true;
			
			PlayerGlow.NameParty = true;
			PlayerGlow.NameFriends = true;
			PlayerGlow.NameGuild = true;
			PlayerGlow.BumpHelperName = true;
			PlayerGlow.NameDisguises = true;
			
			PlayerGlow.GlowParty = true;
			PlayerGlow.GlowFriends = false;
			PlayerGlow.GlowGuild = false;
			PlayerGlow.GlowHelpers = true;
			PlayerGlow.BumpHelperGlow = false;
			PlayerGlow.HighlightDisguises = true;
			
			ExperienceUI.ExpAboveHealth = false;
			ExperienceUI.UseExpInstead = false;
			ExperienceUI.ExpFlowPercentage = false;
			ExperienceUI.ExpFlowShowNames = true;
			ExperienceUI.ExpFlowShowLevel = true;
			ExperienceUI.ExpFlowSlow = false;
			ExperienceUI.ExpFlowSmall = true;
			
			SpellCastingUI.showSpellCastingHUD = true;
			ExperienceUI.StaticBarShadow = false;
			ExperienceUI.SideBarHeaderShadow = true;
			ExperienceUI.SideBarFeedShadow = true;
			
			WorldItemName.SmartDeobfuscate = true;
			WorldItemName.ShortEmerald = false;
			WorldItemName.ShortPotions = false;
			WorldItemName.ShortPowders = false;
			WorldItemName.ShortItems = false;
			
			WorldItemName.NameEmerald = false;
			WorldItemName.NamePotions = true;
			WorldItemName.NamePowders = true;
			WorldItemName.NameKeyScroll = true;
			WorldItemName.NameMisc = true;
			WorldItemName.NameJunk = false;
			
			WorldItemName.NameMythic = true;
			WorldItemName.NameLegendary = true;
			WorldItemName.NameRare = true;
			WorldItemName.NameUnique = true;
			WorldItemName.NameNormal = true;
			WorldItemName.NameSet = true;
			
			WorldItemName.AnnounceMythic = true;
			WorldItemName.MythicSound = true;
			WorldItemName.AnnounceLegendary = true;
			
			WorldItemName.HighlightMythic = true;
			WorldItemName.HighlightLegendary = true;
			WorldItemName.HighlightRare = false;
			WorldItemName.HighlightUnique = false;
			WorldItemName.HighlightSet = false;
			
			DailyChestReminder.DailyChestReminder = true;
			SkillpointUI.showSkillpoints = true;
			SoulpointTime.soulPointTime = true;
			WynnExpansion.InfoOverrideFind = true;
			WynnExpansion.HeaderVersion = false;
			WynnExpansion.ShowTPS = false;
			
			GuildAttack.AttackTimer = true;
			GuildAttack.RemoveExcessWar = false;
			GuildAttack.GuildWarWaypoints = false;
			GuildAttack.WarHornWarmup = true;
			GuildAttack.WarHornAttack = true;
			WarTimer.EnableWarTimer = true;
			WarTimer.WarTimerLeft = true;
			
			PlayerGlow.WarHealth = true;
			PlayerGlow.GlowVeryLowHealth = true;
			PlayerGlow.GlowLowHealth = true;
			PlayerGlow.GlowMidHealth = true;
			PlayerGlow.GlowHighHealth = false;
			PlayerGlow.GlowVeryHighHealth = false;
			
			WynnExpansion.PotionEnabled = true;
			PotionDisplay.PotionShadow = false;
			PotionDisplay.PotionHideTimer = true;
			PotionDisplay.PotionAllignRight = false;
			PotionDisplay.PotionCenterVerticaly = false;
			
			PlayerGlow.PlayerCollision = false;
			ExperienceUI.KillStreak = false;
			ExperienceUI.KillPerMinute = false;
			ItemGuideGUI.SearchBoxActiveDefault = false;
			ItemGuideGUI.ItemGuideShowLore = false;
			ItemGuideGUI.ItemGuideBoxRarity = false;
			ItemGuideGUI.ItemGuidePurple = false;
			
			ChatTimeStamp.ShowTimeStamps = true;
			ChatTimeStamp.ShowSeconds = false;
			ChatTimeStamp.PlainTimeStamp = false;
			ChatTimeStamp.TwelveHourTime = false;
			
			PartyHUD.ShowHealthHUD = true;
			PartyHUD.ShowOutOfRange = false;
			PartyHUD.AllignRight = true;
			PartyHUD.ColourHealthBar = true;
			PartyHUD.ColourName = false;
			PartyHUD.ShowPercentage = true;
			PartyHUD.UseProportionalDisplay = false;
			
		} finally {
			config.save();
		}
	}

	public static void setModule(String module, boolean to) {
		config.get("Modules", module, true).set(to);
		config.save();
		Refresh();
	}
	
	
	
	// Config functions for Item Locks
	public static void LoadStickyItems(){
		String Class = "";
		for (int i=0;i<5;i++) {
			switch (i) {
				case 0: Class = ""; break;
				case 1: Class = "Archer"; break;
				case 2: Class = "Warrior"; break;
				case 3: Class = "Mage"; break;
				case 4: Class = "Assassin"; break;
			}
			for (int Slot=0;Slot<42;Slot++){
				if (Slot >= 6 && Slot <= 8){
					StickyItems.StickySlots[i][Slot] = true;
				}else if (config.getBoolean("LockSlot" + Class + Slot, "StickyItems", false, "") == true){
					StickyItems.StickySlots[i][Slot] = true;
				}
			}
		}
	}
	public static void setStickyItemLock(int Class, int Slot, boolean Lock) {
		String ClassName = "";
		switch (Class) {
			case 0: ClassName = ""; break;
			case 1: ClassName = "Archer"; break;
			case 2: ClassName = "Warrior"; break;
			case 3: ClassName = "Mage"; break;
			case 4: ClassName = "Assassin"; break;
		}
		config.get("StickyItems", "LockSlot" + ClassName + Slot, false).set(Lock);
		config.save();
	}
	public static void setStickyItemDropKey(int to) {
		config.get("StickyItems", "SavedDropKeyCode", 16).set(to);
		config.save();
	}

	public static void ResetDailyTimer(long Alarm) {
		config.get("DailyChest", "Alarm", String.valueOf(Long.MAX_VALUE)).set(String.valueOf(Alarm));
		config.save();
	}

	public static void PostUpdateMessages() {
		/*if (FirstInstall) {
			TextComponentString ThankyouMessage = new TextComponentString("Thank you for installing Wynn Expansion!");
			ThankyouMessage.getStyle().setColor(TextFormatting.GOLD);
			ThankyouMessage.getStyle().setBold(true);
			ExpReference.postToChat(ThankyouMessage);
			
			TextComponentString Space = new TextComponentString("");
			ExpReference.postToChat(Space);
			
			ThankyouMessage = new TextComponentString("If you find any bugs or have suggestions feel free to post them on the Wynncraft Thread");
			ThankyouMessage.getStyle().setColor(TextFormatting.GOLD);
			ExpReference.postToChat(ThankyouMessage);
			
			ExpReference.postToChat(Space);
			FirstInstall = false;
		}*/
		if (ShowUpdateMessages) {
			
			ShowUpdateMessages = false;
		}
	}
}
