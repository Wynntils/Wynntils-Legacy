package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossBarData extends PlayerData {
    public static final Pattern BLOOD_POOL_PATTERN = Pattern.compile("§cBlood Pool §4\\[§c(\\d+)%§4]§r");
    private static final DecimalFormat maxBloodPoolFormat = new DecimalFormat("#0");

    public static final Pattern AWAKENING_PROGRESS_PATTERN = Pattern.compile("§fAwakening §7\\[§f(\\d+)/200§7]§r");

    public BossBarData() {
        maxBloodPoolFormat.setRoundingMode(RoundingMode.DOWN);
    }

    public void updateBossbarStats(SPacketUpdateBossInfo packet) {
        // (!) Do not remove .getName() check, Intellij is wrong about it
        if (packet == null || packet.getName() == null) return;

        updateBloodPool(packet);
        updateAwakeningBar(packet);
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

    private void updateAwakeningBar(SPacketUpdateBossInfo packet) {
        Matcher m = AWAKENING_PROGRESS_PATTERN.matcher(packet.getName().getFormattedText());
        if (!m.matches()) return;

        int awakeningProgress = Integer.parseInt(m.group(1));
        get(CharacterData.class).setAwakeningProgress(awakeningProgress);
    }
}
