/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class SettingsPage extends QuestBookPage {
    public SettingsPage() {
        super("", false, IconContainer.settingsPageIcon);
    }

    @Override
    public void open(boolean showAnimation) {
        McIf.mc().displayGuiScreen(SettingsUI.getInstance(McIf.mc().currentScreen));
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Configuration", TextFormatting.GRAY + "Change the settings", TextFormatting.GRAY + "to the way you want.", "", TextFormatting.RED + "BETA VERSION", TextFormatting.GREEN + "Left click to select");
    }
}
