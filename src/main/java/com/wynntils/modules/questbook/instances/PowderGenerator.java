package com.wynntils.modules.questbook.instances;

import com.wynntils.modules.questbook.enums.PowderElement;

import java.util.*;
import java.util.stream.Collectors;

public class PowderGenerator {
    private static final Map<PowderElement, List<PowderProfile>> powderProfileMap = new HashMap<PowderElement, List<PowderProfile>>() {{
        put(PowderElement.WATER,
                Arrays.asList(new PowderProfile(PowderElement.WATER, 1, 3, 4, 13, 3, 1),
                        new PowderProfile(PowderElement.WATER, 2, 4, 6, 15, 6, 1),
                        new PowderProfile(PowderElement.WATER, 3, 5, 8, 17, 11, 2),
                        new PowderProfile(PowderElement.WATER, 4, 6, 8, 21, 18, 4),
                        new PowderProfile(PowderElement.WATER, 5, 7, 10, 26, 28, 7),
                        new PowderProfile(PowderElement.WATER, 6, 9, 11, 32, 40, 10))
        );
        put(PowderElement.FIRE,
                Arrays.asList(new PowderProfile(PowderElement.FIRE, 1, 2, 5, 14, 3, 1),
                            new PowderProfile(PowderElement.FIRE, 2, 4, 8, 16, 5, 2),
                            new PowderProfile(PowderElement.FIRE, 3, 5, 9, 19, 9, 3),
                            new PowderProfile(PowderElement.FIRE, 4, 6, 9, 24, 16, 5),
                            new PowderProfile(PowderElement.FIRE, 5, 8, 10, 30, 25, 9),
                            new PowderProfile(PowderElement.FIRE, 6, 10, 12, 37, 36, 13))
        );
        put(PowderElement.AIR,
                Arrays.asList(new PowderProfile(PowderElement.AIR, 1, 2, 6, 11, 3, 1),
                        new PowderProfile(PowderElement.AIR, 2, 3, 10, 14, 6, 2),
                        new PowderProfile(PowderElement.AIR, 3, 4, 11, 17, 10, 3),
                        new PowderProfile(PowderElement.AIR, 4, 5, 11, 22, 16, 5),
                        new PowderProfile(PowderElement.AIR, 5, 7, 12, 28, 24, 9),
                        new PowderProfile(PowderElement.AIR, 6, 8, 14, 35, 34, 13))
        );
        put(PowderElement.EARTH,
                Arrays.asList(new PowderProfile(PowderElement.EARTH, 1, 3, 6, 17, 2, 1),
                        new PowderProfile(PowderElement.EARTH, 2, 5, 8, 21, 4, 2),
                        new PowderProfile(PowderElement.EARTH, 3, 6, 10, 25, 8, 3),
                        new PowderProfile(PowderElement.EARTH, 4, 7, 10, 31, 14, 5),
                        new PowderProfile(PowderElement.EARTH, 5, 9, 11, 38, 22, 9),
                        new PowderProfile(PowderElement.EARTH, 6, 11, 13, 46, 30, 13))
        );
        put(PowderElement.THUNDER,
                Arrays.asList(new PowderProfile(PowderElement.THUNDER, 1, 1, 8, 9, 3, 1),
                        new PowderProfile(PowderElement.THUNDER, 2, 1, 12, 11, 5, 1),
                        new PowderProfile(PowderElement.THUNDER, 3, 2, 15, 13, 9, 2),
                        new PowderProfile(PowderElement.THUNDER, 4, 3, 15, 17, 14, 4),
                        new PowderProfile(PowderElement.THUNDER, 5, 4, 17, 22, 20, 7),
                        new PowderProfile(PowderElement.THUNDER, 6, 5, 20, 28, 28, 10))
        );
    }};

    public static List<PowderProfile> getAllPowderProfiles() {
        return powderProfileMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static PowderProfile generatePowderProfile(PowderElement element, int tier) {
        return powderProfileMap.get(element).get(tier);
    }
}
