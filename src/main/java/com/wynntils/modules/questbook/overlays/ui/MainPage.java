package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.ui.OverlayPositionsUI;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.managers.QuestBookHandler;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage extends QuestBookPage {

    public MainPage() {
        super("User Profile", false, 0, null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2; int y = height / 2;
        int posX = (x - mouseX); int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            int right = (posX + 80);
            if(posX >= 0) right = 80;

            int up = (posY) + 30;
            if(posY >= 109) up = 109;
            if(posY <= -109) up = -109;

            GuiInventory.drawEntityOnScreen(x + 80, y + 30, 30, right, up, Minecraft.getMinecraft().player);
        }
        ScreenRenderer.endGL();

        ScreenRenderer.beginGL(0, 0);
        {
            render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

            String guild;
            if (WebManager.getPlayerProfile() != null)
                guild = WebManager.getPlayerProfile().getGuildRank() != null ? WebManager.getPlayerProfile().getGuildName() + " " + WebManager.getPlayerProfile().getGuildRank().getStars() : WebManager.getPlayerProfile().getGuildName();
            else
                guild = "";
            render.drawString(TextFormatting.DARK_AQUA + guild, x + 80, y - 53, CommonColors.CYAN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString(Minecraft.getMinecraft().player.getName(), x + 80, y - 43, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString(PlayerInfo.getPlayerInfo().getCurrentClass().toString() + " Level " + PlayerInfo.getPlayerInfo().getLevel(), x + 80, y + 40, CommonColors.PURPLE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("In Development", x + 80, y + 50, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            int boxTop = y - 15;
            int boxBottom = y + 15;
            for (QuestBookPage qbp: QuestBookHandler.getQuestBookPages()) {
                if (qbp.getIcon() == null || !(qbp.getSlotNb() > (currentPage - 1) * 4 && qbp.getSlotNb() <= (currentPage) * 4)) continue;

                boolean hovering = posX >= (120 - 35 * ((qbp.getSlotNb() - 1) % 4)) && posX <= (150 - 35 * ((qbp.getSlotNb() - 1) % 4)) && posY >= -14 && posY <= 15;
                int leftX = x - 150 + 35 * ((qbp.getSlotNb() - 1) % 4);

                if (hovering) {
                    selected = qbp.getSlotNb();
                    render.drawRect(selected_cube, leftX, boxTop, leftX + 30, boxBottom);
                } else {
                    render.drawRect(unselected_cube, leftX, boxTop, leftX + 30, boxBottom);
                    selected = 0;
                }
                render.drawRect(Textures.UIs.quest_book, leftX + (15 - qbp.getIcon().getWidth()/2), boxTop + (15 - qbp.getIcon().getHeight()/2), qbp.getIcon().getX1(), qbp.getIcon().getY1(hovering), qbp.getIcon().getWidth(), qbp.getIcon().getHeight());
                if (hovering) {
                    hoveredText = qbp.getHoveredDescription();
                }
            }

            render.drawString("Select an option to continue", x - 81, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("Welcome to Wynntils. You can", x - 155, y + 25, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("see your statistics on the right", x - 155, y + 35, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("or select some of the options", x - 155, y + 45, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("above for more features.", x - 155, y + 55, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            render.drawRect(Textures.UIs.quest_book, x + 20, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 48, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 74, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 100, y - 90, 224, 253, 17, 18);
            render.drawRect(Textures.UIs.quest_book, x + 125, y - 90, 224, 253, 17, 18);

            ScreenRenderer.scale(2f);
            render.drawString("User Profile", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }
        ScreenRenderer.endGL();
        renderHoveredText(hoveredText, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (selected == 1) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookHandler.openQuestBookPage(false, QuestsPage.class);
        } else if(selected == 2) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookHandler.openQuestBookPage(false, SettingsPage.class);
        } else if(selected == 3) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            //page = com.wynntils.modules.questbook.enums.QuestBookPage.ITEM_GUIDE;
        } else if (selected == 4) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            OverlayPositionsUI ui = new OverlayPositionsUI(ModCore.mc().currentScreen);
            UI.setupUI(ui);
            ModCore.mc().displayGuiScreen(ui);
        }
    }
}
