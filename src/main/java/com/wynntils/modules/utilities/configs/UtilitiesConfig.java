/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

import java.util.HashMap;
import java.util.HashSet;

@SettingsInfo(name = "main", displayPath = "Main")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;


    @Setting(displayName = "Daily Chest Reminder", description = "Should a message notifying that you can claim your daily chest be sent upon joining a world?")
    public boolean dailyReminder = true;

    @Setting(displayName = "Block Player Collision When AFK", description = "Should player collision be blocked when you are AFK?")
    public boolean blockAfkPushs = true;

    @Setting(displayName = "Hide Vanilla Active Potions Indicators", description = "Should the indicator for active potion effects (black squares) be hidden?")
    public boolean hidePotionGui = true;

    @Setting(displayName = "Add Class & Server Button to Menu", description = "Should a class and server button be displayed on the in-game menu?")
    public boolean addClassServer = true;

    @Setting(displayName = "Hide Nametags Through Walls", description = "Should nametags be hidden when behind opaque blocks?")
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags' Box", description = "Should the box around nametags be hidden?")
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show Players' Armour", description = "Should the worn armour of players be listed underneath their nametag?\n\n§8Crafted armour cannot be displayed.")
    public boolean showArmors = false;

    @Setting(displayName = "Prevent Mythic Loot Chest Closing", description = "Should the closing of loot chests be prevented when they contain mythics?")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Prevent Slot Click on Locked Items", description = "Should moving items to and from locked inventory slots be blocked?")
    public boolean preventSlotClicking = false;

    @Setting(displayName = "Disable FOV Changes with Speed Effect", description = "Should your FOV remain unchanged when you have speed?")
    public boolean disableFovChanges = false;

    @Setting(displayName = "Auto Mount Horse", description = "Should you mount your horse automatically when it is spawned?")
    public boolean autoMount = false;

    @Setting(displayName = "Block Health Potions When at Full Health", description = "Should the mod prevent you from using your health potions when you are at full health?")
    public boolean blockHealingPots = true;


    //HeyZeer0: Do not add @Setting here, or it will be displayed on the configuration
    @Setting(upload = true)
    public HashMap<Integer, HashSet<Integer>> locked_slots = new HashMap<>();

    @SettingsInfo(name = "wars", displayPath = "Wars")
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


        public long dailyReminder = 0L;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "item_highlights", displayPath = "Main/Item Highlights")
    public static class Items extends SettingsClass {
        public static Items INSTANCE;

        @Setting(displayName = "Filter for Ingredients", description = "Should a filter for ingredients and crafted items be displayed?")
        public boolean filterEnabled = true;

        @Setting(displayName = "Item Highlights in Containers", description = "Should items be highlighted according to rarity in remote containers? (chests, bank, etc.)")
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item Highlights in Inventory", description = "Should items be highlighted according to rarity in your inventory?")
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories Highlight", description = "Should your worn accessories be highlighted according to rarity?")
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight Hotbar Items", description = "Should the items in your hotbar be highlighted according to rarity?")
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight Armour Items", description = "Should your worn armour be highlighted according to rarity?")
        public boolean armorHighlight = true;

        @Setting(displayName = "Highlight Mythics", description = "Should mythic items be highlighted?")
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight Legendaries", description = "Should legendary items be highlighted?")
        public boolean legendaryHighlight = true;

        @Setting(displayName = "Highlight Rares", description = "Should rare items be highlighted?")
        public boolean rareHighlight = true;

        @Setting(displayName = "Highlight Uniques", description = "Should unique items be highlighted?")
        public boolean uniqueHighlight = true;

        @Setting(displayName = "Highlight Set Items", description = "Should set items be highlighted?")
        public boolean setHighlight = true;

        @Setting(displayName = "Highlight Normal Items", description = "Should normal items be highlighted?")
        public boolean normalHighlight = false;

        @Setting(displayName = "Highlight Black Market Cosmetics", description = "Should black market cosmetic items be highlighted?")
        public boolean blackMarketEffectsHighlight = true;

        @Setting(displayName = "Highlight Godly Cosmetics", description = "Should godly cosmetic items be highlighted?")
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "Highlight Epic Cosmetics", description = "Should epic cosmetic items be highlighted?")
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "Highlight Rare Cosmetics", description = "Should rare cosmetic items be highlighted?")
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "Highlight Common Cosmetics", description = "Should common cosmetic items be highlighted?")
        public boolean commonEffectsHighlight = true;

        @Setting(displayName = "Show Emerald Count in Containers", description = "Should your emerald count be displayed in remote containers? (chests, bank, etc.)")
        public boolean emeraldCountChest = true;

        @Setting(displayName = "Show Emerald Count in Inventory", description = "Should your emerald count be displayed in your inventory?")
        public boolean emeraldCountInventory = true;

        @Setting(displayName = "Highlight Powders", description = "Should powders be highlighted according to their element?")
        public boolean powderHighlight = true;

        @Setting(displayName = "Highlight Crafting Ingredients", description = "Should crafting ingredients be highlighted according to their tier?")
        public boolean ingredientHighlight = true;

        @Setting(displayName = "Highlight Duplicate Cosmetics", description = "Should duplicate cosmetics be highlighted on the scrap menu")
        public boolean highlightCosmeticDuplicates = true;

        @Setting(displayName = "Minimum Powder Tier Highlight", description = "What should the minimum tier of powders be for it to be highlighted?\n\n§8This setting has no effect if powder highlighting is disabled.")
        @Setting.Limitations.IntLimit(min = 1, max = 6)
        public int minPowderTier = 4;

        @Setting(displayName = "Legendary Item Highlight Colour", description = "What colour should the highlight for legendary items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor lengendaryHighlightColor = new CustomColor(0, 1, 1);

        @Setting(displayName = "Mythic Item Highlight Colour", description = "What colour should the highlight for mythic items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor mythicHighlightColor = new CustomColor(0.3f, 0, 0.3f);

        @Setting(displayName = "Rare Item Highlight Colour", description = "What colour should the highlight for rare items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor rareHighlightColor = new CustomColor(1, 0, 1);

        @Setting(displayName = "Unique Item Highlight Colour", description = "What colour should the highlight for unique items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor uniqueHighlightColor = new CustomColor(1, 1, 0);

        @Setting(displayName = "Set Item Highlight Colour", description = "What colour should the highlight for set items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor setHighlightColor = new CustomColor(0, 1, 0);

        @Setting(displayName = "Normal Item Highlight Colour", description = "What colour should the highlight for normal items be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor normalHighlightColor = new CustomColor(1, 1, 1);

        @Setting(displayName = "Crafted Armour & Weapon Highlight Colour", description = "What colour should the highlight for crafted armour and weapons be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor craftedHighlightColor = new CustomColor(0, .545f, .545f);

        @Setting(displayName = "Ingredient Highlight Colour (1 star)", description = "What colour should the highlight for ingredients with one star be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor ingredientOneHighlightColor = new CustomColor(1, 0.97f, 0.6f);

        @Setting(displayName = "Ingredient Highlight Colour (2 stars)", description = "What colour should the highlight for ingredients with two stars be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor ingredientTwoHighlightColor = new CustomColor(1, 1, 0);

        @Setting(displayName = "Ingredient Highlight Colour (3 stars)", description = "What colour should the highlight for ingredients with three stars be?\n\n§aClick the coloured box to open the colour wheel.")
        public CustomColor ingredientThreeHighlightColor = new CustomColor(0.9f, .3f, 0);

        @Setting(displayName = "Display Item Highlighting on Hotbar", description = "Should the highlight of item rarities be displayed on the hotbar?", order = 0)
        public boolean highlighItemsInHotbar = true;


        @Override
        public void onSettingChanged(String name) {

        }
    }

    @Override
    public void onSettingChanged(String name) {

    }

}
