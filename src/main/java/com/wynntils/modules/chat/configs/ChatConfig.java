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

@SettingsInfo(name = "chat", displayPath = "wynntils.config.chat.display_path")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "wynntils.config.chat.add_timestamps_to_chat.display_name", description = "wynntils.config.chat.add_timestamps_to_chat.description")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "wynntils.config.chat.transparent.display_name", description = "wynntils.config.chat.transparent.description")
    public boolean transparent = false;

    @Setting(displayName = "wynntils.config.chat.timestamp_format.display_name", description = "wynntils.config.chat.timestamp_format.description")
    public String timestampFormat = "HH:mm:ss";

    @Setting(displayName = "wynntils.config.chat.chat_mentions.display_name", description = "wynntils.config.chat.chat_mentions.description")
    public boolean allowChatMentions = true;

    @Setting(displayName = "wynntils.config.chat.spam_filter.display_name", description = "wynntils.config.chat.spam_filter.description")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "wynntils.config.chat.filter_info.display_name", description = "wynntils.config.chat.filter_info.description")
    public boolean filterWynncraftInfo = true;

    @Setting(displayName = "wynntils.config.chat.filter_territory_enter.display_name", description = "wynntils.config.chat.filter_territory_enter.description")
    public boolean filterTerritoryEnter = true;

    public boolean registeredDefaultTabs = false;

    public ArrayList<ChatTab> available_tabs = new ArrayList<>();

    @Setting(displayName = "wynntils.config.chat.preset.display_name", description = "wynntils.config.chat.preset.description")
    public Presets preset = Presets.a;

    @Setting(displayName = "wynntils.config.chat.clickable_invite.display_name", description = "wynntils.config.chat.clickable_invite.description")
    public boolean clickablePartyInvites = true;

    @Setting(displayName = "wynntils.config.chat.clickable_coordinate.display_name", description = "wynntils.config.chat.clickable_coordinate.description")
    public boolean clickableCoordinates = true;

    public enum Presets {
        a("wynntils.config.chat.enum.preset.a"),
        b("wynntils.config.chat.enum.preset.b"),
        vanilla("wynntils.config.chat.enum.preset.vanilla");

        public String displayName;

        Presets(String displayName) {
            this.displayName = displayName;
        }
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
