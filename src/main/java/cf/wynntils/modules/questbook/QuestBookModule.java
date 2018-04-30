/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.questbook.events.ServerEvents;
import cf.wynntils.modules.questbook.managers.QuestManager;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "quest_book", displayName = "Quest Book")
public class QuestBookModule extends Module {

    public void onEnable() {
        registerEvents(new ServerEvents());

        registerKeyBinding("requestUpdate", Keyboard.KEY_O, "tests", true, QuestManager::requestQuestBookReading);
    }

}
