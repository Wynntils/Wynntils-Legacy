/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.events.ServerEvents;
import com.wynntils.modules.utilities.instances.SkillPointAllocation;
import com.wynntils.modules.utilities.managers.WindowIconManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

@SettingsInfo(name = "main", displayPath = "Utilities")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;

    @Setting(displayName = "Class & Hub Buttons", description = "Should 'Class Selection' and 'Back to Hub' buttons be displayed in the Game Menu?", order = 9)
    public boolean addClassHubButtons = true;

    @Setting(displayName = "Options & Profile Buttons", description = "Should 'Wynntils Option' and 'User Profile' buttons be displayed in the Game Menu?", order = 10)
    public boolean addOptionsProfileButtons = true;

    @Setting(displayName = "Daily Chest Reminder", description = "Should a message notifying that you can claim your daily chest be sent upon joining a world?", order = 7)
    public boolean dailyReminder = true;

    @Setting(displayName = "Hide Vanilla Potions Indicators", description = "Should the vanilla indicators for active potion effects be hidden?", order = 11)
    public boolean hidePotionGui = true;

    @Setting(displayName = "Hide Nametags Through Walls", description = "Should nametags be hidden when behind opaque blocks?", order = 12)
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags' Box", description = "Should the box around nametags be hidden?", order = 13)
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show Players' Armour", description = "Should the worn armour of players be listed underneath their nametag?", order = 8)
    public boolean showArmors = false;

    @Setting(displayName = "Mythic Chest Closing", description = "Should the closing of loot chests be prevented when they contain mythics?", order = 3)
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Favorited Item Chest Closing", description = "Should the closing of loot chests be prevented when they contain favorited items?", order = 4)
    public boolean preventFavoritedChestClose = true;

    @Setting(displayName = "Emerald Chest Closing", description = "How many emeralds should there be in a chest (in one stack) for Wynntils to prevent you from closing it?\n\n§8Set to 0 to disable this completely.", order = 4)
    @Setting.Limitations.IntLimit(min = 0, max = 32)
    public int preventEmeraldChestClose = 0;

    @Setting(displayName = "Favorited Item Close Times", description = "How many times should you click to force close a chest with favorite items in it?\n\n§8Setting this to 0 will disable this feature.", order = 4)
    @Setting.Limitations.IntLimit(min = 0, max = 10)
    public int preventFavoritedChestClosingAmount = 0;

    @Setting(displayName = "Clicking on Pouches in Chests", description = "Should opening ingredient and emerald pouches be blocked when opening loot chests?", order = 6)
    public boolean preventOpeningPouchesChest = true;

    @Setting(displayName = "Count Dry Streak", description = "Should the number of chests since your last discovered mythic in a chest be recorded?", order = 16)
    public boolean enableDryStreak = true;

    @Setting(displayName = "Dry Streak Ended Message", description = "Should there be a message when you find a mythic in a loot chest?", order = 17)
    public boolean dryStreakEndedMessage = true;

    @Setting
    public int dryStreakCount = 0;

    @Setting
    public int dryStreakBoxes = 0;

    @Setting(displayName = "Clicking on Locked Items", description = "Should moving items to and from locked inventory slots be prevented?", order = 5)
    public boolean preventSlotClicking = false;

    @Setting(displayName = "Bank Dump Behaviour", description = "What should happen when the bank inventory dump button is clicked?", order = 18)
    public BankButtonSetting bankDumpButton = BankButtonSetting.Confirm;

    @Setting(displayName = "Bank Quick Stash Behaviour", description = "What should happen when the bank quick stash button is clicked?", order = 19)
    public BankButtonSetting bankStashButton = BankButtonSetting.Confirm;

    @Setting(displayName = "FOV Scaling Function", description = "What scaling function should be used for speed-based FOV changes?", order = 21)
    public FovScalingFunction fovScalingFunction = FovScalingFunction.Vanilla;

    @Setting(displayName = "Show Guild Territory Search", description = "Should the search bar be shown in the guild manage territory GUI?\n\n§8Territories that match the search will be highlighted.", order = 22)
    public boolean showGuildTerritoryManageSearchbar = true;

    @Setting(displayName = "Searched Territory Colour", description = "What colour should the highlight for searched items be?\n\n§aClick the coloured box to open the colour wheel.", order = 23)
    public CustomColor guildTerritoryMenuSearchHighlightColor = new CustomColor(80, 242, 242);

    @Setting(displayName = "Show Guild Member Search", description = "Should the search bar be shown in the guild manage members GUI?\n\n§8Members that match the search will be highlighted.", order = 24)
    public boolean showGuildMemberManageSearchbar = true;

    @Setting(displayName = "Searched Member Colour", description = "What colour should the highlight for searched items be?\n\n§aClick the coloured box to open the colour wheel.", order = 25)
    public CustomColor guildMemberMenuSearchHighlightColor = new CustomColor(80, 242, 242);

    @Setting(displayName = "Auto Mount Horse", description = "Should you mount your horse automatically when it is summoned?", order = 3)
    public boolean autoMount = false;

    @Setting(displayName = "Potion Blocking Type", description = "How should the potion blocking feature function?\n\n§7HealthPercent §8- Block potions when health is above %\n§7EffectivePercent §8- Block potions if they are not at least % effective\n§7Never §8- Do not block health potions\n§7Always §8- Always block health potions", order = 1)
    public PotionBlockingType potionBlockingType = PotionBlockingType.HealthPercent;

    @Setting.Limitations.IntLimit(min = 0, max = 100)
    @Setting(displayName = "Health Potion Blocking Threshold", description = "When you have equal or more health than the percentage specified here, Wynntils will block health potions.\n\n§8Set to 0 to disable.", order = 2)
    public int blockHealingPotThreshold = 99;

    @Setting(displayName = "Apply Resource Pack", description = "Should the Wynncraft server resource pack be applied when joining the server instead of selecting your class?", order = 29)
    public boolean autoResource = true;

    @Setting(displayName = "Startup Resource Pack", description = "Should the Wynncraft server resource pack be applied when starting Minecraft?", order = 30)
    public boolean autoResourceOnLoad = false;

    @Setting(displayName = "Change Window Title", description = "Should the mod change the window title to \"Wynncraft\" while on the server?", order = 31)
    public boolean changeWindowTitle = true;

    @Setting(displayName = "Change Window Icon", description = "Should the mod change the window icon to the Wynncraft logo while on the server?\n\n§8This does not work on macOS systems.", order = 32)
    public boolean changeWindowIcon = true;

    @Setting(displayName = "Show Tooltips From Top", description = "Should tooltips be rendered from the top?", order = 27)
    public boolean renderTooltipsFromTop = true;

    @Setting(displayName = "Scale Tooltips", description = "Should tooltips be scaled down so that they fit on your screen?", order = 28)
    public boolean renderTooltipsScaled = false;

    @Setting(displayName = "Show Leaderboard Badges", description = "Should leaderboard players have badges above their heads?", order = 22)
    public boolean renderLeaderboardBadges = true;

    @Setting(displayName = "Prevent Trades & Duels", description = "Should trade and duel requests be disabled while holding an item?\n\n§8Items blocked include weapons, all consumables, horses, compass, and quest book", order = 20)
    public boolean preventTradesDuels = false;

    @Setting(displayName = "Bulk Buy on Shift-Click", description = "Should the option to buy scrolls and potions in bulk while holding shift be available?", order = 14)
    public boolean shiftBulkBuy = true;

    @Setting.Limitations.IntLimit(min = 2, max = 16)
    @Setting(displayName = "Bulk Buy Amount", description = "How many items should be bought when purchasing in bulk?", order = 15)
    public int bulkBuyAmount = 3;

    @Setting(displayName = "Show Death Coordinates", description = "Upon death, should a message be sent containing the coordinates of where you died?", order = 23)
    public boolean deathMessageWithCoords = true;

    @Setting(displayName = "Show Emerald Count in Containers", description = "Should your emerald count be displayed in remote containers?\n\n§8Remote containers are items such as chests and emerald pouches.", order = 24)
    public boolean emeraldCountChest = true;

    @Setting(displayName = "Show Emerald Count in Inventory", description = "Should your emerald count be displayed in your inventory?", order = 25)
    public boolean emeraldCountInventory = true;

    @Setting(displayName = "Show Emerald Count as Text", description = "Should your emerald count be displayed as text instead of icons?", order = 26)
    public boolean emeraldCountText = false;

    @Setting(displayName = "Show consumable charges in hotbar", description = "Should potion, food, and scroll charges be shown in the hotbar?", order = 28)
    public boolean showConsumableChargesHotbar = true;

    @Setting(displayName = "Sort Ingredient Pouch Method", description = "How should the ingredient pouch overview be sorted?\n\n§8Click on the ingredient pouch to refresh.", order = 33)
    public IngPouchSortType sortIngredientPouch = IngPouchSortType.Rarity;

    @Setting(displayName = "Reverse Ingredient Pouch Order", description = "Should the ingredient pouch sort order be reversed?\n\n§8Click on the ingredient pouch to refresh.", order = 34)
    public boolean sortIngredientPouchReverse = false;

    @Setting(upload = false)
    public String lastServerResourcePack = "";

    @Setting(upload = false)
    public String lastServerResourcePackHash = "";

    @Setting
    public Map<Integer, Set<Integer>> locked_slots = new HashMap<>();

    @Setting
    public Map<String, SkillPointAllocation> skillPointLoadouts = new HashMap<>();

    @Setting
    public List<String> favoriteItems = new ArrayList<>();

    @Setting
    public List<String> favoriteIngredients = new ArrayList<>();

    @Setting
    public List<String> favoriteEmeraldPouches = new ArrayList<>();

    @Setting
    public List<String> favoritePowders = new ArrayList<>();

    public enum FovScalingFunction {
        Vanilla,
        Arctangent,
        Sprint_Only,
        None
    }

    public enum BankButtonSetting {
        Default,
        Confirm,
        Block
    }

    public enum PotionBlockingType {
        HealthPercent,
        EffectivePercent,
        Never
    }

    public enum IngPouchSortType {
        Rarity,
        Quantity,
        Alphabetical
    }

    @SettingsInfo(name = "identifications", displayPath = "Utilities/Identifications")
    public static class Identifications extends SettingsClass {
        public static Identifications INSTANCE;

        @Setting(displayName = "Show Advanced Identifications", description = "Should items show advanced identifications?", order = 0)
        public boolean enabled = true;

        @Setting(displayName = "Identifications Decimal Places", description = "How many decimal places should advanced identifications have?\n\n§8This requires your inventory to be reloaded to update. To do so, open the bank once.")
        public IdentificationDecimalPlaces decimalPlaces = IdentificationDecimalPlaces.Zero;

        @Setting(displayName = "Show Item Identification Stars", description = "Should star ratings of stats be shown on items?")
        public boolean addStars = true;

        @Setting(displayName = "Legacy Identification Values", description = "Should mana and life steal stats display as /4s instead of /3s and /5s?")
        public boolean legacyIds = false;

        @Setting(displayName = "Animated Item Names", description = "Should perfect and defective items have animated names?\n\n§8This includes perfect items having rainbow names.")
        public boolean rainbowPerfect = true;

        @Setting.Limitations.FloatLimit(min = 1.0f, max = 20.0f, precision = 1.0f)
        @Setting(displayName = "Defect Obfuscation Percentage", description = "How much should defective item names be obfuscated?\n\n§8Obfuscation is where the characters constantly change and appear corrupted.")
        public float defectiveObfuscationAmount = 8.0f;

        @Setting(displayName = "Categorize Identifications", description = "Should the identifications in an item's tooltip be categorized?")
        public boolean addSpacing = true;

        @Setting(displayName = "Categorize Set Bonuses", description = "Should the set bonus in an item's tooltip be categorized?")
        public boolean addSetBonusSpacing = true;

        @Setting(displayName = "Show Item Reroll Price", description = "Should the reroll price be displayed in your item?")
        public boolean showRerollPrice = true;

        @Setting(displayName = "Unidentified Item Guesses", description = "Should guesses for your unidentified items be displayed?")
        public boolean showItemGuesses = true;

        @Setting(displayName = "Identification Price Guesses", description = "Should guesses for prices of identifying unidentified items be displayed?\n\n§8This requires your inventory to be reloaded to update. To do so, open the bank once.")
        public boolean showGuessesPrice = false;

        public enum IdentificationDecimalPlaces {
            Zero("0"),
            One("0.0"),
            Two("0.00"),
            Three("0.000"),
            Four("0.0000");

            DecimalFormat df;

            IdentificationDecimalPlaces(String format) {
                df = new DecimalFormat(format);
                df.setRoundingMode(RoundingMode.DOWN);
            }

            public String format(double number) {
                return df.format(number);
            }
        }

    }

    @SettingsInfo(name = "afk", displayPath = "Utilities/AFK Protection")
    public static class AfkProtection extends SettingsClass {
        public static AfkProtection INSTANCE;
        @Setting(displayName = "AFK Protection", description = "Should you enter the class selection menu when you are AFK?")
        public boolean afkProtection = false;

        @Setting.Limitations.FloatLimit(min = 1f, max = 30f)
        @Setting(displayName = "Timer Threshold", description = "How many minutes of inactivity is required for AFK Protection to trigger?")
        public float afkProtectionThreshold = 10f;

        @Setting.Limitations.FloatLimit(min = 1f, max = 100f)
        @Setting(displayName = "Health Percentage Threshold", description = "At what percentage of health should AFK Protection be activated?")
        public float healthPercentage = 90f;

        @Setting(displayName = "Show on Hotbar", description = "Should AFK Protection status be shown on the hotbar?")
        public boolean showOnHotbar = true;
    }

    @SettingsInfo(name = "wars", displayPath = "Utilities/Wars")
    public static class Wars extends SettingsClass {
        public static Wars INSTANCE;

        @Setting(displayName = "Entity Filter", description = "Should the nametag of mobs be hidden in war servers?")
        public boolean allowEntityFilter = true;

        @Setting(displayName = "Show Player Health Bar", description = "Should the health bar of other players in a war server be displayed above their heads?")
        public boolean warrerHealthBar = true;

    }

    @SettingsInfo(name = "data", displayPath = "")
    public static class Data extends SettingsClass {
        public static Data INSTANCE;

        @Setting
        public long dailyReminder = 0L;

        @Setting
        public long lastOpenedDailyReward = 0L;
    }

    @SettingsInfo(name = "item_highlights", displayPath = "Utilities/Item Highlights")
    public static class Items extends SettingsClass {
        public static Items INSTANCE;

        @Setting(displayName = "Filter for Ingredients", description = "Should a filter for ingredients and crafted items be displayed?", order = 0)
        public boolean filterEnabled = true;

        @Setting(displayName = "Item Combat Level Arc", description = "Should the required combat level be shown behind items as an arc?", order = 1)
        public boolean itemLevelArc = false;

        @Setting(displayName = "Crafted Item Durability Arc", description = "Should the durability of crafted items be displayed with an arc?", order = 2)
        public boolean craftedDurabilityBars = true;

        @Setting(displayName = "Show Average Unidentified Level", description = "Should the average level of an unidentified item be shown instead of the entire range?", order = 3)
        public boolean averageUnidentifiedLevel = true;

        @Setting(displayName = "Shows Item Tiers", description = "Should the tier of powders, amplifiers, and pouches be shown when pressing the show item level key?", order = 5)
        public boolean levelKeyShowsItemTiers = false;

        @Setting(displayName = "Roman Numeral Item Tiers", description = "Should the tier of powders, amplifiers, and pouches be displayed using Roman numerals?", order = 6)
        public boolean romanNumeralItemTier = true;

        @Setting(displayName = "Item Levels Outside GUIs", description = "Should the item level overlay key be enabled even when no GUI is open?", order = 7)
        public boolean itemLevelOverlayOutsideGui = false;

        @Setting(displayName = "Dungeon Key Specification", description = "Should a letter indicating the destination of dungeon keys be displayed?", order = 8)
        public boolean keySpecification = true;

        @Setting(displayName = "Unidentified Item Type", description = "Should a symbol indicating the type of unidentified items be displayed?", order = 9)
        public boolean unidentifiedSpecification = true;

        @Setting(displayName = "Skill Potion Specification", description = "Should a symbol indicating the skill of skill potions be displayed?", order = 10)
        public boolean potionSpecification = true;

        @Setting(displayName = "Transportation Item Specification", description = "Should a letter indicating the destination of teleport scrolls and boat passes be displayed?", order = 11)
        public boolean transportationSpecification = true;

        @Setting(displayName = "Corkian Amplifier Specification", description = "Should the tier of a Corkian Amplifier be displayed?", order = 12)
        public boolean amplifierSpecification = true;

        @Setting(displayName = "Powder Specification", description = "Should the tier of powders be displayed?", order = 13)
        public boolean powderSpecification = true;

        @Setting(displayName = "Emerald Pouch Specification", description = "Should the tier of emerald pouches be displayed?", order = 14)
        public boolean emeraldPouchSpecification = true;

        @Setting(displayName = "Tier Overlay Size", description = "How large should the tier overlays of emerald pouches, powders, and amplifiers be?", order = 15)
        @Setting.Limitations.FloatLimit(min = 0.5f, max = 1)
        public float specificationTierSize = 1;

        @Setting(displayName = "Emerald Pouch Usage Arc", description = "Should emerald pouch usage be shown with an arc?", order = 16)
        public boolean emeraldPouchArc = true;

        @Setting(displayName = "Item Highlights in Containers", description = "Should items be highlighted according to rarity in remote containers?\n\n§8Remote containers are items such as chests and banks.", order = 17)
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item Highlights in Inventory", description = "Should items be highlighted according to rarity in your inventory?", order = 18)
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories Highlight", description = "Should your worn accessories be highlighted according to rarity?", order = 19)
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight Hotbar Items", description = "Should the items in your hotbar be highlighted according to rarity?", order = 20)
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight Armour Items", description = "Should your worn armour be highlighted according to rarity?", order = 21)
        public boolean armorHighlight = true;

        @Setting(displayName = "Highlight Mythics", description = "Should mythic items be highlighted?", order = 22)
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight Fabled", description = "Should fabled items be highlighted?", order = 23)
        public boolean fabledHighlight = true;

        @Setting(displayName = "Highlight Legendaries", description = "Should legendary items be highlighted?", order = 24)
        public boolean legendaryHighlight = true;

        @Setting(displayName = "Highlight Rares", description = "Should rare items be highlighted?", order = 25)
        public boolean rareHighlight = true;

        @Setting(displayName = "Highlight Uniques", description = "Should unique items be highlighted?", order = 26)
        public boolean uniqueHighlight = true;

        @Setting(displayName = "Highlight Set Items", description = "Should set items be highlighted?", order = 27)
        public boolean setHighlight = true;

        @Setting(displayName = "Highlight Normal Items", description = "Should normal items be highlighted?", order = 28)
        public boolean normalHighlight = false;

        @Setting(displayName = "Highlight Emeralds in chests", description = "Should emeralds be highlighted in chests?", order = 29)
        public boolean emeraldHighlightInChest = false;

        @Setting(displayName = "Highlight Crafted Items", description = "Should crafted items be highlighted?", order = 30)
        public boolean craftedHighlight = true;

        @Setting(displayName = "Highlight Black Market Cosmetics", description = "Should black market cosmetic items be highlighted?", order = 31)
        public boolean blackMarketEffectsHighlight = true;

        @Setting(displayName = "Highlight Godly Cosmetics", description = "Should godly cosmetic items be highlighted?", order = 32)
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "Highlight Epic Cosmetics", description = "Should epic cosmetic items be highlighted?", order = 33)
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "Highlight Rare Cosmetics", description = "Should rare cosmetic items be highlighted?", order = 34)
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "Highlight Common Cosmetics", description = "Should common cosmetic items be highlighted?", order = 35)
        public boolean commonEffectsHighlight = true;

        @Setting(displayName = "Highlight Crafting Ingredients", description = "Should crafting ingredients be highlighted according to their tier?", order = 36)
        public boolean ingredientHighlight = true;

        @Setting(displayName = "Minimum Ingredient Tier Highlight", description = "What should the minimum tier of crafting ingredients be for them to be highlighted?", order = 40)
        @Setting.Limitations.IntLimit(min = 1, max = 3)
        public int minCraftingIngredientHighlightTier = 1;

        @Setting(displayName = "Highlight Duplicate Cosmetics", description = "Should duplicate cosmetics be highlighted in the scrap menu?", order = 41)
        public boolean highlightCosmeticDuplicates = true;

        @Setting(displayName = "Minimum Powder Tier Highlight", description = "What should the minimum tier of powders be for it to be highlighted?\n\n§8Set the value to 0 to disable this setting.", order = 42)
        @Setting.Limitations.IntLimit(min = 0, max = 6)
        public int minPowderTier = 4;

        @Setting(displayName = "Emerald Highlight Colour", description = "What colour should the highlight for emeralds be?\n\n§aClick the coloured box to open the colour wheel.", order = 49)
        public CustomColor emeraldHighlightColor = new CustomColor(0, 150, 0);

        @Setting(displayName = "Profession Filter Highlight Colour", description = "What colour should the highlight for filtered ingredients be?\n\n§aClick the coloured box to open the colour wheel.", order = 50)
        public CustomColor professionFilterHighlightColor = new CustomColor(0.078f, 0.35f, 0.8f);

        @Setting(displayName = "Normal Item Highlight Colour", description = "What colour should the highlight for normal items be?\n\n§aClick the coloured box to open the colour wheel.", order = 51)
        public CustomColor normalHighlightColor = ItemTier.NORMAL.getDefaultHighlightColor();

        @Setting(displayName = "Unique Item Highlight Colour", description = "What colour should the highlight for unique items be?\n\n§aClick the coloured box to open the colour wheel.", order = 52)
        public CustomColor uniqueHighlightColor = ItemTier.UNIQUE.getDefaultHighlightColor();

        @Setting(displayName = "Rare Item Highlight Colour", description = "What colour should the highlight for rare items be?\n\n§aClick the coloured box to open the colour wheel.", order = 53)
        public CustomColor rareHighlightColor = ItemTier.RARE.getDefaultHighlightColor();

        @Setting(displayName = "Set Item Highlight Colour", description = "What colour should the highlight for set items be?\n\n§aClick the coloured box to open the colour wheel.", order = 54)
        public CustomColor setHighlightColor = ItemTier.SET.getDefaultHighlightColor();

        @Setting(displayName = "Legendary Item Highlight Colour", description = "What colour should the highlight for legendary items be?\n\n§aClick the coloured box to open the colour wheel.", order = 55)
        public CustomColor legendaryHighlightColor = ItemTier.LEGENDARY.getDefaultHighlightColor();

        @Setting(displayName = "Fabled Item Highlight Colour", description = "What colour should the highlight for fabled items be?\n\n§aClick the coloured box to open the colour wheel.", order = 56)
        public CustomColor fabledHighlightColor = ItemTier.FABLED.getDefaultHighlightColor();

        @Setting(displayName = "Mythic Item Highlight Colour", description = "What colour should the highlight for mythic items be?\n\n§aClick the coloured box to open the colour wheel.", order = 57)
        public CustomColor mythicHighlightColor = ItemTier.MYTHIC.getDefaultHighlightColor();

        @Setting(displayName = "Crafted Gear Colour", description = "What colour should the highlight for crafted armour and weapons be?\n\n§aClick the coloured box to open the colour wheel.", order = 58)
        public CustomColor craftedHighlightColor = ItemTier.CRAFTED.getDefaultHighlightColor();

        @Setting(displayName = "1 Star Ingredient Colour", description = "What colour should the highlight for ingredients with one star be?\n\n§aClick the coloured box to open the colour wheel.", order = 59)
        public CustomColor ingredientOneHighlightColor = new CustomColor(1, 0.97f, 0.6f);

        @Setting(displayName = "2 Star Ingredient Colour", description = "What colour should the highlight for ingredients with two stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 60)
        public CustomColor ingredientTwoHighlightColor = new CustomColor(1f, 1f, 0f);

        @Setting(displayName = "3 Star Ingredient Colour", description = "What colour should the highlight for ingredients with three stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 61)
        public CustomColor ingredientThreeHighlightColor = new CustomColor(0.9f, .3f, 0);

        @Setting(displayName = "Inventory Highlight Opacity", description = "As a percentage, how opaque should highlights in your inventory be?", order = 62)
        @Setting.Limitations.FloatLimit(min = 30, max = 100, precision = 0.5f)
        public float inventoryAlpha = 100;

        @Setting(displayName = "Hotbar Highlight Opacity", description = "As a percentage, how opaque should highlights in your hotbar be?", order = 63)
        @Setting.Limitations.FloatLimit(min = 10, max = 100, precision = 0.5f)
        public float hotbarAlpha = 30;

        @Setting(displayName = "Colour Number of Skill Points", description = "Should the number of skill points be coloured?")
        public boolean colorSkillPointNumberOverlay = true;
    }

    @SettingsInfo(name = "market", displayPath = "Utilities/Market")
    public static class Market extends SettingsClass {
        public static Market INSTANCE;

        @Setting(displayName = "Automatically Open Chat", description = "Should the chat open when the trade market asks you to type a response?")
        public boolean openChatMarket = true;
    }

    @SettingsInfo(name = "bank", displayPath = "Utilities/Bank")
    public static class Bank extends SettingsClass {
        public static Bank INSTANCE;

        @Setting(displayName = "Show Quick Access Page Numbers", description = "Should page numbers be shown on the quick access buttons in the bank?", order = 1)
        public boolean showQuickAccessNumbers = false;

        @Setting(displayName = "Show Quick Access Button Icons", description = "Should the quick access buttons have a custom icon?", order = 2)
        public boolean showQuickAccessIcons = true;

        @Setting(displayName = "Show Bank Search Bar", description = "Should the bank search bar be shown in the bank GUI?\n\n§8Bank items that match the search will be highlighted.", order = 3)
        public boolean showBankSearchBar = true;

        @Setting(displayName = "Automatic Page Searching", description = "Should pressing a bank page button automatically cycle through pages until the searched item is found?", order = 4)
        public boolean autoPageSearch = false;

        @Setting(displayName = "Confirmation for Bank Pages", description = "Should Wynntils display a confirmation when buying bank pages?", order = 6)
        public boolean addBankConfirmation = true;

        @Setting(displayName = "Open Chat Bank Search", description = "Should the chat open when the bank search asks you to type a response?", order = 7)
        public boolean openChatBankSearch = true;

        @Setting(displayName = "Searched Item Highlight Colour", description = "What colour should the highlight for searched items be?\n\n§aClick the coloured box to open the colour wheel.", order = 20)
        public CustomColor searchHighlightColor = new CustomColor(0.9f, .3f, 0f);

        @Setting(displayName = "Quick Access 1 Destination", description = "Which bank page should the first quick access button take you to?", order = 10)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessOne = 1;

        @Setting(displayName = "Quick Access 2 Destination", description = "Which bank page should the second quick access button take you to?", order = 11)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessTwo = 5;

        @Setting(displayName = "Quick Access 3 Destination", description = "Which bank page should the third quick access button take you to?", order = 12)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessThree = 9;

        @Setting(displayName = "Quick Access 4 Destination", description = "Which bank page should the fourth quick access button take you to?", order = 13)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessFour = 13;

        @Setting(displayName = "Quick Access 5 Destination", description = "Which bank page should the fifth quick access button take you to?", order = 14)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessFive = 17;

        @Setting(displayName = "Quick Access 6 Destination", description = "Which bank page should the sixth quick access button take you to?", order = 15)
        @Setting.Limitations.IntLimit(min = 1, max = 21)
        public int quickAccessSix = 21;

        @Setting
        public Map<Integer, String> pageNames = new HashMap<>();

        @Setting
        public int maxPages = 1;
    }

    @SettingsInfo(name = "Command Keybinds", displayPath = "Utilities/Command Keybinds")
    public static class CommandKeybinds extends SettingsClass {
        public static CommandKeybinds INSTANCE;

        @Setting(displayName = "Command Keybind 1", description = "What command should be run upon pressing the command keybind 1 key?", order = 1)
        public String cKeyBind1 = "totem";

        @Setting(displayName = "Command Keybind 2", description = "What command should be run upon pressing the command keybind 2 key?", order = 2)
        public String cKeyBind2 = "";

        @Setting(displayName = "Command Keybind 3", description = "What command should be run upon pressing the command keybind 3 key?", order = 3)
        public String cKeyBind3 = "";

        @Setting(displayName = "Command Keybind 4", description = "What command should be run upon pressing the command keybind 4 key?", order = 4)
        public String cKeyBind4 = "";

        @Setting(displayName = "Command Keybind 5", description = "What command should be run upon pressing the command keybind 5 key?", order = 5)
        public String cKeyBind5 = "";

        @Setting(displayName = "Command Keybind 6", description = "What command should be run upon pressing the command keybind 6 key?", order = 6)
        public String cKeyBind6 = "";

        @Setting(displayName = "Presets", description = "Click on the button below to cycle through various command presets. The commands will automatically be copied to your clipboard for you to paste in the above fields.", upload = false, order = 9)
        public Presets preset = Presets.CLICK_ME;

        @Override
        public void onSettingChanged(String name) {
            if (name.contentEquals("preset")) {
                if (!(McIf.mc().currentScreen instanceof SettingsUI)) {
                    preset = Presets.CLICK_ME;
                } else if (preset.value != null) {
                    Utils.copyToClipboard(preset.value);
                }
            }
        }

        public enum Presets {
            CLICK_ME("Click me to copy to clipboard", null),
            MANAGE_CURRENT_TERRITORY("Manage the current territory", "guild territory"),
            ATTACK_CURRENT_TERRITORY("Attack the current territory", "guild attack"),
            MANAGE_GUILD("Open the guild manage menu", "guild manage"),
            HOUSING_EDIT_TOGGLE("Toggle edit mode while in housing", "housing edit"),
            OPEN_CLASS_MENU("Open character selection menu", "class"),
            OPEN_PARTYFINDER_MENU("Open partyfinder menu", "partyfinder"),
            OPEN_PETS("Open pets menu", "pets"),
            OPEN_USE_MENU("Open use menu", "use"),
            OPEN_TOTEM_MENU("Open totem menu", "totem"),
            OPEN_CRATES_MENU("Open crates menu", "crates");

            public final String displayName;
            public final String value;

            Presets(String displayName, String value) {
                this.displayName = displayName;
                this.value = value;
            }
        }
    }

    @Override
    public void onSettingChanged(String name) {
        if (name.equalsIgnoreCase("addItemIdentificationSpacing"))
            WebManager.getDirectItems().forEach(ItemProfile::clearGuideStack);
        else if (name.equalsIgnoreCase("changeWindowTitle"))
            ServerEvents.onWindowTitleSettingChanged();
        else if (name.equalsIgnoreCase("changeWindowIcon")) {
            WindowIconManager.update();
        }
    }

}
