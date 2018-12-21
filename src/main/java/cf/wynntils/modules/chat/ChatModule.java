package cf.wynntils.modules.chat;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.events.ClientEvents;

@ModuleInfo(name = "chat", displayName = "Chat")
public class ChatModule extends Module {

    public void onEnable() {
        registerSettings(ChatConfig.class);

        registerEvents(new ClientEvents());
    }

}
