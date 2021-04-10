package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;

import static net.minecraft.util.text.TextFormatting.*;

public class LootRunPage extends QuestBookPage {

    List<String> names;
    int selected;
    String selectedName;

    public LootRunPage() {
        super("Your Lootruns", true, IconContainer.lootrunIcon);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Lootruns", TextFormatting.GRAY + "See all lootruns", TextFormatting.GRAY + "you have", TextFormatting.GRAY + "saved in the game.", "", TextFormatting.GREEN + "Left click to select");
    }


    @Override
    public void initGui() {
        super.initGui();
        initBasicSearch();
    }

    private void initBasicSearch() {
        textField.setMaxStringLength(50);
        initDefaultSearchBar();
    }

    private void initDefaultSearchBar() {
        textField.x = width / 2 + 32;
        textField.y = height / 2 - 97;
        textField.width = 113;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            // render search UI
            //hoveredText = getSearchHandler().drawScreenElements(this, render, mouseX, mouseY, x, y, posX, posY, selected);

            // back to menu button
            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            } else {
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            // title text

            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            // but next and back button
            if (currentPage == pages) {
                render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
            } else {
                if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                }
            }

            if (currentPage == 1) {
                render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            } else {
                if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                }
            }

            // available lootruns
            int currentY = 12;



            if (names.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (names.size() <= i) {
                        break;
                    }
                    render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);

                    render.drawString(names.get(i), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;

                    currentY += 13;
                    if (hovered) {
                        hoveredText = Arrays.asList(names.get(i) + ".json", TextFormatting.GREEN + "Left click to select");
                        selected = i;
                        selectedName = names.get(i);
                    }
                }
                renderHoveredText(hoveredText, mouseX, mouseY);

            }
            else {
                String textToDisplay = "No Lootruns were found!\nTry changing your search.";

                for (String line : textToDisplay.split("\n")) {
                    currentY += render.drawSplitString(line, 120, x + 26, y - 95 + currentY, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE) * 10 + 2;
                }
            }
        }
        ScreenRenderer.endGL();
    }

    @Override
    protected void searchUpdate(String currentText) {
        names = LootRunManager.getStoredLootruns();

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            names.removeIf(c -> !doesSearchMatch(c, lowerCase));
        }

        //names.sort(Comparator.comparingLong(l -> lengt));

        pages = names.size() <= 13 ? 1 : (int) Math.ceil(names.size() / 13d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    @Override
    protected void drawSearchBar(int centerX, int centerY) {
        super.drawSearchBar(centerX, centerY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) { // page forwards button
            goForward();
            return;
        }
        else if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) { // page backwards button
            goBack();
            return;
        }
        else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // quest book back button
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.MAIN.getPage().open(false);
            return;
        }

        int currentY = 12 + 13 * (selected % 13);

        boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;

        if (hovered) {
            System.out.println(selectedName);

            boolean result = LootRunManager.loadFromFile(selectedName);

            String message;
            if (result) message = GREEN + "Loaded loot run " + selectedName + " successfully! " + GRAY + "(" + LootRunManager.getActivePath().getChests().size() + " chests)";
            else {
                message = RED + "The specified loot run doesn't exist!";
            }
            System.out.println(message);

            if (!LootRunManager.getActivePath().getPoints().isEmpty()) {
                Location start = LootRunManager.getActivePath().getPoints().get(0);
                String startingPointMsg = GRAY + "Loot run starts at [" + (int) start.getX() + ", " + (int) start.getZ() + "]";
                System.out.println(startingPointMsg);
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}