/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.configs;

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

    /*@Setting(displayName = "Show Server TPS in the Tab Menu", description = "Should the connected world display its ticks-per-second in the list of players? (Tab menu)")
    public boolean showTPSCount = true;*/ //Just hid this option - perhaps for future optimizations.

    @Setting(displayName = "Hide Potion Gui", description = "Should the potion effect (black squares) be hidden?")
    public boolean hidePotionGui = true;

    @Setting(displayName = "Hide Nametags Through Walls", description = "Should nametags be hidden when behind opaque blocks?")
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags' Box", description = "Should the box around nametags be hidden?")
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show Players' Armor", description = "Should the armor of what players are wearing be listed underneath their nametag?")
    public boolean showArmors = false;

    @Setting(displayName = "Prevent Mythic Loot Chest Closing", description = "Should the closing of loot chests be prevented when they contain mythics?")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Prevent Slot Click on Locked Items", description = "Should moving items to and from locked inventory slots be blocked?")
    public boolean preventSlotClicking = false;

    //HeyZeer0: Do not add @Setting here, or it will be displayed on the configuration
    public HashMap<Integer, HashSet<Integer>> locked_slots = new HashMap<>();

    @SettingsInfo(name = "wars", displayPath = "Wars")
    public static class Wars extends SettingsClass {
        public static Wars INSTANCE;

        @Setting(displayName = "Entity Filter", description = "Should the nametag of mobs be hidden in war servers?")
        public boolean allowEntityFilter = true;

        @Setting(displayName = "Show player health bar", description = "Should the health bar of other warrers be displayed above their heads?")
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

        @Setting(displayName = "Filter for ingredients", description = "Should an ingredients or crafting items filter be available?")
        public boolean filterEnabled = true;

        @Setting(displayName = "Filter persistency", description = "Should the filter remain the same through inventory updates?")
        public boolean saveFilter = true;

        @Setting(displayName = "Item Highlights in Containers", description = "Should items be highlighted according to rarity in remote containers? (chests, bank, etc.)")
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item Highlights in Inventory", description = "Should items be highlighted according to rarity in your inventory?")
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories Highlight", description = "Should your worn accessories be highlighted according to rarity?")
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight Hotbar Items", description = "Should the items in your hotbar be highlighted according to rarity?")
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight Armor Items", description = "Should your worn armor be highlighted according to rarity?")
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

        @Setting(displayName = "Minimum Powder Tier Highlight", description = "What should the minimum tier of powders be for it to be highlighted? This setting has no effect if powder highlighting is disabled.")
        @Setting.Limitations.IntLimit(min = 1, max = 6)
        public int minPowderTier = 4;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "debug_settings", displayPath = "Main/Debug")
    public static class Debug extends SettingsClass {
        public static Debug INSTANCE;

        @Setting.Limitations.StringLimit(maxLength = 15)
        @Setting(displayName = "Test text field", description = "This is a setting you shouldn't need to worry about unless you were specifically told by a developer to modify this.")
        public String testTextField = "default text";

        @Setting(displayName = "Enum test", description = "")//empty description is just no description
        public TestEnum testEnumSetting = TestEnum.TEST_B;

        @Setting.Limitations.IntLimit(min = -36,max = 24,precision = 1)
        @Setting(displayName = "Test Integer Ting", description = "This is a setting you shouldn't need to worry about unless you were specifically told by a developer to modify this.")
        public int lol = -3;

        @Setting.Limitations.FloatLimit(min = 3f,max = 7.4f,precision = 0.2f)
        @Setting(displayName = "Float ting", description = "This is a setting you shouldn't need to worry about unless you were specifically told by a developer to modify this.")
        public float floatlol = 4.6f;

        @Setting.Limitations.DoubleLimit(min = -3.68d,max = 1d,precision = 0.01d)
        @Setting(displayName = "Double tang", description = "This is a setting you shouldn't need to worry about unless you were specifically told by a developer to modify this.")
        public double doublelawl = 0.2d;

        public enum TestEnum {
            TEST_A("Test A"),
            TEST_B("Test B"),
            TEST_C("Test C"),
            TEST_D("Test D"),
            TEST_E("Test E"),
            ;

            public String displayName;

            TestEnum(String displayName) {
                this.displayName = displayName;
            }
        }

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @Override
    public void onSettingChanged(String name) {

    }

}
