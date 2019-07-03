/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import static com.wynntils.modules.core.managers.PacketQueue.queuePackets;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    public static void castFirstSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queuePackets(leftClick, rightClick, releaseClick, leftClick);
            return;
        }

        queuePackets(rightClick, releaseClick, leftClick, rightClick, releaseClick);
    }

    public static void castSecondSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queuePackets(leftClick, leftClick, leftClick);
            return;
        }

        queuePackets(rightClick, releaseClick, rightClick, releaseClick, rightClick, releaseClick);
    }

    public static void castThirdSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queuePackets(leftClick, rightClick, releaseClick, rightClick, releaseClick);
            return;
        }

        queuePackets(rightClick, releaseClick, leftClick, leftClick);
    }

    public static void castFourthSpell() {
        if(!canCastSpell()) return;

        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queuePackets(leftClick, leftClick, rightClick, releaseClick);
            return;
        }

        queuePackets(rightClick, releaseClick, rightClick, releaseClick, leftClick);
    }

    private static boolean canCastSpell() {
        return Reference.onWorld && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE;
    }

}
