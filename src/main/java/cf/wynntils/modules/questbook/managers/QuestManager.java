/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.managers;

import cf.wynntils.ModCore;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

public class QuestManager {

    private static boolean readingQuestBook = false;

    private static ArrayList<QuestInfo> currentQuestsData = new ArrayList<>();
    public static QuestInfo trackedQuest = null;

    /**
     * Requests a full QuestBook re-read, when the player is not with the book in hand
     */
    public static void requestQuestBookReading() {
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
        readingQuestBook = true;
        currentQuestsData.clear();
    }

    public static ArrayList<QuestInfo> getCurrentQuestsData() {
        return currentQuestsData;
    }

    public static QuestInfo getTrackedQuest() {
        return trackedQuest;
    }

    public static void setTrackedQuest(QuestInfo selected) {
        trackedQuest = selected;
    }

    public static boolean isReadingQuestBook() {
        return readingQuestBook;
    }

    public static void setReadingQuestBook(boolean readingQuestBook) {
        QuestManager.readingQuestBook = readingQuestBook;
    }

    public static void updateTrackedQuest() {
        if(trackedQuest == null) return;

        QuestInfo questInfo = currentQuestsData.stream().filter(c -> c.getName().equals(trackedQuest.getName())).findFirst().orElse(null);
        if(questInfo != null && questInfo.getCurrentDescription().equals(trackedQuest.getCurrentDescription())) {
            return;
        }
        trackedQuest = questInfo;
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f));
    }

    public static void addQuestInfo(QuestInfo quest) {
        currentQuestsData.add(quest);
    }

}
