/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.framework.enums.GuildResource;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.objects.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildResourceContainer {

    public static final Pattern GENERATOR_PATTERN = Pattern.compile("\\+([0-9]*) (Emeralds|Ore|Wood|Fish|Crops) per Hour");
    public static final Pattern STORAGE_PATTERN = Pattern.compile("([0-9]+)\\/([0-9]+) stored");

    HashMap<GuildResource, Storage> storage = new HashMap<>();
    HashMap<GuildResource, Integer> generators = new HashMap<>();
    List<String> tradingRoutes = new ArrayList<>();

    String treasury;
    String defences;

    boolean headquarters;
    CustomColor color;

    /**
     * Holds and generates data based on the Achievement values gave by Wynncraft
     * Example of lore parsed here:
     *
     * Celestial Tigers [TGR]
     *
     * +12150 Emeralds per Hour   ---> represents a generator
     * 222/10000 stored           ---> represents a storage
     * Ⓑ 6/500 stored
     * Ⓒ 3/500 stored
     * Ⓚ +28800 Fish per Hour
     * Ⓚ 447/500 stored
     * Ⓙ 3/500 stored
     *
     * ✦ Treasury: High          ---> represents the treasury value (High)
     * Territory Defences: Low   ---> represents the territory defence (Low)
     *
     * Trading Routes:
     * - Tree Island              ---> represents a trading route
     * - Pirate Town
     * - Volcano Upper
     *
     * @param raw the input achievement description without colors
     * @param colored the input achievement description with colors
     */
    public GuildResourceContainer(String[] raw, String[] colored, boolean headquarters) {
        this.headquarters = headquarters;

        for (int i = 0; i < raw.length; i++) {
            String unformatted = raw[i];
            String formatted = colored[i];

            // initial trading route parsing
            if (unformatted.startsWith("-")) {
                tradingRoutes.add(unformatted.substring(2));
                continue;
            }

            // treasury parsing
            if (unformatted.startsWith("✦ Treasury:")) {
                treasury = formatted.substring(14);
                continue;
            }

            // defence parsing
            if (unformatted.startsWith("Territory Defences:")) {
                defences = formatted.substring(24);
                continue;
            }

            // finding the resource type
            GuildResource resource = null;
            for (GuildResource type : GuildResource.values()) {
                if (!formatted.contains(type.getColor().toString())) continue;

                resource = type;
                break;
            }

            if (resource == null) continue;

            // generator
            if (unformatted.contains("per Hour")) {
                Matcher m = GENERATOR_PATTERN.matcher(unformatted);
                if (!m.find()) continue;

                generators.put(resource, Integer.parseInt(m.group(1)));
                continue;
            }

            // storage
            Matcher m = STORAGE_PATTERN.matcher(unformatted);
            if (!m.find()) continue;

            storage.put(resource, new Storage(
                    Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))
            ));
        }

        float h = 0;
        float s = 0.6f;
        float v = 0.9f;

        double sum = generators.entrySet().stream()
                .filter(c -> c.getKey() != GuildResource.EMERALD).map(Map.Entry::getValue)
                .mapToInt(Integer::intValue).sum();

        for (Map.Entry<GuildResource, Integer> generator : generators.entrySet()) {
            switch (generator.getKey()) {
                case ORE:
                    v = 1f;
                    s = 0.3f;
                    break;
                case FISH:
                    h += 180 * (generator.getValue() / sum);
                    break;
                case WOOD:
                    h += 120 * (generator.getValue() / sum);
                    break;
                case CROPS:
                    h += 60 * (generator.getValue() / sum);
                    break;
                case EMERALD:
                    break;
            }
        }

        color = CustomColor.fromHSV(h / 360f, s, v, 1);
    }

    public HashMap<GuildResource, Integer> getGenerators() {
        return generators;
    }

    public HashMap<GuildResource, Storage> getStorage() {
        return storage;
    }

    public List<String> getTradingRoutes() {
        return tradingRoutes;
    }

    public Integer getGeneration(GuildResource resource) {
        return generators.getOrDefault(resource, 0);
    }

    public Storage getStorage(GuildResource resource) {
        return storage.get(resource);
    }

    public String getTreasury() {
        return treasury;
    }

    public String getDefences() {
        return defences;
    }

    public CustomColor getColor() {
        return color;
    }

    public boolean isHeadquarters() {
        return headquarters;
    }

    @Override
    public String toString() {
        return "GuildResourceContainer{" +
                "storage=" + storage +
                ", generators=" + generators +
                ", tradingRoutes=" + tradingRoutes +
                ", treasury='" + treasury + '\'' +
                ", defences='" + defences + '\'' +
                ", headquarters=" + headquarters +
                ", color=" + color +
                '}';
    }

}
