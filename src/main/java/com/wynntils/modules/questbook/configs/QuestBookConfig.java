/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "wynntils.config.questbook.display_path")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "wynntils.config.questbook.allow_questbook.display_name", description = "wynntils.config.questbook.allow_questbook.description")
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "wynntils.config.questbook.compass_follow.display_name", description = "wynntils.config.questbook.compass_follow.description")
    public boolean compassFollowQuests = true;

    @Setting(displayName = "wynntils.config.questbook.search_click.display_name", description = "wynntils.config.questbook.search_click.description")
    public boolean searchBoxClickRequired = true;

    @Setting(displayName = "wynntils.config.questbook.fuzzy_search.display_name", description = "wynntils.config.questbook.fuzzy_search.description")
    public boolean useFuzzySearch = true;

    @Setting(displayName = "Scan Discoveries", description = "Should discoveries be analyzed by the QuestBook?\nDisabling this will make the QuestBook not show the discoveries tab but will make the QuestBook analyses faster")
    public boolean scanDiscoveries = true;

}
