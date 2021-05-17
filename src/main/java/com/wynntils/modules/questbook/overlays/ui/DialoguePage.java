/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DialoguePage extends QuestBookPage {

    private List<List<String>> dialogueSearch = new ArrayList<>();
    final static List<String> textLines = Arrays.asList("Here you can read your", "last few conversations with", "NPCs on this class");

    public DialoguePage() {
        super("Dialogue", true, IconContainer.dialogueIcon);
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
            // Explanatory Text
            drawTextLines(textLines, x - 154, y - 30, 1);

            // Back Button
            drawMenuButton(x, y, posX, posY);

//            // Progress Icon/Mini-Quest Switcher
//            render.drawRect(Textures.UIs.quest_book, x - 87, y - 100, 16, 255 + (showingMiniQuests ? 16 : 0), 16, 16);
//            if (posX >= 71 && posX <= 87 && posY >= 84 && posY <= 100) {
//                hoveredText = new ArrayList<>(showingMiniQuests ? QuestManager.getMiniQuestsLore() : QuestManager.getQuestsLore());
//
//                if (!hoveredText.isEmpty()) {
//                    hoveredText.set(0, showingMiniQuests ? "Mini-Quests:" : "Quests:");
//                    hoveredText.add(" ");
//                    hoveredText.add(TextFormatting.GREEN + "Click to see " + (showingMiniQuests ? "Quests" : "Mini-Quests"));
//                }
//            }
//
            // Page Text
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            drawForwardAndBackButtons(x, y, posX, posY, currentPage, pages);
//
//            // Draw all Quests
            int currentY = 12;
            if (dialogueSearch.size() >= currentPage) {
                List<String> page = dialogueSearch.get(currentPage - 1);
                for (String line : page) {
                    ScreenRenderer.fontRenderer.drawString(line, x + 9, y - 96 + currentY, 0);
                    currentY += 12;
                }
            }
//            if (questSearch.size() > 0) {
//                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
//                    if (questSearch.size() <= i) {
//                        break;
//                    }
//
//                    QuestInfo selected;
//                    try {
//                        selected = questSearch.get(i);
//                    } catch (IndexOutOfBoundsException ex) {
//                        break;
//                    }
//
//                    List<String> lore = new ArrayList<>(selected.getLore());
//                    lore.add("");
//
//                    int animationTick = -1;
//                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !showAnimation) {
//                        if (lastTick == 0 && !animationCompleted) {
//                            lastTick = Minecraft.getSystemTime();
//                        }
//
//                        this.selected = i;
//
//                        if (!animationCompleted) {
//                            animationTick = (int) (Minecraft.getSystemTime() - lastTick) / 2;
//                            if (animationTick >= 133 && selected.getFriendlyName().equals(selected.getName())) {
//                                animationCompleted = true;
//                                animationTick = 133;
//                            }
//                        } else {
//                            if (!selected.getFriendlyName().equals(selected.getName())) {
//                                animationCompleted = false;
//                                lastTick = Minecraft.getSystemTime() - 133 * 2;
//                            }
//
//                            animationTick = 133;
//                        }
//
//                        int width = Math.min(animationTick, 133);
//                        animationTick -= 133 + 200;
//                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
//                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
//                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
//                        } else {
//                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
//                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
//                        }
//
//                        overQuest = selected;
//                        hoveredText = lore;
//                        GlStateManager.disableLighting();
//                    } else {
//                        if (this.selected == i) {
//                            animationCompleted = false;
//
//                            if (!showAnimation) lastTick = 0;
//                            overQuest = null;
//                        }
//
//                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
//                            render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
//                        } else {
//                            render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
//                        }
//                    }
//
//                    render.color(1, 1, 1, 1);
//                    if (selected.getStatus() == QuestStatus.COMPLETED) {
//                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
//                        lore.remove(lore.size() - 1);
//                        lore.remove(lore.size() - 1);
//                        lore.remove(lore.size() - 1);
//                    } else if (selected.getStatus() == QuestStatus.CANNOT_START) {
//                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
//                        lore.remove(lore.size() - 1);
//                        lore.remove(lore.size() - 1);
//                    } else if (selected.getStatus() == QuestStatus.CAN_START) {
//                        lore.remove(lore.size() - 2);
//                        if (!lore.remove(lore.size() - 2).isEmpty()) lore.remove(lore.size() - 2); // quest is tracked, has extra line
//                        if (selected.isMiniQuest()) {
//                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 272, 245, 11, 7);
//                        } else {
//                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
//                        }
//                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
//                            lore.add(TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
//                        } else {
//                            lore.add(TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
//                        }
//                    } else if (selected.getStatus() == QuestStatus.STARTED) {
//                        lore.remove(lore.size() - 2);
//                        if (!lore.remove(lore.size() - 2).isEmpty()) lore.remove(lore.size() - 2); // quest is tracked, has extra line
//                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
//                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
//                            lore.add(TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
//                        } else {
//                            lore.add(TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
//                        }
//                    }
//
//                    if (selected.hasTargetLocation()) {
//                        lore.add(TextFormatting.YELLOW + (TextFormatting.BOLD + "Middle click to view on map!"));
//                    }
//                    lore.add(TextFormatting.GOLD + (TextFormatting.BOLD + "Right click to open on the wiki!"));
//
//                    String name = selected.getFriendlyName();
//                    if (this.selected == i && !name.equals(selected.getName()) && animationTick > 0) {
//                        name = selected.getName();
//                        int maxScroll = fontRenderer.getStringWidth(name) - (120 - 10);
//                        int scrollAmount = (animationTick / 20) % (maxScroll + 60);
//
//                        if (maxScroll <= scrollAmount && scrollAmount <= maxScroll + 40) {
//                            // Stay on max scroll for 20 * 40 animation ticks after reaching the end
//                            scrollAmount = maxScroll;
//                        } else if (maxScroll <= scrollAmount) {
//                            // And stay on minimum scroll for 20 * 20 animation ticks after looping back to the start
//                            scrollAmount = 0;
//                        }
//
//                        ScreenRenderer.enableScissorTestX(x + 26, 13 + 133 - 2 - 26);
//                        {
//                            render.drawString(name, x + 26 - scrollAmount, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
//                        }
//                        ScreenRenderer.disableScissorTest();
//                    } else {
//                        render.drawString(name, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
//                    }
//
//                    currentY += 13;
//                }
//            } else {
//                String textToDisplay;
//                if (QuestManager.getCurrentQuests().size() == 0 || textField.getText().equals("") ||
//                        (showingMiniQuests && QuestManager.getCurrentQuests().stream().noneMatch(QuestInfo::isMiniQuest))) {
//                    textToDisplay = String.format("Loading %s...\nIf nothing appears soon, try pressing the reload button.", showingMiniQuests ? "Mini-Quests" : "Quests");
//                } else {
//                    textToDisplay = String.format("No %s found!\nTry searching for something else.", showingMiniQuests ? "mini-quests" : "quests");
//                }
//
//                for (String line : textToDisplay.split("\n")) {
//                    currentY += render.drawSplitString(line, 120, x + 26, y - 95 + currentY, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE) * 10 + 2;
//                }
//
//                updateSearch();
//            }
//
//            // Reload Data button
//            if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
//                hoveredText = Arrays.asList("Reload Button!", TextFormatting.GRAY + "Reloads all quest data.");
//                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 218, 281, 240, 303);
//            } else {
//                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 240, 281, 262, 303);
//            }
//
//            // Sort method button
//            int dX = 0;
//            if (-11 <= posX && posX <= -1 && 89 <= posY && posY <= 99) {
//                hoveredText = sort.hoverText.stream().map(I18n::format).collect(Collectors.toList());
//                dX = 22;
//            }
//            render.drawRect(Textures.UIs.quest_book, x + 1, y - 99, x + 12, y - 88, sort.tx1 + dX, sort.ty1, sort.tx2 + dX, sort.ty2);
        }
        ScreenRenderer.endGL();
        renderHoveredText(mouseX, mouseY);
    }

    @Override
    protected void searchUpdate(String currentText) {
        dialogueSearch = new ArrayList<>();

        for (List<String> conversation : QuestManager.getCurrentDialogue()) {
            List<String> currentPage = new ArrayList<>();
            List<List<String>> conversationWrapped = conversation
                    .stream()
                    .map((original) -> ScreenRenderer.fontRenderer.listFormattedStringToWidth(original, 145))
                    .collect(Collectors.toList());

            for (List<String> line : conversationWrapped) {
                if (currentPage.size() + line.size() > 14) {
                    dialogueSearch.add(currentPage);
                    currentPage = new ArrayList<>();
                }
                currentPage.addAll(line);
            }

            dialogueSearch.add(currentPage);
        }

//        System.out.println(dialogueSearch);
//        if (currentText != null && !currentText.isEmpty()) {
//            String lowerCase = currentText.toLowerCase();
//            questSearch.removeIf(c -> !doesSearchMatch(c.getName().toLowerCase(), lowerCase));
//        }
//
//        questSearch.sort(sort.comparator);
//
        pages = dialogueSearch.size() < 1 ? 1 : dialogueSearch.size();
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);
        checkForwardAndBackButtons(posX, posY);

        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // Update Data
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestManager.updateAllAnalyses(true);
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Dialogue", TextFormatting.GRAY + "Read your last", TextFormatting.GRAY + "few conversations", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    public void open(boolean showAnimation) {
        super.open(showAnimation);

        QuestManager.readQuestBook();
    }

}
