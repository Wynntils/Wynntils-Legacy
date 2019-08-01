package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class QuestsPage extends QuestBookPage {

    private ArrayList<QuestInfo> questSearch;
    private QuestInfo overQuest;

    public QuestsPage() {
        super("Quests", true, IconContainer.questPageIcon);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2; int y = height / 2;
        int posX = (x - mouseX); int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            render.drawString("Here you can see all quests", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("available for you. You can", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("also search for a specific", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("quest just by typing its name.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can go to the next page", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("by clicking on the two buttons", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("or by scrolling your mouse.", x - 154, y + 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can pin/unpin a quest", x - 154, y + 50, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("by clicking on it.", x - 154, y + 60, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            } else {
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            render.drawRect(Textures.UIs.quest_book, x - 86, y - 100, 206, 252, 15, 15);
            if (posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    hoveredText = new ArrayList<>(QuestManager.secretdiscoveryLore);
                    hoveredText.add(0, "Secret Discoveries:");
                } else {
                    hoveredText = new ArrayList<>(QuestManager.discoveryLore);
                    hoveredText.add(" ");
                    hoveredText.add(TextFormatting.GREEN + "Hold shift to see Secret Discoveries!");
                    hoveredText.add(TextFormatting.GREEN + "Click to see all of your Discoveries!");
                }
            }

            int pages = questSearch.size() <= 13 ? 1 : (int) Math.ceil(questSearch.size() / 13d);
            if (pages < currentPage) {
                currentPage = pages;
            }

            //but next and back button
            if (currentPage == pages) {
                render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                acceptNext = false;
            } else {
                acceptNext = true;
                if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                }
            }

            if (currentPage == 1) {
                acceptBack = false;
                render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            } else {
                acceptBack = true;
                if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                }
            }

            //calculating pages
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            //drawing all quests
            int currentY = 12;
            if (questSearch.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (questSearch.size() <= i) {
                        break;
                    }

                    QuestInfo selected;
                    try {
                        selected = questSearch.get(i);
                    } catch (IndexOutOfBoundsException ex) {
                        break;
                    }

                    List<String> lore = new ArrayList<>(selected.getLore());

                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = Minecraft.getMinecraft().world.getTotalWorldTime();
                        }

                        this.selected = i;

                        int animationTick;
                        if (!animationCompleted) {
                            animationTick = (int) ((Minecraft.getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                            if (animationTick >= 133) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        } else {
                            animationTick = 133;
                        }

                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }

                        overQuest = selected;
                        hoveredText = lore;
                        GlStateManager.disableLighting();
                    } else {
                        if (this.selected == i) {
                            animationCompleted = false;

                            if (!requestOpening) lastTick = 0;
                            overQuest = null;
                        }

                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                            render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }
                    }

                    render.color(1, 1, 1, 1);
                    if (selected.getStatus() == QuestStatus.COMPLETED) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                    } else if (selected.getStatus() == QuestStatus.CANNOT_START) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
                        lore.remove(lore.size() - 1);
                        lore.remove(lore.size() - 1);
                    } else if (selected.getStatus() == QuestStatus.CAN_START) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                            lore.set(lore.size() - 1, TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
                        } else {
                            lore.set(lore.size() - 1, TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
                        }
                    } else if (selected.getStatus() == QuestStatus.STARTED) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                            lore.set(lore.size() - 1, TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
                        } else {
                            lore.set(lore.size() - 1, TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
                        }
                    }
                    lore.add(TextFormatting.GOLD + (TextFormatting.BOLD + "Right click to open on the wiki!"));

                    render.drawString(selected.getQuestbookFriendlyName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    currentY += 13;
                }
            }

            //Reload Quest Data button
            if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
                hoveredText = Arrays.asList("Reload Button!", TextFormatting.GRAY + "Reloads all quest data.");
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 219, 282, 240, 303);
            } else {
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 240, 282, 261, 303);
            }
        }
        ScreenRenderer.endGL();
        renderHoveredText(hoveredText, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if (overQuest != null) {
            if (mouseButton != 1) {
                if (overQuest.getStatus() == QuestStatus.COMPLETED || overQuest.getStatus() == QuestStatus.CANNOT_START)
                    return;
                if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(overQuest.getName())) {
                    QuestManager.setTrackedQuest(null);
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                    return;
                }
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
                QuestManager.setTrackedQuest(overQuest);
            } else {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    String url = "https://wynncraft.gamepedia.com/";
                    //Link Overrides
                    if (overQuest.getName().equals("The House of Twain")) {
                        url += "The_House_of_Twain_(Quest)";
                    } else if (overQuest.getName().equals("Tower of Ascension")) {
                        url += "Tower_of_Ascension_(Quest)";
                    } else if (overQuest.getName().equals("The Qira Hive")) {
                        url += "The_Qira_Hive_(Quest)";
                    } else if (overQuest.getName().equals("The Realm of Light")) {
                        url += "The_Realm_of_Light_(Quest)";
                    } else if (overQuest.getName().equals("Temple of the Legends")) {
                        url += "Temple_of_the_Legends_(Quest)";
                    } else if (overQuest.getName().equals("Taproot")) {
                        url += "Taproot_(Quest)";
                    } else if (overQuest.getName().equals("The Passage")) {
                        url += "The_Passage_(Quest)";
                    } else if (overQuest.getName().equals("Zhight Island")) {
                        url += "Zhight_Island_(Quest)";
                    } else if (overQuest.getName().equals("The Tower of Amnesia")) {
                        url += "The_Tower_of_Amnesia_(Quest)";
                    } else if (overQuest.getName().equals("Pit of the Dead")) {
                        url += "Pit_of_the_Dead_(Quest)";
                    } else {
                        url += URLEncoder.encode(overQuest.getName().replace(" ", "_").replace("À", ""), "UTF-8");
                    }
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ignored) {
                        StringSelection selection = new StringSelection(url);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, null);
                        TextComponentString text = new TextComponentString("Error opening link, it has been copied to your clipboard");
                        text.getStyle().setColor(TextFormatting.DARK_RED);
                        ModCore.mc().player.sendMessage(text);
                    }
                }
            }
        }

        if (acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            currentPage++;
            return;
        } else if (acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            currentPage--;
            return;
        } else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookPages.MAIN.getPage().open(false);
            return;
        } else if (posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookPages.DISCOVERIES.getPage().open(false);
            return;
        } else if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestManager.requestAnalyse();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        overQuest = null;
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void searchUpdate(String currentText) {
        HashMap<String, QuestInfo> questsMap = QuestManager.getCurrentQuestsData();

        questSearch = currentText != null && !currentText.isEmpty() ? (ArrayList<QuestInfo>) questsMap.values().stream()
                .filter(c -> doesSearchMatch(c.getName().toLowerCase(), currentText.toLowerCase()))
                .collect(Collectors.toList())
                : new ArrayList<>(questsMap.values());

        questSearch.sort(Comparator.comparing(QuestInfo::getMinLevel));
        questSearch.sort(Comparator.comparing(QuestInfo::getStatus));
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Quest Book", TextFormatting.GRAY + "See and pin all your", TextFormatting.GRAY + "current available", TextFormatting.GRAY + "quests.",  "", TextFormatting.GREEN + "Left click to select");
    }
}
