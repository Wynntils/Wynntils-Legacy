/*
 *  * Copyright © Wynntils - 2019.
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

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Transparent Chat", description = "Should the chat window be transparent?")
    public boolean transparent = false;

    @Setting(displayName = "Chat Timestamp Format", description = "How should the timestamps be displayed?\n\n§8This has no effect if chat timestamps are disabled.")
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack rather than flood the chat?")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft Info messages be filtered?\n\n§8Messages starting with §4[Info]§8 will no longer appear in chat.")
    public boolean filterWynncraftInfo = true;

    @Setting(displayName = "Filter Territory Enter", description = "Should territory enter messages be displayed in chat?\n\n§8Territory enter messages look like §7[You are now entering Detlas]§8.")
    public boolean filterTerritoryEnter = true;

    public boolean registeredDefaultTabs = false;

    public ArrayList<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "Alter Chat Tab by Presets", description = "Which pre-made selection of chat tabs should be used?\n\na - Global, Guild, Party\n\nb - Global, Shouts, Guild/Party, PMs\n\nvanilla - All")
    public Presets preset = Presets.a;

    @Setting(displayName = "Clickable Party Invites", description = "Should party invites provide a clickable command?")
    public boolean clickablePartyInvites = true;

    @Setting(displayName = "Clickable Coordinates", description = "Should coordinates displayed in chat be clickable as a '/compass' command?")
    public boolean clickableCoordinates = true;

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
