/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DialoguePage extends QuestBookListPage<String> {

    final static List<String> textLines = Arrays.asList("Here you can read your", "last few conversations with", "NPCs on this class");


    public DialoguePage() {
        super("Dialogue", true, IconContainer.dialogueIcon);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Dialogue", TextFormatting.GRAY + "Read your last", TextFormatting.GRAY + "few conversations", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    public void open(boolean showAnimation) {
        super.open(showAnimation);

        QuestManager.updateAnalysis(AnalysePosition.QUESTS, true, true);
    }

    @Override
    protected void drawEntry(String entryInfo, int index, boolean hovered) {
        int x = width / 2;
        int y = height / 2;
        int currentY = 13 + index * 12;

        render.drawString(entryInfo, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
    }

    @Override
    public void postEntries(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);

        // Explanatory Text
        drawTextLines(textLines, x - 154, y - 30, 1);

        // Back Button
        drawMenuButton(x, y, posX, posY);
    }

    @Override
    protected String getEmptySearchString() {
        return  "You don't have any dialogue!";
    }

    @Override
    protected List<List<String>> getSearchResults(String text) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();

        //Takes in current dialogue, a list of dialogues each made of lines
        //and flattens it into lines, then splits those lines
        List<List<String>> lines = QuestManager.getCurrentDialogue()
                .stream() //List of dialogues
                .flatMap(Collection::stream) //List of lines
                .map(line -> ScreenRenderer.fontRenderer.listFormattedStringToWidth(line, 120)) //List of split lines
                .collect(Collectors.toList());

        for (List<String> line : lines) {
            if (page.size() + line.size() > 14) {
                pages.add(page);
                page = new ArrayList<>();
            }

            page.addAll(line);
        }

        return pages;
    }

    @Override
    protected boolean isHovered(int index, int posX, int posY) {
        return false;
    }

    @Override
    protected List<String> getHoveredText(String entryInfo) {
        return new ArrayList<>();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);

        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // Update Data
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestManager.updateAllAnalyses(true);
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleEntryClick(String itemInfo, int mouseButton) {
        //NO-OP
    }
}
