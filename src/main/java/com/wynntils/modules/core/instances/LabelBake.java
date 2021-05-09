/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.core.utils.objects.Location;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelBake {
    public static class LabelLocation {
        private final int x;
        private final int z;

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LabelLocation that = (LabelLocation) o;

            if (x != that.x) return false;
            return z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + z;
            return result;
        }

        public LabelLocation(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public LabelLocation(Location location) {
            this.x = (int) location.x;
            this.z = (int) location.z;
        }
    }

    public static class BakeCollector<K, V> {
        private final Map<K, List<V>> map = new HashMap<>();
        private final BiPredicate<K, List<V>> verifier;

        public BakeCollector(BiPredicate<K, List<V>> verifier) {
            this.verifier = verifier;
        }

        public void addAndVerify(K key, V value) {
            List<V> values = map.get(key);
            if (values == null) {
                values = new LinkedList<>();
                values.add(value);
                map.put(key, values);
            } else {
                values.add(value);
                if (verifier.test(key, values)) {
                    // Finished baking, remove key
                    map.remove(key);
                }
            }
        }
    }

    public static class NpcBaker {
        private final Map<LabelLocation, String> nameMap = new HashMap<>();
        private final Map<LabelLocation, Location> locationMap = new HashMap<>();

        public void registerNpcLocation(Location location) {
            LabelLocation l = new LabelLocation(location);
            String name = nameMap.get(l);
            if (name != null) {
                finalizeNpc(location, name);
            } else {
                locationMap.put(l, location);
            }

        }

        public void registerName(String name, Location location) {
            LabelLocation l = new LabelLocation(location);
            Location orgLoc = locationMap.get(l);
            if (orgLoc != null) {
                finalizeNpc(orgLoc, name);
            } else {
                nameMap.put(l, name);
            }
        }

        private void finalizeNpc(Location location, String name) {
            System.out.println("NPC: " + name + ", " + location);
            detectedNpcs.put(location, name);
        }

    }

    private static final NpcBaker npcBaker = new NpcBaker();
    private static final Map<Location, String> detectedNpcs = new HashMap<>();
    private static final Map<Location, String> detectedServices = new HashMap<>();

    private static final List<String> markers = Arrays.asList(
            "Armour Merchant",
            "Blacksmith",
            "Click to go to your housing plot",
            "Dungeon Scroll Merchant",
            "Emerald Merchant",
            "Item Identifier",
            "Liquid Merchant",
            "Party Finder",
            "Potion Merchant",
            "Powder Master",
            "Scroll Merchant",
            "Trade Market",
            "Weapon Merchant",
            "Ⓛ Alchemism Ⓛ",
            "Ⓗ Armouring Ⓗ",
            "Ⓐ Cooking Ⓐ",
            "Ⓓ Jeweling Ⓓ",
            "Ⓔ Scribing Ⓔ",
            "Ⓕ Tailoring Ⓕ",
            "Ⓖ Weaponsmithing Ⓖ",
            "Ⓘ Woodworking Ⓘ");

    private static final List<String> ignore = Arrays.asList(
            "Buy & sell items",
            "on the market",
            "Looking for a group?",
            "Accessories",
            "Boots and Pants",
            "Bows",
            "Click for Options",
            "Crafting Station",
            "Food",
            "Helmets and Chestplates",
            "Potions",
            "Scrolls",
            "Spears and Daggers",
            "Bows, Wands and Reliks",
            "Left-Click to set up booth"
            );



    private static boolean npcBakeVerifier(LabelLocation location, List<String> labels) {
        if (labels.stream().anyMatch(x -> x.equals("NPC"))) {
            String npcName = labels.stream().filter(x -> !x.equals("NPC")).findFirst().get();
            return true;
        }
        return false;
    }

    public static void dumpDetectedLocations(String filename) {
        for (Location key : detectedNpcs.keySet()) {
            String name = detectedNpcs.get(key);
            printInstance("NPC", name, key);
        }

        for (Location key : detectedServices.keySet()) {
            String name = detectedServices.get(key);
            printInstance("Service", name, key);
        }
    }

    private static void printInstance(String type, String name, Location key) {
        System.out.println(type + ": " + name + ": " + key);
    }

    public static void handleLabel(String label, Location location) {
        if (markers.stream().anyMatch(l -> l.equals(label))) {
            System.out.println("MARKER: " + label + ", " + location);
            detectedServices.put(location, label);
            dumpDetectedLocations("");
            return;
        }

        if (ignore.stream().anyMatch(l -> l.equals(label))) return;

        // Ignore shops
        if (label.endsWith("'s Shop")) return;

        if (label.equals("NPC")) {
            npcBaker.registerNpcLocation(location);
            return;
        }

        // It might be an NPC name
        npcBaker.registerName(label, location);
    }

    public static void handleNpc(String label, Location location) {
        if (!(label.equals("Sell, scrap and repair items") || label.equals("NPC"))) return;

        npcBaker.registerNpcLocation(location);
    }

}
