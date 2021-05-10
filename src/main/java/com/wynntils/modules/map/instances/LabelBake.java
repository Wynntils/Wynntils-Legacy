/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.utils.objects.Location;

import java.util.*;

public class LabelBake {

    private static final List<String> SERVICES = Arrays.asList(
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
            "Crafting Station",
            "Food",
            "Helmets and Chestplates",
            "Potions",
            "Scrolls",
            "Spears and Daggers",
            "Bows, Wands and Reliks",
            "Left-Click to set up booth"
    );

    public static final LocationBaker locationBaker = new LocationBaker();
    public static final Map<Location, String> detectedNpcs = new HashMap<>();
    public static final Map<Location, String> detectedDungeons = new HashMap<>();
    public static final Map<Location, String> detectedTerritories = new HashMap<>();
    public static final Map<Location, String> detectedServices = new HashMap<>();

    public static void handleLabel(String label, String formattedLabel, Location location) {
        if (SERVICES.stream().anyMatch(l -> l.equals(label))) {
            detectedServices.put(location, label);
            return;
        }

        if (IGNORE.stream().anyMatch(l -> l.equals(label))) return;

        if (label.equals("NPC")) {
            locationBaker.registerNpcLocation(location);
            return;
        }

        if (label.equals("Click for Options")) {
            locationBaker.registerTerritoryLocation(location);
            return;
        }

        if (formattedLabel.equals("§6Dungeon")) {
            locationBaker.registerDungeonLocation(location);
            return;
        }

        // It might be an NPC name
        locationBaker.registerName(label, formattedLabel, location);
        String frm = formattedLabel.replace("§", "%");
        System.out.println("LABEL: " + label + " " + location + " " + frm + "==" + formattedLabel);
    }

    public static void handleNpc(String label, String formattedLabel, Location location) {
        if (!(label.equals("Sell, scrap and repair items") || label.equals("NPC"))) return;

        System.out.println("formatted NPC: " + formattedLabel);
        locationBaker.registerNpcLocation(location);
    }

    public static void onWorldJoin(WynnWorldEvent.Join e) {
        // Remove data from the lobby when joining a world
        locationBaker.nameMap.clear();
        locationBaker.formattedNameMap.clear();
        locationBaker.npcLocationMap.clear();
        detectedNpcs.clear();
        detectedServices.clear();
    }

    public static class LocationBaker {
        public final Map<LabelLocation, String> nameMap = new HashMap<>();
        public final Map<LabelLocation, String> formattedNameMap = new HashMap<>();
        private final Map<LabelLocation, Location> npcLocationMap = new HashMap<>();
        private final Map<LabelLocation, Location> dungeonLocationMap = new HashMap<>();
        private final Map<LabelLocation, Location> territoryLocationMap = new HashMap<>();
        public final Map<LabelLocation, Location> otherLocMap = new HashMap<>();

        public void registerNpcLocation(Location location) {
            LabelLocation l = new LabelLocation(location);
            String name = nameMap.get(l);
            if (name != null) {
                String formattedName = formattedNameMap.get(l);
                System.out.println("NPC: " + name + " as " + formattedName);
                finalizeNpc(location, name);
                nameMap.remove(l);
                formattedNameMap.remove(l);
                otherLocMap.remove(l);
            } else {
                npcLocationMap.put(l, location);
            }
        }

        public void registerDungeonLocation(Location location) {
            LabelLocation l = new LabelLocation(location);
            String name = nameMap.get(l);
            if (name != null) {
                String formattedName = formattedNameMap.get(l);
                System.out.println("DUNGEON: " + name + " as " + formattedName);
                finalizeDungeon(location, name);
                nameMap.remove(l);
                formattedNameMap.remove(l);
                otherLocMap.remove(l);
            } else {
                dungeonLocationMap.put(l, location);
            }
        }

        public void registerTerritoryLocation(Location location) {
            LabelLocation l = new LabelLocation(location);
            String name = nameMap.get(l);
            if (name != null) {
                String formattedName = formattedNameMap.get(l);
                System.out.println("TERRITORY: " + name + " as " + formattedName);
                finalizeTerritory(location, name);
                nameMap.remove(l);
                formattedNameMap.remove(l);
                otherLocMap.remove(l);
            } else {
                territoryLocationMap.put(l, location);
            }
        }

        public void registerName(String name, String formattedLabel, Location location) {
            LabelLocation l = new LabelLocation(location);
            Location npcLoc = npcLocationMap.get(l);
            Location dungeonLoc = dungeonLocationMap.get(l);
            if (npcLoc != null) {
                finalizeNpc(npcLoc, name);
                npcLocationMap.remove(l);
            } else if (dungeonLoc != null) {
                finalizeDungeon(dungeonLoc, name);
                dungeonLocationMap.remove(l);
            } else{
                nameMap.put(l, name);
                formattedNameMap.put(l, formattedLabel);
                otherLocMap.put(l, location);
            }
        }

        private void finalizeNpc(Location location, String name) {
            detectedNpcs.put(location, name);
        }

        private void finalizeDungeon(Location location, String name) {
            detectedDungeons.put(location, name);
        }

        private void finalizeTerritory(Location location, String name) {
            detectedTerritories.put(location, name);
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
