/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.questbook.managers;


import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.core.enums.InventoryResult;
import com.wynntils.modules.core.instances.inventory.FakeInventory;
import com.wynntils.modules.core.instances.inventory.InventoryOpenByItem;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.events.custom.QuestBookUpdateEvent;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.QuestInfo;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.util.text.TextFormatting.*;

public class QuestManager {

    private static final Pattern QUEST_BOOK_WINDOW_TITLE_PATTERN = Pattern.compile("\\[Pg\\. \\d+] [a-zA-Z0-9_ ]+'s? (?:Discoveries|(?:Mini-)?Quests)");
    private static final int MESSAGE_ID = 423375494;
    private static final Pattern DIALOGUE_PAGE_PATTERN = Pattern.compile(TextFormatting.GRAY + "Page \\[(\\d+)/(\\d+)\\]");

    private static FakeInventory lastInventory = null;
    private static Map<String, QuestInfo> currentQuests = new LinkedHashMap<>();
    private static Map<String, QuestInfo> currentMiniQuests = new LinkedHashMap<>();
    private static Map<String, DiscoveryInfo> currentDiscoveries = new LinkedHashMap<>();
    private static List<List<String>> currentDialogue = new ArrayList<>();
    private static String trackedQuest = null;

    private static List<String> questsLore = new ArrayList<>();
    private static List<String> miniQuestsLore = new ArrayList<>();
    private static List<String> discoveriesLore = new ArrayList<>();
    private static List<String> secretDiscoveriesLore = new ArrayList<>();

    private static boolean hasInterrupted = false;
    private static boolean fullRead = true;
    private static EnumSet<AnalysePosition> queuedPositions = EnumSet.range(AnalysePosition.QUESTS, AnalysePosition.MINIQUESTS);
    private static AnalysePosition currentPosition = null;

    public static synchronized boolean shouldRead() {
        return !queuedPositions.isEmpty();
    }

    public static void updateAnalysis(Collection<AnalysePosition> position, boolean full, boolean immediate) {
        synchronized (QuestManager.class) {
            queuedPositions.addAll(position);
            fullRead = fullRead || full;
        }

        if (immediate) {
            readQuestBook();
        }
    }

    public static void updateAnalysis(AnalysePosition position, boolean full, boolean immediate) {
        updateAnalysis(EnumSet.of(position), full, immediate);
    }

    public static void updateAllAnalyses(boolean immediate) {
        updateAnalysis(EnumSet.allOf(AnalysePosition.class), true, immediate);
    }

