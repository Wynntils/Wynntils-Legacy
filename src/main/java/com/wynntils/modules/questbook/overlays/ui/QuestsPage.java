package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.enums.QuestLevelType;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class QuestsPage extends QuestBookPage {

    private ArrayList<QuestInfo> questSearch;
    private QuestInfo overQuest;
    private SortMethod sort = SortMethod.LEVEL;
    private boolean showingMiniQuests = false;

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

            boolean hoveringOverDiscoveries = posX >= 81 && posX <= 97 && posY >= 84 && posY <= 100;
            render.drawRect(Textures.UIs.quest_book, x - 97, y - 100, 0, 255 + (hoveringOverDiscoveries ? 16 : 0), 16, 16);
            if (hoveringOverDiscoveries) {
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

            render.drawRect(Textures.UIs.quest_book, x - 76, y - 100, 16, 255 + (showingMiniQuests ? 16 : 0), 16, 16);
            if (posX >= 61 && posX <= 76 && posY >= 84 && posY <= 100) {
                hoveredText = new ArrayList<>(showingMiniQuests ? QuestManager.miniquestsLore : QuestManager.questsLore);
                hoveredText.add(0, showingMiniQuests ? "Mini-Quests:" : "Quests:");
                hoveredText.add(" ");
                hoveredText.add(TextFormatting.GREEN + "Click to see " + (showingMiniQuests ? "Quests" : "Mini-Quests"));
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

                    int animationTick = -1;
                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = Minecraft.getSystemTime();
                        }

                        this.selected = i;

                        if (!animationCompleted) {
                            animationTick = (int) (Minecraft.getSystemTime() - lastTick) / 2;
                            if (animationTick >= 133 && selected.getQuestbookFriendlyName().equals(selected.getName())) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        } else {
                            if (!selected.getQuestbookFriendlyName().equals(selected.getName())) {
                                animationCompleted = false;
                                lastTick = Minecraft.getSystemTime() - 133 * 2;
                            }
                            animationTick = 133;
                        }

                        int width = Math.min(animationTick, 133);
                        animationTick -= 133 + 200;
                        if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
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
                        if (selected.isMiniQuest()) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 272, 245, 11, 7);
                        } else {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
                        }
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

                    String name = selected.getQuestbookFriendlyName();
                    if (this.selected == i && !name.equals(selected.getName()) && animationTick > 0) {
                        name = selected.getName();
                        int maxScroll = fontRenderer.getStringWidth(name) - (120 - 10);
                        int scrollAmount = (animationTick / 20) % (maxScroll + 60);

                        if (maxScroll <= scrollAmount && scrollAmount <= maxScroll + 40) {
                            // Stay on max scroll for 20 * 40 animation ticks after reaching the end
                            scrollAmount = maxScroll;
                        } else if (maxScroll <= scrollAmount) {
                            // And stay on minimum scroll for 20 * 20 animation ticks after looping back to the start
                            scrollAmount = 0;
                        }

                        GL11.glEnable(GL11.GL_SCISSOR_TEST);
                        {
                            // Scissor test is in screen coordinates, so y is inverted and scale needs to be manually applied
                            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
                            double scaleW = Minecraft.getMinecraft().displayWidth / res.getScaledWidth_double();
                            double scaleH = Minecraft.getMinecraft().displayHeight / res.getScaledHeight_double();
                            GL11.glScissor((int) ((x + 26 + ScreenRenderer.drawingOrigin().x) * scaleW), (int) ((y + 87 - currentY - ScreenRenderer.drawingOrigin().y) * scaleH), (int) ((13 + 133 - 2 - 26) * scaleW), (int) ((96 - 87) * scaleH));
                            render.drawString(name, x + 26 - scrollAmount, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                        }
                        GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    } else {
                        render.drawString(name, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }

                    currentY += 13;
                }
            } else {
                String textToDisplay;
                if (
                    QuestManager.getCurrentQuestsData().size() == 0 || searchBarText.equals("") ||
                    (showingMiniQuests && QuestManager.getCurrentQuestsData().values().stream().noneMatch(QuestInfo::isMiniQuest))
                ) {
                    textToDisplay = String.format("Loading %s...\nIf nothing appears soon, try pressing the reload button.", showingMiniQuests ? "mini-quests" : "quests");
                } else {
                    textToDisplay = String.format("No %s found!\nTry searching for something else.", showingMiniQuests ? "mini-quests" : "quests");
                }

                for (String line : textToDisplay.split("\n")) {
                    currentY += render.drawSplitString(line, 120, x + 26, y - 95 + currentY, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE) * 10 + 2;
                }
            }

            //Reload Quest Data button
            if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
                hoveredText = Arrays.asList("Reload Button!", TextFormatting.GRAY + "Reloads all quest data.");
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 218, 281, 240, 303);
            } else {
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 240, 281, 262, 303);
            }

            // Sort method button
            int dX = 0;
            if (-11 <= posX && posX <= -1 && 89 <= posY && posY <= 99) {
                hoveredText = sort.hoverText.stream().map(I18n::format).collect(Collectors.toList());
                dX = 22;
            }
            render.drawRect(Textures.UIs.quest_book, x + 1, y - 99, x + 12, y - 88, sort.tx1 + dX, sort.ty1, sort.tx2 + dX, sort.ty2);
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

                String url = "https://wynncraft.gamepedia.com/";
                String path = overQuest.getName();
                //Link Overrides
                switch (path) {
                    case "The House of Twain":
                    case "Tower of Ascension":
                    case "The Qira Hive":
                    case "The Realm of Light":
                    case "Temple of the Legends":
                    case "Taproot":
                    case "The Passage":
                    case "Zhight Island":
                    case "The Tower of Amnesia":
                    case "Pit of the Dead":
                        path += " (Quest)";
                        break;
                    default:
                        break;
                }

                if (overQuest.isMiniQuest()) {
                    url += "Quests#Miniquests";  // Don't encode #
                } else {
                    url += URLEncoder.encode(path.replace(' ', '_'), "UTF-8");
                }

                boolean opened = false;
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                        opened = true;
                    } catch (Exception ignored) { }
                }

                if (!opened) {
                    Utils.copyToClipboard(url);
                    TextComponentString text = new TextComponentString("Error opening link, it has been copied to your clipboard");
                    text.getStyle().setColor(TextFormatting.DARK_RED);
                    ModCore.mc().player.sendMessage(text);
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
        } else if (posX >= 81 && posX <= 97 && posY >= 84 && posY <= 100) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookPages.DISCOVERIES.getPage().open(false);
            return;
        } else if (posX >= 61 && posX <= 76 && posY >= 84 && posY <= 100) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            showingMiniQuests = !showingMiniQuests;
            searchBarText = "";
            updateSearch();
            return;
        } else if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestManager.scanMiniquests();
            QuestManager.requestAnalyse();
            return;
        } else if (-11 <= posX && posX <= -1 && 89 <= posY && posY <= 99 && (mouseButton == 0 || mouseButton == 1)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            sort = SortMethod.values()[(sort.ordinal() + (mouseButton == 0 ? 1 : SortMethod.values().length - 1)) % SortMethod.values().length];
            updateSearch();
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
    protected void searchUpdate(String currentText) {
        HashMap<String, QuestInfo> questsMap = QuestManager.getCurrentQuestsData();

        questSearch = new ArrayList<>(questsMap.values());

        questSearch.removeIf(q -> q.isMiniQuest() != showingMiniQuests);

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            questSearch.removeIf(c -> !doesSearchMatch(c.getName().toLowerCase(), lowerCase));
        }

        questSearch.sort(sort.comparator);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Quest Book", TextFormatting.GRAY + "See and pin all your", TextFormatting.GRAY + "current available", TextFormatting.GRAY + "quests.",  "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    public void open(boolean requestOpening) {
        super.open(requestOpening);
        QuestManager.wasBookOpened();
    }

    private enum SortMethod {
        LEVEL(
            Comparator.comparing(QuestInfo::getStatus)
            .thenComparing(q -> q.getLevelType() != QuestLevelType.COMBAT).thenComparingInt(QuestInfo::getMinLevel),
            130, 281, 152, 303, Arrays.asList(
            "Sort by Level",  // Replace with translation keys during l10n
            "Lowest level quests first"
        )),
        DISTANCE(Comparator.comparing(QuestInfo::getStatus).thenComparingLong(q -> {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player == null) {
                return 0;
            }
            if (q.getX() == Integer.MIN_VALUE) {
                // No coordinate
                return Long.MAX_VALUE;
            }
            long dX = (long) (player.posX - q.getX());
            long dZ = (long) (player.posZ - q.getZ());
            return dX * dX + dZ * dZ;
        }).thenComparing(q -> q.getLevelType() != QuestLevelType.COMBAT).thenComparingInt(QuestInfo::getMinLevel),
            174, 281, 196, 303, Arrays.asList(
            "Sort by Distance",
            "Closest quests first"
        ));

        SortMethod(Comparator<QuestInfo> comparator, int tx1, int ty1, int tx2, int ty2, List<String> hoverText) {
            this.comparator = comparator;
            this.tx1 = tx1; this.ty1 = ty1; this.tx2 = tx2; this.ty2 = ty2;
            this.hoverText = hoverText;
        }

        Comparator<QuestInfo> comparator;
        int tx1, ty1, tx2, ty2;
        List<String> hoverText;
    }

}
