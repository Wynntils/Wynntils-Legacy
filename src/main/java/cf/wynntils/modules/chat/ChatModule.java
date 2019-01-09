package cf.wynntils.modules.chat;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.events.ClientEvents;
import cf.wynntils.modules.chat.managers.TabManager;

@ModuleInfo(name = "chat", displayName = "Chat")
public class ChatModule extends Module {

    private static ChatModule module;

    public void onEnable() {
        module = this;

        registerSettings(ChatConfig.class);
        registerEvents(new ClientEvents());

        TabManager.startTabs();
    }

    public static ChatModule getModule() {
        return module;
    }

}
