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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Setting(displayName = "Prevent Favorited Item Chest Closing", description = "Should the closing of loot chests be prevented when they contain favorited items?")
    public boolean preventFavoritedChestClose = true;

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
