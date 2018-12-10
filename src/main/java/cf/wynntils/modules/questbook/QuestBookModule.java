/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.questbook.configs.QuestBookConfig;
import cf.wynntils.modules.questbook.events.ClientEvents;
import cf.wynntils.modules.questbook.events.ServerEvents;
import cf.wynntils.modules.questbook.overlays.hud.TrackedQuestOverlay;
import cf.wynntils.modules.questbook.overlays.ui.QuestBookGUI;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "quest_book", displayName = "Quest Book")
public class QuestBookModule extends Module {

    public static final QuestBookGUI gui = new QuestBookGUI();

    public void onEnable() {
        registerEvents(new ServerEvents());
        registerEvents(new ClientEvents());

        registerSettings(QuestBookConfig.class);
        registerOverlay(new TrackedQuestOverlay(), Priority.HIGHEST);

        registerKeyBinding("Open Quest Book", Keyboard.KEY_K, "Wynntils", true, gui::openAtQuests);
        registerKeyBinding("Open Item Guide", Keyboard.KEY_I, "Wynntils", true, gui::openAtItemGuide);
    }

}
