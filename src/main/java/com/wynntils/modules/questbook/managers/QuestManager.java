/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.managers;

import com.wynntils.core.framework.enums.FilterType;
import com.wynntils.core.utils.Pair;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.instances.FakeInventory;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.enums.QuestSize;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.overlays.ui.DiscoveriesPage;
import com.wynntils.modules.questbook.overlays.ui.QuestsPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {

    private static long readRequestTime = 0;

    private static HashMap<String, QuestInfo> currentQuestsData = new HashMap<>();
    public static HashMap<String, DiscoveryInfo> currentDiscoveryData = new HashMap<>();
    public static QuestInfo trackedQuest = null;

    public static List<String> discoveryLore = new ArrayList<>();
    public static List<String> secretdiscoveryLore = new ArrayList<>();

    private static boolean secretDiscoveries = false;
    private static FakeInventory currentInventory = null;

    private static boolean analyseRequested = false;
    private static boolean bookOpened = false;

    /**
     * Queue a full QuestBook analyse
     */
    public static void requestAnalyse() {
        analyseRequested = true;
    }

    public static void executeQueue() {
        if(!analyseRequested || (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiContainer)) return;

        analyseRequested = false;
        readQuestBook();
    }

    /**
     * Requests a full QuestBook re-read, when the player is not with the book in hand
     */
    private static void readQuestBook() {
        if (!QuestBookConfig.INSTANCE.allowCustomQuestbook) return;

        if(currentInventory != null && currentInventory.isOpen()) {
            currentInventory.close();

            readQuestBook();
            return;
        }

        FakeInventory fakeInventory = new FakeInventory("[Pg.", 7);
        secretDiscoveries = false;

        fakeInventory.onReceiveItems(i -> {
            if(i.getWindowTitle().contains("Quests")) { //Quests
                Pair<Integer, ItemStack> next = i.findItem(">>>>>", FilterType.CONTAINS);
                Pair<Integer, ItemStack> discoveries = i.findItem("Discoveries", FilterType.EQUALS);

                //lore
                if(discoveries != null) {
                    discoveryLore = Utils.getLore(discoveries.b);
                    discoveryLore.removeAll(Arrays.asList("", null));
                }

                //parsing
                for(ItemStack item : i.getItems()) {
                    if(!item.hasDisplayName()) continue; //not a valid quest

                    List<String> lore = Utils.getLore(item);
                    if(lore.isEmpty()) continue; //not a valid quest

                    List<String> realLore = lore.stream().map(Utils::stripColor).collect(Collectors.toList());
                    if(!realLore.contains("Right click to track")) continue; //not a valid quest

                    QuestStatus status = null;
                    if(lore.get(0).contains("Completed!")) status = QuestStatus.COMPLETED;
                    else if(lore.get(0).contains("Started")) status = QuestStatus.STARTED;
                    else if(lore.get(0).contains("Can start")) status = QuestStatus.CAN_START;
                    else if(lore.get(0).contains("Cannot start")) status = QuestStatus.CANNOT_START;
                    if(status == null) continue;

                    int minLevel = Integer.valueOf(Utils.stripColor(lore.get(2)).replace("✔ Combat Lv. Min: ", "").replace("✖ Combat Lv. Min: ", ""));
                    QuestSize size = QuestSize.valueOf(Utils.stripColor(lore.get(3)).replace("- Length: ", "").toUpperCase());

                    String description = "";
                    for(int x = 5; x < lore.size(); x++) {
                        if(lore.get(x).equalsIgnoreCase(TextFormatting.GRAY + "Right click to track")) {
                            break;
                        }
                        description = description + Utils.stripColor(lore.get(x));
                    }

                    String displayName = Utils.stripColor(item.getDisplayName());
                    QuestInfo quest = new QuestInfo(displayName, status, minLevel, size, description, lore);
                    currentQuestsData.put(displayName, quest);

                    if(trackedQuest != null && trackedQuest.getName().equals(displayName)) {
                        if(quest.getStatus() == QuestStatus.COMPLETED) trackedQuest = null;
                        else trackedQuest = quest;
                    }
                }

                QuestBookPages.QUESTS.getPage().updateSearch();
                //pagination
                if (next != null) i.clickItem(next.a, 1, ClickType.PICKUP);
                else if (QuestBookConfig.INSTANCE.scanDiscoveries && discoveries != null) i.clickItem(discoveries.a, 1, ClickType.PICKUP);
                else i.close();
            } else if (i.getWindowTitle().contains("Discoveries")) { //Discoveries
                Pair<Integer, ItemStack> next = i.findItem(">>>>>", FilterType.CONTAINS);
                Pair<Integer, ItemStack> sDiscoveries = i.findItem("Secret Discoveries", FilterType.EQUALS);

                //lore
                if (sDiscoveries != null) {
                    secretdiscoveryLore = Utils.getLore(sDiscoveries.b);
                    secretdiscoveryLore.removeAll(Arrays.asList("", null));
                }

                for (ItemStack item : i.getItems()) { //parsing discoveries
                    if(!item.hasDisplayName()) continue; //not a valid discovery

                    List<String> lore = Utils.getLore(item);
                    if(lore.isEmpty() || !Utils.stripColor(lore.get(0)).contains("✔ Combat Lv")) continue; //not a valid discovery

                    String displayName = item.getDisplayName();
                    displayName = displayName.substring(0, displayName.length() - 1);

                    DiscoveryType discoveryType = null;
                    if (displayName.charAt(1) == 'e') discoveryType = DiscoveryType.WORLD;
                    else if (displayName.charAt(1) == 'f') discoveryType = DiscoveryType.TERRITORY;
                    else if (displayName.charAt(1) == 'b') discoveryType = DiscoveryType.SECRET;

                    int minLevel = Integer.valueOf(Utils.stripColor(lore.get(0)).replace("✔ Combat Lv. Min: ", ""));

                    String description = "";
                    for (int x = 2; x < lore.size(); x ++) {
                        description = description + Utils.stripColor(lore.get(x));
                    }

                    currentDiscoveryData.put(displayName, new DiscoveryInfo(displayName, minLevel, description, lore, discoveryType));
                }

                QuestBookPages.DISCOVERIES.getPage().updateSearch();
                //pagination
                if (next != null) i.clickItem(next.a, 1, ClickType.PICKUP);
                else if (!secretDiscoveries && sDiscoveries != null) {
                    secretDiscoveries = true;
                    i.clickItem(sDiscoveries.a, 1, ClickType.PICKUP);
                }
                else i.close();
            }else i.close();
        });
        fakeInventory.onClose(c -> {
            currentInventory = null;
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.GRAY + "[Quest Book Analyzed]"));
        });
        currentInventory = fakeInventory;

        fakeInventory.open();
    }

    /**
     * Returns the current quests data
     *
     * @return the current quest data in a {@link java.util.ArrayList}
     */
    public static HashMap<String, QuestInfo> getCurrentQuestsData() {
        return currentQuestsData;
    }

    /**
     * Returns the current tracked quest
     * if null, no quest is being tracked
     *
     * @return the current tracked quest
     */
    public static QuestInfo getTrackedQuest() {
        return trackedQuest;
    }
    
    /**
     * Returns the current discoveries data
     *
     * @return the current discovery data in a {@link java.util.ArrayList}
     */
    public static HashMap<String, DiscoveryInfo> getCurrentDiscoveriesData() {
        return currentDiscoveryData;
    }

    /**
     * Defines the current tracked quest
     *
     * @param selected the quest that you want to track
     */
    public static void setTrackedQuest(QuestInfo selected) {
        trackedQuest = selected;
    }

    /**
     * Check if the book was already opened before, if false it will request a read
     */
    public static void wasBookOpened() {
        if(bookOpened) return;

        bookOpened = true;
        requestAnalyse();
    }

    /**
     * Clears the entire collected book data
     */
    public static void clearData() {
        currentQuestsData.clear();
        currentDiscoveryData.clear();
        trackedQuest = null;
        analyseRequested = false;
        bookOpened = false;
        currentInventory = null;
    }

}
