/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.managers;


import com.wynntils.ModCore;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.utils.ItemUtils;
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
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.util.text.TextFormatting.*;

public class QuestManager {

    private static final Pattern QUEST_BOOK_WINDOW_TITLE_PATTERN = Pattern.compile("\\[Pg\\. \\d+] [a-zA-Z0-9_]{3,16}'s? (?:Discoveries|(?:Mini-)?Quests)");
    private static final int MESSAGE_ID = 423375494;

    private static FakeInventory lastInventory = null;
    private static LinkedHashMap<String, QuestInfo> currentQuests = new LinkedHashMap<>();
    private static LinkedHashMap<String, QuestInfo> currentMiniQuests = new LinkedHashMap<>();
    private static LinkedHashMap<String, DiscoveryInfo> currentDiscoveries = new LinkedHashMap<>();
    private static String trackedQuest = null;

    private static List<String> questsLore = new ArrayList<>();
    private static List<String> miniQuestsLore = new ArrayList<>();
    private static List<String> discoveriesLore = new ArrayList<>();
    private static List<String> secretDiscoveriesLore = new ArrayList<>();

    private static boolean hasInterrupted = false;
    private static boolean fullRead = false;
    private static AnalysePosition currentPosition = AnalysePosition.QUESTS;

    public static boolean shouldRead() {
        return currentQuests.isEmpty() || hasInterrupted;
    }

    public static void readQuestBook(AnalysePosition nextPosition) {
        readQuestBook(nextPosition, currentQuests.isEmpty());
    }

    public static void readLastPage() {
        readQuestBook(currentPosition, fullRead);
    }

    public static void queueOnOpen(AnalysePosition position, boolean full) {
        currentPosition = position;
        fullRead = full;

        hasInterrupted = true;
    }

