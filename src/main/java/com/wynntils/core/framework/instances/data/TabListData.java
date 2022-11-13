/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */
package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.overlays.hud.ConsumableTimerOverlay;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabListData extends PlayerData {

    /*
    CG1 is the color and symbol used for the effect, and the strength modifier string (eg. "79%")
      NCG1 is for strength modifiers without a decimal, and the % sign
      NCG2 is the decimal point and second \d+ option for strength modifiers with a decimal
    CG2 is the actual name of the effect
    CG3 is the duration string (eg. "1:23")

    Note: Buffs like "+190 Main Attack Damage" will have the +190 be considered as part of the name.
    Buffs like "17% Frenzy" will have the 17% be considered as part of the prefix.
    This is because the 17% in Frenzy (and certain other buffs) can change, but the static scroll buffs cannot.

    https://regexr.com/729qc
     */
    private static final Pattern TAB_EFFECT_PATTERN = Pattern.compile("(.+?§7 ?(?:\\d+(?:\\.\\d+)?%)?) ?([%\\-+\\/\\da-zA-Z\\s]+?) §[84a]\\((.+?)\\).*");

    /**
     * Updates the ConsumableTimerOverlay with the effects from the tab list
     * Only updates if the showEffects setting is enabled for the Consumable Timer
     */
    public void updateTabListFooterEffects(SPacketPlayerListHeaderFooter packet) {
        if (!OverlayConfig.ConsumableTimer.INSTANCE.showEffects) return;

        String text = packet.getFooter().getFormattedText();
        if (text.isEmpty()) ConsumableTimerOverlay.clearStaticTimers(false); // No timers, get rid of them
        if (!text.startsWith("§d§lStatus Effects§r")) return;

        ConsumableTimerOverlay.clearStaticTimers(false);

        String[] effects = text.split("\\s\\s"); // Effects are split up by 2 spaces)
        for (String effect : effects) {
            effect = effect.trim();
            if (effect.isEmpty()) continue;

            Matcher m = TAB_EFFECT_PATTERN.matcher(effect);
            if (!m.find()) continue;

            // See comment at TAB_EFFECT_PATTERN definition for the what the group numbers are
            ConsumableTimerOverlay.addStaticTimer(m.group(1), m.group(2), m.group(3), false);
        }
    }
}
