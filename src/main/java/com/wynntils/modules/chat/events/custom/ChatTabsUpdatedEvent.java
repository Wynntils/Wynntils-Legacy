package com.wynntils.modules.chat.events.custom;

import com.wynntils.modules.chat.instances.ChatTab;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatTabsUpdatedEvent extends Event {

    private final ChatTab modifiedTab;

    public ChatTabsUpdatedEvent(ChatTab modifiedTab) {
        this.modifiedTab = modifiedTab;
    }

    public ChatTab getModifiedTab() {
        return modifiedTab;
    }

    public static class TabRemoved extends ChatTabsUpdatedEvent {
        public TabRemoved(ChatTab modifiedTab) {
            super(modifiedTab);
        }
    }

    public static class TabAdded extends ChatTabsUpdatedEvent {
        public TabAdded(ChatTab modifiedTab) {
            super(modifiedTab);
        }
    }

}