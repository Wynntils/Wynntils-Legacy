/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.utilities.events.ServerEvents;
import com.wynntils.modules.utilities.instances.SkillPointAllocation;
import com.wynntils.modules.utilities.managers.WindowIconManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SettingsInfo(name = "main", displayPath = "Utilities")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;

    @Setting(displayName = "Class & Hub Buttons", description = "Should 'Class selection' and 'Back to Hub' buttons be displayed on the in-game menu?", order = 1)
    public boolean addClassHubButtons = true;

    @Setting(displayName = "Options & Profile Buttons", description = "Should 'Wynntils Option' and 'User Profile' buttons be displayed on the in-game menu?", order = 2)
    public boolean addOptionsProfileButtons = true;

    @Setting(displayName = "Daily Chest Reminder", description = "Should a message notifying that you can claim your daily chest be sent upon joining a world?")
    public boolean dailyReminder = true;

    @Setting(displayName = "Hide Vanilla Potions Indicators", description = "Should the vanilla indicators for active potion effects be hidden?")
    public boolean hidePotionGui = true;

    @Setting(displayName = "Hide Nametags Through Walls", description = "Should nametags be hidden when behind opaque blocks?")
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags' Box", description = "Should the box around nametags be hidden?")
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show Players' Armour", description = "Should the worn armour of players be listed underneath their nametag?")
    public boolean showArmors = false;

    @Setting(displayName = "Prevent Mythic Chest Closing", description = "Should the closing of loot chests be prevented when they contain mythics?")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Prevent Clicking on Locked Items", description = "Should moving items to and from locked inventory slots be blocked?")
    public boolean preventSlotClicking = false;

    @Setting(displayName = "FOV Scaling Function", description = "What scaling function should be used for speed-based FOV changes?")
    public FovScalingFunction fovScalingFunction = FovScalingFunction.Vanilla;

    @Setting(displayName = "Auto Mount Horse", description = "Should you mount your horse automatically when it is spawned?")
    public boolean autoMount = false;

    @Setting(displayName = "Block Health ", description = "Should the mod prevent you from using your health potions when you are at full health?")
    public boolean blockHealingPots = true;

    @Setting(displayName = "Apply Resource Pack", description = "Should the Wynncraft server resource pack be applied when joining the server instead of when selecting your class?")
    public boolean autoResource = true;

    @Setting(displayName = "Startup Resource Pack", description = "Should the Wynncraft server resource pack be applied when starting Minecraft?")
    public boolean autoResourceOnLoad = false;

    @Setting(displayName = "Change Window Title", description = "Should the mod change the window title to \"Wynncraft\" while on the server?")
    public boolean changeWindowTitle = true;

    @Setting(displayName = "Change Window Icon", description = "Should the mod change the window icon to the Wynncraft logo while on the server?\n\n§8This does not work on macOS systems.")
    public boolean changeWindowIcon = true;

    @Setting(displayName = "Show Tooltips From Top", description = "Should tooltips be rendered from the top?")
    public boolean renderTooltipsFromTop = true;

    @Setting(displayName = "Scale Tooltips", description = "Should tooltips be scaled down so that they fit on your screen?")
    public boolean renderTooltipsScaled = false;

    @Setting(displayName = "Show Leaderboard Badges", description = "Should leaderboard players have a badge above their heads?")
    public boolean renderLeaderboardBadges = true;

    @Setting(displayName = "Shift-click Accessories", description = "Allow accessories to be shift-clicked on and off?")
    public boolean shiftClickAccessories = true;

    @Setting(displayName = "Prevent Trades/Duels in Combat", description = "Should trade and duel requests be disabled while holding an item?")
    public boolean preventTradesDuels = false;

    @Setting(upload = false)
    public String lastServerResourcePack = "";

    @Setting(upload = false)
    public String lastServerResourcePackHash = "";

    @Setting
    public Map<Integer, Set<Integer>> locked_slots = new HashMap<>();

    @Setting
    public Map<String, SkillPointAllocation> skillPointLoadouts = new HashMap<>();

    public enum FovScalingFunction {
        Vanilla,
        Arctangent,
        Sprint_Only,
        None
    }

    @SettingsInfo(name = "identifications", displayPath = "Utilities/Identifications")
    public static class Identifications extends SettingsClass {
        public static Identifications INSTANCE;

        @Setting(displayName = "Show Advanced Identifications", description = "Should items show advanced identifications?", order = 0)
        public boolean enabled = true;

        @Setting(displayName = "Advanced Identifications Decimal Places", description = "How many decimal places should advanced identifications have?\n\n§8 This requires your inventory to be reloaded to update. To do that, open the bank once.")
        public IdentificationDecimalPlaces decimalPlaces = IdentificationDecimalPlaces.Zero;

        @Setting(displayName = "Show Item Identification Stars", description = "Should the star rating of an item's identifications be shown?")
        public boolean addStars = false;

        @Setting(displayName = "Legacy Identification Values", description = "Should mana and life steal stats display as /4s instead of /3s and /5s?")
        public boolean legacyIds = false;

        @Setting(displayName = "Rainbow Perfect Items", description = "Should perfect items have rainbow names?")
        public boolean rainbowPerfect = true;

        @Setting(displayName = "Categorize Identifications", description = "Should the identifications in an item's tooltip be categorized?")
        public boolean addSpacing = true;

        @Setting(displayName = "Categorize Set Bonuses", description = "Should the set bonus in an item's tooltip be categorized?")
        public boolean addSetBonusSpacing = true;

        @Setting(displayName = "Show Item Reroll Price", description = "Should the reroll price be displayed in your item?")
        public boolean showRerollPrice = true;

        @Setting(displayName = "Unidentified Item Guesses", description = "Should guesses for your unidentified items be displayed?")
        public boolean showItemGuesses = true;

        @Setting(displayName = "Identification Price Guesses", description = "Should the guesses for prices of identifying unidentified items be displayed?\n\n§8 This requires your inventory to be reloaded to update. To do that, open the bank once.")
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
        @Setting(displayName = "Timer Threshold", description = "How many minutes of inactivity is required for AFK Protection?")
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

        @Setting(displayName = "Block Workstations", description = "Should the mod prevent you from clicking on workstations while in war servers?")
        public boolean blockWorkstations = true;

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

        @Setting(displayName = "Crafted Item Durability Arc", description = "Should crafted items' durability be shown with an arc?", order = 2)
        public boolean craftedDurabilityBars = true;

        @Setting(displayName = "Show Average Unidentified Level", description = "Should the average level of an unidentified item be shown instead of the entire range?", order = 3)
        public boolean averageUnidentifiedLevel = true;

        @Setting(displayName = "Roman Numeral Powder Tier", description = "Should the tier of powders be displayed using roman numerals?", order = 4)
        public boolean romanNumeralPowderTier = true;

        @Setting(displayName = "Item Levels Outside GUIs", description = "Should the item level overlay key be enabled even when no GUI is open?", order = 5)
        public boolean itemLevelOverlayOutsideGui = false;

        @Setting(displayName = "Dungeon Key Specification", description = "Should a letter indicating the destination of dungeon keys be displayed?", order = 8)
        public boolean keySpecification = true;

        @Setting(displayName = "Unidentified Item Type", description = "Should a symbol indicating the type of unidentified items be displayed?", order = 9)
        public boolean unidentifiedSpecification = true;

        @Setting(displayName = "Skill Potion Specification", description = "Should a symbol indicating the skill of skill potions be displayed?", order = 10)
        public boolean potionSpecification = true;

        @Setting(displayName = "Transportation Item Specification", description = "Should a letter indicating the destination of teleport scrolls and boat passes be displayed?", order = 11)
        public boolean transportationSpecification = true;

        @Setting(displayName = "Amplifier Specification", description = "Should the tier of a Corkian Amplifier be displayed?", order = 12)
        public boolean amplifierSpecification = true;

        @Setting(displayName = "Item Highlights in Containers", description = "Should items be highlighted according to rarity in remote containers?\n\n§8Remote containers are items such as chests and banks.", order = 15)
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item Highlights in Inventory", description = "Should items be highlighted according to rarity in your inventory?", order = 16)
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories Highlight", description = "Should your worn accessories be highlighted according to rarity?", order = 17)
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight Hotbar Items", description = "Should the items in your hotbar be highlighted according to rarity?", order = 18)
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight Armour Items", description = "Should your worn armour be highlighted according to rarity?", order = 19)
        public boolean armorHighlight = true;

        @Setting(displayName = "Highlight Mythics", description = "Should mythic items be highlighted?", order = 20)
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight Fabled", description = "Should fabled items be highlighted?", order = 21)
        public boolean fabledHighlight = true;

        @Setting(displayName = "Highlight Legendaries", description = "Should legendary items be highlighted?", order = 22)
        public boolean legendaryHighlight = true;

        @Setting(displayName = "Highlight Rares", description = "Should rare items be highlighted?", order = 23)
        public boolean rareHighlight = true;

        @Setting(displayName = "Highlight Uniques", description = "Should unique items be highlighted?", order = 24)
        public boolean uniqueHighlight = true;

        @Setting(displayName = "Highlight Set Items", description = "Should set items be highlighted?", order = 25)
        public boolean setHighlight = true;

        @Setting(displayName = "Highlight Normal Items", description = "Should normal items be highlighted?", order = 26)
        public boolean normalHighlight = false;

        @Setting(displayName = "Highlight Black Market Cosmetics", description = "Should black market cosmetic items be highlighted?", order = 30)
        public boolean blackMarketEffectsHighlight = true;

        @Setting(displayName = "Highlight Godly Cosmetics", description = "Should godly cosmetic items be highlighted?", order = 31)
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "Highlight Epic Cosmetics", description = "Should epic cosmetic items be highlighted?", order = 32)
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "Highlight Rare Cosmetics", description = "Should rare cosmetic items be highlighted?", order = 33)
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "Highlight Common Cosmetics", description = "Should common cosmetic items be highlighted?", order = 34)
        public boolean commonEffectsHighlight = true;

        // TODO: move these 3 configs
        @Setting(displayName = "Show Emerald Count in Containers", description = "Should your emerald count be displayed in remote containers?\n\n§8Remote containers are items such as chests and banks.", order = 101)
        public boolean emeraldCountChest = true;

        @Setting(displayName = "Show Emerald Count in Inventory", description = "Should your emerald count be displayed in your inventory?", order = 102)
        public boolean emeraldCountInventory = true;

        @Setting(displayName = "Show Emerald Count as Text", description = "Should your emerald count be displayed as text instead of icons?", order = 103)
        public boolean emeraldCountText = false;

        @Setting(displayName = "Highlight Crafting Ingredients", description = "Should crafting ingredients be highlighted according to their tier?", order = 40)
        public boolean ingredientHighlight = true;

        @Setting(displayName = "Highlight Duplicate Cosmetics", description = "Should duplicate cosmetics be highlighted on the scrap menu", order = 41)
        public boolean highlightCosmeticDuplicates = true;

        @Setting(displayName = "Minimum Powder Tier Highlight", description = "What should the minimum tier of powders be for it to be highlighted?\n\n§8Set to 0 to disable.", order = 42)
        @Setting.Limitations.IntLimit(min = 0, max = 6)
        public int minPowderTier = 4;

        @Setting(displayName = "Normal Item Highlight Colour", description = "What colour should the highlight for normal items be?\n\n§aClick the coloured box to open the colour wheel.", order = 50)
        public CustomColor normalHighlightColor = ItemTier.NORMAL.getDefaultHighlightColor();

        @Setting(displayName = "Unique Item Highlight Colour", description = "What colour should the highlight for unique items be?\n\n§aClick the coloured box to open the colour wheel.", order = 51)
        public CustomColor uniqueHighlightColor = ItemTier.UNIQUE.getDefaultHighlightColor();

        @Setting(displayName = "Rare Item Highlight Colour", description = "What colour should the highlight for rare items be?\n\n§aClick the coloured box to open the colour wheel.", order = 52)
        public CustomColor rareHighlightColor = ItemTier.RARE.getDefaultHighlightColor();

        @Setting(displayName = "Set Item Highlight Colour", description = "What colour should the highlight for set items be?\n\n§aClick the coloured box to open the colour wheel.", order = 53)
        public CustomColor setHighlightColor = ItemTier.SET.getDefaultHighlightColor();

        @Setting(displayName = "Legendary Item Highlight Colour", description = "What colour should the highlight for legendary items be?\n\n§aClick the coloured box to open the colour wheel.", order = 54)
        public CustomColor legendaryHighlightColor = ItemTier.LEGENDARY.getDefaultHighlightColor();

        @Setting(displayName = "Fabled Item Highlight Colour", description = "What colour should the highlight for fabled items be?\n\n§aClick the coloured box to open the colour wheel.", order = 55)
        public CustomColor fabledHighlightColor = ItemTier.FABLED.getDefaultHighlightColor();

        @Setting(displayName = "Mythic Item Highlight Colour", description = "What colour should the highlight for mythic items be?\n\n§aClick the coloured box to open the colour wheel.", order = 56)
        public CustomColor mythicHighlightColor = ItemTier.MYTHIC.getDefaultHighlightColor();

        @Setting(displayName = "Crafted Armour & Weapon Highlight Colour", description = "What colour should the highlight for crafted armour and weapons be?\n\n§aClick the coloured box to open the colour wheel.", order = 57)
        public CustomColor craftedHighlightColor = ItemTier.CRAFTED.getDefaultHighlightColor();

        @Setting(displayName = "Ingredient Highlight Colour (1 star)", description = "What colour should the highlight for ingredients with one star be?\n\n§aClick the coloured box to open the colour wheel.", order = 58)
        public CustomColor ingredientOneHighlightColor = new CustomColor(1, 0.97f, 0.6f);

        @Setting(displayName = "Ingredient Highlight Colour (2 stars)", description = "What colour should the highlight for ingredients with two stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 59)
        public CustomColor ingredientTwoHighlightColor = new CustomColor(1f, 1f, 0f);

        @Setting(displayName = "Ingredient Highlight Colour (3 stars)", description = "What colour should the highlight for ingredients with three stars be?\n\n§aClick the coloured box to open the colour wheel.", order = 60)
        public CustomColor ingredientThreeHighlightColor = new CustomColor(0.9f, .3f, 0);

        @Setting(displayName = "Inventory Item Highlight Opacity %", description = "How opaque should highlights in your inventory be? (As a percentage)", order = 61)
        @Setting.Limitations.FloatLimit(min = 0, max = 100, precision = 0.5f)
        public float inventoryAlpha = 100;

        @Setting(displayName = "Hotbar Item Highlight Opacity %", description = "Should the highlight of item rarities be displayed on the hotbar?\n\n§8Set to 0 to disable.", order = 62)
        @Setting.Limitations.FloatLimit(min = 0, max = 100, precision = 0.5f)
        public float hotbarAlpha = 30;
    }

    @SettingsInfo(name = "market", displayPath = "Utilities/Market")
    public static class Market extends SettingsClass {
        public static Market INSTANCE;

        @Setting(displayName = "Price Formatting", description = "Should market prices be displayed in a custom format?")
        public boolean displayInCustomFormat = true;

        @Setting(displayName = "Market Prices Format", description = "What format should market prices be displayed in?\n\n§8Brackets indicate all parameters inside must not be 0.")
        @Setting.Features.StringParameters(parameters = { "les", "ebs", "es", "stx", "le", "eb", "e" })
        public String customFormat = "(%stx%stx )(%le%%les% )(%eb%%ebs% )(%e%%es%)";

        @Setting(displayName = "Open Chat", description = "Should the chat open when the trade market asks you to type a response?")
        public boolean openChatMarket = true;
    }

    @SettingsInfo(name = "bank", displayPath = "Utilities/Bank")
    public static class Bank extends SettingsClass {
        public static Bank INSTANCE;

        @Setting(displayName = "Show Quick Access Page Numbers", description = "Should the page number that the bank quick access buttons send you to be shown?", order = 1)
        public boolean showQuickAccessNumbers = false;

        @Setting(displayName = "Show Quick Access Button Icons", description = "Should the quick access buttons have a custom icon?", order = 2)
        public boolean showQuickAccessIcons = true;

        @Setting(displayName = "Show Bank Search Bar", description = "Should the bank search bar be shown in the bank GUI?\n\n§aBank items that match the search will be highlighted.", order = 3)
        public boolean showBankSearchBar = true;

        @Setting(displayName = "Automatic Page Searching", description = "Should pressing a bank page button automatically cycle through pages until the searched item is found?", order = 4)
        public boolean autoPageSearch = false;

        @Setting(displayName = "Confirmation for Bank Pages", description = "Should Wynntils display a confirmation when buying bank pages?", order = 6)
        public boolean addBankConfirmation = true;

        @Setting(displayName = "Open Chat Bank Search", description = "Should the chat open when the bank search asks you to type a response?", order = 7)
        public boolean openChatBankSearch = true;

        @Setting(displayName = "Searched Item Highlight Color", description = "What colour should the highlight for searched items be?\n\n§aClick the coloured box to open the colour wheel.", order = 20)
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
