/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;

import static com.wynntils.modules.core.managers.PacketQueue.queueComplexPacket;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    public static void castFirstSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 1, false));
            queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 2, true));
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 3, false));
            return;
        }

        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 1, true));
        queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 2, false));
        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 3, true));
    }

    public static void castSecondSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 1, false));
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 2, false));
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 3, false));
            return;
        }

        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 1, true));
        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 2, true));
        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 3, true));
    }

    public static void castThirdSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 1, false));
            queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 2, true));
            queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 3, true));
            return;
        }

        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 1, true));
        queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 2, false));
        queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 3, false));
    }

    public static void castFourthSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 1, false));
            queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 2, false));
            queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 3, true));
            return;
        }

        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 1, true));
        queueComplexPacket(rightClick, SPacketChat.class, (c) -> checkKey(c, 2, true));
        queueComplexPacket(leftClick, SPacketChat.class, (c) -> checkKey(c, 3, false));
    }

    private static boolean canCastSpell() {
        return Reference.onWorld && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE;
    }

    public static boolean checkKey(Packet<?> input, int pos, boolean rightClick) {
        SPacketChat title = (SPacketChat) input;
        if(title.getType() != ChatType.GAME_INFO) return false;

        String key = rightClick ? "R" : "L";
        String message = TextFormatting.getTextWithoutFormattingCodes(title.getChatComponent().getUnformattedText());

        switch (pos) {
            case 1:
                return message.contains(" " + key + "-");// _key-
            case 2:
                return message.contains("-" + key + "-");// -key-
            case 3:
                return message.contains("-" + key + " ");// -key_
        }

        return false;
    }

}
