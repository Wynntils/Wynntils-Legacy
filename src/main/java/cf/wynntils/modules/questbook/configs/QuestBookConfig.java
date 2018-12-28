/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "Quest Book")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "Allow Wynntils' Questbook", description = "Should Wynncraft's quest book be replaced by Wynntils'?")
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "Set Quest Location to Compass", description = "Should the compass point towards given coordinates of quests?")
    public boolean compassFollowQuests = true;

}
