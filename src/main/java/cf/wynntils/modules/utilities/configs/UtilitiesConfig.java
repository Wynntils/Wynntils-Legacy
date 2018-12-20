package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
@SettingsInfo(name = "main", displayPath = "Main")
public class UtilitiesConfig extends SettingsClass {
    public static UtilitiesConfig INSTANCE;


    @Setting(displayName = "Daily Chest Reminder", description = "Should you get a chat notification when you join if you need to loot your daily chest")
    public boolean dailyReminder = true;

    @Setting(displayName = "Show Server TPS on TAB", description = "Should the server ticks per second be shown while holding TAB")
    public boolean showTPSCount = true;

    @Setting(displayName = "Hide Nametags through walls", description = "Should the nametags be hidden to see through walls")
    public boolean hideNametags = true;

    @Setting(displayName = "Hide Nametags boxes", description = "Should the nametags boxes be hidden")
    public boolean hideNametagBox = true;

    @Setting(displayName = "Show players armor", description = "Should what players are wearing be listed in their name tag")
    public boolean showArmors = false;

    @Setting(displayName = "Prevent mythic loot chest closing", description = "Should the closing of loot chests be prevented when that loot chest contains a mythic")
    public boolean preventMythicChestClose = true;

    @Setting(displayName = "Prevent slot click on locked items", description = "Should the slot click be blocked when clicking on a locked item?")
    public boolean preventSlotClicking = false;

    //HeyZeer0: Do not add @Setting here, or it will be displayed on the configuration
    public HashMap<Integer, HashSet<Integer>> locked_slots = new HashMap<>();

    @SettingsInfo(name = "wars", displayPath = "Wars")
    public static class Wars extends SettingsClass {
        public static Wars INSTANCE;

        @Setting(displayName = "Entity Filter", description = "Should useless entities be filtered out")
        public boolean allowEntityFilter = true;

    }

    @SettingsInfo(name = "data", displayPath = "")
    public static class Data extends SettingsClass {
        public static Data INSTANCE;


        public long dailyReminder = 0L;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "chat", displayPath = "Main/Chat")
    public static class Chat extends SettingsClass {
        public static Chat INSTANCE;


        @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps")
        public boolean addTimestampsToChat = true;

        @Setting(displayName = "Chat Mentions", description = "The game ping a sound when your name appears in chat")
        public boolean allowChatMentions = true;

        @Setting(displayName = "Chat Spam Filter", description = "Repeating chat messages would stack up instead of filling the screen")
        public boolean blockChatSpamFilter = true;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "item_highlights", displayPath = "Main/Item Highlights")
    public static class Items extends SettingsClass {
        public static Items INSTANCE;


        @Setting(displayName = "Item highlights in containers", description = "Should items be highlighted in _nlremote containers(chests, bank, etc)")
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item highlights in inventory", description = "Should items be highlighted in _nlthe player's inventory")
        public boolean mainHighlightInventory = true;

        @Setting(displayName = "Accessories highlight", description = "Should the worn accessories be highlighted")
        public boolean accesoryHighlight = true;

        @Setting(displayName = "Highlight hotbar items", description = "Should the items in the hotbar be highlighted")
        public boolean hotbarHighlight = true;

        @Setting(displayName = "Highlight armor items", description = "Should the player's armor be highlighted")
        public boolean armorHighlight = true;

        @Setting(displayName = "Highlight mythics", description = "Should mythic items be highlighted")
        public boolean mythicHighlight = true;

        @Setting(displayName = "Highlight legendaries", description = "Should legendary items be highlighted")
        public boolean legendaryHighlight = true;

        @Setting(displayName = "Highlight rares", description = "Should rare items be highlighted")
        public boolean rareHighlight = true;

        @Setting(displayName = "Highlight uniques", description = "Should unique items be highlighted")
        public boolean uniqueHighlight = true;

        @Setting(displayName = "Highlight sets", description = "Should set items be highlighted")
        public boolean setHighlight = true;

        @Setting(displayName = "Highlight normals", description = "Should normal items be highlighted")
        public boolean normalHighlight = false;

        @Setting(displayName = "Highlight godly cosmetics", description = "Should godly cosmetic items be highlighted")
        public boolean godlyEffectsHighlight = true;

        @Setting(displayName = "Highlight epic cosmetics", description = "Should epic cosmetic items be highlighted")
        public boolean epicEffectsHighlight = true;

        @Setting(displayName = "Highlight rare cosmetics", description = "Should rare cosmetic items be highlighted")
        public boolean rareEffectsHighlight = true;

        @Setting(displayName = "Highlight common cosmetics", description = "Should common cosmetic items be highlighted")
        public boolean commonEffectsHighlight = true;

        @Setting(displayName = "Show emerald count in containers", description = "Show emerald count in remote containers(chests, bank, etc)")
        public boolean emeraldCountChest = true;

        @Setting(displayName = "Show emerald count in inventory", description = "Show emerald count in the player's inventory")
        public boolean emeraldCountInventory = true;

        @Setting(displayName = "Highlight powders", description = "Should powders be highlighted")
        public boolean powderHighlight = true;

        @Setting(displayName = "Min powder tier highlight", description = "The minimum tier of powder that should be highlighted. No effect if powder highlighting is disabled")
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
        @Setting(displayName = "Test text field", description = "this is a weird piece of text used to check the description length handling on complicated and long descriptions")
        public String testTextField = "default text";

        @Setting(displayName = "Enum test", description = "")//empty description is just no description
        public TestEnum testEnumSetting = TestEnum.TEST_B;

        @Setting.Limitations.IntLimit(min = -36,max = 24,precision = 1)
        @Setting(displayName = "Test Integer Ting", description = "this tests the usability of integer settings")
        public int lol = -3;

        @Setting.Limitations.FloatLimit(min = 3f,max = 7.4f,precision = 0.2f)
        @Setting(displayName = "Float ting", description = "dis float setting")
        public float floatlol = 4.6f;

        @Setting.Limitations.DoubleLimit(min = -3.68d,max = 1d,precision = 0.01d)
        @Setting(displayName = "Double tang", description = "ye, dis description")
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

