/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.modules.core.managers.PacketQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import static com.wynntils.core.framework.instances.PlayerInfo.SPELL_LEFT;
import static com.wynntils.core.framework.instances.PlayerInfo.SPELL_RIGHT;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    private static final int[] spellUnlock = { 1, 11, 21, 31 };

    private static void queueSpell(int spellNumber, boolean a, boolean b, boolean c) {
        if (!canCastSpell(spellNumber)) return;

        int level = PlayerInfo.getPlayerInfo().getLevel();
        boolean isLowLevel = level <= 11;
        Class<?> packetClass = isLowLevel ? SPacketTitle.class : SPacketChat.class;
        PacketQueue.queueComplexPacket(a == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 0, a, isLowLevel));
        PacketQueue.queueComplexPacket(b == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 1, b, isLowLevel));
        PacketQueue.queueComplexPacket(c == PlayerInfo.SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, 2, c, isLowLevel));
    }

    public static void castFirstSpell() {
        if (PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(1, SPELL_LEFT, SPELL_RIGHT, SPELL_LEFT);
            return;
        }

        queueSpell(1, SPELL_RIGHT, SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        if (PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(2, SPELL_LEFT, SPELL_LEFT, SPELL_LEFT);
            return;
        }

        queueSpell(2, SPELL_RIGHT, SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        if (PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(3, SPELL_LEFT, SPELL_RIGHT, SPELL_RIGHT);
            return;
        }

        queueSpell(3, SPELL_RIGHT, SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        if (PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            queueSpell(4, SPELL_LEFT, SPELL_LEFT, SPELL_RIGHT);
            return;
        }

        queueSpell(4, SPELL_RIGHT, SPELL_RIGHT, SPELL_LEFT);
    }

    private static boolean canCastSpell(int spell) {
        if (!Reference.onWorld || PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.NONE) {
            return false;
        }

        if (PlayerInfo.getPlayerInfo().getLevel() < spellUnlock[spell - 1]) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You have not yet unlocked this spell! You need to be level " + spellUnlock[spell - 1]
            ));
            return false;
        }

        return true;
    }

    private static boolean checkKey(Packet<?> input, int pos, boolean clickType, boolean isLowLevel) {
        boolean[] spell;

        if (isLowLevel) {
            SPacketTitle title = (SPacketTitle) input;
            if (title.getType() != SPacketTitle.Type.SUBTITLE) return false;

            spell = PlayerInfo.getPlayerInfo().parseSpellFromTitle(title.getMessage().getFormattedText());
        } else {
            SPacketChat title = (SPacketChat) input;
            if (title.getType() != ChatType.GAME_INFO) return false;

            PlayerInfo.getPlayerInfo().updateActionBar(title.getChatComponent().getUnformattedText());

            spell = PlayerInfo.getPlayerInfo().getLastSpell();
        }

        return pos < spell.length && spell[pos] == clickType;
    }

}
