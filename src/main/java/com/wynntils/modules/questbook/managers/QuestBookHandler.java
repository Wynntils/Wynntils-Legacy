package com.wynntils.modules.questbook.managers;

import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.Minecraft;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class QuestBookHandler {

    private static HashMap<String, Class<? extends QuestBookPage>> questBookPages = new HashMap<>();

    public static void registerPage(String ID, Class<? extends QuestBookPage> questBookPage) {
        questBookPages.put(ID, questBookPage);
    }

    public static void openQuestBook() {
        openQuestBookOnPage("MainMap");
    }

    public static void openQuestBookOnPage(String ID) {
        goToQuestBookPage(ID, true);
    }

    public static void goToQuestBookPage(String ID, boolean requestOpening) {
        try {
            Minecraft.getMinecraft().displayGuiScreen(questBookPages.get(ID).getConstructor(boolean.class).newInstance(requestOpening));
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<String, Class<? extends QuestBookPage>> getQuestBookPages() {
        return questBookPages;
    }
}
