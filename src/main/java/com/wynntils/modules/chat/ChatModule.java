/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.events.ClientEvents;
import com.wynntils.modules.chat.managers.TabManager;

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
