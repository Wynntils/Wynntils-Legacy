/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extend this class when a QuestBookPage has a list, contains methods for pages
 * @param <T>
 */
public class QuestBookListPage<T> extends QuestBookPage {
    //Search is a list of pages, where a page contains the items on that page
    protected List<List<T>> search = new ArrayList<>();
    //Selected Item is the item selected
    protected T selectedItem;

    /**
     * Base class for all questbook list pages
     *
     * @param title         a string displayed on the left page
     * @param showSearchBar boolean of whether there is a searchbar needed for that page
     * @param icon          the icon that corresponds to the page
     */
    public QuestBookListPage(String title, boolean showSearchBar, IconContainer icon) {
        super(title, showSearchBar, icon);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);

        ScreenRenderer.beginGL(0, 0);
        {
            preItem(mouseX, mouseY, partialTicks);

            // Page Text
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            //Forward and backward button
            drawForwardAndBackButtons(x, y, posX, posY, currentPage, pages);

            // Draw all Search Results
            if (search.size() > 0) {
                List<T> page = search.get(currentPage - 1);

                if (page.size() > 0) {
                    for (int i = 0; i < page.size(); i++) {
                        T currentItem = page.get(i);

                        if (isHovered(i, posX, posY) && !showAnimation) {
                            //hovered
                            drawItem(currentItem, i, true);

                            selectedItem = currentItem;
                            //selected is set relative to the page
                            selected = i;
                            hoveredText = getHoveredText(selectedItem);
                        } else {
                            if (selected == i) {
                                selectedItem = null;
                            }

                            //not hovered
                            drawItem(currentItem, i, false);
                        }
                    }
                }
            } else {
                String textToDisplay = getEmptySearchString();
                int currentY = 12;

                for (String line : textToDisplay.split("\n")) {
                    currentY += render.drawSplitString(line, 120, x + 26, y - 95 + currentY, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE) * 10 + 2;
                }

                updateSearch();
            }
            postItem(mouseX, mouseY, partialTicks);
        }
        ScreenRenderer.endGL();
        renderHoveredText(mouseX, mouseY);

    }

    @Override
    protected void searchUpdate(String currentText) {
        search = getSearchResults(currentText);

        pages = search.size() == 0 ? 1 : search.size();
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (selectedItem != null && search.get(currentPage - 1).size() > selected) {
            handleItemClick(selectedItem, mouseButton);
        }

        checkForwardAndBackButtons(posX, posY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        selectedItem = null;
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Trims a string to the width desired and add "..." to the end
     *
     * @param str string to trim
     * @param width width to trim to trim too
     * @return trimmed string
     */
    public static String getTrimmedName(String str, int width) {
        if (!(McIf.mc().fontRenderer.getStringWidth(str) > width)) return str;

        str += "...";

        while (McIf.mc().fontRenderer.getStringWidth(str) > width) {
            str = str.substring(0, str.length() - 4).trim() + "...";
        }

        return str;
    }

    /**
     * Called before the item renderering
     */
    protected void preItem(int mouseX, int mouseY, float partialTicks) {};

    /**
     * Draws an entry in search
     * @param itemInfo The info for the item
     * @param index The index of the item relative to the page
     * @param hovered Whether the item is hovered
     */
    protected void drawItem(T itemInfo, int index, boolean hovered) {};

    /**
     * Called after the item renderering
     */
    protected void postItem(int mouseX, int mouseY, float partialTicks) {};

    /**
     * Determines whether an item is hovered
     * @param index
     * @param posX mouse X position relative to center
     * @param posY mouse Y position relative to center
     * @return Whether or not it is hovered
     */
    protected boolean isHovered(int index, int posX, int posY) {
        return false;
    };

    /**
     * Gets hovered text
     */
    protected List<String> getHoveredText(T itemInfo) {
        return null;
    };

    /**
     * Get what to display when search results are empty
     */
    protected String getEmptySearchString() {
       return "";
    };

    /**
     * Returns the search results, should be sorted
     */
    protected List<List<T>> getSearchResults(String text) {
        return null;
    };

    /**
     * Handles a mouse input on an item
     */
    protected void handleItemClick(T itemInfo, int mouseButton) {};
}
