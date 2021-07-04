/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.overlays.ui.MainWorldMapUI;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.enums.QuestLevelType;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.request.Request;
import com.wynntils.webapi.request.RequestHandler;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuestsPage extends QuestBookListPage<QuestInfo> {

    private SortMethod sort = SortMethod.LEVEL;
    private boolean showingMiniQuests = false;
    final static List<String> textLines = Arrays.asList("Here you can see all quests", "available for you. You can", "also search for a specific", "quest just by typing its name.", "You can go to the next page", "by clicking on the two buttons", "or by scrolling your mouse.", "", "You can pin/unpin a quest", "by clicking on it.");

    public QuestsPage() {
        super("Quests", true, IconContainer.questPageIcon);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Quest Book", TextFormatting.GRAY + "See and pin all your", TextFormatting.GRAY + "current available", TextFormatting.GRAY + "quests.", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    public void open(boolean showAnimation) {
        super.open(showAnimation);

        QuestManager.readQuestBook();
    }

    @Override
    protected void drawEntry(QuestInfo entryInfo, int index, boolean hovered) {
        int x = width / 2;
        int y = height / 2;
        int currentY = 13 + index * 12;
        boolean toCrop = !entryInfo.getFriendlyName().equals(entryInfo.getName());

        int animationTick = -1;
        if (hovered && !showAnimation) {
            if (lastTick == 0 && !animationCompleted) {
                lastTick = McIf.getSystemTime();
            }

            if (!animationCompleted) {
                animationTick = (int) (McIf.getSystemTime() - lastTick) / 2;
                if (animationTick >= 133 && !toCrop) {
                    animationCompleted = true;
                    animationTick = 133;
                }
            } else {
                if (toCrop) {
                    animationCompleted = false;
                    lastTick = McIf.getSystemTime() - 133 * 2;
                }

                animationTick = 133;
            }

            int width = Math.min(animationTick, 133);
            animationTick -= 133 + 200;
            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(entryInfo.getName())) {
                render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
            } else {
                render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
            }

            GlStateManager.disableLighting();
        } else {
            if (selected == index) {
                animationCompleted = false;

                if (!showAnimation) lastTick = 0;
            }

            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(entryInfo.getName())) {
                render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
            } else {
                render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
            }
        }

        render.color(1, 1, 1, 1);
        if (entryInfo.getStatus() == QuestStatus.COMPLETED) {
            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
        } else if (entryInfo.getStatus() == QuestStatus.CANNOT_START) {
            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
        } else if (entryInfo.getStatus() == QuestStatus.CAN_START) {
            if (entryInfo.isMiniQuest()) {
                render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 272, 245, 11, 7);
            } else {
                render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
            }
        } else if (entryInfo.getStatus() == QuestStatus.STARTED) {
            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
        }

        String name = entryInfo.getFriendlyName();
        if (selected == index && !name.equals(entryInfo.getName()) && animationTick > 0) {
            name = entryInfo.getName();
            int maxScroll = fontRenderer.getStringWidth(name) - (120 - 10);
            int scrollAmount = (animationTick / 20) % (maxScroll + 60);

            if (maxScroll <= scrollAmount && scrollAmount <= maxScroll + 40) {
                // Stay on max scroll for 20 * 40 animation ticks after reaching the end
                scrollAmount = maxScroll;
            } else if (maxScroll <= scrollAmount) {
                // And stay on minimum scroll for 20 * 20 animation ticks after looping back to the start
                scrollAmount = 0;
            }

            ScreenRenderer.enableScissorTestX(x + 26, 13 + 133 - 2 - 26);
            {
                render.drawString(name, x + 26 - scrollAmount, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.disableScissorTest();
        } else {
            render.drawString(name, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }
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

        // Progress Icon/Mini-Quest Switcher
        render.drawRect(Textures.UIs.quest_book, x - 87, y - 100, 16, 255 + (showingMiniQuests ? 16 : 0), 16, 16);
        if ( posX >= 71 && posX <= 87 && posY >= 84 && posY <= 100) {
            hoveredText = new ArrayList<>(showingMiniQuests ? QuestManager.getMiniQuestsLore() : QuestManager.getQuestsLore());

            if (!hoveredText.isEmpty()) {
                hoveredText.set(0, showingMiniQuests ? "Mini-Quests:" : "Quests:");
                hoveredText.add(" ");
                hoveredText.add(TextFormatting.GREEN + "Click to see " + (showingMiniQuests ? "Quests" : "Mini-Quests"));
            }
        }

        // Dialogue page button
        render.drawRect(Textures.UIs.quest_book, x - 30, y - 97, 222, 306, 14, 11);
        if (posX >= 16 && posX <= 30 && posY >= 86 && posY < 97) {
            hoveredText = new ArrayList<>();
            hoveredText.add(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Dialogue History");
            hoveredText.add(TextFormatting.GRAY + "Click here to view");
            hoveredText.add(TextFormatting.GRAY + "your dialogue history");
            hoveredText.add(" ");
            hoveredText.add(TextFormatting.GREEN + "Left click to select");
        }

        // Reload Data button
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

    @Override
    protected boolean isHovered(int index, int posX, int posY) {
        int currentY = 13 + 12 * index;

        return search.get(currentPage - 1).size() > index && posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;
    }

    @Override
    protected List<String> getHoveredText(QuestInfo entryInfo) {
        List<String> lore = new ArrayList<>(entryInfo.getLore());
        lore.add("");

        switch (entryInfo.getStatus()) {
            case COMPLETED:
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
                break;
            case CANNOT_START:
                lore.remove(lore.size() - 1);
                lore.remove(lore.size() - 1);
                break;
            case CAN_START:
            case STARTED:
                lore.remove(lore.size() - 2);
                if(!lore.remove(lore.size() - 2).isEmpty()) lore.remove(lore.size() - 2); // quest is tracked, has extra line

                if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(entryInfo.getName())) {
                    lore.add(TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
                } else {
                    lore.add(TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
                }
                break;

        }

        if (entryInfo.hasTargetLocation()) {
            lore.add(TextFormatting.YELLOW + (TextFormatting.BOLD + "Middle click to view on map!"));
        }
        lore.add(TextFormatting.GOLD + (TextFormatting.BOLD + "Right click to open on the wiki!"));

        return lore;
    }

    @Override
    protected String getEmptySearchString() {
        if (QuestManager.getCurrentQuests().size() == 0 || textField.getText().equals("") ||
                (showingMiniQuests && QuestManager.getCurrentQuests().stream().noneMatch(QuestInfo::isMiniQuest))) {
            return String.format("Loading %s...\nIf nothing appears soon, try pressing the reload button.", showingMiniQuests ? "Mini-Quests" : "Quests");
        }

        return String.format("No %s found!\nTry searching for something else.", showingMiniQuests ? "mini-quests" : "quests");
    }

    @Override
    protected List<List<QuestInfo>> getSearchResults(String text) {
        List<QuestInfo> quests;
        if (showingMiniQuests) quests = new ArrayList<>(QuestManager.getCurrentMiniQuests());
        else quests = new ArrayList<>(QuestManager.getCurrentQuests());

        if (text != null && !text.isEmpty()) {
            String lowerCase = text.toLowerCase();
            quests.removeIf(c -> !doesSearchMatch(c.getName().toLowerCase(), lowerCase));
        }

        quests.sort(sort.comparator);

        return getListSplitIntoParts(quests, 13);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);

        if (posX >= 71 && posX <= 87 && posY >= 84 && posY <= 100) { // Mini-Quest Switcher
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            showingMiniQuests = !showingMiniQuests;
            textField.setText("");
            updateSearch();
            return;
        } else if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // Update Data
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestManager.updateAllAnalyses(true);
            return;
        } else if (-11 <= posX && posX <= -1 && 89 <= posY && posY <= 99 && (mouseButton == 0 || mouseButton == 1)) { // Change Sort Method
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            sort = SortMethod.values()[(sort.ordinal() + (mouseButton == 0 ? 1 : SortMethod.values().length - 1)) % SortMethod.values().length];
            updateSearch();
            return;
        } else if (posX >= 16 && posX <= 30 && posY >= 86 && posY < 97) { // Dialogue button
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookPages.DIALOGUE.getPage().open(false);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleEntryClick(QuestInfo itemInfo, int mouseButton) {
        switch (mouseButton) {
            case 0: // left click
                if (selectedEntry.getStatus() == QuestStatus.COMPLETED || selectedEntry.getStatus() == QuestStatus.CANNOT_START)
                    return;

                if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selectedEntry.getName())) {
                    QuestManager.setTrackedQuest(null);
                    McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                    return;
                }
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
                QuestManager.setTrackedQuest(selectedEntry);
                break;
            case 1: // right click
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

                final String baseUrl = "https://wynncraft.fandom.com/wiki/";

                if (selectedEntry.isMiniQuest()) {
                    String type = selectedEntry.getFriendlyName().split(" ")[0];

                    String wikiName = "Quests#" + type + "ing_Posts"; // Don't encode #

                    Utils.openUrl(baseUrl + wikiName);
                } else {
                    String name = selectedEntry.getName();
                    String wikiQuestPageNameQuery = WebManager.getApiUrl("WikiQuestQuery");
                    String url = wikiQuestPageNameQuery + Utils.encodeForCargoQuery(name);
                    Request req = new Request(url, "WikiQuestQuery");

                    RequestHandler handler = new RequestHandler();

                    handler.addAndDispatch(req.handleJsonArray(jsonOutput -> {
                        String pageTitle = jsonOutput.get(0).getAsJsonObject().get("_pageTitle").getAsString();
                        Utils.openUrl(baseUrl + Utils.encodeForWikiTitle(pageTitle));
                        return true;
                    }), true);
                }
                break;
            case 2: // middle click
                if (!selectedEntry.hasTargetLocation()) return;

                Location loc = selectedEntry.getTargetLocation();
                Utils.displayGuiScreen(new MainWorldMapUI((float) loc.x, (float) loc.z));
                break;
        }
    }

    private enum SortMethod {
        LEVEL(
                Comparator.comparing(QuestInfo::getStatus)
                        .thenComparing(q -> !q.getMinLevel().containsKey(QuestLevelType.COMBAT) && !q.getMinLevel().isEmpty()).thenComparingInt((q) -> {
                    if (q.getMinLevel().containsKey(QuestLevelType.COMBAT)) {
                        return q.getMinLevel().get(QuestLevelType.COMBAT);
                    } else if (!q.getMinLevel().isEmpty()) {
                        return q.getMinLevel().values().iterator().next();
                    } else {
                        return 1;
                    }
                }),
                130, 281, 152, 303, Arrays.asList(
                "Sort by Level", // Replace with translation keys during l10n
                "Lowest level quests first")),
        DISTANCE(Comparator.comparing(QuestInfo::getStatus).thenComparingLong(q -> {
            EntityPlayerSP player = McIf.player();
            if (player == null || !q.hasTargetLocation()) {
                return 0;
            }

            return (long) new Location(player).distance(q.getTargetLocation());
        }).thenComparing(q -> !q.getMinLevel().containsKey(QuestLevelType.COMBAT) && !q.getMinLevel().isEmpty()).thenComparingInt((q) -> {
            if (q.getMinLevel().containsKey(QuestLevelType.COMBAT)) {
                return q.getMinLevel().get(QuestLevelType.COMBAT);
            } else if (!q.getMinLevel().isEmpty()) {
                return q.getMinLevel().values().iterator().next();
            } else {
                return 1;
            }
        }),
                173, 281, 195, 303, Arrays.asList(
                "Sort by Distance",
                "Closest quests first"));

        SortMethod(Comparator<QuestInfo> comparator, int tx1, int ty1, int tx2, int ty2, List<String> hoverText) {
            this.comparator = comparator;
            this.tx1 = tx1;
            this.ty1 = ty1;
            this.tx2 = tx2;
            this.ty2 = ty2;
            this.hoverText = hoverText;
        }

        Comparator<QuestInfo> comparator;
        int tx1, ty1, tx2, ty2;
        List<String> hoverText;
    }

}
