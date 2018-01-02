package com.wynndevs;

import com.jagrosh.discordipc.entities.DiscordBuild;
import com.wynndevs.core.Reference;
import com.wynndevs.core.config.GuiConfig;
import com.wynndevs.modules.market.enums.ResetAccount;
import net.minecraftforge.common.config.Config;

import java.util.UUID;

@Config.LangKey("config.richpresence.title")
@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID)
public class ConfigValues {

    @GuiConfig(title = "RichPresence", isInstance = true)
    @Config.LangKey("config.richpresence")
    public static WynncraftRichPresence wynnRichPresence = new WynncraftRichPresence();

    @Config.LangKey("config.market")
    public static ConfigValues.Account marketAccount = new Account();

    @GuiConfig(title = "Expansion", isInstance = true)
    @Config.LangKey("config.expansion")
    public static Expansion wynnExpansion = new Expansion();

    @GuiConfig(title = "Inventory", isInstance = true)
    @Config.LangKey("config.inventory")
    public static Inventory inventoryConfig = new Inventory();

    @GuiConfig(title = "Receive chat mention notifications")
    @Config.Comment("Do you want to mention notifications ?")
    public static boolean mentionNotification = true;

    public static class WynncraftRichPresence {

        @GuiConfig(title = "Entering Notifier")
        @Config.Comment("Do you want to receive entering notifications?")
        public boolean enteringNotifier = true;

        @Config.LangKey("config.richpresence.discord")
        public Discord discordConfig = new Discord();

    }

    public static class Inventory {

        @GuiConfig(title = "Highlight lengendary items in inventories")
        public boolean highlightLegendary = true;

        @GuiConfig(title = "Highlight mythic items in inventories")
        public boolean highlightMythic = true;

        @GuiConfig(title = "Highlight set items in inventories")
        public boolean highlightSet = true;

        @GuiConfig(title = "Highlight unique items in inventories")
        public boolean highlightUnique = true;

        @GuiConfig(title = "Highlight rare items in inventories")
        public boolean highlightRare = true;

        @GuiConfig(title = "Allow emerald count at player inventory")
        public boolean allowEmeraldCount = true;

    }
    
    
    public static class Expansion {

        //to EHTYCSCYTHE
        //place your configs here, and if you want to create sub-interfaces feel free
        //@GuiConfig(title = "the string that will show at the config") -- to boolean values
        //@GuiConfig(title = "the string that wills how at the config", interface = true) -- to instance values like the wynnRichPresence method
			
		@GuiConfig(title = "Items", isInstance = true)
		@Config.LangKey("config.expansion.items")
		public Items items = new Items();

		@GuiConfig(title = "Chat", isInstance = true)
		@Config.LangKey("config.expansion.chats")
		public Chats chat = new Chats();

		public static class Chats {
			@GuiConfig(title = "Party Chat", isInstance = true)
			@Config.LangKey("config.expansion.chats.party")
			public Chat party = new Chat(0);

			@GuiConfig(title = "Guild Chat", isInstance = true)
			@Config.LangKey("config.expansion.chats.guild")
			public Chat guild = new Chat(1);

			public static class Chat {

				@GuiConfig(title = "Enable Chat")
                @Config.LangKey("config.expansion.chats.enabled")
				@Config.Comment("Show the side chat")
				public boolean a_enabled = false;

				public Chat(int chat){
					switch (chat) {
						case 0:
							this.a_enabled = true;
							break;
						case 1:
							this.a_enabled = true;
							break;
					}
				}

			}

		}
		
		public static class Items {
			@GuiConfig(title = "Mythic", isInstance = true)
			@Config.LangKey("config.expansion.items.mythic")
			public Item mythic = new Item(5);
			
			@GuiConfig(title = "Legendary", isInstance = true)
			@Config.LangKey("config.expansion.items.legendary")
			public Item legendary = new Item(4);
			
			@GuiConfig(title = "Rare", isInstance = true)
			@Config.LangKey("config.expansion.items.rare")
			public Item rare = new Item(3);
			
			@GuiConfig(title = "Unique", isInstance = true)
			@Config.LangKey("config.expansion.items.unique")
			public Item unique = new Item(2);
			
			@GuiConfig(title = "Set", isInstance = true)
			@Config.LangKey("config.expansion.items.set")
			public Item set = new Item(1);
			
