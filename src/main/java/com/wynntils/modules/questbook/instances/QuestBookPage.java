/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.reference.Easing;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestBookPage extends GuiScreen {

    protected final ScreenRenderer render = new ScreenRenderer();
    private long time;
    private boolean open = false;

    // Page specific information
    private final String title;
    private final IconContainer icon;
    protected boolean showAnimation;
    protected List<String> hoveredText = new ArrayList<>();

    private final boolean showSearchBar;
    protected int currentPage;
    protected boolean acceptNext, acceptBack;
    protected int pages = 1;
    protected int selected;
    protected GuiTextField textField = null;

    // Animation
    protected long lastTick;
    protected boolean animationCompleted;

    protected long delay = McIf.getSystemTime();

    // Colours
    protected static final CustomColor background_1 = CustomColor.fromInt(0x000000, 0.3f);
    protected static final CustomColor background_2 = CustomColor.fromInt(0x000000, 0.2f);
    protected static final CustomColor background_3 = CustomColor.fromInt(0x00ff00, 0.3f);
    protected static final CustomColor background_4 = CustomColor.fromInt(0x008f00, 0.2f);

    protected static final CustomColor unselected_cube = new CustomColor(0, 0, 0, 0.2f);
    protected static final CustomColor selected_cube = new CustomColor(0, 0, 0, 0.3f);
    protected static final CustomColor selected_cube_2 = CustomColor.fromInt(0x11c920, 0.3f);

    /**
     * Base class for all questbook pages
     * @param title a string displayed on the left page
     * @param showSearchBar boolean of whether there is a searchbar needed for that page
     * @param icon the icon that corresponds to the page
     */
    public QuestBookPage(String title, boolean showSearchBar, IconContainer icon) {
        this.title = title;
        this.showSearchBar = showSearchBar;
        this.icon = icon;
    }

    /**
     * Resets all basic information needed for various features on all pages
     */
    @Override
    public void initGui() {
        if (open) {
            if (!showSearchBar) return;

            textField.x = width / 2 + 32;
            textField.y = height / 2 - 97;
            return;
        }

        open = true;
        currentPage = 1;
        selected = -1;
        searchUpdate("");
        refreshAccepts();
        time = McIf.getSystemTime();
        lastTick = McIf.getSystemTime();

        if (showSearchBar) {
            textField = new GuiTextField(0, McIf.mc().fontRenderer, width / 2 + 32, height / 2 - 97, 113, 23);
            textField.setFocused(!QuestBookConfig.INSTANCE.searchBoxClickRequired);
            textField.setMaxStringLength(50);
            textField.setEnableBackgroundDrawing(false);
            textField.setCanLoseFocus(QuestBookConfig.INSTANCE.searchBoxClickRequired);
            textField.setGuiResponder(new GuiPageButtonList.GuiResponder() {

                @Override
                public void setEntryValue(int id, String value) {
                    searchUpdate(value);
                }

                @Override
                public void setEntryValue(int id, float value) {}

                @Override
                public void setEntryValue(int id, boolean value) {}
            });
        }

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = height / 2;

        ScreenRenderer.beginGL(0, 0);
        {
            if (showAnimation) {
                float animationTick = Easing.BACK_IN.ease((McIf.getSystemTime() - time) + 1000, 1f, 1f, 600f);
                animationTick /= 10f;

                if (animationTick <= 1) {
                    ScreenRenderer.scale(animationTick);

                    x = (int) (x / animationTick);
                    y = (int) (y / animationTick);
                } else {
                    ScreenRenderer.resetScale();
                    showAnimation = false;
                }

            } else {
                x = width / 2;
                y = height / 2;
            }

            render.drawRect(Textures.UIs.quest_book, x - (339 / 2), y - (220 / 2), 0, 0, 339, 220);

            ScreenRenderer.scale(0.7f);
            render.drawString(CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE ? "Stable v" + Reference.VERSION : "CE Build " + (Reference.BUILD_NUMBER == -1 ? "?" : Reference.BUILD_NUMBER), (x - 80) / 0.7f, (y + 86) / 0.7f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            ScreenRenderer.resetScale();

            render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);

            ScreenRenderer.scale(2f);
            render.drawString(title, (x - 158f) / 2.0f, (y - 74) / 2.0f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            ScreenRenderer.resetScale();

            /*Render search bar when needed*/
            if (showSearchBar) {
                drawSearchBar(x, y);
            }
        }

        ScreenRenderer.endGL();
    }

    protected void drawSearchBar(int centerX, int centerY) {
        render.drawRect(Textures.UIs.quest_book, centerX + 13, centerY - 109, 52, 255, 133, 23);
        textField.drawTextBox();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!showSearchBar) return;

        textField.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 1) return;
        if (mouseX < textField.x || mouseX >= textField.x + textField.width) return;
        if (mouseY < textField.y || mouseY >= textField.y + textField.height) return;

        textField.setText("");
        searchUpdate("");
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        if (mDWheel <= -1 && (McIf.getSystemTime() - delay >= 15)) {
            if (acceptNext) {
                delay = McIf.getSystemTime();
                goForward();
            }
        } else if (mDWheel >= 1 && (McIf.getSystemTime() - delay >= 15)) {
            if (acceptBack) {
                delay = McIf.getSystemTime();
                goBack();
            }
        }
        super.handleMouseInput();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT || keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL) return;
        if (showSearchBar) {
            textField.textboxKeyTyped(typedChar, keyCode);
            currentPage = 1;
            refreshAccepts();
            updateSearch();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        if (showSearchBar) {
            textField.updateCursorCounter();
        }
    }

    protected void renderHoveredText(int mouseX, int mouseY) {
        ScreenRenderer.beginGL(0, 0);
        {
            GlStateManager.disableLighting();
            if (hoveredText != null) drawHoveringText(hoveredText, mouseX, mouseY);
        }
        ScreenRenderer.endGL();
    }

    protected void searchUpdate(String currentText) { }

    protected boolean doesSearchMatch(String toCheck, String searchText) {
        return QuestBookConfig.INSTANCE.useFuzzySearch ? StringUtils.fuzzyMatch(toCheck, searchText) : toCheck.contains(searchText);
    }

    protected void goForward() {
        if (acceptNext) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            currentPage++;
            refreshAccepts();
        }
    }

    protected void goBack() {
        if (acceptBack) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            currentPage--;
            refreshAccepts();
        }
    }

    protected void refreshAccepts() {
        acceptBack = currentPage > 1;
        acceptNext = currentPage < pages;
    }

    public void open(boolean showAnimation) {
        this.showAnimation = showAnimation;

        if (showAnimation) WynntilsSound.QUESTBOOK_OPENING.play(); // sfx
        McIf.mc().displayGuiScreen(this);
    }

    public void updateSearch() {
        if (showSearchBar && textField != null) {
            searchUpdate(textField.getText());
        }
    }

    public IconContainer getIcon() {
        return icon;
    }

    /**
     * Can be null
     * @return a list of strings - each index representing a new line.
     */
    public List<String> getHoveredDescription() { return hoveredText; }

    /**
     * Draw the Menu Button
     *
     * @param x drawingOrigin x
     * @param y drawingOrigin y
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     */
    protected void drawMenuButton(int x, int y, int posX, int posY) {
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
            return;
        }

        render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
    }

    /**
     * Draws the Forward and Back Button
     *
     * @param x drawingOrigin x
     * @param y drawingOrigin y
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     */
    protected void drawForwardAndBackButtons(int x, int y, int posX, int posY, int currentPage, int pages) {
        drawForwardButton(x, y, posX, posY, currentPage == pages);
        drawBackButton(x, y, posX, posY, currentPage == 1);
    }

    /**
     * Draws the Forward Button
     *
     * @param x drawingOrigin x
     * @param y drawingOrigin y
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     * @param atLimit whether the button can be pressed
     */
    protected void drawForwardButton(int x, int y, int posX, int posY, boolean atLimit) {
        //Reached page limit
        if (atLimit) {
            render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
            return;
        }

        //Hovering
        if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
            render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
            return;
        }

        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
    }

    /**
     * Draws the Back button
     *
     * @param x drawingOrigin x
     * @param y drawingOrigin y
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     * @param atLimit whether the button can be pressed
     */
    protected void drawBackButton(int x, int y, int posX, int posY, boolean atLimit) {
        //Reached page limit
        if (atLimit) {
            render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            return;
        }

        //Hovering
        if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
            render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            return;
        }

        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
    }

    /**
     * Draws a list of text lines
     *
     * @param lines list of lines to be rendered
     * @param startX x to start rendering at
     * @param startY y to start rendering at
     */
    protected void drawTextLines(List<String> lines, int startX, int startY, int scale) {
        int currentY = startY;
        for (String line : lines) {
            ScreenRenderer.scale(scale);
            render.drawString(line, startX, currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            ScreenRenderer.resetScale();
            currentY += 10 * scale;
        }
    }

    /**
     * Checks if menu button is clicked, goes back to MainPage
     *
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     *
     */
    protected void checkMenuButton(int posX, int posY) {
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // Back Button
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.MAIN.getPage().open(false);
        }
    }

    /**
     * Checks if Forward or Back button is clicked, changes page
     *
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     *
     */
    protected void checkForwardAndBackButtons(int posX, int posY) {
        checkForwardButton(posX, posY);
        checkBackButton(posX, posY);
    }

    /**
     * Checks if Forward button is clicked, goes forward a page
     *
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     *
     */
    protected void checkForwardButton(int posX, int posY) {
        if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) { // Next Page Button
            goForward();
        }
    }

    /**
     * Checks if Back button is clicked, goes back a page
     *
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     *
     */
    protected void checkBackButton(int posX, int posY) {
        if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) { // Back Page Button
            goBack();
        }
    }

}
