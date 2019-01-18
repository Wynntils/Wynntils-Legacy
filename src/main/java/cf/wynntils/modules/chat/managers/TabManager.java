package cf.wynntils.modules.chat.managers;

import cf.wynntils.modules.chat.ChatModule;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.instances.ChatTab;

import java.util.ArrayList;
import java.util.Collections;

public class TabManager {

    private static ArrayList<ChatTab> availableTabs;

    public static void startTabs() {
        availableTabs = ChatConfig.INSTANCE.available_tabs;

        if(!ChatConfig.INSTANCE.registeredDefaultTabs) {
            availableTabs.add(new ChatTab("Global", ".*", "", true, 0));
            availableTabs.add(new ChatTab("Guild", "(?!^&3\\[Parkour\\])(^&3\\[(.*?)\\])|(^&3You were not in the territory)", "/g", false, 1));
            availableTabs.add(new ChatTab("Party", "(^&7\\[&r&e(.*?)\\])|(^&eYou are not in a party!)", "/p", false, 2));

            Collections.sort(availableTabs);
            ChatConfig.INSTANCE.registeredDefaultTabs = true;
            ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
        }

        if(availableTabs.size() >= 2 && availableTabs.get(1).getRegex().equals("(&3\\[(.*?)\\])|(&3You were not in the territory)")) {
            ChatTab tab = availableTabs.get(1);
            tab.update(tab.getName(), "^(&3&3\\[(.*?)])|(&3You were not in the territory)", "/g", false, 1);
        }
    }

    public static void registerNewTab(ChatTab tab) {
        availableTabs.add(tab);
        Collections.sort(availableTabs);
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
        if(availableTabs.size() <= id || availableTabs.get(id) == null) return getTabById(id-1);
        return availableTabs.get(id);
    }

    public static void updateTab(int id, String name, String regex, String autoCommand, boolean lowPriority, int orderNb) {
        availableTabs.get(id).update(name, regex.replace("&", "§"), autoCommand, lowPriority, orderNb);
        Collections.sort(availableTabs);
        saveConfigs();
    }

    private static void saveConfigs() {
        ChatConfig.INSTANCE.available_tabs = availableTabs;
        ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
    }
}
