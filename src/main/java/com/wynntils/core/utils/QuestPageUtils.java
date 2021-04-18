package com.wynntils.core.utils;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;

public class QuestPageUtils {
    /**
     * Draw the Menu Button
     *
     * @param render the ScreenRender to be used
     * @param x x (from drawingOrigin) to render at
     * @param y y (from drawingOrigin) to render at
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     *
     * @return the hovered text
     */
    public static List<String> drawMenuButton(ScreenRenderer render, int x, int y, int posX, int posY) {
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
        }

        render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);

        return null;
    }

    /**
     * Draws the Forward and Back Button
     *
     * @param render the ScreenRender to be used
     * @param x x (from drawingOrigin) to render at
     * @param y y (from drawingOrigin) to render at
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     */
    public static void drawForwardAndBackButtons(ScreenRenderer render, int x, int y, int posX, int posY, int currentPage, int pages) {
        drawForwardButton(render, x, y, posX, posY, currentPage == pages);
        drawBackButton(render, x, y, posX, posY, currentPage == 1);
    }

    /**
     * Draws the Forward Button
     *
     * @param render the ScreenRender to be used
     * @param x x (from drawingOrigin) to render at
     * @param y y (from drawingOrigin) to render at
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     * @param atLimit whether the button can be pressed
     */
    public static void drawForwardButton(ScreenRenderer render, int x, int y, int posX, int posY, boolean atLimit) {
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
     * @param render the ScreenRender to be used
     * @param x x (from drawingOrigin) to render at
     * @param y y (from drawingOrigin) to render at
     * @param posX mouseX (from drawingOrigin)
     * @param posY mouseY (from drawingOrigin)
     * @param atLimit whether the button can be pressed
     */
    public static void drawBackButton(ScreenRenderer render, int x, int y, int posX, int posY, boolean atLimit) {
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
}
