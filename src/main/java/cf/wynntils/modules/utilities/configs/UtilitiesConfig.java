package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

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

        @Setting(displayName = "Highligh rares", description = "Should rare items be highlighted")
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

        @Setting(displayName = "Highlight Shape", description = "What shape should the highlight be")
        public InvHighlight highlightShape = InvHighlight.CIRCLE;

        @Setting(displayName = "Show emerald count in containers", description = "Show emerald count in remote containers(chests, bank, etc)")
        public boolean emeraldCountChest = true;

        @Setting(displayName = "Show emerald count in inventory", description = "Show emerald count in the player's inventory")
        public boolean emeraldCountInventory = true;

        public enum InvHighlight {
            CIRCLE("Circle"),
            SQUARE("Square"),
            ;

            public String displayName;

            InvHighlight(String displayName) {
                this.displayName = displayName;
            }
        }

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @SettingsInfo(name = "hud_settings", displayPath = "Main/HUD")
    public static class HUD extends SettingsClass {
        public static HUD INSTANCE;

        @Setting(displayName = "Health Texture", description = "What texture to use for the health bar")
        public HealthTextures healthTexture = HealthTextures.a;

        @Setting(displayName = "Mana Texture", description = "What texture to use for the mana bar")
        public ManaTextures manaTexture = ManaTextures.a;

        @Setting(displayName = "EXP Texture", description = "What texture to use for the exp bar")
        public ExpTextures expTexture = ExpTextures.a;

        @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
        public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

        @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
        @Setting(displayName = "Animation Speed", description = "How fast should the bar changes happen(0 for instant)")
        public float animated = 2f;

        public enum HealthTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

        public enum ManaTextures {
            Wynn,
            a,
            b,
            c,
            d
            //following the format, to add more textures, register them here with a name and create a special case in the render method
        }

        public enum ExpTextures {
            wynn,
            a,
            b,
            c
            //following the format, to add more textures, register them here with a name and add to the bars.png texture 16 more pixels in height, NOTE THAT SPECIAL ONES MUST BE IN THE END!
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
