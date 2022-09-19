/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.core.framework.instances.data.SpellData.SPELL_LEFT;
import static com.wynntils.core.framework.instances.data.SpellData.SPELL_RIGHT;

public class QuickCastManager implements Listener {

    private static final CPacketAnimation leftClick = new CPacketAnimation(EnumHand.MAIN_HAND);
    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);

    private static final Pattern SPELL_PATTERN =
            StringUtils.compileCCRegex("§([LR]|Right|Left)§-§([LR?]|Right|Left)§-§([LR?]|Right|Left)§");
    private static final Pattern CLASS_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Class Req:.+");
    private static final Pattern COMBAT_LVL_REQ_OK_PATTERN = Pattern.compile("§a✔§7 Combat Lv. Min:.+");
    private static final Pattern SKILL_POINT_MIN_NOT_REACHED_PATTERN = Pattern.compile("§c✖§7 (.+) Min: \\d+");

    private static final int QUEUE_TICK_DELAY = 4;

    private static final Queue<Packet<?>> spellPacketQueue = new LinkedList<>();
    private static List<Boolean> spellInProgress = new ArrayList<>(3);
    private static int lastSelectedSlot = 0;
    private static int spellResetCountdown = 0;
    private static int packetQueueCountdown = 0;

    /**
     * Queues the spell to be sent to the server.
     * true in boolean fields represents right click.
     */
    private static void queueSpell(boolean first, boolean second, boolean third) {
        if (!spellPacketQueue.isEmpty()) {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + "Cannot cast a spell while another spell cast is in progress."
            ));
            return;
        }

        NetHandlerPlayClient connection = McIf.mc().getConnection();
        if (connection == null) return;

        boolean isArcher = PlayerInfo.get(CharacterData.class).getCurrentClass() == ClassType.ARCHER;

        List<Boolean> spell = new ArrayList<>(3);
        spell.add(isArcher != first); // Inverts booleans (r -> l, l -> r) if archer
        spell.add(isArcher != second);
        spell.add(isArcher != third);

        for (int i = 0; i < spellInProgress.size(); i++) {
            if (spellInProgress.get(i) != spell.get(i)) {
                McIf.player().sendMessage(new TextComponentString(
                        TextFormatting.GRAY + "Finish your incompatible spell-cast first."
                ));
                return;
            }
        }

        lastSelectedSlot = McIf.player().inventory.currentItem;
        List<Boolean> remainderToCast = spell.subList(spellInProgress.size(), spell.size());
        remainderToCast.stream().map(QuickCastManager::getPacket).forEach(spellPacketQueue::add);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld) return;

        // Clear spell after the 40 tick timeout period
        if (spellResetCountdown > 0 && --spellResetCountdown <= 0) {
            spellInProgress.clear();
        }

        if (spellPacketQueue.isEmpty()) return; // Nothing to cast, return
        if (--packetQueueCountdown > 0) return; // Don't cast on this tick, return

        if (McIf.mc().currentScreen != null) return; // Don't cast if a GUI is open

        int currentSelectedSlot = McIf.player().inventory.currentItem;
        boolean slotChanged = currentSelectedSlot != lastSelectedSlot;

        NetHandlerPlayClient connection = McIf.mc().getConnection();
        if (connection == null) return;

        if (slotChanged) connection.sendPacket(new CPacketHeldItemChange(lastSelectedSlot));
        connection.sendPacket(spellPacketQueue.remove());
        if (slotChanged) connection.sendPacket(new CPacketHeldItemChange(currentSelectedSlot));

        // Number of ticks to delay between spell cast button inputs
        if (!spellPacketQueue.isEmpty()) packetQueueCountdown = QUEUE_TICK_DELAY;
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        spellPacketQueue.clear();
        spellInProgress.clear();
    }

    @SubscribeEvent
    public void onSubtitleUpdate(PacketEvent<SPacketTitle> e) {
        if (!Reference.onWorld || e.getPacket().getType() != SPacketTitle.Type.SUBTITLE) return;
        // Subtitle for low level players
        tryUpdateSpell(e.getPacket().getMessage().getUnformattedText());
    }

    @SubscribeEvent
    public void onActionbarMessageUpdate(PacketEvent<SPacketChat> e) {
        if (!Reference.onWorld || e.getPacket().getType() != ChatType.GAME_INFO) return;

        tryUpdateSpell(e.getPacket().getChatComponent().getUnformattedText());
    }

    private static void tryUpdateSpell(String text) {
        List<Boolean> spell = getSpellFromString(text);
        if (spell == null) return;
        if (spellInProgress.equals(spell)) return;
        if (spell.size() == 3) {
            spellInProgress.clear();
            spellResetCountdown = 0;
        } else {
            spellInProgress = spell;
            spellResetCountdown = 40;
        }

    }

    private static List<Boolean> getSpellFromString(String s) {
        Matcher spellMatcher = SPELL_PATTERN.matcher(s);
        if (!spellMatcher.find()) return null;

        int size = 1;
        for (; size < 3; ++size) {
            if (spellMatcher.group(size + 1).equals("?")) break;
        }

        List<Boolean> spell = new ArrayList<>(3);
        for (int i = 0; i < size; ++i) {
            spell.add(i, spellMatcher.group(i + 1).charAt(0) == 'R');
        }

        return spell;
    }

    private static CastCheckStatus checkSpellCastRequest() {
        // Check that the player is holding a weapon
        ItemStack heldItem = McIf.player().getHeldItemMainhand();
        if (heldItem.isEmpty() || !ItemUtils.getStringLore(heldItem).contains("§7 Class Req:")) return CastCheckStatus.NOT_HOLDING_WEAPON;

        // Check that the held weapon is the correct class
        Matcher weaponClassMatcher = CLASS_REQ_OK_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        if (!weaponClassMatcher.find()) return CastCheckStatus.WEAPON_WRONG_CLASS;

        // Check that the the user meets the level requirement for the weapon
        Matcher weaponLevelMatcher = COMBAT_LVL_REQ_OK_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        if (!weaponLevelMatcher.find()) return CastCheckStatus.WEAPON_LEVEL_REQ_NOT_MET;

        // Make sure that the user has enough skill points for the weapon
        Matcher weaponSkillPointMatcher = SKILL_POINT_MIN_NOT_REACHED_PATTERN.matcher(ItemUtils.getStringLore(heldItem));
        // (!) Not a negated check; the pattern matches when the skill point requirement is not met
        if (weaponSkillPointMatcher.find()) return CastCheckStatus.WEAPON_SP_REQ_NOT_MET;

        return CastCheckStatus.OK;
    }

    private static Packet<?> getPacket(boolean direction) {
        return direction ? rightClick : leftClick;
    }

    public static void castFirstSpell() {
        CastCheckStatus status = checkSpellCastRequest();
        if (status != CastCheckStatus.OK) {
            status.sendMessage();
            return;
        }
        queueSpell(SPELL_RIGHT, SPELL_LEFT, SPELL_RIGHT);
    }

    public static void castSecondSpell() {
        CastCheckStatus status = checkSpellCastRequest();
        if (status != CastCheckStatus.OK) {
            status.sendMessage();
            return;
        }
        queueSpell(SPELL_RIGHT, SPELL_RIGHT, SPELL_RIGHT);
    }

    public static void castThirdSpell() {
        CastCheckStatus status = checkSpellCastRequest();
        if (status != CastCheckStatus.OK) {
            status.sendMessage();
            return;
        }
        queueSpell(SPELL_RIGHT, SPELL_LEFT, SPELL_LEFT);
    }

    public static void castFourthSpell() {
        CastCheckStatus status = checkSpellCastRequest();
        if (status != CastCheckStatus.OK) {
            status.sendMessage();
            return;
        }
        queueSpell(SPELL_RIGHT, SPELL_RIGHT, SPELL_LEFT);
    }

    private enum CastCheckStatus {
        NOT_HOLDING_WEAPON("The held item is not a weapon."),
        WEAPON_WRONG_CLASS("The held weapon is not for this class."),
        WEAPON_LEVEL_REQ_NOT_MET("You must level up your character to use this weapon."),
        WEAPON_SP_REQ_NOT_MET("The current class does not have enough %s assigned to use the held weapon."),
        OK("");

        private final String message;

        CastCheckStatus(String message) {
            this.message = message;
        }

        public void sendMessage() {
            McIf.player().sendMessage(new TextComponentString(
                    TextFormatting.GRAY + message)
            );
        }
    }
}