			@GuiConfig(title = "Normal", isInstance = true)
			@Config.LangKey("config.expansion.items.normal")
			public Item normal = new Item(0);
			
			@GuiConfig(title = "Powders", isInstance = true)
			@Config.LangKey("config.expansion.items.powders")
			public Powders powders = new Powders();
			
			@GuiConfig(title = "Potions", isInstance = true)
			@Config.LangKey("config.expansion.items.potions")
			public Potions potions = new Potions();
			
			@GuiConfig(title = "Misc", isInstance = true)
			@Config.LangKey("config.expansion.items.misc")
			public Misc misc = new Misc();
			
			@GuiConfig(title = "Junk", isInstance = true)
			@Config.LangKey("config.expansion.items.junk")
			public Junk junk = new Junk();
			
			@GuiConfig(title = "Other", isInstance = true)
			@Config.LangKey("config.expansion.items.other")
			public Other other = new Other();
			
			
			public static class Item {
				@GuiConfig(title = "Show Nameplates")
				@Config.LangKey("config.expansion.items.nameplate")
				@Config.Comment("Show Nameplates above items")
				public boolean a_nameplate = false;
				
				@Config.LangKey("config.expansion.items.idformat")
				@Config.Comment("Nameplate format for Identified items\n%n - Will be the Item's name\n%t - Will be the type of item (ring, wand...)\n%l - Will be the Item's level\n& - can be used to change the colour of the text")
				public String b_idformat = "";
				
				@Config.LangKey("config.expansion.items.unidformat")
				@Config.Comment("Nameplate format for Unidentified items\n%t - Will be the type of item (ring, wand...)\n%l - Will be the Item's level\n& - can be used to change the colour of the text")
				public String c_unidformat = "";
				
				@GuiConfig(title = "Highlight on the ground")
				@Config.LangKey("config.expansion.items.highlight.ground")
				@Config.Comment("Highlight items on the ground")
				public boolean d_highlightGround = false;
				
				@GuiConfig(title = "Highlight in Inventories")
				@Config.LangKey("config.expansion.items.highlight.inv")
				@Config.Comment("Highlight items on the ground")
				public boolean e_highlightInv = false;
				
				@GuiConfig(title = "Announcements")
				@Config.LangKey("config.expansion.items.announce")
				@Config.Comment("Display a large Announcment when a item is dropped")
				public boolean f_announce = false;
				
				@Config.LangKey("config.expansion.items.announce.size")
				@Config.Comment("Size of notification to display")
				public float g_announce_size = 1.0f;
				
				@GuiConfig(title = "Sound")
				@Config.LangKey("config.expansion.items.sound")
				@Config.Comment("Play a sound when a item is dropped")
				public boolean h_sound = false;
				
				@Config.LangKey("config.expansion.items.sound.location")
				@Config.Comment("Resource location of sound to play")
				public String i_sound_location = "minecraft:entity.ghast.hurt";
				
