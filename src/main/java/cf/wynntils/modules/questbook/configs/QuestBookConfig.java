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

    @Setting(displayName = "Search Box Requires Click", description = "If enabled, you must click on the search box before typing to search. If disabled, you can type immediately after opening the page.")
    public boolean searchBoxClickRequired = true;

    @Setting(displayName = "Fuzzy Search", description = "If enabled, a different search algorithm is used that will allow matching of acronyms and abbreviations")
    public boolean useFuzzySearch = true;

}
