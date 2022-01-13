/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.questbook.enums;

import java.util.Arrays;

import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.overlays.ui.DialoguePage;
import com.wynntils.modules.questbook.overlays.ui.DiscoveriesPage;
import com.wynntils.modules.questbook.overlays.ui.HUDConfigPage;
import com.wynntils.modules.questbook.overlays.ui.ItemPage;
import com.wynntils.modules.questbook.overlays.ui.LootRunPage;
import com.wynntils.modules.questbook.overlays.ui.MainPage;
import com.wynntils.modules.questbook.overlays.ui.QuestsPage;
import com.wynntils.modules.questbook.overlays.ui.SettingsPage;

public enum QuestBookPages {

    MAIN(new MainPage(), 0),
    QUESTS(new QuestsPage(), 1),
    SETTINGS(new SettingsPage(), 2),
    ITEMGUIDE(new ItemPage(), 3),
    HUDCONFIG(new HUDConfigPage(), 4),
    DIALOGUE(new DialoguePage(), 5),
    DISCOVERIES(new DiscoveriesPage(), 6),
    LOOTRUNS(new LootRunPage(), 7);

    private QuestBookPage questBookPage;
    private int slotNb;

    QuestBookPages(QuestBookPage questBookPage, int slotNb) {
        this.questBookPage = questBookPage;
        this.slotNb = slotNb;
    }

    public QuestBookPage getPage() {
        return questBookPage;
    }

    public static QuestBookPage getPageBySlot(int slot) {
        return Arrays.stream(QuestBookPages.values()).filter(questBookPages -> questBookPages.getSlotNb() == slot).findFirst().get().getPage();
    }

    public int getSlotNb() {
        return slotNb;
    }
}
