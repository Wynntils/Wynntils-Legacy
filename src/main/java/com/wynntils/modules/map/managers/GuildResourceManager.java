/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.map.managers;

import com.wynntils.McIf;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.util.StringUtils;

import java.util.HashMap;

public class GuildResourceManager {

    private static final HashMap<String, GuildResourceContainer> resources = new HashMap<>();

    /**
     * Proccess the advancement packet into resource containers
     * @param info the input packet
     */
    public static void processAdvancements(SPacketAdvancementInfo info) {
        if (info.getAdvancementsToAdd().isEmpty()) return;

        for (Advancement.Builder advancement : info.getAdvancementsToAdd().values()) {
            Advancement built = advancement.build(null);

            String territoryName = McIf.getUnformattedText(built.getDisplayText()).replace("[", "")
                    .replace("]", "");
            // the territory name has a shit ton of spaces at the end to make the advancement box bigger
            while (territoryName.endsWith(" ")) {
                territoryName = territoryName.substring(0, territoryName.length() - 1);
            }

            // ignore empty display texts they are used to generate the "lines"
            if (territoryName.isEmpty()) continue;

            // headquarters frame is challenge
            boolean headquarters = built.getDisplay().getFrame() == FrameType.CHALLENGE;

            // description is literaly a raw string with \n so we have to split
            // the text component also didn't parse the colors corrently so we can't use the unformatted text
            // no clue why although.
            String description = McIf.getUnformattedText(built.getDisplay().getDescription());
            String[] colored = description.split("\n");
            String[] raw = StringUtils.stripControlCodes(description).split("\n");

            GuildResourceContainer container = new GuildResourceContainer(raw, colored, headquarters);
            resources.put(territoryName, container);
        }
    }

    /**
     * Attempts to retrieve the resource container for the provided territory
     *
     * @param territory the input territory full name
     * @return the resource container if available otherwise null
     */
    public static GuildResourceContainer getResources(String territory) {
        return resources.getOrDefault(territory, null);
    }

    public static HashMap<String, GuildResourceContainer> getResources() {
        return resources;
    }

}
