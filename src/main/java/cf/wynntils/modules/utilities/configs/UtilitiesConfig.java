package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "main", displayPath = "Main")
public class UtilitiesConfig extends SettingsHolder {
    public static UtilitiesConfig INSTANCE;


    @Setting(displayName = "Daily Chest Reminder", description = "Should you get a chat notification when you join if you need to loot your daily chest")
    public boolean dailyReminder = true;

    @Setting(displayName = "Show Server TPS on TAB", description = "Should the server ticks per second be shown while holding TAB")
    public boolean showTPSCount = true;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SettingsInfo(name = "highlights", displayPath = "Main/Item Items")
    public static class Data extends SettingsHolder {
        public static Data INSTANCE;


        public long dailyReminder = 0L;

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SettingsInfo(name = "chat", displayPath = "Main/Chat")
    public static class Chat extends SettingsHolder {
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SettingsInfo(name = "item_highlights", displayPath = "Main/Item Items")
    public static class Items extends SettingsHolder {
        public static Items INSTANCE;


        @Setting(displayName = "Item highlights in containers", description = "Should items be highlighted in remote containers(chests, bank, etc)")
        public boolean mainHighlightChest = true;

        @Setting(displayName = "Item highlights in inventory", description = "Should items be highlighted in the player's inventory")
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

        @Override
        public void onSettingChanged(String name) {

        }
    }

    @Override
    public void onSettingChanged(String name) {

    }

}
