package com.wynndevs.expansion.Options;

import com.wynndevs.ModCore;
import com.wynndevs.expansion.ExpReference;
import com.wynndevs.expansion.Experience.ExperienceUI;
import com.wynndevs.expansion.Experience.SkillpointUI;
import com.wynndevs.expansion.Experience.SoulpointTime;
import com.wynndevs.expansion.Experience.SpellCastingUI;
import com.wynndevs.expansion.ItemGuide.ItemGuideGUI;
import com.wynndevs.expansion.Misc.*;
import com.wynndevs.expansion.PartyFriendsGuild.*;
import com.wynndevs.expansion.QuestBook.GuiQuestBook;
import com.wynndevs.expansion.Update.Update;
import com.wynndevs.expansion.WebAPI.Territory;
import com.wynndevs.expansion.WynnExpansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GuiSHCMWynnOptions extends GuiScreenMod {
	private static final ResourceLocation TEXTURE_OPTIONS = new ResourceLocation(ExpReference.MOD_ID, "textures/gui/options.png");
	private static Delay refreshDelay = new Delay(10f, true);
	
	private static OptionsButton btnOptions = new OptionsButton();
	private static ExitButton btnExit = new ExitButton();
	
	private static ChangePageButton btnNextPage = new ChangePageButton();
	private static ChangePageButton btnPrevPage = new ChangePageButton();
	
	private static ModPageButton btnModPage = new ModPageButton();
	private static EggButton btnEgg = new EggButton();
	
	
	private static ToggleButton tglDisplayName = new ToggleButton();
	private static ToggleButton tglUseLegacyExperience = new ToggleButton();
	private static ToggleButton tglEnableSidebar = new ToggleButton();
	private static ToggleButton tglEnableScrollingSidebar = new ToggleButton();
	private static ToggleButton tglPotionShiftOff = new ToggleButton();
	private static ToggleButton tglDisableFOV = new ToggleButton();
	
	private static ToggleButton tgltipDisabler = new ToggleButton();
	private static ToggleButton tglTerritoryNews = new ToggleButton();
	private static ToggleButton tglCompactNews = new ToggleButton();
	private static ToggleButton tglOptimiseWar = new ToggleButton();
	private static ToggleButton tglCompass = new ToggleButton();
	private static ToggleButton tglCompassType = new ToggleButton();
	
	private static ToggleButton tglNameParty = new ToggleButton();
	private static ToggleButton tglNameFriends = new ToggleButton();
	private static ToggleButton tglNameGuild = new ToggleButton();
	private static ToggleButton tglBumpHelperName = new ToggleButton();
	private static ToggleButton tglNameDisguises = new ToggleButton();
	
	private static ToggleButton tglGlowParty = new ToggleButton();
	private static ToggleButton tglGlowFriends = new ToggleButton();
	private static ToggleButton tglGlowGuild = new ToggleButton();
	private static ToggleButton tglGlowHelpers = new ToggleButton();
	private static ToggleButton tglBumpHelperGlow = new ToggleButton();
	private static ToggleButton tglHighlightDisguises = new ToggleButton();
	
	private static ToggleButton tglExpAboveHealth = new ToggleButton();
	private static ToggleButton tglUseExpInstead = new ToggleButton();
	private static ToggleButton tglExpFlowPercentage = new ToggleButton();
	private static ToggleButton tglExpFlowShowNames = new ToggleButton();
	private static ToggleButton tglExpFlowShowLevel = new ToggleButton();
	private static ToggleButton tglExpFlowSlow = new ToggleButton();
	private static ToggleButton tglExpFlowSmall = new ToggleButton();
	
	private static ToggleButton tglShowSpellCastingHUD = new ToggleButton();
	private static ToggleButton tglStaticBarShadow = new ToggleButton();
	private static ToggleButton tglSideBarHeaderShadow = new ToggleButton();
	private static ToggleButton tglSideBarFeedShadow = new ToggleButton();
	
	private static ToggleButton tglSmartDeobfuscate = new ToggleButton();
	private static ToggleButton tglShortEmerald = new ToggleButton();
	private static ToggleButton tglShortPotions = new ToggleButton();
	private static ToggleButton tglShortPowders = new ToggleButton();
	private static ToggleButton tglShortItems = new ToggleButton();
	
	private static ToggleButton tglNameEmerald = new ToggleButton();
	private static ToggleButton tglNamePotions = new ToggleButton();
	private static ToggleButton tglNamePowders = new ToggleButton();
	private static ToggleButton tglNameKeyScroll = new ToggleButton();
	private static ToggleButton tglNameMisc = new ToggleButton();
	private static ToggleButton tglNameJunk = new ToggleButton();
	
	private static ToggleButton tglNameMythic = new ToggleButton();
	private static ToggleButton tglNameLegendary = new ToggleButton();
	private static ToggleButton tglNameRare = new ToggleButton();
	private static ToggleButton tglNameUnique = new ToggleButton();
	private static ToggleButton tglNameNormal = new ToggleButton();
	private static ToggleButton tglNameSet = new ToggleButton();
	
	private static ToggleButton tglAnnounceMythic = new ToggleButton();
	private static ToggleButton tglMythicSound = new ToggleButton();
	private static ToggleButton tglAnnounceLegendary = new ToggleButton();
	
	private static ToggleButton tglHighlightMythic = new ToggleButton();
	private static ToggleButton tglHighlightLegendary = new ToggleButton();
	private static ToggleButton tglHighlightRare = new ToggleButton();
	private static ToggleButton tglHighlightUnique = new ToggleButton();
	private static ToggleButton tglHighlightSet = new ToggleButton();
	
	private static ToggleButton tglDailyChestReminder = new ToggleButton();
	private static ToggleButton tglShowSkillpoints = new ToggleButton();
	private static ToggleButton tglSoulPointTime  = new ToggleButton();
	private static ToggleButton tglInfoOverrideFind  = new ToggleButton();
	private static ToggleButton tglHeaderVersion  = new ToggleButton();
	private static ToggleButton tglShowTPS  = new ToggleButton();
	
	private static ToggleButton tglAttackTimer  = new ToggleButton();
	private static ToggleButton tglRemoveExcessWar  = new ToggleButton();
	private static ToggleButton tglGuildWarWaypoints  = new ToggleButton();
	private static ToggleButton tglWarHornWarmup  = new ToggleButton();
	private static ToggleButton tglWarHornAttack  = new ToggleButton();
	private static ToggleButton tglEnableWarTimer  = new ToggleButton();
	private static ToggleButton tglWarTimerLeft  = new ToggleButton();
	
	private static ToggleButton tglWarHealth = new ToggleButton();
	private static ToggleButton tglGlowVeryLowHealth = new ToggleButton();
	private static ToggleButton tglGlowLowHealth = new ToggleButton();
	private static ToggleButton tglGlowMidHealth = new ToggleButton();
	private static ToggleButton tglGlowHighHealth = new ToggleButton();
	private static ToggleButton tglGlowVeryHighHealth = new ToggleButton();
	
	private static ToggleButton tglPotionEnabled = new ToggleButton();
	private static ToggleButton tglPotionShadow = new ToggleButton();
	private static ToggleButton tglPotionHideTimer = new ToggleButton();
	private static ToggleButton tglPotionAllignRight = new ToggleButton();
	private static ToggleButton tglPotionCenterVerticaly = new ToggleButton();
	
	private static ToggleButton tglPlayerCollision = new ToggleButton();
	private static ToggleButton tglKillStreak = new ToggleButton();
	private static ToggleButton tglKillPerMinute = new ToggleButton();
	private static ToggleButton tglSearchBoxActiveDefault = new ToggleButton();
	private static ToggleButton tglItemGuideShowLore = new ToggleButton();
	private static ToggleButton tglItemGuideBoxRarity = new ToggleButton();
	private static ToggleButton tglItemGuidePurple = new ToggleButton();
	
	private static ToggleButton tglShowTimeStamps = new ToggleButton();
	private static ToggleButton tglShowSeconds = new ToggleButton();
	private static ToggleButton tglPlainTimeStamp = new ToggleButton();
	private static ToggleButton tglTwelveHourTime = new ToggleButton();
	
	private static ToggleButton tglShowHealthHUD = new ToggleButton();
	private static ToggleButton tglShowOutOfRange = new ToggleButton();
	private static ToggleButton tglAllignRight = new ToggleButton();
	private static ToggleButton tglColourHealthBar = new ToggleButton();
	private static ToggleButton tglColourName = new ToggleButton();
	private static ToggleButton tglShowPercentage = new ToggleButton();
	private static ToggleButton tglUseProportionalDisplay = new ToggleButton();
	
	private static ToggleButton tglAttackShadowTerritory = new ToggleButton();
	private static ToggleButton tglAttackShadowTimer = new ToggleButton();
	private static ToggleButton tglAttackColourTimer = new ToggleButton();
	
	
	private static int page = 1;
	private static int maxPages = 1;
	
	private static boolean EnableEgg = true;
	
	private List<GuiButton> pageButtons = new ArrayList<GuiButton>();
	
	public GuiSHCMWynnOptions() {
		page = 1;
	}
	
	@Override
	public void initGui() {
		btnOptions = new OptionsButton(0, (this.width / 2) + 84, 15);
		btnExit = new ExitButton(-1, (this.width / 2) + 100, 15);
		
		btnNextPage = new ChangePageButton(1, (this.width / 2) + 25, 50, true);
		btnPrevPage = new ChangePageButton(2, (this.width / 2) - 52, 50, false);
		
		btnModPage = new ModPageButton(3, (this.width / 2) + 55, 50);
		btnEgg = new EggButton(4, (this.width / 2) -115, 15);
		
		pageButtons.add(tglDisplayName = new ToggleButton(100, (this.width / 2) - 110, 80, "Show Nameplates for items", 1));
		pageButtons.add(tglUseLegacyExperience = new ToggleButton(101, (this.width / 2) - 110, 95, "Use the classic way of showing " + (ExperienceUI.UseExpInstead ? "Exp" : "XP"), 1));
		pageButtons.add(tglEnableSidebar = new ToggleButton(102, (this.width / 2) - 110, 110, "Enable the " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " Sidebar Header", 1));
		pageButtons.add(tglEnableScrollingSidebar = new ToggleButton(103, (this.width / 2) - 110, 125, "Enable the " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " Sidebar Feed", 1));
		pageButtons.add(tglPotionShiftOff = new ToggleButton(104, (this.width / 2) - 110, 155, "Disable Inventory shifting under effects", 1));
		pageButtons.add(tglDisableFOV = new ToggleButton(105, (this.width / 2) - 110, 170, "Disable FOV changes", 1));
		
		pageButtons.add(tgltipDisabler = new ToggleButton(200, (this.width / 2) - 110, 80, "Disable the tips that Wynncraft provides", 2));
		pageButtons.add(tglTerritoryNews = new ToggleButton(201, (this.width / 2) - 110, 95, "Show live Territory takeover news feed", 2));
		pageButtons.add(tglCompactNews = new ToggleButton(202, (this.width / 2) - 110, 110, "Shorten Guild names in news feed", 2));
		pageButtons.add(tglOptimiseWar = new ToggleButton(203, (this.width / 2) - 110, 125, "Only render ESSENTIAL entities in WAR", 2));
		pageButtons.add(tglCompass = new ToggleButton(204, (this.width / 2) - 110, 155, "Show current location", 2));
		pageButtons.add(tglCompassType = new ToggleButton(205, (this.width / 2) - 110, 170, "Only show when holding a compass", 2));
		
		pageButtons.add(tglNameParty = new ToggleButton(300, (this.width / 2) - 110, 80, "Colour Party member nameplates", 3));
		pageButtons.add(tglNameFriends = new ToggleButton(301, (this.width / 2) - 110, 95, "Colour Friends nameplates", 3));
		pageButtons.add(tglNameGuild = new ToggleButton(302, (this.width / 2) - 110, 110, "Colour Guild member nameplates", 3));
		pageButtons.add(tglBumpHelperName = new ToggleButton(305, (this.width / 2) - 110, 155, "Increase Helper nameplate priority", 3));
		pageButtons.add(tglNameDisguises = new ToggleButton(306, (this.width / 2) - 110, 170, "Colour Nameplates of Disguised players", 3));
		
		pageButtons.add(tglGlowParty = new ToggleButton(320, (this.width / 2) - 110, 80, "Highlight Party members", 4));
		pageButtons.add(tglGlowFriends = new ToggleButton(321, (this.width / 2) - 110, 95, "Highlight Friends", 4));
		pageButtons.add(tglGlowGuild = new ToggleButton(322, (this.width / 2) - 110, 110, "Highlight Guild members", 4));
		pageButtons.add(tglGlowHelpers = new ToggleButton(323, (this.width / 2) - 110, 125, "Highlight Helpers of the mod", 4));
		pageButtons.add(tglBumpHelperGlow = new ToggleButton(325, (this.width / 2) - 110, 155, "Increase Helper highlight priority", 4));
		pageButtons.add(tglHighlightDisguises = new ToggleButton(326, (this.width / 2) - 110, 170, "Allow Highliting of Disguised players", 4));
		
		pageButtons.add(tglExpAboveHealth = new ToggleButton(400, (this.width / 2) - 110, 80, "Show " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " above action bar", 5));
		pageButtons.add(tglUseExpInstead = new ToggleButton(401, (this.width / 2) - 110, 95, "Replace \"XP\" with \"Exp\" ", 5));
		pageButtons.add(tglExpFlowPercentage = new ToggleButton(402, (this.width / 2) - 110, 110, "Show " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + "% gain in feed", 5));
		pageButtons.add(tglExpFlowShowNames = new ToggleButton(403, (this.width / 2) - 110, 125, "Show Mob name in " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " feed", 5));
		pageButtons.add(tglExpFlowShowLevel = new ToggleButton(404, (this.width / 2) - 110, 140, "Show Mob level in " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " feed", 5));
		pageButtons.add(tglExpFlowSlow = new ToggleButton(405, (this.width / 2) - 110, 155, "Slow " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " sidebar feed", 5));
		pageButtons.add(tglExpFlowSmall = new ToggleButton(406, (this.width / 2) - 110, 170, "Use small " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " sidebar text", 5));
		
		pageButtons.add(tglShowSpellCastingHUD = new ToggleButton(450, (this.width / 2) - 110, 80, "Display icons showing spell key bindings", 6));
		pageButtons.add(tglStaticBarShadow = new ToggleButton(451, (this.width / 2) - 110, 95, "Add a shadow to " + (ExperienceUI.UseExpInstead ? "Exp" : "XP") + " Bar", 6));
		pageButtons.add(tglSideBarHeaderShadow = new ToggleButton(452, (this.width / 2) - 110, 110, "Add a shadow to Sidebar Header", 6));
		pageButtons.add(tglSideBarFeedShadow = new ToggleButton(453, (this.width / 2) - 110, 125, "Add a shadow to Sidebar Feed", 6));
		
		pageButtons.add(tglSmartDeobfuscate = new ToggleButton(500, (this.width / 2) - 110, 80, "Show true names for ID'd items", 7));
		pageButtons.add(tglShortItems = new ToggleButton(505, (this.width / 2) - 110, 110, "Shorten Item names", 7));
		pageButtons.add(tglShortEmerald = new ToggleButton(502, (this.width / 2) - 110, 125, "Shorten Emerald names", 7));
		pageButtons.add(tglShortPotions = new ToggleButton(503, (this.width / 2) - 110, 140, "Shorten Potion names", 7));
		pageButtons.add(tglShortPowders = new ToggleButton(504, (this.width / 2) - 110, 155, "Shorten Powder names", 7));
		
		pageButtons.add(tglNameEmerald = new ToggleButton(600, (this.width / 2) - 110, 80, "Allow Nameplate for Emeralds", 8));
		pageButtons.add(tglNamePotions = new ToggleButton(601, (this.width / 2) - 110, 95, "Allow Nameplate for Potions", 8));
		pageButtons.add(tglNamePowders = new ToggleButton(602, (this.width / 2) - 110, 110, "Allow Nameplate for Powders", 8));
		pageButtons.add(tglNameMisc = new ToggleButton(604, (this.width / 2) - 110, 155, "Allow Nameplate for Misc", 8));
		pageButtons.add(tglNameJunk = new ToggleButton(605, (this.width / 2) - 110, 170, "Allow Nameplate for Junk", 8));
		
		pageButtons.add(tglNameMythic = new ToggleButton(700, (this.width / 2) - 110, 80, "Allow Mythic Nameplates", 9));
		pageButtons.add(tglNameLegendary = new ToggleButton(701, (this.width / 2) - 110, 95, "Allow Legendary Nameplates", 9));
		pageButtons.add(tglNameRare = new ToggleButton(702, (this.width / 2) - 110, 110, "Allow Rare Nameplates", 9));
		pageButtons.add(tglNameUnique = new ToggleButton(703, (this.width / 2) - 110, 125, "Allow Unique Nameplates", 9));
		pageButtons.add(tglNameNormal = new ToggleButton(704, (this.width / 2) - 110, 140, "Allow Normal Nameplates", 9));
		pageButtons.add(tglNameSet = new ToggleButton(705, (this.width / 2) - 110, 170, "Allow Set Nameplates", 9));
		
		pageButtons.add(tglHighlightMythic = new ToggleButton(900, (this.width / 2) - 110, 80, "Highlight Mythic items", 10));
		pageButtons.add(tglHighlightLegendary = new ToggleButton(901, (this.width / 2) - 110, 95, "Highlight Legendary items", 10));
		pageButtons.add(tglHighlightRare = new ToggleButton(902, (this.width / 2) - 110, 110, "Highlight Rare items", 10));
		pageButtons.add(tglHighlightUnique = new ToggleButton(903, (this.width / 2) - 110, 125, "Highlight Unique items", 10));
		pageButtons.add(tglHighlightSet = new ToggleButton(904, (this.width / 2) - 110, 170, "Highlight Set items", 10));
		
		pageButtons.add(tglAnnounceMythic = new ToggleButton(800, (this.width / 2) - 110, 80, "Show Big message on Mythic drop", 11));
		pageButtons.add(tglMythicSound = new ToggleButton(801, (this.width / 2) - 110, 95, "Play Sound on Mythic drop", 11));
		pageButtons.add(tglAnnounceLegendary = new ToggleButton(802, (this.width / 2) - 110, 110, "Show message on Legendary drop", 11));
		
		pageButtons.add(tglDailyChestReminder = new ToggleButton(1000, (this.width / 2) - 110, 80, "Daily Rewards reminder", 12));
		pageButtons.add(tglShowSkillpoints = new ToggleButton(1001, (this.width / 2) - 110, 95, "Show skillpoints on compass", 12));
		pageButtons.add(tglSoulPointTime = new ToggleButton(1002, (this.width / 2) - 110, 110, "Add Soul Point regen time to Soul Points", 12));
		pageButtons.add(tglInfoOverrideFind = new ToggleButton(1022, (this.width / 2) - 110, 125, "Redirect /find to /info", 12));
		pageButtons.add(tglHeaderVersion = new ToggleButton(1024, (this.width / 2) - 110, 155, "Show Version number in TAB menu", 12));
		pageButtons.add(tglShowTPS = new ToggleButton(1025, (this.width / 2) - 110, 170, "Show Estimated TPS in TAB menu", 12));
		
		pageButtons.add(tglAttackTimer = new ToggleButton(1003, (this.width / 2) - 110, 80, "Show a War attack timer", 13));
		pageButtons.add(tglRemoveExcessWar = new ToggleButton(1004, (this.width / 2) - 110, 95, "Hide Excess war Messages", 13));
		pageButtons.add(tglGuildWarWaypoints = new ToggleButton(1005, (this.width / 2) - 110, 110, "Add quick waypoint message on Wars", 13));
		pageButtons.add(tglWarHornWarmup = new ToggleButton(1006, (this.width / 2) - 110, 125, "Play sound on Countdown Start", 13));
		pageButtons.add(tglWarHornAttack = new ToggleButton(1007, (this.width / 2) - 110, 140, "Play sound at 5 Seconds left", 13));
		pageButtons.add(tglEnableWarTimer = new ToggleButton(1008, (this.width / 2) - 110, 155, "Show Timer in wars", 13));
		pageButtons.add(tglWarTimerLeft = new ToggleButton(1009, (this.width / 2) - 110, 170, "Allign war timer to the top left", 13));
		
		pageButtons.add(tglWarHealth = new ToggleButton(1150, (this.width / 2) - 110, 80, "Use health based highlights in wars", 14));
		pageButtons.add(tglGlowVeryLowHealth = new ToggleButton(1151, (this.width / 2) - 110, 110, "Enable Critical health Highlights", 14));
		pageButtons.add(tglGlowLowHealth = new ToggleButton(1152, (this.width / 2) - 110, 125, "Enable Low health Highlights", 14));
		pageButtons.add(tglGlowMidHealth = new ToggleButton(1153, (this.width / 2) - 110, 140, "Enable Half health Highlights", 14));
		pageButtons.add(tglGlowHighHealth = new ToggleButton(1154, (this.width / 2) - 110, 155, "Enable High health Highlights", 14));
		pageButtons.add(tglGlowVeryHighHealth = new ToggleButton(1155, (this.width / 2) - 110, 170, "Enable Max health Highlights", 14));
		
		pageButtons.add(tglPotionEnabled = new ToggleButton(1100, (this.width / 2) - 110, 80, "Enable Potion hud and timers", 15));
		pageButtons.add(tglPotionShadow = new ToggleButton(1101, (this.width / 2) - 110, 95, "Add shadow to Potion timers", 15));
		pageButtons.add(tglPotionHideTimer = new ToggleButton(1102, (this.width / 2) - 110, 110, "Hide remaning time on Potion Hud", 15));
		pageButtons.add(tglPotionAllignRight = new ToggleButton(1103, (this.width / 2) - 110, 125, "Align Potion hud to the right", 15));
		pageButtons.add(tglPotionCenterVerticaly = new ToggleButton(1104, (this.width / 2) - 110, 140, "Lower potion hud", 15));
		
		pageButtons.add(tglPlayerCollision = new ToggleButton(1200, (this.width / 2) - 110, 80, "Allow Player Collisions", 16));
		pageButtons.add(tglKillStreak = new ToggleButton(1201, (this.width / 2) - 110, 95, "Show Kill Streak counter", 16));
		pageButtons.add(tglKillPerMinute = new ToggleButton(1202, (this.width / 2) - 110, 110, "Show Kills Per Minute", 16));
		pageButtons.add(tglSearchBoxActiveDefault = new ToggleButton(1203, (this.width / 2) - 110, 125, "Auto select Item guide search bar", 16));
		pageButtons.add(tglItemGuideShowLore = new ToggleButton(1204, (this.width / 2) - 110, 140, "Show Lore of items in Item Guide", 16));
		pageButtons.add(tglItemGuideBoxRarity = new ToggleButton(1205, (this.width / 2) - 110, 155, "Colour Item Guide Items based on rarity", 16));
		pageButtons.add(tglItemGuidePurple = new ToggleButton(1206, (this.width / 2) - 110, 170, "Use Purple Item Guide instead", 15));
		
		pageButtons.add(tglShowTimeStamps = new ToggleButton(1300, (this.width / 2) - 110, 80, "Add Timestamps to chat messages", 17));
		pageButtons.add(tglShowSeconds = new ToggleButton(1301, (this.width / 2) - 110, 95, "Show Seconds in Timestamps", 17));
		pageButtons.add(tglPlainTimeStamp = new ToggleButton(1302, (this.width / 2) - 110, 110, "Use plain Timestamps", 17));
		pageButtons.add(tglTwelveHourTime = new ToggleButton(1303, (this.width / 2) - 110, 125, "Use twelve Hour time", 17));
		
		pageButtons.add(tglShowHealthHUD = new ToggleButton(1400, (this.width / 2) - 110, 80, "Show Health HUD for Party", 18));
		pageButtons.add(tglShowOutOfRange = new ToggleButton(1401, (this.width / 2) - 110, 95, "Show Out of Range Players", 18));
		pageButtons.add(tglAllignRight = new ToggleButton(1402, (this.width / 2) - 110, 110, "Align to Right side of screen", 18));
		pageButtons.add(tglColourHealthBar = new ToggleButton(1403, (this.width / 2) - 110, 125, "Use Coloured Health bars", 18));
		pageButtons.add(tglColourName = new ToggleButton(1404, (this.width / 2) - 110, 140, "Use Coloured Names", 18));
		pageButtons.add(tglShowPercentage = new ToggleButton(1405, (this.width / 2) - 110, 155, "Show Health Percentage", 18));
		pageButtons.add(tglUseProportionalDisplay = new ToggleButton(1406, (this.width / 2) - 110, 170, "Use Health Proportional Bars", 18));
		
		pageButtons.add(tglAttackShadowTerritory = new ToggleButton(1500, (this.width / 2) - 110, 80, "Add Shadow to Attack Territory", 19));
		pageButtons.add(tglAttackShadowTimer = new ToggleButton(1501, (this.width / 2) - 110, 95, "Add Shadow to Attack Timer", 19));
		pageButtons.add(tglAttackColourTimer = new ToggleButton(1502, (this.width / 2) - 110, 110, "Attack Timer change colour for time left", 19));
		
		
		this.addButton(btnOptions);
		this.addButton(btnExit);
		
		this.addButton(btnNextPage);
		this.addButton(btnPrevPage);
		
		this.addButton(btnModPage);
		this.addButton(btnEgg);
		
		for (GuiButton btn : pageButtons) {
			this.addButton(btn);
			if (((ToggleButton) btn).pageNumber > maxPages) maxPages = ((ToggleButton) btn).pageNumber;
		}
		
		RedrawButtons();
	}
	
	@Override
	public void updateScreen() {
		if (refreshDelay.Passed()) {
			Config.Refresh();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);

		this.drawTexturedModalRect((this.width / 2) - 128, 5, 768, 256, 256, 193);

		tglDisplayName.active = WorldItemName.DisplayName;
		tglUseLegacyExperience.active = WynnExpansion.UseLegacyExperience;
		tglEnableSidebar.active = ExperienceUI.EnableSidebar;
		tglEnableScrollingSidebar.active = ExperienceUI.EnableScrollingSidebar;
		tglPotionShiftOff.active = WynnExpansion.PotionShiftOff;
		tglDisableFOV.active = WynnExpansion.DisableFOV;
		
		tgltipDisabler.active = ChatManipulator.tipDisabler;
		tglTerritoryNews.active = WynnExpansion.TerritoryNews;
		tglCompactNews.active = Territory.CompactNews;
		tglOptimiseWar.active = WynnExpansion.OptimiseWar;
		tglCompass.active = CompassUI.Compass;
		tglCompassType.active = CompassUI.CompassType;
		
		tglNameParty.active = PlayerGlow.NameParty;
		tglNameFriends.active = PlayerGlow.NameFriends;
		tglNameGuild.active = PlayerGlow.NameGuild;
		tglBumpHelperName.active = PlayerGlow.BumpHelperName;
		tglNameDisguises.active = PlayerGlow.NameDisguises;
		
		tglGlowParty.active = PlayerGlow.GlowParty;
		tglGlowFriends.active = PlayerGlow.GlowFriends;
		tglGlowGuild.active = PlayerGlow.GlowGuild;
		tglGlowHelpers.active = PlayerGlow.GlowHelpers;
		tglBumpHelperGlow.active = PlayerGlow.BumpHelperGlow;
		tglHighlightDisguises.active = PlayerGlow.HighlightDisguises;
		
		tglExpAboveHealth.active = ExperienceUI.ExpAboveHealth;
		tglUseExpInstead.active = ExperienceUI.UseExpInstead;
		tglExpFlowPercentage.active = ExperienceUI.ExpFlowPercentage;
		tglExpFlowShowNames.active = ExperienceUI.ExpFlowShowNames;
		tglExpFlowShowLevel.active = ExperienceUI.ExpFlowShowLevel;
		tglExpFlowSlow.active = ExperienceUI.ExpFlowSlow;
		tglExpFlowSmall.active = ExperienceUI.ExpFlowSmall;
		
		tglShowSpellCastingHUD.active = SpellCastingUI.ShowSpellCastingHUD;
		tglStaticBarShadow.active = ExperienceUI.StaticBarShadow;
		tglSideBarHeaderShadow.active = ExperienceUI.SideBarHeaderShadow;
		tglSideBarFeedShadow.active = ExperienceUI.SideBarFeedShadow;
		
		tglSmartDeobfuscate.active = WorldItemName.SmartDeobfuscate;
		tglShortEmerald.active = WorldItemName.ShortEmerald;
		tglShortPotions.active = WorldItemName.ShortPotions;
		tglShortPowders.active = WorldItemName.ShortPowders;
		tglShortItems.active = WorldItemName.ShortItems;
		
		tglNameEmerald.active = WorldItemName.NameEmerald;
		tglNamePotions.active = WorldItemName.NamePotions;
		tglNamePowders.active = WorldItemName.NamePowders;
		tglNameKeyScroll.active = WorldItemName.NameKeyScroll;
		tglNameMisc.active = WorldItemName.NameMisc;
		tglNameJunk.active = WorldItemName.NameJunk;
		
		tglNameMythic.active = WorldItemName.NameMythic;
		tglNameLegendary.active = WorldItemName.NameLegendary;
		tglNameRare.active = WorldItemName.NameRare;
		tglNameUnique.active = WorldItemName.NameUnique;
		tglNameNormal.active = WorldItemName.NameNormal;
		tglNameSet.active = WorldItemName.NameSet;
		
		tglAnnounceMythic.active = WorldItemName.AnnounceMythic;
		tglMythicSound.active = WorldItemName.MythicSound;
		tglAnnounceLegendary.active = WorldItemName.AnnounceLegendary;
		
		tglHighlightMythic.active = WorldItemName.HighlightMythic;
		tglHighlightLegendary.active = WorldItemName.HighlightLegendary;
		tglHighlightRare.active = WorldItemName.HighlightRare;
		tglHighlightUnique.active = WorldItemName.HighlightUnique;
		tglHighlightSet.active = WorldItemName.HighlightSet;
		
		tglDailyChestReminder.active = DailyChestReminder.DailyChestReminder;
		tglShowSkillpoints.active = SkillpointUI.ShowSkillpoints;
		tglSoulPointTime.active = SoulpointTime.SoulPointTime;
		tglInfoOverrideFind.active = WynnExpansion.InfoOverrideFind;
		tglHeaderVersion.active = WynnExpansion.HeaderVersion;
		tglShowTPS.active = WynnExpansion.ShowTPS;
		
		tglAttackTimer.active = GuildAttack.AttackTimer;
		tglRemoveExcessWar.active = GuildAttack.RemoveExcessWar;
		tglGuildWarWaypoints.active = GuildAttack.GuildWarWaypoints;
		tglWarHornWarmup.active = GuildAttack.WarHornWarmup;
		tglWarHornAttack.active = GuildAttack.WarHornAttack;
		tglEnableWarTimer.active = WarTimer.EnableWarTimer;
		tglWarTimerLeft.active = WarTimer.WarTimerLeft;
		
		tglWarHealth.active = PlayerGlow.WarHealth;
		tglGlowVeryLowHealth.active = PlayerGlow.GlowVeryLowHealth;
		tglGlowLowHealth.active = PlayerGlow.GlowLowHealth;
		tglGlowMidHealth.active = PlayerGlow.GlowMidHealth;
		tglGlowHighHealth.active = PlayerGlow.GlowHighHealth;
		tglGlowVeryHighHealth.active = PlayerGlow.GlowVeryHighHealth;
		
		tglPotionEnabled.active = WynnExpansion.PotionEnabled;
		tglPotionShadow.active = PotionDisplay.PotionShadow;
		tglPotionHideTimer.active = PotionDisplay.PotionHideTimer;
		tglPotionAllignRight.active = PotionDisplay.PotionAllignRight;
		tglPotionCenterVerticaly.active = PotionDisplay.PotionCenterVerticaly;
		
		tglPlayerCollision.active = PlayerGlow.PlayerCollision;
		tglKillStreak.active = ExperienceUI.KillStreak;
		tglKillPerMinute.active = ExperienceUI.KillPerMinute;
		tglSearchBoxActiveDefault.active = ItemGuideGUI.SearchBoxActiveDefault;
		tglItemGuideShowLore.active = ItemGuideGUI.ItemGuideShowLore;
		tglItemGuideBoxRarity.active = ItemGuideGUI.ItemGuideBoxRarity;
		tglItemGuidePurple.active = ItemGuideGUI.ItemGuidePurple;
		
		tglShowTimeStamps.active = ChatTimeStamp.ShowTimeStamps;
		tglShowSeconds.active = ChatTimeStamp.ShowSeconds;
		tglPlainTimeStamp.active = ChatTimeStamp.PlainTimeStamp;
		tglTwelveHourTime.active = ChatTimeStamp.TwelveHourTime;
		
		tglShowHealthHUD.active = PartyHUD.ShowHealthHUD;
		tglShowOutOfRange.active = PartyHUD.ShowOutOfRange;
		tglAllignRight.active = PartyHUD.AllignRight;
		tglColourHealthBar.active = PartyHUD.ColourHealthBar;
		tglColourName.active = PartyHUD.ColourName;
		tglShowPercentage.active = PartyHUD.ShowPercentage;
		tglUseProportionalDisplay.active = PartyHUD.UseProportionalDisplay;
		
		tglAttackShadowTerritory.active = GuildAttackTimer.AttackShadowTerritory;
		tglAttackShadowTimer.active = GuildAttackTimer.AttackShadowTimer;
		tglAttackColourTimer.active = GuildAttackTimer.AttackColourTimer;

		this.drawCenteredStringPlain(mc.fontRenderer, page + "/" + maxPages, this.width / 2, 53, Integer.parseInt("858585", 16));
// EASTER EGG HERE
		if (EnableEgg){
			btnEgg.visible = true;
			if (Update.newUpdate){
				this.drawStringPlain(mc.fontRenderer, ExpReference.VERSION, (this.width / 2) - 109, 15, 0.9f, Integer.parseInt("FFA700", 16));
				this.drawStringPlain(mc.fontRenderer, "New Update:", (this.width / 2) - 115, 25, 0.9f, Integer.parseInt("FFA700", 16));
				this.drawStringPlain(mc.fontRenderer, "v" + Update.latest, (this.width / 2) - 115, 35, 0.9f, Integer.parseInt("FFA700", 16));
			}else{
				this.drawStringPlain(mc.fontRenderer, ExpReference.VERSION, (this.width / 2) - 109, 15, 0.9f, Integer.parseInt("FFA700", 16));
			}
		}else{
			btnEgg.visible = false;
			if (Update.newUpdate){
				this.drawStringPlain(mc.fontRenderer, "v" + ExpReference.VERSION, (this.width / 2) - 115, 15, 0.9f, Integer.parseInt("FFA700", 16));
				this.drawStringPlain(mc.fontRenderer, "New Update:", (this.width / 2) - 115, 25, 0.9f, Integer.parseInt("FFA700", 16));
				this.drawStringPlain(mc.fontRenderer, "v" + Update.latest, (this.width / 2) - 115, 35, 0.9f, Integer.parseInt("FFA700", 16));
			}else{
				this.drawStringPlain(mc.fontRenderer, "v" + ExpReference.VERSION, (this.width / 2) - 115, 15, 0.9f, Integer.parseInt("FFA700", 16));
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void RedrawButtons() {
		for (GuiButton btn : pageButtons) {
			if (((ToggleButton) btn).pageNumber == page) {
				btn.enabled = true;
				btn.visible = true;
			} else {
				btn.enabled = false;
				btn.visible = false;
			}
		}
		if (ExpReference.inWar()){
		btnOptions.visible = false;
		}
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			switch (button.id) {
			case -1:
				mc.displayGuiScreen(null);
				break;
			case 0:
				mc.displayGuiScreen(new GuiQuestBook());
				break;
			case 1:
				if (page + 1 > maxPages) {
					page = 1;
				} else {
					page++;
				}
				RedrawButtons();
				break;
			case 2:
				if (page - 1 < 1) {
					page = maxPages;
				} else {
					page--;
				}
				RedrawButtons();
				break;
			case 3:
				try {
					Desktop.getDesktop().browse(new URI("https://forums.wynncraft.com/threads/unofficial-wynn-expansion-a-continuation-of-shsupersms-wynncraft-mod.189153"));
				} catch (Exception ignored) {
				}
				break;
			case 4:
				mc.displayGuiScreen(new EggPage());
				break;
			case 100: Config.setModule("DisplayName", !WorldItemName.DisplayName); RedrawButtons(); break;
			case 101: Config.setModule("UseLegacyExperience", !WynnExpansion.UseLegacyExperience); RedrawButtons(); break;
			case 102: Config.setModule("EnableSidebar", !ExperienceUI.EnableSidebar); RedrawButtons(); break;
			case 103: Config.setModule("EnableScrollingSidebar", !ExperienceUI.EnableScrollingSidebar); RedrawButtons(); break;
			case 104: Config.setModule("PotionShiftOff", !WynnExpansion.PotionShiftOff); RedrawButtons(); break;
			case 105: Config.setModule("DisableFOV", !WynnExpansion.DisableFOV); RedrawButtons(); break;
			
			case 200: Config.setModule("tipDisabler", !ChatManipulator.tipDisabler); RedrawButtons(); break;
			case 201: Config.setModule("TerritoryNews", !WynnExpansion.TerritoryNews); RedrawButtons(); break;
			case 202: Config.setModule("CompactNews", !Territory.CompactNews); RedrawButtons(); break;
			case 203: Config.setModule("OptimiseWar", !WynnExpansion.OptimiseWar); RedrawButtons(); break;
			case 204: Config.setModule("Compass", !CompassUI.Compass); RedrawButtons(); break;
			case 205: Config.setModule("CompassType", !CompassUI.CompassType); RedrawButtons(); break;
			
			case 300: Config.setModule("NameParty", !PlayerGlow.NameParty); RedrawButtons(); break;
			case 301: Config.setModule("NameFriends", !PlayerGlow.NameFriends); RedrawButtons(); break;
			case 302: Config.setModule("NameGuild", !PlayerGlow.NameGuild); RedrawButtons(); break;
			case 305: Config.setModule("BumpHelperName", !PlayerGlow.BumpHelperName); RedrawButtons(); break;
			case 306: Config.setModule("NameDisguises", !PlayerGlow.NameDisguises); RedrawButtons(); break;
			
			case 320: Config.setModule("GlowParty", !PlayerGlow.GlowParty); RedrawButtons(); break;
			case 321: Config.setModule("GlowFriends", !PlayerGlow.GlowFriends); RedrawButtons(); break;
			case 322: Config.setModule("GlowGuild", !PlayerGlow.GlowGuild); RedrawButtons(); break;
			case 323: Config.setModule("GlowHelpers", !PlayerGlow.GlowHelpers); RedrawButtons(); break;
			case 325: Config.setModule("BumpHelperGlow", !PlayerGlow.BumpHelperGlow); RedrawButtons(); break;
			case 326: Config.setModule("HighlightDisguises", !PlayerGlow.HighlightDisguises); RedrawButtons(); break;
			
			case 400: Config.setModule("ExpAboveHealth", !ExperienceUI.ExpAboveHealth); RedrawButtons(); break;
			case 401: Config.setModule("UseExpInstead", !ExperienceUI.UseExpInstead); RedrawButtons(); break;
			case 402: Config.setModule("ExpFlowPercentage", !ExperienceUI.ExpFlowPercentage); RedrawButtons(); break;
			case 403: Config.setModule("ExpFlowShowNames", !ExperienceUI.ExpFlowShowNames); RedrawButtons(); break;
			case 404: Config.setModule("ExpFlowShowLevel", !ExperienceUI.ExpFlowShowLevel); RedrawButtons(); break;
			case 405: Config.setModule("ExpFlowSlow", !ExperienceUI.ExpFlowSlow); RedrawButtons(); break;
			case 406: Config.setModule("ExpFlowSmall", !ExperienceUI.ExpFlowSmall); RedrawButtons(); break;
			
			case 450: Config.setModule("ShowSpellCastingHUD", !SpellCastingUI.ShowSpellCastingHUD); RedrawButtons(); break;
			case 451: Config.setModule("StaticBarShadow", !ExperienceUI.StaticBarShadow); RedrawButtons(); break;
			case 452: Config.setModule("SideBarHeaderShadow", !ExperienceUI.SideBarHeaderShadow); RedrawButtons(); break;
			case 453: Config.setModule("SideBarFeedShadow", !ExperienceUI.SideBarFeedShadow); RedrawButtons(); break;
			
			case 500: Config.setModule("SmartDeobfuscate", !WorldItemName.SmartDeobfuscate); RedrawButtons(); break;
			case 502: Config.setModule("ShortEmerald", !WorldItemName.ShortEmerald); RedrawButtons(); break;
			case 503: Config.setModule("ShortPotions", !WorldItemName.ShortPotions); RedrawButtons(); break;
			case 504: Config.setModule("ShortPowders", !WorldItemName.ShortPowders); RedrawButtons(); break;
			case 505: Config.setModule("ShortItems", !WorldItemName.ShortItems); RedrawButtons(); break;
			
			case 600: Config.setModule("NameEmerald", !WorldItemName.NameEmerald); RedrawButtons(); break;
			case 601: Config.setModule("NamePotions", !WorldItemName.NamePotions); RedrawButtons(); break;
			case 602: Config.setModule("NamePowders", !WorldItemName.NamePowders); RedrawButtons(); break;
			case 603: Config.setModule("NameKeyScroll", !WorldItemName.NameKeyScroll); RedrawButtons(); break;
			case 604: Config.setModule("NameMisc", !WorldItemName.NameMisc); RedrawButtons(); break;
			case 605: Config.setModule("NameJunk", !WorldItemName.NameJunk); RedrawButtons(); break;
			
			case 700: Config.setModule("NameMythic", !WorldItemName.NameMythic); RedrawButtons(); break;
			case 701: Config.setModule("NameLegendary", !WorldItemName.NameLegendary); RedrawButtons(); break;
			case 702: Config.setModule("NameRare", !WorldItemName.NameRare); RedrawButtons(); break;
			case 703: Config.setModule("NameUnique", !WorldItemName.NameUnique); RedrawButtons(); break;
			case 704: Config.setModule("NameNormal", !WorldItemName.NameNormal); RedrawButtons(); break;
			case 705: Config.setModule("NameSet", !WorldItemName.NameSet); RedrawButtons(); break;
			
			case 800: Config.setModule("AnnounceMythic", !WorldItemName.AnnounceMythic); RedrawButtons(); break;
			case 801: Config.setModule("MythicSound", !WorldItemName.MythicSound); RedrawButtons(); break;
			case 802: Config.setModule("AnnounceLegendary", !WorldItemName.AnnounceLegendary); RedrawButtons(); break;
			
			case 900: Config.setModule("HighlightMythic", !WorldItemName.HighlightMythic); RedrawButtons(); break;
			case 901: Config.setModule("HighlightLegendary", !WorldItemName.HighlightLegendary); RedrawButtons(); break;
			case 902: Config.setModule("HighlightRare", !WorldItemName.HighlightRare); RedrawButtons(); break;
			case 903: Config.setModule("HighlightUnique", !WorldItemName.HighlightUnique); RedrawButtons(); break;
			case 904: Config.setModule("HighlightSet", !WorldItemName.HighlightSet); RedrawButtons(); break;
			
			case 1000: Config.setModule("DailyChestReminder", !DailyChestReminder.DailyChestReminder); RedrawButtons(); break;
			case 1001: Config.setModule("ShowSkillpoints", !SkillpointUI.ShowSkillpoints); RedrawButtons(); break;
			case 1002: Config.setModule("SoulPointTime", !SoulpointTime.SoulPointTime); RedrawButtons(); break;
			case 1022: Config.setModule("InfoOverrideFind", !WynnExpansion.InfoOverrideFind); RedrawButtons(); break;
			case 1024: Config.setModule("HeaderVersion", !WynnExpansion.HeaderVersion); ModCore.mc().ingameGUI.getTabList().resetFooterHeader(); RedrawButtons(); break;
			case 1025: Config.setModule("ShowTPS", !WynnExpansion.ShowTPS); ModCore.mc().ingameGUI.getTabList().resetFooterHeader(); RedrawButtons(); break;
			
			case 1003: Config.setModule("AttackTimer", !GuildAttack.AttackTimer); if (!GuildAttack.AttackTimer) {Config.setModule("RemoveExcessWar", false);} RedrawButtons(); break;
			case 1004: Config.setModule("RemoveExcessWar", !GuildAttack.RemoveExcessWar); if (GuildAttack.RemoveExcessWar) {Config.setModule("AttackTimer", true);} RedrawButtons(); break;
			case 1005: Config.setModule("GuildWarWaypoints", !GuildAttack.GuildWarWaypoints); RedrawButtons(); break;
			case 1006: Config.setModule("WarHornWarmup", !GuildAttack.WarHornWarmup); RedrawButtons(); break;
			case 1007: Config.setModule("WarHornAttack", !GuildAttack.WarHornAttack); RedrawButtons(); break;
			case 1008: Config.setModule("EnableWarTimer", !WarTimer.EnableWarTimer); RedrawButtons(); break;
			case 1009: Config.setModule("WarTimerLeft", !WarTimer.WarTimerLeft); RedrawButtons(); break;
			
			case 1150: Config.setModule("WarHealth", !PlayerGlow.WarHealth); RedrawButtons(); break;
			case 1151: Config.setModule("GlowVeryLowHealth", !PlayerGlow.GlowVeryLowHealth); RedrawButtons(); break;
			case 1152: Config.setModule("GlowLowHealth", !PlayerGlow.GlowLowHealth); RedrawButtons(); break;
			case 1153: Config.setModule("GlowMidHealth", !PlayerGlow.GlowMidHealth); RedrawButtons(); break;
			case 1154: Config.setModule("GlowHighHealth", !PlayerGlow.GlowHighHealth); RedrawButtons(); break;
			case 1155: Config.setModule("GlowVeryHighHealth", !PlayerGlow.GlowVeryHighHealth); RedrawButtons(); break;
			
			case 1100: Config.setModule("PotionEnabled", !WynnExpansion.PotionEnabled); RedrawButtons(); break;
			case 1101: Config.setModule("PotionShadow", !PotionDisplay.PotionShadow); RedrawButtons(); break;
			case 1102: Config.setModule("PotionHideTimer", !PotionDisplay.PotionHideTimer); RedrawButtons(); break;
			case 1103: Config.setModule("PotionAllignRight", !PotionDisplay.PotionAllignRight); RedrawButtons(); break;
			case 1104: Config.setModule("PotionCenterVerticaly", !PotionDisplay.PotionCenterVerticaly); RedrawButtons(); break;
			
			case 1200: Config.setModule("PlayerCollision", !PlayerGlow.PlayerCollision); RedrawButtons(); break;
			case 1201: Config.setModule("KillStreak", !ExperienceUI.KillStreak); RedrawButtons(); break;
			case 1202: Config.setModule("KillPerMinute", !ExperienceUI.KillPerMinute); RedrawButtons(); break;
			case 1203: Config.setModule("SearchBoxActiveDefault", !ItemGuideGUI.SearchBoxActiveDefault); RedrawButtons(); break;
			case 1204: Config.setModule("ItemGuideShowLore", !ItemGuideGUI.ItemGuideShowLore); RedrawButtons(); break;
			case 1205: Config.setModule("ItemGuideBoxRarity", !ItemGuideGUI.ItemGuideBoxRarity); RedrawButtons(); break;
			case 1206: Config.setModule("ItemGuidePurple", !ItemGuideGUI.ItemGuidePurple); RedrawButtons(); break;
			
			case 1300: Config.setModule("ShowTimeStamps", !ChatTimeStamp.ShowTimeStamps); RedrawButtons(); break;
			case 1301: Config.setModule("ShowSeconds", !ChatTimeStamp.ShowSeconds); RedrawButtons(); break;
			case 1302: Config.setModule("PlainTimeStamp", !ChatTimeStamp.PlainTimeStamp); RedrawButtons(); break;
			case 1303: Config.setModule("TwelveHourTime", !ChatTimeStamp.TwelveHourTime); RedrawButtons(); break;
			
			case 1400: Config.setModule("ShowHealthHUD", !PartyHUD.ShowHealthHUD); RedrawButtons(); break;
			case 1401: Config.setModule("ShowOutOfRange", !PartyHUD.ShowOutOfRange); RedrawButtons(); break;
			case 1402: Config.setModule("AllignRight", !PartyHUD.AllignRight); RedrawButtons(); break;
			case 1403: Config.setModule("ColourHealthBar", !PartyHUD.ColourHealthBar); if (PartyHUD.ColourName) {Config.setModule("ColourName", false);} RedrawButtons(); break;
			case 1404: Config.setModule("ColourName", !PartyHUD.ColourName); if (PartyHUD.ColourHealthBar) {Config.setModule("ColourHealthBar", false);} RedrawButtons(); break;
			case 1405: Config.setModule("ShowPercentage", !PartyHUD.ShowPercentage); RedrawButtons(); break;
			case 1406: Config.setModule("UseProportionalDisplay", !PartyHUD.UseProportionalDisplay); RedrawButtons(); break;
			
			case 1500: Config.setModule("AttackShadowTerritory", !GuildAttackTimer.AttackShadowTerritory); RedrawButtons(); break;
			case 1501: Config.setModule("AttackShadowTimer", !GuildAttackTimer.AttackShadowTimer); RedrawButtons(); break;
			case 1502: Config.setModule("AttackColourTimer", !GuildAttackTimer.AttackColourTimer); RedrawButtons(); break;
			
			}
		}
		Config.Refresh();
	}

	@Override
	protected String GetButtonTooltip(int buttonId) {
		switch (buttonId) {
		case 0:
			return "Back to Quest Book";
		case 1:
			return "Next Page";
		case 2:
			return "Previous Page";
		case 3:
			return "Open Mod Thread";
		}
		return null;
	}

	static class OptionsButton extends GuiButton {

		public OptionsButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 14, 14, "");
		}

		public OptionsButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);

				this.drawTexturedModalRect(this.x, this.y, 226, 194 + (hover ? 14 : 0), 14, 14);
			}
		}
	}

	static class ChangePageButton extends GuiButton {
		private boolean right = true;

		public ChangePageButton(int buttonId, int x, int y, boolean right) {
			super(buttonId, x, y, 27, 14, "");
			this.right = right;
		}

		public ChangePageButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);

				int textureX = (this.right ? 28 : 0);
				int textureY = (hover ? 194 : 208);

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 28, 14);
			}
		}
	}

	static class ToggleButton extends GuiButton {

		public ToggleButton(int buttonId, int x, int y, String text, int pageNumber) {
			super(buttonId, x, y, 8, 14, "");
			this.text = text;
			this.pageNumber = pageNumber;
		}

		public ToggleButton() {
			super(-1, -1, -1, "");
		}

		String text = "";
		public int pageNumber = 0;
		public boolean active = false;

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible && this.pageNumber == page) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);

				this.drawTexturedModalRect(this.x, this.y, 240 + (active ? 8 : 0), 194 + (hover ? 14 : 0), 8, 14);

				this.drawStringPlain(mc.fontRenderer, text, this.x + 10, this.y + 2, Integer.parseInt("858585", 16));

			}
		}

		private void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color) {
			fontRendererIn.drawString(text, x, y, color);
		}
	}

	static class ModPageButton extends GuiButton {

		public ModPageButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 27, 14, "");
		}

		public ModPageButton() {
			super(-1, -1, -1, "");
		}

		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);

				int textureX = 56;
				int textureY = (hover ? 194 : 208);

				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 28, 14);
			}
		}
	}
	
	static class EggButton extends GuiButton {
		
		public EggButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 6, 10, "");
		}
		
		public EggButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				//boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				this.drawStringPlain(mc.fontRenderer, "v", this.x, this.y, 0.9f, Integer.parseInt("FFA700", 16));
			}
		}
		
		public void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
			GL11.glScalef(size,size,size);
			float mSize = (float)Math.pow(size,-1);
			this.drawStringPlain(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
			GL11.glScalef(mSize,mSize,mSize);
		}
		private void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color) {
			fontRendererIn.drawString(text, x, y, color);
		}
	}
	
	static class ExitButton extends GuiButton {
		
		public ExitButton(int buttonId, int x, int y) {
			super(buttonId, x, y, 14, 14, "");
		}
		
		public ExitButton() {
			super(-1, -1, -1, "");
		}
		
		public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			if (this.visible) {
				boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
				
				this.drawTexturedModalRect(this.x, this.y, 242, 222 + (hover ? 14 : 0), 14, 14);
			}
		}
	}
}
