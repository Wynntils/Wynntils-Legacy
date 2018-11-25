/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.managers;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.modules.questbook.enums.QuestStatus;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;

public class QuestManager {

    private static boolean readingQuestBook = false;
    private static long readRequestTime = 0;

    private static ArrayList<QuestInfo> currentQuestsData = new ArrayList<>();
    public static QuestInfo trackedQuest = null;
    public static List<String> discoveryLore = new ArrayList<>();
    public static List<String> secretdiscoveryLore = new ArrayList<>();

    /**
     * Requests a full QuestBook re-read, when the player is not with the book in hand
     */
    public static void requestQuestBookReading() {
        readRequestTime = System.currentTimeMillis();
        readingQuestBook = true;
        currentQuestsData.clear();

        Minecraft mc = ModCore.mc();
        int slot = mc.player.inventory.currentItem;

        if(slot == 7) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            return;
        }

        mc.getConnection().sendPacket(new CPacketHeldItemChange(7));
        mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
    }

    /**
     * Requests a full QuestBook re-read, when the player already clicked on the book by itself
     */
    public static void requestLessIntrusiveQuestBookReading() {
        readRequestTime = System.currentTimeMillis();
        readingQuestBook = true;
        currentQuestsData.clear();
    }

    /**
     * Returns the current quests data
     *
     * @return the current quest data in a {@link java.util.ArrayList}
     */
    public static ArrayList<QuestInfo> getCurrentQuestsData() {
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
     * Sets the current tracked quest.
     *
     * @param selected the track that will be tracked.
     */
    public static void setTrackedQuest(QuestInfo selected) {
        trackedQuest = selected;
    }

    /**
     * If the questbook is being read.
     * This can be used to avoid duplicates or crashes
     *
     * @return if the questbook is being read
     */
    public static boolean isReadingQuestBook() {
        if(System.currentTimeMillis() - readRequestTime >= 3000) {
            readingQuestBook = false;
            Reference.LOGGER.warn("Could not read questbook, timedout (" + (System.currentTimeMillis() - readRequestTime) + "ms)");
        }
        return readingQuestBook;
    }

    /**
     * Called when the questbook is starting or finish to be read
     * @see cf.wynntils.modules.questbook.events.ServerEvents#onInventoryReceiveItems line 107
     *
     * @param readingQuestBook selected boolean
     */
    public static void setReadingQuestBook(boolean readingQuestBook) {
        readRequestTime = System.currentTimeMillis();
        QuestManager.readingQuestBook = readingQuestBook;
    }

    /**
     * Called when the questbook updates to update the current tracked quest
     * @see cf.wynntils.modules.questbook.events.ServerEvents#onInventoryReceiveItems line 106
     *
     */
    public static void updateTrackedQuest() {
        if(trackedQuest == null) return;

        QuestInfo questInfo = currentQuestsData.stream().filter(c -> c.getName().equals(trackedQuest.getName())).findFirst().orElse(null);
        if(questInfo != null && questInfo.getCurrentDescription().equals(trackedQuest.getCurrentDescription())) {
            return;
        }
        trackedQuest = questInfo;
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f));

        if(trackedQuest.getStatus() == QuestStatus.COMPLETED) trackedQuest = null;
    }

    /**
     * Registers a new {@link cf.wynntils.modules.questbook.instances.QuestInfo} into the cache
     * Called when the questbook is reading for new quests
     * @see cf.wynntils.modules.questbook.events.ServerEvents#parseQuest(ItemStack) line 150
     *
     * @param quest the quest that will be registered
     */
    public static void addQuestInfo(QuestInfo quest) {
        currentQuestsData.add(quest);
    }

    /**
     * Update the current discovery lore
     *
     * @param lore the selected lore
     */
    public static void updateDiscoveryLore(String name, List<String> lore) {
        List<String> list = new ArrayList<>();
        list.add(name); list.addAll(lore);
        discoveryLore = list;
    }

    /**
     * Update the current secret discovery lore
     *
     * @param lore the selected lore
     */
    public static void updateSecretDiscoveryLore(String name, List<String> lore) {
        List<String> list = new ArrayList<>();
        list.add(name); list.addAll(lore);
        secretdiscoveryLore = list;
    }

}
