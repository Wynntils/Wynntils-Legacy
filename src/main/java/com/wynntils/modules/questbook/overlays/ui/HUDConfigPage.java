/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.settings.ui.OverlayPositionsUI;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class HUDConfigPage extends QuestBookPage {
    public HUDConfigPage() {
        super("", false, IconContainer.hudConfigIcon);
    }

    @Override
    public void open(boolean showAnimation) {
        OverlayPositionsUI ui = new OverlayPositionsUI(McIf.mc().currentScreen);
        UI.setupUI(ui);
        McIf.mc().displayGuiScreen(ui);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Overlay Configuration", TextFormatting.GRAY + "Change position", TextFormatting.GRAY + "and enable/disable", TextFormatting.GRAY + "the various", TextFormatting.GRAY + "Wynntils overlays.", "", TextFormatting.GREEN + "Left click to select");
    }
}
