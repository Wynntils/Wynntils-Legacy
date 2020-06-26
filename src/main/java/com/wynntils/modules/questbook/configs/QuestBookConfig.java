/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "Quest Book")
public class QuestBookConfig extends SettingsClass {

    public static QuestBookConfig INSTANCE;

    @Setting(displayName = "Replace Wynncraft Quest Book", description = "Should Wynncraft's quest book be replaced by Wynntils' custom quest book?\n\n§8The quest book can still be accessed through the \"Open Quest Book\" hotkey")
    public boolean allowCustomQuestbook = true;

    @Setting(displayName = "Set Quest Location to Compass", description = "Should the compass point towards given coordinates of quests?")
    public boolean compassFollowQuests = true;

    @Setting(displayName = "Search Box Requires Click", description = "Should you be required to click on the search bar before typing or be able to type in the search bar immediately after opening the quest book?")
    public boolean searchBoxClickRequired = true;

    @Setting(displayName = "Fuzzy Search", description = "Should a different search algorithm be used that allows searching for acronyms and abbreviations?")
    public boolean useFuzzySearch = true;

    @Setting(displayName = "Update When Opening the QuestBook", description = "Should the QuestBook only update itself when you open it?")
    public boolean updateWhenOpen = false;

    @Setting(displayName = "Show Discoveries That Are Unavailable", description = "When viewing undiscovered discoveries should discoveries that cannot be discovered currently be displayed?")
    public boolean showAllDiscoveries = false;
    
    @Setting(displayName = "Secret Discovery Location Tracking", description = "When viewing secret discoveries, which should allow tracking? (Coordinates are obtained via the Wynncraft Wiki)")
    public SecretSpoilMode spoilSecretDiscoveries = SecretSpoilMode.ONLY_DISCOVERED;
    
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
