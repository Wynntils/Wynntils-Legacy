package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class QuestBookListPage<T> extends QuestBookPage {
    private List<T> search = new ArrayList<>();
    T selectedItem;
    Comparator<T> sort;

    /**
     * Base class for all questbook list pages
     *
     * @param title         a string displayed on the left page
     * @param showSearchBar boolean of whether there is a searchbar needed for that page
     * @param icon          the icon that corresponds to the page
     */
    public QuestBookListPage(String title, boolean showSearchBar, IconContainer icon, Comparator<T> sort) {
        super(title, showSearchBar, icon);
        this.sort = sort;
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
            int currentY = 12;
            if (search.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (search.size() <= i) {
                    break;
                }

                    T currentItem;
                    try {
                        currentItem = search.get(i);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }

                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !showAnimation) {
                        //hovered
                        drawItem(currentItem, i, true);

                        selectedItem = currentItem;
                        hoveredText = getHoveredText();
                    } else {
                        //not hovered
                        drawItem(currentItem, i, false);
                    }

                    //TODO - see if this is necessary
                    render.color(1, 1, 1, 1);
                    drawIcon(currentItem, i);

                    currentY += 13;
                }
            } else {
                String textToDisplay = getBlankDisplayText();

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
    protected void searchUpdate(String currentText) {
        search = getSearchResults();

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            search.removeIf(c -> !doesSearchMatch(getName(c), lowerCase));
        }

        search.sort(sort);

        pages = search.size() <= 13 ? 1 : (int) Math.ceil(search.size() / 13d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    protected abstract String getBlankDisplayText();

    protected abstract void drawIcon(T item, int index);

    protected abstract String getName(T item);

    protected abstract List<T> getSearchResults();

    protected abstract void drawItem(T item, int index, boolean hovered);

    protected abstract List<String> getHoveredText();

}
