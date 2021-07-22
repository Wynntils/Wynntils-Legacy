/*
 *  * Copyright © Wynntils - 2021.
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

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?", order = 5)
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Transparent Chat", description = "Should the chat window be transparent?", order = 2)
    public boolean transparent = false;

    @Setting(displayName = "Chat Timestamp Format", description = "How should the timestamps be displayed?\n\n§8This has no effect if chat timestamps are disabled.", order = 6)
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?", order = 3)
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Mentions Nicknames", description = "Besides your username, what other names should trigger chat mentions?\n\n§8Multiple nicknames can be added by using commas as separators.", order = 4)
    public String mentionNames = "";

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack?", order = 7)
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft Info messages be filtered?\n\n§8Messages starting with §4[Info]§8 will no longer appear in chat.", order = 8)
    public boolean filterWynncraftInfo = true;

    @Setting(displayName = "Filter Join Messagse", description = "Should Wynncraft Join messages be filtered?", order = 9)
    public boolean filterJoinMessages = false;

    @Setting(displayName = "Filter Territory Enter", description = "Should territory enter messages be displayed in chat?\n\n§8Territory enter messages look like §7[You are now entering Detlas]§8.", order = 10)
    public boolean filterTerritoryEnter = true;

    @Setting(displayName = "Improved Powder Manual", description = "Should the powder manual be replaced with a cleaner menu?", order = 11)
    public boolean customPowderManual = true;

    @Setting(displayName = "Show Held Item Chat Message", description = "Should details of your compass and soul points be shown in chat while you are holding them?", order = 12)
    public boolean heldItemChat = true;

    @Setting(displayName = "Chat History Length", description = "How many messages should be saved in the chat history?", order = 1)
    @Setting.Limitations.IntLimit(min = 10, max = 2000)
    public int chatHistorySize = 100;

    @Setting
    public boolean registeredDefaultTabs = false;

    @Setting
    public List<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "Alter Chat Tab by Presets", description = "Which premade selection of chat tabs should be used?\n\na - Global, Guild, Party\n\nb - Global, Shouts, Guild/Party, PMs\n\nvanilla - All", order = 0)
    public Presets preset = Presets.vanilla;

    @Setting(displayName = "Wynnic Translation Condition", description = "What is the condition for translating Wynnic and Gavellian be translated?\n\n§8Discovery - The discovery for the transcriber has been discovered\n\n§8Book - The transcriber is in the player's inventory", order = 12)
    public TranslateConditions translateCondition = TranslateConditions.always;

    @Setting(displayName = "Directly Translate Wynnic", description = "Should Ingame languages such as Wynnic be directly replaced with English translations in chat?\n\n§8If this option is disabled, translation tooltips will appear when hovering over messages with Wynnic/Gavellian.", order = 13)
    public boolean translateIntoChat = false;

    @Setting(displayName = "Wynnic Translation Colors", description = "Should Wynnic be colored green and Gavellian purple?", order = 14)
    public boolean coloredTranslation = true;

    @Setting(displayName = "Use brackets for translation", description = "Should text be translated to Wynnic and Gavellian using a button or curly brackets for Wynnic and angle brackets for gavellian?", order = 15)
    public boolean useBrackets = false;

    @Setting(displayName = "Clickable Party Invites", description = "Should party invites provide a clickable command?", order = 16)
    public boolean clickablePartyInvites = true;

    @Setting(displayName = "Clickable Coordinates", description = "Should coordinates that are displayed in chat be clickable as a '/compass' command?", order = 17)
    public boolean clickableCoordinates = true;

    @Setting(displayName = "Clickable Trade Requests", description = "Should trade requests provide a clickable command?", order = 18)
    public boolean clickableTradeMessage = true;

    @Setting(displayName = "Clickable Duel Requests", description = "Should duel requests provide a clickable command?", order = 19)
    public boolean clickableDuelMessage = true;

    public enum Presets {
        a,
        b,
        vanilla
    }

    public enum TranslateConditions {
        always,
        discovery,
        book,
        never
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
