package cf.wynntils.modules.chat.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.chat.enums.ChatTab;
import cf.wynntils.modules.chat.overlays.ChatOverlay;

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps attached before the beginning of them?")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Chat Mentions", description = "Should a sound play when your username appears in chat?")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Spam Filter", description = "Should repeating messages stack rather than flood the chat?")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Chat Tabs", description = "Should the chat be separated into three chat tabs/channels? (global, guild, party)")
    public boolean enableChatTabs = true;

    @Override
    public void onSettingChanged(String name) {
        if(!enableChatTabs) ChatOverlay.getChat().setCurrentTab(ChatTab.GLOBAL);
    }

}
