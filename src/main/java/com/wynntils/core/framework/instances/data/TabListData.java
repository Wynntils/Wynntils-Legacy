package com.wynntils.core.framework.instances.data;

import com.wynntils.core.framework.instances.containers.PlayerData;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;

public class TabListData extends PlayerData {

    public void updateTabListFooterEffects(SPacketPlayerListHeaderFooter packet) {
        String text = packet.getFooter().getUnformattedText();
        if (!text.startsWith("§d§lStatus Effects§r")) return;

        String[] effects = text.split("\\s\\s"); // Effects are split up by 2 spaces
        for (int i = 1; i < effects.length; i++) {
            System.out.println(effects[i]);
        }
    }
}
