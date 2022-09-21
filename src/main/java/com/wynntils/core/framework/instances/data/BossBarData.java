package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossBarData extends PlayerData {
    public static final Pattern BLOOD_POOL_PATTERN = Pattern.compile("§cBlood Pool §4\\[§c(\\d+)%§4]§r");
    public static final Pattern MANA_BANK_PATTERN = Pattern.compile("§bMana Bank §3\\[(\\d+)/(\\d+)§3]§r");
    private static final DecimalFormat maxBloodPoolFormat = new DecimalFormat("#0");

    public static final Pattern AWAKENED_PROGRESS_PATTERN = Pattern.compile("§fAwakening §7\\[§f(\\d+)/200§7]§r");

    public BossBarData() {
        maxBloodPoolFormat.setRoundingMode(RoundingMode.DOWN);
    }

    public void updateBossbarStats(SPacketUpdateBossInfo packet) {
        // (!) Do not remove .getName() check, Intellij is wrong about it
        if (packet == null || packet.getName() == null) return;

        updateBloodPool(packet);
        updateAwakenedBar(packet);
        updateManaBankBar(packet);
    }

    private void updateBloodPool(SPacketUpdateBossInfo packet) {
        Matcher m = BLOOD_POOL_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        int bloodPool = Integer.parseInt(m.group(1));
        get(CharacterData.class).setBloodPool(bloodPool);
        if (packet.getPercent() != 0) { // This is only sent initially, don't attempt to div by zero
            int maxBloodPool = Integer.parseInt(maxBloodPoolFormat.format(bloodPool / packet.getPercent()));
            get(CharacterData.class).setMaxBloodPool(maxBloodPool);
        }
    }

    private void updateManaBankBar(SPacketUpdateBossInfo packet) {
        Matcher m = MANA_BANK_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;
        get(CharacterData.class).setManaBank(Integer.parseInt(m.group(1)));
        get(CharacterData.class).setMaxManaBank(Integer.parseInt(m.group(2)));
    }

    private void updateAwakenedBar(SPacketUpdateBossInfo packet) {
        Matcher m = AWAKENED_PROGRESS_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;
        int awakeningProgress = Integer.parseInt(m.group(1));
        get(CharacterData.class).setAwakenedProgress(awakeningProgress);
    }
}
