/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.modules.core.managers.PacketQueue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;

import static com.wynntils.core.framework.instances.PlayerInfo.SPELL_LEFT;
import static com.wynntils.core.framework.instances.PlayerInfo.SPELL_RIGHT;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    private static void queueSpell(boolean a, boolean b, boolean c) {
        if (!canCastSpell()) return;

        PacketQueue.queueComplexPacket(a == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, SPacketChat.class, e -> checkKey(e, 0, a));
        PacketQueue.queueComplexPacket(b == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, SPacketChat.class, e -> checkKey(e, 1, b));
        PacketQueue.queueComplexPacket(c == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, SPacketChat.class, e -> checkKey(e, 2, c));
    }

    public static void castFirstSpell() {
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(SPELL_LEFT, SPELL_RIGHT, SPELL_LEFT);
            return;
        }

        queueSpell(SPELL_RIGHT, SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(SPELL_LEFT, SPELL_LEFT, SPELL_LEFT);
            return;
        }

        queueSpell(SPELL_RIGHT, SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(SPELL_LEFT, SPELL_RIGHT, SPELL_RIGHT);
            return;
        }

        queueSpell(SPELL_RIGHT, SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(SPELL_LEFT, SPELL_LEFT, SPELL_RIGHT);
            return;
        }

        queueSpell(SPELL_RIGHT, SPELL_RIGHT, SPELL_LEFT);
    }

    private static boolean canCastSpell() {
        return Reference.onWorld && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE;
    }

    public static boolean checkKey(Packet<?> input, int pos, boolean clickType) {
        SPacketChat title = (SPacketChat) input;
        if(title.getType() != ChatType.GAME_INFO) return false;

        PlayerInfo.getPlayerInfo().updateActionBar(title.getChatComponent().getUnformattedText());
        boolean[] spell = PlayerInfo.getPlayerInfo().getLastSpell();
        return pos < spell.length && spell[pos] == clickType;
    }

}
