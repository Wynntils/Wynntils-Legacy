/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.questbook.commands.CommandExportDiscoveries;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.events.ClientEvents;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.questbook.overlays.hud.TrackedQuestOverlay;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "quest_book", displayName = "Quest Book")
public class QuestBookModule extends Module {

    public void onEnable() {
        registerEvents(new ClientEvents());

        registerSettings(QuestBookConfig.class);
        registerOverlay(new TrackedQuestOverlay(), Priority.HIGHEST);

        registerCommand(new CommandExportDiscoveries());

        registerKeyBinding("Open Quest Book", Keyboard.KEY_K, "Wynntils", true, () -> QuestBookPages.QUESTS.getPage().open(true));
        registerKeyBinding("Open Discoveries", Keyboard.KEY_U, "Wynntils", true, () -> QuestBookPages.DISCOVERIES.getPage().open(true));
        registerKeyBinding("Open Item Guide", Keyboard.KEY_NONE, "Wynntils", true, () -> QuestBookPages.ITEMGUIDE.getPage().open(true));
        registerKeyBinding("Open HUD configuration", Keyboard.KEY_NONE, "Wynntils", true, () -> QuestBookPages.HUDCONFIG.getPage().open(true));
        registerKeyBinding("Open Menu", Keyboard.KEY_I, "Wynntils", true, () -> {
            QuestBookPages.MAIN.getPage().open(true);
            QuestManager.readQuestBook();
        });
    }

}
