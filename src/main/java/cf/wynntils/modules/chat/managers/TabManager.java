package cf.wynntils.modules.chat.managers;

import cf.wynntils.modules.chat.ChatModule;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.instances.ChatTab;

import java.util.ArrayList;

public class TabManager {

    private static ArrayList<ChatTab> availableTabs;

    public static void startTabs() {
        availableTabs = ChatConfig.INSTANCE.available_tabs;

        if(!ChatConfig.INSTANCE.registeredDefaultTabs) {
            availableTabs.add(new ChatTab("Global", ".*", "", true));
            availableTabs.add(new ChatTab("Guild", "(^&3\\[(.*?)\\])|(^&3You were not in the territory)", "/g", false));
            availableTabs.add(new ChatTab("Party", "(^&7\\[&r&e(.*?)\\])|(^&eYou are not in a party!)", "/p", false));

            ChatConfig.INSTANCE.registeredDefaultTabs = true;
            ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
        }

        if(availableTabs.size() >= 2 && availableTabs.get(1).getRegex().equals("(&3\\[(.*?)\\])|(&3You were not in the territory)")) {
            ChatTab tab = availableTabs.get(1);
            tab.update(tab.getName(), "^(&3&3\\[(.*?)])|(&3You were not in the territory)", "/g", false);
        }
    }

    public static void registerNewTab(ChatTab tab) {
        availableTabs.add(tab);
        saveConfigs();
    }

    public static int deleteTab(int id) {
        if(id > availableTabs.size()) return 0;

        availableTabs.remove(id);
        saveConfigs();
        return id == 0 ? 0 : id - 1;
    }

    public static ArrayList<ChatTab> getAvailableTabs() {
        return availableTabs;
    }

    public static ChatTab getTabById(int id) {
        if(availableTabs.get(id) == null) return getTabById(id-1);
        return availableTabs.get(id);
    }

    public static void updateTab(int id, String name, String regex, String autoCommand, boolean lowPriority) {
        availableTabs.get(id).update(name, regex.replace("&", "§"), autoCommand, lowPriority);
        saveConfigs();
    }

    private static void saveConfigs() {
        ChatConfig.INSTANCE.available_tabs = availableTabs;
        ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
    }

}
