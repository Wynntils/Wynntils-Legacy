package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.modules.questbook.instances.QuestBookPage;

public class MainPage extends QuestBookPage {

    public MainPage() {
        this.title = "User Profile";
        this.showSearchBar = false;
        this.icon = null;
    }

    public MainPage(boolean requestOpening) {
        this();
        this.requestOpening = requestOpening;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }
}
