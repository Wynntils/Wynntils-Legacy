/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.helpers.Delay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.core.framework.instances.data.SpellData.SPELL_LEFT;
import static com.wynntils.core.framework.instances.data.SpellData.SPELL_RIGHT;

public class QuickCastManager {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    private static final CPacketPlayerDigging releaseClick = new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

    private enum CastCheckStatus {
        CHAR_LEVEL_REQ_NOT_MET,
        NOT_HOLDING_WEAPON,
        WEAPON_WRONG_CLASS,
        WEAPON_LEVEL_REQ_NOT_MET,
        WEAPON_SP_REQ_NOT_MET,
        OK
    }

    private static final int[] spellUnlock = { 1, 6, 10, 10 };
    private static long earliestCastable = System.currentTimeMillis();
    private static final int delayTicks = 3; // 3 ticks = 150ms (6.666 cps), 2 ticks = 100ms (10 cps)
    private static final Pattern CLASS_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Class Req:.+");
    private static final Pattern COMBAT_LVL_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Combat Lv. Min:.+");
    private static final Pattern SKILL_POINT_MIN_NOT_REACHED_PATTERN = Pattern.compile("§c✖§7 (.+) Min: \\d+");

    /**
     * Queues the spell to be sent to the server.
     * true in boolean fields represents right click.
     */
    private static void queueSpell(boolean second, boolean third) {
        if (System.currentTimeMillis() < earliestCastable) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "Cannot start casting a spell while another spell cast is in progress."
            ));
            return;
        }

        NetHandlerPlayClient connection = McIf.mc().getConnection();
        if (connection == null) return;

        boolean first = SPELL_RIGHT;
        if (PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER) {
            // Archer spells are inverted
            first = !first;
            second = !second;
            third = !third;
        }
        // Variables used in the lambda expressions below should be final
        final boolean finalSecond = second;
        final boolean finalThird = third;

        connection.sendPacket(first ? rightClick : leftClick);
        new Delay(() -> connection.sendPacket(finalSecond ? rightClick : leftClick), delayTicks);
        new Delay(() -> connection.sendPacket(finalThird ? rightClick : leftClick), delayTicks * 2);
        // earliestCastable = (current time) + (delay in ticks * 3) * (50ms per tick) + (10ms buffer)
        // * 3 so there is still a delay between separate casts
        // + 10L as another buffer to prevent issues when spamming
        earliestCastable = System.currentTimeMillis() + delayTicks * 3L * 50L + 10L;
    }

    private static CastCheckStatus checkSpellCastRequest(int spellNumber) {
        // Check that the player has the minimum level required for the spell
        // These levels are implicit as you only need a certain number of AP to unlock the spell
        if (PlayerInfo.get(CharacterData.class).getLevel() < spellUnlock[spellNumber - 1]) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You have not yet unlocked this spell! You need to be level " + spellUnlock[spellNumber - 1] + " with the spell unlocked in the ability tree!"
            ));
            return CastCheckStatus.CHAR_LEVEL_REQ_NOT_MET;
        }

        // Check that the player is holding a weapon
        ItemStack heldItem = McIf.player().getHeldItemMainhand();
        if (heldItem.isEmpty() || !ItemUtils.getStringLore(heldItem).contains("§7 Class Req:")) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held item is not a weapon."
            ));
            return CastCheckStatus.NOT_HOLDING_WEAPON;
        }

        // Check that the held weapon is the correct class
        Matcher weaponClassMatcher = CLASS_REQ_OK_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        if (!weaponClassMatcher.find()) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The held weapon is not for this class."
            ));
            return CastCheckStatus.WEAPON_WRONG_CLASS;
        }

        // Check that the the user meets the level requirement for the weapon
        Matcher weaponLevelMatcher = COMBAT_LVL_REQ_OK_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        if (!weaponLevelMatcher.find()) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "You must level up your character to use this weapon."
            ));
            return CastCheckStatus.WEAPON_LEVEL_REQ_NOT_MET;
        }

        // Make sure that the user has enough skill points for the weapon
        Matcher weaponSkillPointMatcher = SKILL_POINT_MIN_NOT_REACHED_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        if (weaponSkillPointMatcher.find()) { // (!) Not a negated check; the pattern matches when the skill point requirement is not met
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "The current class does not have enough " + weaponSkillPointMatcher.group(1) + " assigned to use the held weapon."
            ));
            return CastCheckStatus.WEAPON_SP_REQ_NOT_MET;
        }

        return CastCheckStatus.OK;
    }

    public static void castFirstSpell() {
        if (checkSpellCastRequest(1) != CastCheckStatus.OK) return;
        queueSpell(SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        if (checkSpellCastRequest(2) != CastCheckStatus.OK) return;
        queueSpell(SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        if (checkSpellCastRequest(3) != CastCheckStatus.OK) return;
        queueSpell(SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        if (checkSpellCastRequest(4) != CastCheckStatus.OK) return;
        queueSpell(SPELL_RIGHT, SPELL_LEFT);
    }
}