				public Item(int Tier){
					switch(Tier){
					case 0: this.a_nameplate = false; this.b_idformat = "%n &6[Lv. %l]"; this.c_unidformat = "Unidentified %t &6[Lv. %l]"; this.d_highlightGround = false; this.e_highlightInv = false; this.f_announce = false; this.g_announce_size = 1.0f; this.h_sound = false; this.i_sound_location = "wynnexp:drum"; break;
					case 1: this.a_nameplate = true; this.b_idformat = "&a%n &6[Lv. %l]"; this.c_unidformat = "&aUnidentified %t &6[Lv. %l]"; this.d_highlightGround = true; this.e_highlightInv = true; this.f_announce = false; this.g_announce_size = 1.25f; this.h_sound = false; this.i_sound_location = "wynnexp:drum"; break;
					case 2: this.a_nameplate = false; this.b_idformat = "&e%n &6[Lv. %l]"; this.c_unidformat = "&eUnidentified %t &6[Lv. %l]"; this.d_highlightGround = true; this.e_highlightInv = true; this.f_announce = false; this.g_announce_size = 1.25f; this.h_sound = false; this.i_sound_location = "wynnexp:drum"; break;
					case 3: this.a_nameplate = true; this.b_idformat = "&d%n &6[Lv. %l]"; this.c_unidformat = "&dUnidentified %t &6[Lv. %l]"; this.d_highlightGround = true; this.e_highlightInv = true; this.f_announce = false; this.g_announce_size = 1.5f; this.h_sound = false; this.i_sound_location = "wynnexp:drum"; break;
					case 4: this.a_nameplate = true; this.b_idformat = "&b%n &6[Lv. %l]"; this.c_unidformat = "&bUnidentified %t &6[Lv. %l]"; this.d_highlightGround = true; this.e_highlightInv = true; this.f_announce = true; this.g_announce_size = 2.0f; this.h_sound = true; this.i_sound_location = "wynnexp:legendary"; break;
					case 5: this.a_nameplate = true; this.b_idformat = "&5%n &6[Lv. %l]"; this.c_unidformat = "&5Unidentified %t &6[Lv. %l]"; this.d_highlightGround = true; this.e_highlightInv = true; this.f_announce = true; this.g_announce_size = 4.0f; this.h_sound = true; this.i_sound_location = "wynnexp:mythic"; break;
					}
				}
			}
			public static class Powders {
				@GuiConfig(title = "Fire", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.fire")
				public PowderType fire = new PowderType(0);
				
				@GuiConfig(title = "Water", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.water")
				public PowderType water = new PowderType(1);
				
				@GuiConfig(title = "Air", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.air")
				public PowderType air = new PowderType(2);
				
				@GuiConfig(title = "Thunder", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.thunder")
				public PowderType thunder = new PowderType(3);
				
				@GuiConfig(title = "Earth", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.earth")
				public PowderType earth = new PowderType(4);
				
				@GuiConfig(title = "Blank", isInstance = true)
				@Config.LangKey("config.expansion.items.powders.blank")
				public PowderType blank = new PowderType(5);
				
				public static class PowderType {
					@Config.LangKey("config.expansion.items.powders.name_format")
					@Config.Comment("Nameplate format for Identified Normal items\n%t - Will show the powder's Tier (eg: IV)\n%T - Will display the Tier numerically (eg: 4)\n& - can be used to change the colour of the text")
					public String name_format = "";
					
					@Config.LangKey("config.expansion.items.powders.highlight_colour")
					@Config.Comment("Code used for Highlights\n§00 §11 §22 §33 §44 §55 §66 §77\n§88 §99 §aa §bb §cc §dd §ee §ff")
					public char highlight_colour = 'r';
					
					@GuiConfig(title = "Tier VI", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier6 = new Powder(6);
					
					@GuiConfig(title = "Tier V", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier5 = new Powder(5);
					
					@GuiConfig(title = "Tier IV", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier4 = new Powder(4);
					
					@GuiConfig(title = "Tier III", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier3 = new Powder(3);
					
					@GuiConfig(title = "Tier II", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier2 = new Powder(2);
					
					@GuiConfig(title = "Tier I", isInstance = true)
					@Config.LangKey("config.expansion.items.powders.blank")
					public Powder Tier1 = new Powder(1);
					
					private PowderType(int Type){
						switch (Type) {
						case 0: this.name_format = "%c✹ Fire Powder %t"; this.highlight_colour = 'c'; break;
						case 1: this.name_format = "%b❉ Fire Powder %t"; this.highlight_colour = 'b'; break;
						case 2: this.name_format = "%f❋ Fire Powder %t"; this.highlight_colour = 'f'; break;
						case 3: this.name_format = "%e✦ Fire Powder %t"; this.highlight_colour = 'e'; break;
						case 4: this.name_format = "%2✤ Fire Powder %t"; this.highlight_colour = '2'; break;
						case 5: this.name_format = "%8█ Fire Powder %t"; this.highlight_colour = '8'; break;
						default: break;
						}
					}
					
					public static class Powder {
						@GuiConfig(title = "Nameplate")
						@Config.LangKey("config.expansion.items.powders.name")
						@Config.Comment("Show Nameplate when on the ground")
						public boolean name = true;
						
						@GuiConfig(title = "Highlight on ground")
						@Config.LangKey("config.expansion.items.powders.highlight")
						@Config.Comment("Highlight when on the ground")
						public boolean highlights = false;
						
						@GuiConfig(title = "Announcments")
						@Config.LangKey("config.expansion.items.powders.announcement")
						@Config.Comment("Show Nameplate for T6 powders")
						public boolean announce = false;
						
						@Config.LangKey("config.expansion.items.powders.announcement.size")
						@Config.Comment("Show Nameplate for T6 powders")
						public float announce_size = 1.0f;
						
						@GuiConfig(title = "Sound")
						@Config.LangKey("config.expansion.items.powders.sound")
						@Config.Comment("Show Nameplate for T6 powders")
						public boolean sound = false;
						
						@Config.LangKey("config.expansion.items.powders.sound.location")
						@Config.Comment("Show Nameplate for T6 powders")
						public String sound_location = "wynnexp:drum";
						
						private Powder(int Tier){
							switch (Tier) {
							case 1: this.announce = false; this.announce_size = 1.0f; break;
							case 2: this.announce = false; this.announce_size = 1.125f; break;
							case 3: this.announce = false; this.announce_size = 1.25f; break;
							case 4: this.announce = true; this.announce_size = 1.5f; break;
							case 5: this.announce = false; this.announce_size = 2.0f; break;
							case 6: this.announce = false; this.announce_size = 2.5f; break;
							default: break;
							}
						}
					}
				}
			}
			public static class Potions {
			
			}
			public static class Quest {
				@GuiConfig(title = "Show Nameplates")
				@Config.LangKey("config.expansion.items.nameplate")
				@Config.Comment("Show Nameplates above Normal items")
				public boolean a_nameplate = false;
				
				@Config.LangKey("config.expansion.items.format")
				@Config.Comment("Nameplate format for Identified Normal items\n%n - Will be the Item's name\n& - can be used to change the colour of the text")
				public String b_format = "&c%n";
			}
			public static class Misc {
				@GuiConfig(title = "Show Nameplates")
				@Config.LangKey("config.expansion.items.nameplate")
				@Config.Comment("Show Nameplates above Normal items")
				public boolean a_nameplate = false;
				
				@Config.LangKey("config.expansion.items.format")
				@Config.Comment("Nameplate format for Identified Normal items\n%n - Will be the Item's name\n& - can be used to change the colour of the text")
				public String b_format = "&7%n";
			}
			public static class Junk {
				@GuiConfig(title = "Show Nameplates")
				@Config.LangKey("config.expansion.items.nameplate")
				@Config.Comment("Show Nameplates above Normal items")
				public boolean a_nameplate = false;
				
				@Config.LangKey("config.expansion.items.format")
				@Config.Comment("Nameplate format for Identified Normal items\n%n - Will be the Item's name\n& - can be used to change the colour of the text")
				public String b_format = "&7%n";
			}
			public static class Other {
				// Stuff here relating to other items (Scrolls, Keys, etc)
			}
			
		}
		/*
		//Items on ground
		WorldItemName.DisplayName = true;
		WorldItemName.SmartDeobfuscate = true;
		WorldItemName.ShortEmerald = false;
		WorldItemName.ShortPotions = false;
		WorldItemName.ShortPowders = false;
		WorldItemName.ShortItems = false;
	
		WorldItemName.NameEmerald = false;
		WorldItemName.NamePotions = true;
		WorldItemName.NamePowders = true;
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
		
		
		// Experience
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
	
		SpellCastingUI.ShowSpellCastingHUD = true;
		ExperienceUI.StaticBarShadow = false;
		ExperienceUI.SideBarHeaderShadow = true;
		ExperienceUI.SideBarFeedShadow = true;
	
		
	
		DailyChestReminder.DailyChestReminder = true;
		SkillpointUI.ShowSkillpoints = true;
		SoulpointTime.SoulPointTime = true;
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
		PartyHUD.UseProportionalDisplay = false;*/
    }

    public static class Discord {

        @Config.Comment("Your nicknname and class will be showed at rich presence")
        public boolean showNicknameAndClass = true;

        @Config.RequiresMcRestart
        @Config.LangKey("config.richpresence.discordversion")
        @Config.Comment("Your discord version | Availabe: Any, Stable, Canary, PTB")
        public DiscordBuild discordBuild = DiscordBuild.ANY;
    }

    public static class Account {

        @Config.RequiresMcRestart
        public String accountName = UUID.randomUUID().toString();

        @Config.RequiresMcRestart
        public String accountPass = UUID.randomUUID().toString();

        @Config.LangKey("config.market.resetaccount")
        public ResetAccount resetAccount = ResetAccount.NO;

    }

}
