package com.wynntils.modules.questbook.instances;

import com.wynntils.modules.questbook.enums.QuestLevelType;
import com.wynntils.modules.questbook.enums.QuestSize;
import com.wynntils.modules.questbook.enums.QuestStatus;

import java.util.List;

public class MiniQuestInfo extends QuestInfo {

    public MiniQuestInfo(String name, QuestStatus status, int minLevel, QuestLevelType levelType, boolean hasLevel, QuestSize size, String currentDescription, List<String> lore) {
        super(name, status, minLevel, levelType, hasLevel, size, currentDescription, lore);
    }

    @Override
    public boolean isMiniQuest() {
        return true;
    }

}
