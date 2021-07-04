/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainPage extends QuestBookPage {

    public MainPage() {
        super("User Profile", false, null);
    }

    @Override
    public void initGui() {
        super.initGui();
        pages = (int) Math.ceil(Arrays.stream(QuestBookPages.values()).max(Comparator.comparingInt(QuestBookPages::getSlotNb)).get().getSlotNb() / 4d);
        refreshAccepts();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2; int y = height / 2;
        int posX = (x - mouseX); int posY = (y - mouseY);
        hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            int right = Math.min(posX + 80, 80);

            int up = Math.min(Math.max((posY) + 30, -109), 109);

            GuiInventory.drawEntityOnScreen(x + 80, y + 30, 30, right, up, McIf.player());
        }
        ScreenRenderer.endGL();

        ScreenRenderer.beginGL(0, 0);
        {
            String guild;
            if (WebManager.getPlayerProfile() != null)
                guild = WebManager.getPlayerProfile().getGuildRank() != null ? WebManager.getPlayerProfile().getGuildName() + " " + WebManager.getPlayerProfile().getGuildRank().getStars() : WebManager.getPlayerProfile().getGuildName();
            else
                guild = "";
            render.drawString(TextFormatting.DARK_AQUA + guild, x + 80, y - 53, CommonColors.CYAN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString(McIf.player().getName(), x + 80, y - 43, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString(PlayerInfo.get(CharacterData.class).getCurrentClass().toString() + " Level " + PlayerInfo.get(CharacterData.class).getLevel(), x + 80, y + 40, CommonColors.PURPLE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("In Development", x + 80, y + 50, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString(WebManager.getCurrentSplash(), x + 82, y + 70, CommonColors.RAINBOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            selected = 0;
            for (QuestBookPages val: QuestBookPages.values()) {
                QuestBookPage qbp = val.getPage();
                if (qbp.getIcon() == null) continue;

                int leftX = x - 150 + 35 * ((val.getSlotNb() - 1) % 4);
                int topY = y - 45 + 35 * ((val.getSlotNb() - 1) / 4);

                boolean hovering = posX >= x - leftX - 30 && posX <= x - leftX && posY >= y - topY - 30 && posY <= y - topY;

                if (hovering) {
                    selected = val.getSlotNb();
                    render.drawRect(selected_cube, leftX, topY, leftX + 30, topY + 30);
                } else {
                    render.drawRect(unselected_cube, leftX, topY, leftX + 30, topY + 30);
                }
                render.drawRect(Textures.UIs.quest_book, leftX + (15 - qbp.getIcon().getWidth()/2), topY + (15 - qbp.getIcon().getHeight()/2), qbp.getIcon().getX1(), qbp.getIcon().getY1(hovering), qbp.getIcon().getWidth(), qbp.getIcon().getHeight());
                if (hovering) {
                    hoveredText = qbp.getHoveredDescription();
                }
            }

            render.drawSplitString("Welcome to Wynntils. You can see your statistics on the right or select some of the options above for more features",
                    155, x - 150, y + 30, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            render.drawRect(Textures.UIs.quest_book, x + 20, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 48, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 74, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 100, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 125, y - 90, 224, 253, 17, 18);
        }
        ScreenRenderer.endGL();
        renderHoveredText(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (selected > 0) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.getPageBySlot(selected).open(false);
        }
    }
}
