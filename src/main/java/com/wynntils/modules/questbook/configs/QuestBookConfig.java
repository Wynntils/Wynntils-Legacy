/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "Quest Book")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "Replace Wynncraft Quest Book", description = "Should Wynncraft's quest book be replaced with Wynntils' custom quest book?", order = 0)
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "Auto-Update Quest Book", description = "Should the Wynntils quest book update automatically as you do quests?\n\n§8This setting works best if the scoreboard overlay is enabled as well.", order = 1)
    public boolean autoUpdateQuestbook = true;

    @Setting(displayName = "Set Quest Location to Compass", description = "Should the compass point towards given coordinates of quests?")
    public boolean compassFollowQuests = true;

    @Setting(displayName = "Require Search Box Click", description = "Should you be required to click on the search bar before typing after opening the quest book?")
    public boolean searchBoxClickRequired = true;

    @Setting(displayName = "Fuzzy Search", description = "Should a different search algorithm be used that allows searching for acronyms and abbreviations?")
    public boolean useFuzzySearch = true;

    @Setting(displayName = "Show Unavailable Discoveries", description = "When viewing undiscovered discoveries, should discoveries that cannot be currently found be displayed?")
    public boolean showAllDiscoveries = false;

    @Setting(displayName = "Secret Discoveries Tracking", description = "When viewing secret discoveries, which should be tracked?\n\n§8Coordinates are obtained via the Wynncraft Wiki.")
    public SecretSpoilMode spoilSecretDiscoveries = SecretSpoilMode.ONLY_DISCOVERED;

    @Setting(displayName = "Long Advanced Search Bar", description = "Should the large search bar be used when advanced item search mode is enabled?", order = 150)
    public boolean advItemSearchLongBar = true;

    @Setting(upload = false)
    public boolean advancedItemSearch = false;

    public enum SecretSpoilMode {
        ALL,
        ONLY_DISCOVERED,
        ONLY_UNDISCOVERED,
        NONE;

        // Returns true if the input follows the rule
        public boolean followsRule(boolean discovered) {
            switch (this) {
                case ALL:
                    return true;
                case NONE:
                    return false;
                case ONLY_DISCOVERED:
                    return discovered;
                case ONLY_UNDISCOVERED:
                    return !discovered;
                default:
                    return false;
            }
        }
    }

}
