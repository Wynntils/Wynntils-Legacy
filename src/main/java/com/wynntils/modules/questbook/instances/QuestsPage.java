package com.wynntils.modules.questbook.instances;

public class QuestsPage extends QuestBookPage {

    public QuestsPage() {
        this.name = "Quests";
        this.showSearchBar = true;
        this.icon = IconContainer.questPageIcon;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

    public void searchUpdate(String currentText) {
        super.searchUpdate(currentText);
    }
}
