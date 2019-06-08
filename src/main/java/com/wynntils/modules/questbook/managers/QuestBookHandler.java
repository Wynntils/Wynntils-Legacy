package com.wynntils.modules.questbook.managers;

import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.Minecraft;

import java.util.HashMap;

public class QuestBookHandler {

    private static HashMap<String, QuestBookPage> questBookPages = new HashMap<>();

    public static void registerPage(String ID, QuestBookPage questBookPage) {
        questBookPages.put(ID, questBookPage);
    }

    public static void openQuestBook() {
        Minecraft.getMinecraft().displayGuiScreen(questBookPages.get("MainPage"));
    }

    public static void openQuestBookOnPage(String ID) {
        Minecraft.getMinecraft().displayGuiScreen(questBookPages.get(ID));
    }

    public static HashMap<String, QuestBookPage> getQuestBookPages() {
        return questBookPages;
    }
}
