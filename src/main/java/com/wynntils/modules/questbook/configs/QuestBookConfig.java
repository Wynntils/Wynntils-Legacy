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

    @Setting(displayName = "wynntils.config.questbook.scan_discoveries.display_name", description = "wynntils.config.questbook.scan_discoveries.description")
    public boolean scanDiscoveries = true;

    @Setting(displayName = "Hide Mini Quests", description = "Should mini quests be hidden from the quest book?")
    public boolean hideMiniQuests = false;

}
