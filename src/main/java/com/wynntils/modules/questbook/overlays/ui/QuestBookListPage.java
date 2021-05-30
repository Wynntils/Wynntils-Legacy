package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Extend this class when the QuestBook has a list, contains methods for pages
 * @param <T>
 */
public abstract class QuestBookListPage<T> extends QuestBookPage {
    //Search is a list of pages, where a page contains 13 or less T
    protected List<List<T>> search = new ArrayList<>();
    protected T selectedItem;

    //Instantiated
    Comparator<T> sort;
    BiPredicate<String, T> filter;
    /**
     * Base class for all questbook list pages
     *
     * @param title         a string displayed on the left page
     * @param showSearchBar boolean of whether there is a searchbar needed for that page
     * @param icon          the icon that corresponds to the page
     */
    public QuestBookListPage(String title, boolean showSearchBar, IconContainer icon, Comparator<T> sort, BiPredicate<String, T> filter) {
        super(title, showSearchBar, icon);
        this.sort = sort;
        this.filter = filter;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);
        hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            // Page Text
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            // Draw all Search Results
            if (search.size() > 0) {
                List<T> page = search.get(currentPage);

                if (page.size() > 0) {

                    for (int i = 0; i < page.size(); i++) {
                        if (search.size() <= i) {
                            break;
                        }

                        T currentItem = page.get(i);

                        if (isHovered(i, posX, posY) && !showAnimation) {
                            //hovered
                            drawItem(currentItem, i, true);

                            selectedItem = currentItem;
                            selected = i;
                            hoveredText = getHoveredText(selectedItem);
                        } else {
                            if (this.selected == i) {
                                selectedItem = null;
                                selected = -1;
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
        }
        ScreenRenderer.endGL();
        renderHoveredText(mouseX, mouseY);
    }

    @Override
    public void searchUpdate(String currentText) {
        search = getSearchResults(currentText);

        pages = search.size() <= 13 ? 1 : (int) Math.ceil(search.size() / 13d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (selectedItem != null && search.size() > selected) {
            handleItemClick(selectedItem, mouseButton);
        }

    }

    /**
     * Trims a string to the width desired and add "..." to the end
     *
     * @param str string to trim
     * @param width width to trim to trim too
     * @return trimmed string
     */

    //TODO - implement it when using
    public static String getTrimmedName(String str, int width) {
        if (!(McIf.mc().fontRenderer.getStringWidth(str) > width)) return str;

        str += "...";

        while (McIf.mc().fontRenderer.getStringWidth(str) > width) {
            str = str.substring(0, str.length() - 4).trim() + "...";
        }

        return str;
    }

    /**
     * Get what to display when search results are empty
     */
    protected abstract String getEmptySearchString();

    /**
     * Returns the search results, should be sorted
     */
    protected abstract List<List<T>> getSearchResults(String text);

    /**
     * Draws an entry in search
     * @param itemInfo The info for item
     * @param index The index of the item
     * @param hovered Whether the item is hovered
     */
    protected abstract void drawItem(T itemInfo, int index, boolean hovered);

    protected abstract boolean isHovered(int index, int posX, int posY);

    /**
     * Gets hovered text
     */
    protected abstract List<String> getHoveredText(T itemInfo);

    /**
     * Handles a mouse input on an item
     */
    protected abstract void handleItemClick(T itemInfo, int mouseButton);
}
