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

    private static final Pattern WYBEL_OWNER = Pattern.compile("^(?:\\[.*\\])|(?:.*'s .*)$");
    private static final Pattern WYBEL_LEVEL = Pattern.compile("^Lv. [0-9]+.*$");

    private static final BakeCollector<LabelLocation, String> npcBaker = new BakeCollector<>(LabelBake::npcBakeVerifier);

    private static boolean npcBakeVerifier(LabelLocation location, List<String> labels) {
        if (labels.stream().anyMatch(x -> x.equals("NPC"))) {
            String npcName = labels.stream().filter(x -> !x.equals("NPC")).findFirst().get();
            System.out.println("NPC: " + npcName + ", " + location);
            return true;
        }
        return false;
    }

    public static void handleLabel(String label, Location location) {
        if (markers.stream().anyMatch(l -> l.equals(label))) {
   //         System.out.println("MARKER: " + label + ", " + location);
            return;
        }
        if (ignore.stream().anyMatch(l -> l.equals(label))) {
    //        System.out.println("IGNORE: " + label + ", " + location);
            return;
        }

        // bake shop
        if (label.endsWith("'s Shop")) {
            System.out.println("SHOP:" + label + ", " + location);
            return;
        }

// bake wybel
        Matcher m9 = WYBEL_OWNER.matcher(label);
        if (m9.find()) {
            System.out.println("MATCH WYB_OWN:" + label);
            return;
        }

        Matcher m10 = WYBEL_LEVEL.matcher(label);
        if (m10.find()) {
            System.out.println("MATCH WYB_LEV:" + label);
            return;
        }

        // bake npc
        if (label.equals("NPC")) {
    //        System.out.println("NPC_AT:" + label + ", " + location);
            npcBaker.addAndVerify(new LabelLocation(location), label);
            return;
        }

        System.out.println("OTHER: " + label + ", " + location);
        npcBaker.addAndVerify(new LabelLocation(location), label);
    }

    public static void handleNpc(String label, Location location) {
        if (!(label.equals("Sell, scrap and repair items") || label.equals("NPC"))) return;
// bake NPC
        npcBaker.addAndVerify(new LabelLocation(location), label);
   //     System.out.println("BOT_AT: " + label + ", " + location);
    }

}