    public static void readQuestBook(AnalysePosition nextPosition, boolean isFullRead) {
        if (lastInventory != null && lastInventory.isOpen()) return;

        currentPosition = nextPosition;
        fullRead = isFullRead;

        if (ModCore.mc().player.openContainer != null && !(ModCore.mc().player.openContainer instanceof ContainerPlayer)) {
            hasInterrupted = true;
            sendMessage(RED + "[Quest book analysis failed, manually open your book to try again]");
            return;
        }

        sendMessage(GRAY + "[Analysing quest book...]");
        hasInterrupted = false;

        ArrayList<ItemStack> gatheredQuests = new ArrayList<>();
        ArrayList<ItemStack> gatheredMiniQuests = new ArrayList<>();
        ArrayList<ItemStack> gatheredDiscoveries = new ArrayList<>();

        FakeInventory inv = new FakeInventory(QUEST_BOOK_WINDOW_TITLE_PATTERN, new InventoryOpenByItem(7));
        inv.setLimitTime(15000); // 15 seconds

        inv.onReceiveItems((i) -> {
            Pair<Integer, ItemStack> nextClick = null;

            // lores
            if (questsLore.isEmpty()) {
                Pair<Integer, ItemStack> quests = i.findItem("Quests", FilterType.CONTAINS);
                if (quests != null) questsLore = ItemUtils.getLore(quests.b);
            }
            if (miniQuestsLore.isEmpty()) {
                Pair<Integer, ItemStack> miniQuests = i.findItem("Mini-Quests", FilterType.CONTAINS);
                if (miniQuests != null) miniQuestsLore = ItemUtils.getLore(miniQuests.b);
            }
            if (discoveriesLore.isEmpty()) {
                Pair<Integer, ItemStack> discoveries = i.findItem("Discoveries", FilterType.CONTAINS);
                if (discoveries != null) discoveriesLore = ItemUtils.getLore(discoveries.b);
            }
            if (secretDiscoveriesLore.isEmpty()) {
                Pair<Integer, ItemStack> secretDiscoveries = i.findItem("Secret Discoveries", FilterType.EQUALS);
                if (secretDiscoveries != null) secretDiscoveriesLore = ItemUtils.getLore(secretDiscoveries.b);
            }

            // go to the right page
            if(!i.getWindowTitle().contains(currentPosition.getWindowName())) {
                nextClick = i.findItem(currentPosition.getItemName(), FilterType.EQUALS);

                i.clickItem(nextClick.a, 1, ClickType.PICKUP); // 1 because otherwise wynn sends the inventory twice
                return;
            }

            // page scanning
            if (currentPosition == AnalysePosition.QUESTS) {
                nextClick = i.findItem(">>>>>", FilterType.CONTAINS); // next page item

                for (ItemStack stack : i.getInventory()) {
                    if (!stack.hasDisplayName()) continue; // also checks for nbt

                    List<String> lore = ItemUtils.getLore(stack).stream().map(TextFormatting::getTextWithoutFormattingCodes).collect(Collectors.toList());
                    if (lore.isEmpty() || !lore.contains("Right click to track")) continue; //not a valid quest

                    if (fullRead) {
                        gatheredQuests.add(stack);
                        continue;
                    }

                    String displayName = getTextWithoutFormattingCodes(stack.getDisplayName());
                    displayName = displayName.trim().replace("À", "");
                    if (currentQuests.containsValue(displayName) && currentQuests.get(displayName).equals(stack)) {
                        continue;
                    }

                    gatheredQuests.add(stack);
                    nextClick = null;
                    break;
                }
            }

            if (currentPosition == AnalysePosition.MINIQUESTS) {
                nextClick = i.findItem(">>>>>", FilterType.CONTAINS); // next page item

                for (ItemStack stack : i.getInventory()) {
                    if (!stack.hasDisplayName()) continue; // also checks for nbt

                    List<String> lore = ItemUtils.getLore(stack).stream().map(TextFormatting::getTextWithoutFormattingCodes).collect(Collectors.toList());
                    if (lore.isEmpty() || !lore.contains("Right click to track")) continue; //not a valid mini-quest

                    if (fullRead) {
                        gatheredMiniQuests.add(stack);
                        continue;
                    }

                    String displayName = getTextWithoutFormattingCodes(stack.getDisplayName());
                    if (currentMiniQuests.containsValue(displayName) && currentMiniQuests.get(displayName).equals(stack)) {
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
                    if (currentDiscoveries.containsValue(displayName)) {
                        continue;
                    }

                    gatheredDiscoveries.add(stack);
                    nextClick = null;
                    break;
                }
            }

            // effective pagination
            if (nextClick == null) {
                if (!fullRead || currentPosition == AnalysePosition.MINIQUESTS || currentPosition == AnalysePosition.SECRET_DISCOVERIES) {
                    i.close();
                    return;
                }

                //go to next page
                if (currentPosition == AnalysePosition.QUESTS) {
                    currentPosition = AnalysePosition.MINIQUESTS;
                    nextClick = i.findItem(currentPosition.getItemName(), FilterType.EQUALS);
                }

                if (currentPosition == AnalysePosition.DISCOVERIES) {
                    currentPosition = AnalysePosition.SECRET_DISCOVERIES;
                    nextClick = i.findItem(currentPosition.getItemName(), FilterType.EQUALS);
                }
            }

            i.clickItem(nextClick.a, 1, ClickType.PICKUP); // 1 because otherwise wynn sends the inventory twice
        });

        inv.onClose((i, result) -> {
            if (result == InventoryResult.CLOSED_SUCCESSFULLY) {
                if (!gatheredQuests.isEmpty()) parseQuests(gatheredQuests);
                if (!gatheredMiniQuests.isEmpty()) parseMiniQuests(gatheredMiniQuests);
                if (!gatheredDiscoveries.isEmpty()) parseDiscoveries(gatheredDiscoveries);

                FrameworkManager.getEventBus().post(new QuestBookUpdateEvent());
                sendMessage(GRAY + "[Quest book analyzed]");
                return;
            }

            hasInterrupted = true;
            sendMessage(RED + "[Quest book analysis failed, manually open your book to try again]");
        });

        inv.open();
        lastInventory = inv;
    }

    private static void parseQuests(ArrayList<ItemStack> quests) {
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

    private static void parseMiniQuests(ArrayList<ItemStack> miniQuests) {
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

    private static void parseDiscoveries(ArrayList<ItemStack> discoveries) {
        for (ItemStack stack : discoveries) {
            try {
                DiscoveryInfo discovery = new DiscoveryInfo(stack);
                if (!discovery.isValid()) continue;

                currentDiscoveries.put(discovery.getName(), discovery);
            } catch (Exception ex) {
                ex.printStackTrace();
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

    public static void clearData() {
        currentQuests.clear();
        currentMiniQuests.clear();
        currentDiscoveries.clear();

        questsLore.clear();
        miniQuestsLore.clear();
        discoveriesLore.clear();
        secretDiscoveriesLore.clear();

        hasInterrupted = false;
        lastInventory = null;
        trackedQuest = null;
    }

    public static void completeQuest(String name, boolean isMini) {
        if (trackedQuest != null && trackedQuest.equalsIgnoreCase(name)) trackedQuest = null;

        QuestInfo info = isMini ? getMiniQuest(name) : getQuest(name);
        if (info == null) {
            readQuestBook(AnalysePosition.QUESTS, true);
            return;
        }

        info.setAsCompleted();
    }

    private static void sendMessage(String msg) {
        // Can be called from nio thread by FakeInventory
        Minecraft.getMinecraft().addScheduledTask(() ->
                ChatOverlay.getChat().printChatMessageWithOptionalDeletion(new TextComponentString(msg), MESSAGE_ID)
        );
    }

}
