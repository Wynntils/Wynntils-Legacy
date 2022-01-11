/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.ActionBarData;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.SpellData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.core.managers.PacketQueue;
import net.minecraft.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.core.framework.instances.data.SpellData.SPELL_LEFT;
import static com.wynntils.core.framework.instances.data.SpellData.SPELL_RIGHT;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    private static final int[] spellUnlock = { 1, 11, 21, 31 };

    private static final Pattern WEAPON_SPEED_PATTERN = Pattern.compile("§7.+ Attack Speed");
    private static final Pattern CLASS_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Class Req:.+");
    private static final Pattern COMBAT_LVL_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Combat Lv. Min:.+");
    private static final Pattern SPELL_POINT_MIN_NOT_REACHED_PATTERN = Pattern.compile("§c✖§7 (.+) Min: (\\d+)");

    public static final boolean[] NO_SPELL = new boolean[0];

    //Fixes most bugs when user spams quickcast hotkeys, by not allowing them to start spell casting until the current one is finished
    public static boolean[] spellInProgress = NO_SPELL;

    private static void queueSpell(int spellNumber, boolean a, boolean b, boolean c) {
        boolean[] spell = new boolean[]{a, b, c};
        boolean[] maybeHalfSpell = canCastSpell(spellNumber, spell);
        if (maybeHalfSpell.length == 0) return;

        spellInProgress = spell;

        int level = PlayerInfo.get(CharacterData.class).getLevel();
        boolean isLowLevel = level <= 11;
        Class<?> packetClass = isLowLevel ? SPacketTitle.class : SPacketChat.class;
        int offset = 3 - maybeHalfSpell.length;
        for (int i = 0; i < maybeHalfSpell.length; i++) {
            final int finalI = i;
            PacketQueue.queueComplexPacket(maybeHalfSpell[i] == SPELL_LEFT ? leftClick : rightClick, packetClass, e -> checkKey(e, finalI + offset, maybeHalfSpell[finalI], isLowLevel));
        }
    }

    public static void castFirstSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(1, SPELL_LEFT, SPELL_RIGHT, SPELL_LEFT);
            return;
        }

        queueSpell(1, SPELL_RIGHT, SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(2, SPELL_LEFT, SPELL_LEFT, SPELL_LEFT);
            return;
        }

        queueSpell(2, SPELL_RIGHT, SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(3, SPELL_LEFT, SPELL_RIGHT, SPELL_RIGHT);
            return;
        }

        queueSpell(3, SPELL_RIGHT, SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            queueSpell(4, SPELL_LEFT, SPELL_LEFT, SPELL_RIGHT);
            return;
        }

        queueSpell(4, SPELL_RIGHT, SPELL_RIGHT, SPELL_LEFT);
    }

    private static boolean[] canCastSpell(int spell, boolean[] checkedSpell) {
        if (!Reference.onWorld || !PlayerInfo.get(CharacterData.class).isLoaded()) {
            return NO_SPELL;
        }

        if (PlayerInfo.get(CharacterData.class).getLevel() < spellUnlock[spell - 1]) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You have not yet unlocked this spell! You need to be level " + spellUnlock[spell - 1]
            ));
            return NO_SPELL;
        }

        if (PlayerInfo.get(CharacterData.class).getCurrentMana() == 0) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You do not have enough mana to cast this spell!"
            ));
            return NO_SPELL;
        }

        ItemStack heldItem = McIf.player().getHeldItemMainhand();

        List<String> lore = ItemUtils.getLore(heldItem);

        //If item has attack speed line, it is a weapon
        boolean isWeapon = false;
        //Check class reqs to see if the weapon can be used by current class
        boolean classReqOk = false;
        //Is the current combat level enough to use the weapon
        boolean combatLvlMinReached = false;
        //If there is a spell point requirement that is not reached, store it and print it later
        String notReachedSpellPointRequirements = null;

        int i = 0;
        for (; i < lore.size(); i++) {
            if (WEAPON_SPEED_PATTERN.matcher(lore.get(i)).matches())
            {
                isWeapon = true;
                break;
            }
        }

        if (!isWeapon)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held item is not a weapon."
            ));
            return NO_SPELL;
        }

        for (; i < lore.size(); i++) {
            if (CLASS_REQ_OK_PATTERN.matcher(lore.get(i)).matches())
            {
                classReqOk = true;
                break;
            }
        }

        if (!classReqOk)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held weapon is not for this class."
            ));
            return NO_SPELL;
        }

        for (; i < lore.size(); i++) {
            if (COMBAT_LVL_REQ_OK_PATTERN.matcher(lore.get(i)).matches())
            {
                combatLvlMinReached = true;
                break;
            }
        }

        if (!combatLvlMinReached)
        {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The current class level is too low to use the held weapon."
            ));
            return NO_SPELL;
        }

        for (; i < lore.size(); i++) {
            Matcher matcher = SPELL_POINT_MIN_NOT_REACHED_PATTERN.matcher(lore.get(i));
            if (matcher.matches())
            {
                notReachedSpellPointRequirements = matcher.group(1);
                break;
            }
        }

        if (notReachedSpellPointRequirements != null) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The current class does not have enough " + notReachedSpellPointRequirements + " assigned to use the held weapon."
            ));
            return NO_SPELL;
        }

        //If there is spell casting in progress, don't interfere
        boolean[] lastSpell = PlayerInfo.get(SpellData.class).getLastSpell();
        int lastSpellLength = lastSpell.length;
        if ((lastSpellLength != 0 && lastSpellLength != 3) || spellInProgress.length != 0)
        {
            if (lastSpellLength != 0 && lastSpellLength != 3 && spellInProgress.length == 0) {
                for (int i1 = 0; i1 < lastSpellLength; i1++) {
                    if (lastSpell[i1] != checkedSpell[i1]) {
                        McIf.player().sendMessage(new TextComponentString(
                                TextFormatting.GRAY + "Cannot start casting a spell while another spell cast is in progress."
                        ));
                        return NO_SPELL;
                    }
                }

                boolean[] halfSpell = new boolean[checkedSpell.length - lastSpellLength];
                System.arraycopy(checkedSpell, lastSpellLength, halfSpell, 0, halfSpell.length);
                return halfSpell;
            }

            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "Cannot start casting a spell while another spell cast is in progress."
            ));
            return NO_SPELL;
        }

        return checkedSpell;
    }

    private static boolean checkKey(Packet<?> input, int pos, boolean clickType, boolean isLowLevel) {
        boolean[] spell;

        SpellData data = PlayerInfo.get(SpellData.class);
        if (isLowLevel) {
            SPacketTitle title = (SPacketTitle) input;
            if (title.getType() != SPacketTitle.Type.SUBTITLE) return false;

            spell = data.parseSpellFromTitle(McIf.getFormattedText(title.getMessage()));
        } else {
            SPacketChat title = (SPacketChat) input;
            if (title.getType() != ChatType.GAME_INFO) return false;

            PlayerInfo.get(ActionBarData.class).updateActionBar(McIf.getUnformattedText(title.getChatComponent()));

            spell = data.getLastSpell();
        }

        boolean successful = pos < spell.length && spell[pos] == clickType;

        //If we sent the last part of the spell, reset the boolean, allowing the keybind to cast spells again
        /*
        Bolyai:
        This might not be 100% safe without checking if successful is true,
        as user interference during spell casting happens and we need to reset spellInProgress or it will get stuck,
        this case it should be fine, as there are other checks to make sure we don't spam spells.

        If spell casting is still very spammy, the if statement below should be changed to: if ((successful && pos == 2) || isThisLastResend)
        isThisLastResend should be true if PacketResponse's tries >= maxTries (or by simply counting retries and assuming maxTries was not changed).
        */
        if (pos == 2) {
            spellInProgress = NO_SPELL;
        }
        return successful;
    }
}
