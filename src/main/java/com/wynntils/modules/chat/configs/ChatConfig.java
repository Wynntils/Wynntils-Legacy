/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.managers.ChatManager;
import com.wynntils.modules.chat.managers.TabManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?", order = 3)
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Transparent Chat", description = "Should the chat window be transparent?", order = 4)
    public boolean transparent = false;

    @Setting(displayName = "Chat Timestamp Format", description = "How should the timestamps be displayed?\n\n§8This has no effect if chat timestamps are disabled.")
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Mentions Nicknames", description = "Besides your username, what other names should trigger chat mentions?\n\n§8Multiple nicknames can be added by using commas as separators.", order = 1)
    public String mentionNames = "";

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack?")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft Info messages be filtered?\n\n§8Messages starting with §4[Info]§8 will no longer appear in chat.")
    public boolean filterWynncraftInfo = true;

    @Setting(displayName = "Filter Territory Enter", description = "Should territory enter messages be displayed in chat?\n\n§8Territory enter messages look like §7[You are now entering Detlas]§8.")
    public boolean filterTerritoryEnter = true;

    @Setting(displayName = "Improved Powder Manual", description = "Should the powder manual be replaced with a cleaner menu?")
    public boolean customPowderManual = true;

    @Setting(displayName = "Show Held Item Chat Message", description = "Should details of your compass and soul points be shown in chat while you are holding them?")
    public boolean heldItemChat = true;

    @Setting(displayName = "Chat History Length", description = "How many messages should be saved in the chat history?", order = 5)
    @Setting.Limitations.IntLimit(min = 10, max = 2000)
    public int chatHistorySize = 100;

    @Setting
    public boolean registeredDefaultTabs = false;

    @Setting
    public List<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "Alter Chat Tab by Presets", description = "Which premade selection of chat tabs should be used?\n\na - Global, Guild, Party\n\nb - Global, Shouts, Guild/Party, PMs\n\nvanilla - All", order = 0)
    public Presets preset = Presets.vanilla;

    @Setting(displayName = "Clickable Party Invites", description = "Should party invites provide a clickable command?")
    public boolean clickablePartyInvites = true;

    @Setting(displayName = "Clickable Coordinates", description = "Should coordinates that are displayed in chat be clickable as a '/compass' command?")
    public boolean clickableCoordinates = true;

    @Setting(displayName = "Clickable Trade Requests", description = "Should trade requests provide a clickable command?")
    public boolean clickableTradeMessage = true;

    @Setting(displayName = "Clickable Duel Requests", description = "Should duel requests provide a clickable command?")
    public boolean clickableDuelMessage = true;

    @Setting(displayName = "Wynnic Translations", description = "Should Wynnic be directly replaced with English translations in chat?\n\n§8If this option is disabled, translation tooltips will appear when hovering over Wynnic messages.")
    public boolean translateIntoChat = false;

    @Setting(displayName = "Use brackets for translation", description = "Should text be translated to Wynnic using a button or curly brackets?")
    public boolean useBrackets = false;

    public enum Presets {
        a,
        b,
        vanilla
    }

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("timestampFormat")) {
            try {
                ChatManager.dateFormat = new SimpleDateFormat(timestampFormat);
                ChatManager.validDateFormat = true;
            } catch (IllegalArgumentException ex) {
                ChatManager.validDateFormat = false;
            }
        } else if (name.equals("preset")) {
            TabManager.registerPresets();
        }
    }

}
