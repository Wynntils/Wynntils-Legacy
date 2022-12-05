package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossBarData extends PlayerData {

    private static final DecimalFormat maxBloodPoolFormat = new DecimalFormat("#0");

    public static final Pattern BLOOD_POOL_PATTERN = Pattern.compile("§cBlood Pool §4\\[§c(\\d+)%§4]§r");
    public static UUID BLOOD_POOL_UUID;

    public static final Pattern MANA_BANK_PATTERN = Pattern.compile("§bMana Bank §3\\[(\\d+)/(\\d+)§3]§r");
    public static UUID MANA_BANK_UUID;

    public static final Pattern AWAKENING_PATTERN = Pattern.compile("§fAwakening §7\\[§f(\\d+)/200§7]§r");
    public static UUID AWAKENING_UUID;

    public static final Pattern CORRUPTED_PATTERN = Pattern.compile("§cCorrupted §4\\[§c(\\d+)%§4]§r");
    public static UUID CORRUPTED_UUID;

    public static final Pattern FOCUS_PATTERN = Pattern.compile("§eFocus §6\\[§e(\\d+)/(\\d+)§6]§r");
    public static UUID FOCUS_UUID;

    public BossBarData() {
        maxBloodPoolFormat.setRoundingMode(RoundingMode.DOWN);
    }

    public void processAddPacket(SPacketUpdateBossInfo packet) {
        // This is a new bar being added, set the UUID field
        String title = packet.getName().getFormattedText();

        // Using regex because it is possible a guild name could contain the same text as the boss bar
        Matcher bloodPoolMatcher = BLOOD_POOL_PATTERN.matcher(title);
        if (bloodPoolMatcher.matches()) {
            BLOOD_POOL_UUID = packet.getUniqueId();
            // ADD packets do not contain percent data, leave it as 1 for now to show the bar
            // The percent packet will update it later
            // This is a problem exclusive to the Blood Pool bar
            get(CharacterData.class).setMaxBloodPool(1);
            return;
        }

        Matcher manaBankMatcher = MANA_BANK_PATTERN.matcher(title);
        if (manaBankMatcher.matches()) {
            MANA_BANK_UUID = packet.getUniqueId();
            return;
        }

        Matcher awakeningMatcher = AWAKENING_PATTERN.matcher(title);
        if (awakeningMatcher.matches()) {
            AWAKENING_UUID = packet.getUniqueId();
            return;
        }

        Matcher corruptedMatcher = CORRUPTED_PATTERN.matcher(title);
        if (corruptedMatcher.matches()) {
            CORRUPTED_UUID = packet.getUniqueId();
            return;
        }

        Matcher focusMatcher = FOCUS_PATTERN.matcher(title);
        if (focusMatcher.matches()) {
            FOCUS_UUID = packet.getUniqueId();
            return;
        }
    }

    public void processNamePacket(SPacketUpdateBossInfo packet) {
        if (packet.getOperation() != SPacketUpdateBossInfo.Operation.UPDATE_NAME && packet.getOperation() != SPacketUpdateBossInfo.Operation.ADD) return;
        UUID uuid = packet.getUniqueId();

        if (uuid.equals(BLOOD_POOL_UUID)) {
            updateBloodPool(packet);
        } else if (uuid.equals(MANA_BANK_UUID)) {
            updateManaBank(packet);
        } else if (uuid.equals(AWAKENING_UUID)) {
            updateAwakened(packet);
        } else if (uuid.equals(CORRUPTED_UUID)) {
            updateCorrupted(packet);
        } else if (uuid.equals(FOCUS_UUID)) {
            updateFocus(packet);
        }
    }

    public void processPctPacket(SPacketUpdateBossInfo packet) {
        UUID uuid = packet.getUniqueId();

        if (uuid.equals(BLOOD_POOL_UUID)) {
            updateBloodPoolFromPct(packet);
        }
    }

    /**
     * Clears the given bossbar's stats back to -1
     * Also removes any related visible bars from the user
     * @param packet the packet of the bossbar to clear
     */
    public void processRemovePacket(SPacketUpdateBossInfo packet) {
        UUID uuid = packet.getUniqueId();

        if (uuid.equals(BLOOD_POOL_UUID)) {
            BLOOD_POOL_UUID = null;
            get(CharacterData.class).setBloodPool(-1);
            get(CharacterData.class).setMaxBloodPool(-1);

        } else if (uuid.equals(MANA_BANK_UUID)) {
            MANA_BANK_UUID = null;
            get(CharacterData.class).setManaBank(-1);
            get(CharacterData.class).setMaxManaBank(-1);

        } else if (uuid.equals(AWAKENING_UUID)) {
            AWAKENING_UUID = null;
            get(CharacterData.class).setAwakenedProgress(-1);

        } else if (uuid.equals(CORRUPTED_UUID)) {
            CORRUPTED_UUID = null;
            get(CharacterData.class).setCorruptedProgressPercent(-1);

        } else if (uuid.equals(FOCUS_UUID)) {
            FOCUS_UUID = null;
            get(CharacterData.class).setFocus(-1);
            get(CharacterData.class).setMaxFocus(-1);
        }
    }

    private void updateBloodPool(SPacketUpdateBossInfo packet) {
        Matcher m = BLOOD_POOL_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        get(CharacterData.class).setBloodPool(Integer.parseInt(m.group(1)));
    }

    private void updateBloodPoolFromPct(SPacketUpdateBossInfo packet) {
        if (packet.getOperation() != SPacketUpdateBossInfo.Operation.UPDATE_PCT) return;

        int bloodPool = get(CharacterData.class).getCurrentBloodPool();
        if (packet.getPercent() != 0) { // This is only sent initially, don't attempt to div by zero
            int maxBloodPool = Integer.parseInt(maxBloodPoolFormat.format(bloodPool / packet.getPercent()));
            get(CharacterData.class).setMaxBloodPool(maxBloodPool);
        }
    }

    private void updateManaBank(SPacketUpdateBossInfo packet) {
        Matcher m = MANA_BANK_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        get(CharacterData.class).setManaBank(Integer.parseInt(m.group(1)));
        get(CharacterData.class).setMaxManaBank(Integer.parseInt(m.group(2)));
    }

    private void updateAwakened(SPacketUpdateBossInfo packet) {
        Matcher m = AWAKENING_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        get(CharacterData.class).setAwakenedProgress(Integer.parseInt(m.group(1)));
    }

    private void updateCorrupted(SPacketUpdateBossInfo packet) {
        Matcher m = CORRUPTED_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        get(CharacterData.class).setCorruptedProgressPercent(Integer.parseInt(m.group(1)));
    }

    private void updateFocus(SPacketUpdateBossInfo packet) {
        Matcher m = FOCUS_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        get(CharacterData.class).setFocus(Integer.parseInt(m.group(1)));
        get(CharacterData.class).setMaxFocus(Integer.parseInt(m.group(2)));
    }
}
