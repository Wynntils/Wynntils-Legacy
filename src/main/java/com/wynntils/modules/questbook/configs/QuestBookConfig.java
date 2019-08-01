/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "Quest Book")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "Allow Wynntils' Quest Book", description = "Should Wynncraft's quest book be replaced by Wynntils'?")
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "Set Quest Location to Compass", description = "Should the compass point towards given coordinates of quests?")
    public boolean compassFollowQuests = true;

    @Setting(displayName = "Search Box Requires Click", description = "Should you be required to click on the search bar before typing or be able to type in the search bar immediately after opening the quest book?")
    public boolean searchBoxClickRequired = true;

    @Setting(displayName = "Fuzzy Search", description = "Should a different search algorithm be used that allows searching for acronyms and abbreviations?")
    public boolean useFuzzySearch = true;

    @Setting(displayName = "Scan Discoveries", description = "Should discoveries be analysed by the quest book?\n\n§8Disabling this will cause the quest book to not show the Discoveries tab but will make the quest book analyse faster.")
    public boolean scanDiscoveries = true;

    @Setting(displayName = "Hide Mini Quests", description = "Should mini quests be hidden from the quest book?")
    public boolean hideMiniQuests = false;

}
