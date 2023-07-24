/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.questbook;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.questbook.commands.CommandExportDiscoveries;
import com.wynntils.modules.questbook.commands.CommandExportFavorites;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.Guides;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.events.ClientEvents;
import com.wynntils.modules.questbook.overlays.hud.TrackedQuestOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "quest_book", displayName = "Quest Book")
public class QuestBookModule extends Module {

    private static QuestBookModule module;

    public void onEnable() {
        module = this;

        registerEvents(new ClientEvents());

        registerSettings(QuestBookConfig.class);
        registerOverlay(new TrackedQuestOverlay(), Priority.HIGHEST);

        registerCommand(new CommandExportDiscoveries());
        registerCommand(new CommandExportFavorites());

        registerKeyBinding("Open Quest Book", Keyboard.KEY_K, "Wynntils", KeyConflictContext.IN_GAME, true, () -> QuestBookPages.QUESTS.getPage().open(true));
        registerKeyBinding("Open Discoveries", Keyboard.KEY_U, "Wynntils", KeyConflictContext.IN_GAME, true, () -> QuestBookPages.DISCOVERIES.getPage().open(true));
        registerKeyBinding("Open Item Guide", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> Guides.ITEM_GUIDE.getPage().open(true));
        registerKeyBinding("Open Lootrun List", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> QuestBookPages.LOOTRUNS.getPage().open(true));
        registerKeyBinding("Open HUD configuration", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> QuestBookPages.HUDCONFIG.getPage().open(true));
        registerKeyBinding("Open Menu", Keyboard.KEY_I, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            QuestBookPages.MAIN.getPage().open(true);
            //QuestManager.readQuestBook();
        });
    }

    public static QuestBookModule getModule() {
        return module;
    }

}
