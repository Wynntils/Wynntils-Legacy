package com.wynntils.modules.questbook.enums;

import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.overlays.ui.IngredientPage;
import com.wynntils.modules.questbook.overlays.ui.ItemPage;

import java.util.Arrays;
import java.util.Comparator;

public enum Guides {

    ITEMGUIDE(new ItemPage(), 0),
    INGREDIENTGUIDE(new IngredientPage(), 1);

    private QuestBookPage questBookPage;
    private int slotNb;

    Guides(QuestBookPage questBookPage, int slotNb) {
        this.questBookPage = questBookPage;
        this.slotNb = slotNb;
    }

    public static QuestBookPage[] GetAllGuides() {
        return Arrays.stream(Guides.values()).sorted(Comparator.comparingInt(o -> o.slotNb)).map(guide -> guide.questBookPage).toArray(QuestBookPage[]::new);
    }
}
