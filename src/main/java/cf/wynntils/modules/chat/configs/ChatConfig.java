package cf.wynntils.modules.chat.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.chat.enums.ChatTab;
import cf.wynntils.modules.chat.overlays.ChatOverlay;

@SettingsInfo(name = "chat", displayPath = "Chat")
public class ChatConfig extends SettingsClass {
    public static ChatConfig INSTANCE;

    @Setting(displayName = "Chat Timestamps", description = "Should chat messages have timestamps")
    public boolean addTimestampsToChat = false;

    @Setting(displayName = "Chat Mentions", description = "The game ping a sound when your name appears in chat")
    public boolean allowChatMentions = true;

    @Setting(displayName = "Chat Spam Filter", description = "Repeating chat messages would stack up instead of filling the screen")
    public boolean blockChatSpamFilter = true;

    @Setting(displayName = "Chat Tabs", description = "Should the chat be separated on chat tabs")
    public boolean enableChatTabs = true;

    @Override
    public void onSettingChanged(String name) {
        if(!enableChatTabs) ChatOverlay.getChat().setCurrentTab(ChatTab.GLOBAL);
    }

}
