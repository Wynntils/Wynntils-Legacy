package cf.wynntils.modules.chat.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.chat.instances.ChatTab;

import java.util.ArrayList;

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack rather than flood the chat?")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Filter Info Messages", description = "Should Wynncraft info messages be filtered. (Messages starting with §4[Info]§f will no longer appear)")
    public boolean filterWynncraftInfo = true;

    public boolean registeredDefaultTabs = false;

    public ArrayList<ChatTab> available_tabs = new ArrayList<>();

    @Override
    public void onSettingChanged(String name) {

    }

}