    public static void readQuestBook() {
        if (!Reference.onWorld) return;

        if (McIf.player().openContainer != null && !(McIf.player().openContainer instanceof ContainerPlayer)) {
            Reference.LOGGER.error("[QuestManager] Opened container is not ContainerPlayer (openContainer class: " + McIf.player().openContainer.getClass().getSimpleName() + ")");
            interrupt();
            return;
        }

        boolean empty;

        synchronized (QuestManager.class) {
            if (lastInventory != null && lastInventory.isOpen()) return;
            empty = queuedPositions.isEmpty();
        }

        if (empty) {
            // Did a "full update" (All positions were up to date, so don't open fake inventory)
            FrameworkManager.getEventBus().post(new QuestBookUpdateEvent.Full());
            return;
        }

        sendQuestBookMessage(GRAY + "[Analysing quest book...]");
        hasInterrupted = false;

        List<ItemStack> gatheredQuests = new ArrayList<>();
        List<ItemStack> gatheredMiniQuests = new ArrayList<>();
        List<ItemStack> gatheredDiscoveries = new ArrayList<>();
        List<List<String>> gatheredDialogue = new ArrayList<>();

        FakeInventory inv = new FakeInventory(QUEST_BOOK_WINDOW_TITLE_PATTERN, new InventoryOpenByItem(7));
        inv.setLimitTime(15000); // 15 seconds

        inv.onReceiveItems((i) -> {
            int mouseButton = 1;
            Pair<Integer, ItemStack> nextClick = null;

            // lores
            updateLores(i);

            boolean fullRead;
            synchronized (QuestManager.class) {
                fullRead = QuestManager.fullRead; // get synchronised copy

                // Get next queued position if not currently on a position
                if (currentPosition == null) {
                    if (QuestManager.queuedPositions.isEmpty()) {
                        i.close();
                        return;
                    }

                    currentPosition = queuedPositions.iterator().next();
                }
            }

            // go to the right page
            if (switchToCorrectPage(i, currentPosition)) return;

            // page scanning
            if (currentPosition == AnalysePosition.QUESTS) {
                nextClick = i.findItem("Dialogue History", FilterType.CONTAINS);
                mouseButton = 0;
                List<String> dialogueLore = ItemUtils.getLore(nextClick.b);

                if (dialogueLore.contains(TextFormatting.GRAY + "There's nothing here yet")) {
                    nextClick = null;
                    mouseButton = 1;
                } else {
                    int remove = 3;
                    String page = dialogueLore.get(dialogueLore.size() - 3);
                    Matcher matcher = DIALOGUE_PAGE_PATTERN.matcher(page);
                    if (!matcher.matches()) {
                        remove = 1;
                        page = dialogueLore.get(dialogueLore.size() - 1);
                        matcher = DIALOGUE_PAGE_PATTERN.matcher(page);
                    }
                    if (matcher.matches()) {
                        int currentPage = Integer.parseInt(matcher.group(1));
                        int maxPage = Integer.parseInt(matcher.group(2));
                        if (gatheredDialogue.size() < currentPage) {
                            gatheredDialogue.add(new ArrayList<>(dialogueLore.subList(1, dialogueLore.size() - remove - 1)));
                        }
                        if (currentPage >= maxPage || gatheredDialogue.size() >= maxPage) {
                            nextClick = null;
                            mouseButton = 1;
                        }
                    } else {
                        nextClick = null;
                        mouseButton = 1;
                    }
                }

                if (nextClick == null) {
                    nextClick = i.findItem(">>>>>", FilterType.CONTAINS); // next page item

                    for (ItemStack stack : i.getInventory()) {
                        if (!stack.hasDisplayName()) continue; // also checks for nbt

                        List<String> lore = ItemUtils.getUnformattedLore(stack);
                        // uppercase on beta
                        if (lore.isEmpty() || (!lore.contains("Right click to track") && !lore.contains("RIGHT-CLICK TO TRACK") && !lore.contains("Right click to stop tracking"))) continue; // not a valid quest

                        if (fullRead) {
                            gatheredQuests.add(stack);
                            continue;
                        }

                        String displayName = StringUtils.normalizeBadString(getTextWithoutFormattingCodes(stack.getDisplayName()));
                        if (currentQuests.containsKey(displayName) && currentQuests.get(displayName).equals(stack)) {
                            continue;
                        }

                        gatheredQuests.add(stack);
                        nextClick = null;
                        break;
                    }
                }
            }

            if (currentPosition == AnalysePosition.MINIQUESTS) {
                nextClick = i.findItem(">>>>>", FilterType.CONTAINS); // next page item

                for (ItemStack stack : i.getInventory()) {
                    if (!stack.hasDisplayName()) continue; // also checks for nbt

                    List<String> lore = ItemUtils.getUnformattedLore(stack);
                    if (lore.isEmpty() || (!lore.contains("Right click to track") && !lore.contains("RIGHT-CLICK TO TRACK"))) continue; // not a valid mini-quest

                    if (fullRead) {
                        gatheredMiniQuests.add(stack);
                        continue;
                    }

                    String displayName = getTextWithoutFormattingCodes(stack.getDisplayName());
                    if (currentMiniQuests.containsKey(displayName) && currentMiniQuests.get(displayName).equals(stack)) {
                        continue;
                    }

                    gatheredMiniQuests.add(stack);
                    nextClick = null;
                    break;
                }
            }

            if (currentPosition == AnalysePosition.DISCOVERIES || currentPosition == AnalysePosition.SECRET_DISCOVERIES) {
                nextClick = i.findItem(">>>>>", FilterType.CONTAINS); // next page item

                for (ItemStack stack : i.getInventory()) {
                    if (!stack.hasDisplayName()) continue; // also checks for nbt

                    List<String> lore = ItemUtils.getLore(stack);
                    if (lore.isEmpty() || !getTextWithoutFormattingCodes(lore.get(0)).contains("✔ Combat Lv")) continue;

                    if (fullRead) {
                        gatheredDiscoveries.add(stack);
                        continue;
                    }

                    String displayName = getTextWithoutFormattingCodes(stack.getDisplayName());
                    if (currentDiscoveries.containsKey(displayName)) {
                        continue;
                    }

                    gatheredDiscoveries.add(stack);
                    nextClick = null;
                    break;
                }
            }

            // effective pagination
            if (nextClick == null) {
                if (!gatheredQuests.isEmpty()) {
                    parseQuests(gatheredQuests);
                    gatheredQuests.clear();
                }
                if (!gatheredMiniQuests.isEmpty()) {
                    parseMiniQuests(gatheredMiniQuests);
                    gatheredMiniQuests.clear();
                }
                if (!gatheredDiscoveries.isEmpty()) {
                    parseDiscoveries(gatheredDiscoveries);
                    gatheredDiscoveries.clear();
                }
                if (!gatheredDialogue.isEmpty()) {
                    parseDialogue(gatheredDialogue);
                    gatheredDialogue.clear();
                }

                AnalysePosition previousPosition = currentPosition;

                // Remove from queue now that it has been analysed, and get the next position
                synchronized (QuestManager.class) {
                    queuedPositions.remove(currentPosition);
                    currentPosition = queuedPositions.isEmpty() ? null : queuedPositions.iterator().next();
                }

                FrameworkManager.getEventBus().post(new QuestBookUpdateEvent.Partial(previousPosition));

                if (currentPosition == null) {
                    i.close();
                    return;
                }

                // Go to next page
                nextClick = i.findItem(currentPosition.getItemName(), FilterType.EQUALS);

                if (nextClick == null) {
                    Reference.LOGGER.error("[QuestManager] Failed to switch to next position (" + previousPosition + " to " + currentPosition + ")");
                    i.closeUnsuccessfully();
                    interrupt();
                    return;
                }

                i.clickItem(nextClick.a, mouseButton, ClickType.PICKUP);
                return;
            }

            i.clickItem(nextClick.a, mouseButton, ClickType.PICKUP); // 1 because otherwise wynn sends the inventory twice
        });

        inv.onClose((i, result) -> {
            lastInventory = null;

            if (result != InventoryResult.CLOSED_SUCCESSFULLY) {
                Reference.LOGGER.error("[QuestManager] Failed to close inventory successfully (result: " + result.toString() + ")");
                interrupt();
                return;
            }

            if (!gatheredQuests.isEmpty()) parseQuests(gatheredQuests);
            if (!gatheredMiniQuests.isEmpty()) parseMiniQuests(gatheredMiniQuests);
            if (!gatheredDiscoveries.isEmpty()) parseDiscoveries(gatheredDiscoveries);

            FrameworkManager.getEventBus().post(new QuestBookUpdateEvent.Full());
            sendQuestBookMessage(GRAY + "[Quest book analyzed]");
            WynntilsSound.QUESTBOOK_UPDATE.play(0.5f, 1f);
        });

        synchronized (QuestManager.class) {
            if (lastInventory == null || !lastInventory.isOpen()) {
                lastInventory = inv;
                lastInventory.open();
            }
        }
    }

