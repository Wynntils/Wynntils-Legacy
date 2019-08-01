package com.wynntils.modules.questbook.managers;

import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.Minecraft;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class QuestBookHandler {

    private static ArrayList<QuestBookPage> questBookPages = new ArrayList<>();

    public static void registerPage(QuestBookPage questBookPage) {
        questBookPages.add(questBookPage);
    }

    public static void openQuestBookPage(boolean requestOpening, Class<? extends QuestBookPage> instance) {
        getQuestBookpage(instance).open(requestOpening);
    }

    public static QuestBookPage getQuestBookpage(Class<? extends QuestBookPage> instance) {
        return questBookPages.stream().filter(instance::isInstance).findFirst().get();
    }

    public static ArrayList<QuestBookPage> getQuestBookPages() {
        return questBookPages;
    }
}
