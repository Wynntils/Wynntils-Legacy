/*
 *  * Copyright © Wynntils - 2022.
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

    @Setting(displayName = "Click to Copy", description = "Should chat messages be copied to your clipboard when right clicked?", order = 5)
    public boolean clickToCopyMessage = false;

    @Setting(displayName = "Transparent Chat", description = "Should the chat window be transparent?", order = 2)
    public boolean transparent = false;

    @Setting(displayName = "Chat Timestamp Format", description = "How should timestamps be displayed?\n\n§8This setting has no effect if Chat Timestamps is disabled.", order = 6)
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username is mentioned in a message?", order = 3)
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Mentions Nicknames", description = "Besides your username, what other names should trigger Chat Mentions?\n\n§8Multiple nicknames can be added by using commas as separators.", order = 4)
    public String mentionNames = "";

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack?", order = 7)
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft info messages be hidden from chat?\n\n§8Messages starting with §4[Info]§8 will no longer appear in chat.", order = 8)
    public boolean filterWynncraftInfo = true;

    @Setting(displayName = "Filter Join Messages", description = "Should Wynncraft join messages be hidden from chat?", order = 9)
    public boolean filterJoinMessages = false;

    @Setting(displayName = "Filter Event Messages", description = "Should Wynncraft join messages be hidden from chat?\n\n§8Messages starting with §6[Event] §8will no longer appear in chat.", order = 10)
    public boolean filterEventMessages = false;

    @Setting(displayName = "Filter Territory Enter", description = "Should territory enter messages be hidden from chat?\n\n§8Territory enter messages look like §7[You are now entering Detlas]§8.", order = 11)
    public boolean filterTerritoryEnter = true;

    @Setting(displayName = "Filter Resource Warnings", description = "Should guild territory resource production warnings be hidden from chat?", order = 15)
    public boolean filterResourceWarnings = false;

    @Setting(displayName = "Filter Party Finder Messages", description = "Should Party Finder recommendation messages be hidden from chat?.", order = 12)
    public boolean filterPartyFinder = false;

    @Setting(displayName = "Show Held Item Chat Message", description = "Should details of your compass and soul points be shown in chat while you are holding them?", order = 13)
    public boolean heldItemChat = true;

    @Setting(displayName = "Recolor Guild War Messages", description = "Should successful guild war messages be colored green instead of red?", order = 16)
    public boolean recolorGuildWarSuccess = true;

    @Setting(displayName = "Use Guild Role Names", description = "Should guild stars be translated into guild roles in the chat?", order = 17)
    public boolean guildRoleNames = false;

    @Setting(displayName = "Chat History Length", description = "How many messages should be saved in the chat history?", order = 1)
    @Setting.Limitations.IntLimit(min = 10, max = 2000)
    public int chatHistorySize = 100;

    @Setting
    public boolean registeredDefaultTabs = false;

    @Setting
    public List<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "Alter Chat Tab by Presets", description = "Which premade selection of chat tabs should be used?\n\na - Global, Guild, Party\n\nb - Global, Shouts, Guild/Party, PMs\n\nc - All, Guild, Party, Bombs, Global, Local, Private, Shouts\n\nvanilla - All", order = 0)
    public Presets preset = Presets.vanilla;

    @Setting(displayName = "Wynnic Translation Condition", description = "When should Wynnic and Gavellian be translated?\n\nDiscovery: The discovery for the transcriber has been discovered.\n\nBook: The transcriber is in your inventory.", order = 12)
    public TranslateConditions translateCondition = TranslateConditions.always;

    @Setting(displayName = "Translate Wynnic", description = "Should in-game languages such as Wynnic be directly replaced with English translations in chat?\n\n§8If this option is disabled, translation tooltips will appear when hovering over messages with Wynnic or Gavellian.", order = 13)
    public boolean translateIntoChat = false;

    @Setting(displayName = "Wynnic Translation Colors", description = "Should Wynnic be coloured green and Gavellian be coloured purple?", order = 14)
    public boolean coloredTranslation = true;

    @Setting(displayName = "Use Brackets for Translation", description = "How should sent messages be translated to Wynnic and Gavellian?\n\nIf disabled, there will be two buttons next to the message field that allow you to select Wynnic and Gavellian.\n\nIf enabled, you can type in Wynnic using §a{§rcurly brackets§a} §rand in Gavellian using §5<§rangle brackets§5>§r.", order = 15)
    public boolean useBrackets = false;

    @Setting(displayName = "Clickable Coordinates", description = "Should coordinates automatically execute /compass when clicked on in chat?", order = 17)
    public boolean clickableCoordinates = true;

    @Setting(displayName = "Clickable Duel Requests", description = "Should duel requests provide a clickable command?", order = 19)
    public boolean clickableDuelMessage = true;

    @Setting(displayName = "Right Click for Dialogue", description = "Should dialogue progress when you right click an NPC?\n\n§8This may not work on every NPC.", order = 20)
    public boolean rightClickDialogue = false;

    public enum Presets {
        a,
        b,
        c,
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
