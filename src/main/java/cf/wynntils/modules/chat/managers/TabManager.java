package cf.wynntils.modules.chat.managers;

import cf.wynntils.modules.chat.ChatModule;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.instances.ChatTab;

import java.util.ArrayList;

public class TabManager {

    private static ArrayList<ChatTab> availableTabs;

    static {
        availableTabs = ChatConfig.INSTANCE.available_tabs;

        if(!ChatConfig.INSTANCE.registeredDefaultTabs) {
            availableTabs.add(new ChatTab("Global", ".*", "", true));
            availableTabs.add(new ChatTab("Guild", "(&3\\[(.*?)\\])|(&3You were not in the territory)", "/g", false));
            availableTabs.add(new ChatTab("Party", "(&7\\[&r&e(.*?)\\])|(&eYou are not in a party!)", "/p", false));

            ChatConfig.INSTANCE.registeredDefaultTabs = true;
            ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
        }
    }

    public static void registerNewTab(ChatTab tab) {
        availableTabs.add(tab);
        saveConfigs();
    }

    public int deleteTab(int id) {
        if(availableTabs.size() > id) return 0;

        availableTabs.remove(id);
        saveConfigs();
        return id == 0 ? 0 : id - 1;
    }

    public static ArrayList<ChatTab> getAvailableTabs() {
        return availableTabs;
    }

    public static ChatTab getTabById(int id) {
        return availableTabs.get(id);
    }

    private static void saveConfigs() {
        ChatConfig.INSTANCE.available_tabs = availableTabs;
        ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
    }

}
