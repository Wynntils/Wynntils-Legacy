/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.utils.objects.Location;

import java.util.*;

public class LabelBake {

    private static final List<String> MARKERS = Arrays.asList(
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
            "Ⓘ Woodworking Ⓘ"
    );

    private static final List<String> IGNORE = Arrays.asList(
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

    public static final NpcBaker npcBaker = new NpcBaker();
    public static final Map<Location, String> detectedNpcs = new HashMap<>();
    public static final Map<Location, String> detectedServices = new HashMap<>();

    public static void handleLabel(String label, Location location) {
        if (MARKERS.stream().anyMatch(l -> l.equals(label))) {
            detectedServices.put(location, label);
            return;
        }

        if (IGNORE.stream().anyMatch(l -> l.equals(label))) return;

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

    public static class NpcBaker {
        public final Map<LabelLocation, String> nameMap = new HashMap<>();
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
            detectedNpcs.put(location, name);
        }
    }

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
}
