/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "QuestBook")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "Allow Wynntils Questbook", description = "Should the default questbook be replaced by our own")
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "Set quest location to compass", description = "Should the compass follow the quest coordinates")
    public boolean compassFollowQuests = true;

    @Setting(displayName = "Quest NPC icons", description = "Should quest NPCs have icons above their heads")
    public boolean questGiverIcons = true;

}