    private static void updateLores(FakeInventory i) {
        List<Pair<Integer, ItemStack>> loreStacks = i.findItems(
            Arrays.asList("Quests", "Mini-Quests", "Discoveries", "Secret Discoveries"),
            Arrays.asList(FilterType.CONTAINS, FilterType.CONTAINS, FilterType.CONTAINS, FilterType.EQUALS)
        );

        Pair<Integer, ItemStack> quests = loreStacks.get(0);
        if (quests != null) questsLore = ItemUtils.getLore(quests.b);

        Pair<Integer, ItemStack> miniQuests = loreStacks.get(1);
        if (miniQuests != null) miniQuestsLore = ItemUtils.getLore(miniQuests.b);

        Pair<Integer, ItemStack> discoveries = loreStacks.get(2);
        if (discoveries != null) discoveriesLore = ItemUtils.getLore(discoveries.b);

        Pair<Integer, ItemStack> secretDiscoveries = loreStacks.get(3);
        if (secretDiscoveries != null) secretDiscoveriesLore = ItemUtils.getLore(secretDiscoveries.b);
    }

    private static void parseQuests(List<ItemStack> quests) {
        for (ItemStack stack : quests) {
            try {
                QuestInfo quest = new QuestInfo(stack, false);
                if (!quest.isValid()) continue;

                //update tracked quest
                if (trackedQuest != null && trackedQuest.equalsIgnoreCase(quest.getName())) {
                    if (quest.getStatus() == QuestStatus.COMPLETED) trackedQuest = null;
                    else quest.updateAsTracked();
                }

                currentQuests.put(quest.getName(), quest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void parseMiniQuests(List<ItemStack> miniQuests) {
        for (ItemStack stack : miniQuests) {
            try {
                QuestInfo miniQuest = new QuestInfo(stack, true);
                if (!miniQuest.isValid()) continue;

                currentMiniQuests.put(miniQuest.getName(), miniQuest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void parseDiscoveries(List<ItemStack> discoveries) {
        for (ItemStack stack : discoveries) {
            try {
                DiscoveryInfo discovery = new DiscoveryInfo(stack, true);
                if (!discovery.isValid()) continue;

                currentDiscoveries.put(discovery.getName(), discovery);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void parseDialogue(List<List<String>> dialogue) {
        currentDialogue.clear();
        List<String> currentList = new ArrayList<>();
        for (List<String> page : dialogue) {
            boolean previousStart = true;
            StringBuilder currentLine = new StringBuilder();
            for (String line : page) {
                if (line.equals(TextFormatting.DARK_GRAY + "• Dialogue Start")) {
                    currentList = new ArrayList<>();
                    currentList.add(line);
                    previousStart = true;
                } else if (line.equals(TextFormatting.DARK_GRAY + "• Dialogue End")) {
                    if (currentLine.length() != 0) {
                        currentList.add(currentLine.toString());
                        currentLine = new StringBuilder();
                    }
                    currentList.add(line);
                    currentDialogue.add(currentList);
                    previousStart = false;
                } else {
                    if (!previousStart) {
                        int lastColorCode = currentLine.lastIndexOf("§");
                        if (lastColorCode > -1 && !line.startsWith("§" + currentLine.charAt(lastColorCode + 1))) {
                            currentList.add(currentLine.toString());
                            currentLine = new StringBuilder();
                        } else {
                            currentLine.append(' ');
                        }
                    }
                    currentLine.append(line);
                    previousStart = false;
                }
            }
            if (currentLine.length() != 0) {
                currentList.add(currentLine.toString());
            }
        }
    }

    public static Collection<QuestInfo> getCurrentQuests() {
        return currentQuests.values();
    }

    public static QuestInfo getQuest(String name) {
        return currentQuests.getOrDefault(name, null);
    }

    public static Collection<QuestInfo> getCurrentMiniQuests() {
        return currentMiniQuests.values();
    }

    public static QuestInfo getMiniQuest(String name) {
        return currentMiniQuests.getOrDefault(name, null);
    }

    public static Collection<DiscoveryInfo> getCurrentDiscoveries() {
        return currentDiscoveries.values();
    }

    public static DiscoveryInfo getDiscovery(String name) {
        return currentDiscoveries.getOrDefault(name, null);
    }

    public static List<List<String>> getCurrentDialogue() {
        return currentDialogue;
    }

    public static List<String> getQuestsLore() {
        return questsLore;
    }

    public static List<String> getMiniQuestsLore() {
        return miniQuestsLore;
    }

    public static List<String> getDiscoveriesLore() {
        return discoveriesLore;
    }

    public static List<String> getSecretDiscoveriesLore() {
        return secretDiscoveriesLore;
    }

    public static String getTrackedQuestName() {
        return trackedQuest;
    }

    public static QuestInfo getTrackedQuest() {
        return currentQuests.containsKey(trackedQuest) ? currentQuests.get(trackedQuest) : currentMiniQuests.get(trackedQuest);
    }

    public static void setTrackedQuest(QuestInfo quest) {
        trackedQuest = quest != null ? quest.getName() : null;
        if (trackedQuest != null) quest.updateAsTracked();
    }

    public static boolean hasTrackedQuest() {
        return trackedQuest != null;
    }

    public static boolean hasInterrupted() {
        return hasInterrupted;
    }

    public static boolean isAnalysing() {
        return lastInventory != null && lastInventory.isOpen();
    }

    public static synchronized void clearData() {
        currentQuests.clear();
        currentMiniQuests.clear();
        currentDiscoveries.clear();
        currentDialogue.clear();

        questsLore.clear();
        miniQuestsLore.clear();
        discoveriesLore.clear();
        secretDiscoveriesLore.clear();

        hasInterrupted = false;
        if (lastInventory != null) lastInventory.closeUnsuccessfully();
        lastInventory = null;
        trackedQuest = null;

        fullRead = true;
        queuedPositions = EnumSet.range(AnalysePosition.QUESTS, AnalysePosition.MINIQUESTS);
        currentPosition = null;
    }

    public static void completeQuest(String name, boolean isMini) {
        name = StringUtils.normalizeBadString(name);
        if (trackedQuest != null && trackedQuest.equalsIgnoreCase(name)) trackedQuest = null;

        QuestInfo info = isMini ? getMiniQuest(name) : getQuest(name);
        if (info == null) {
            updateAnalysis(isMini ? AnalysePosition.MINIQUESTS : AnalysePosition.QUESTS, true, false);
            return;
        }

        info.setAsCompleted();
    }

    private static boolean switchToCorrectPage(FakeInventory i, AnalysePosition currentPosition) {
        if (i.getWindowTitle().contains(currentPosition.getWindowName())) return false;

        Pair<Integer, ItemStack> nextClick = i.findItem(currentPosition.getItemName(), FilterType.EQUALS);

        if (nextClick == null) {
            Reference.LOGGER.error("[QuestManager] Failed to switch pages (nextClick was null)");
            interrupt();
            i.closeUnsuccessfully();
        } else {
            i.clickItem(nextClick.a, 1, ClickType.PICKUP); // 1 because otherwise wynn sends the inventory twice
        }

        return true;
    }

    private static void interrupt() {
        hasInterrupted = true;
        sendQuestBookMessage(RED + "[Quest book analysis failed, manually open your book to try again]");
    }

    private static void sendQuestBookMessage(String msg) {
        // Can be called from nio thread by FakeInventory
        McIf.mc().addScheduledTask(() ->
            ChatOverlay.getChat().printChatMessageWithOptionalDeletion(new TextComponentString(msg), MESSAGE_ID)
        );
    }

}
