/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.managers;

import cf.wynntils.ModCore;
import cf.wynntils.modules.core.instances.PacketFilter;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;

public class QuestManager {

    private static boolean readingQuestBook = false;

    private static ArrayList<QuestInfo> currentQuestsData = new ArrayList<>();
    public static QuestInfo trackedQuest = null;

    public static void requestQuestBookReading() {
        readingQuestBook = true;
        currentQuestsData.clear();

        Minecraft mc = ModCore.mc();
        int slot = mc.player.inventory.currentItem;

        PacketFilter.instance.sendPacket(new CPacketHeldItemChange(7));
        PacketFilter.instance.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        PacketFilter.instance.sendPacket(new CPacketHeldItemChange(slot));
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

    public static void addQuestInfo(QuestInfo quest) {
        currentQuestsData.add(quest);
    }

}
