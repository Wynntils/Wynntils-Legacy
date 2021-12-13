/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.instances;

import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelBake {

    private static final Pattern MINIQUEST_POST = Pattern.compile("§2§a.* Post §2\\[.* Lv\\. [0-9]+\\]");

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
            "Left-Click to set up booth",
            "EXIT"
    );

    public static final LocationBaker locationBaker = new LocationBaker();
    public static final Map<Location, String> detectedServices = new HashMap<>();
    public static final Map<Location, String> detectedMiniquests = new HashMap<>();

    public static void handleLabel(String label, String formattedLabel, Location location, Entity entity) {
        if (SERVICES.stream().anyMatch(l -> l.equals(label))) {
            if (entity instanceof EntityArmorStand) {
                detectedServices.put(location, label);
            }
            // If it's named as a service but is not an armor stand, we'll ignore it
            // (typically item frames outside the shop)
            return;
        }

        if (IGNORE.stream().anyMatch(l -> l.equals(label))) return;

        Matcher m = MINIQUEST_POST.matcher(formattedLabel);
        if (m.find()) {
            detectedMiniquests.put(location, label);
            return;
        }

        if (label.equals("NPC")) {
            locationBaker.registerTypeLocation(BakerType.NPC, location);
            return;
        }

        if (label.equals("Click for Options")) {
            locationBaker.registerTypeLocation(BakerType.TERRITORY, location);
            return;
        }

        if (formattedLabel.equals("§6Dungeon")) {
            locationBaker.registerTypeLocation(BakerType.DUNGEON, location);
            return;
        }

        if (formattedLabel.matches("^§b.*'s §7Shop$")) {
            locationBaker.registerTypeLocation(BakerType.BOOTH, location);
            return;
        }

        // It might be an NPC name
        locationBaker.registerName(label, formattedLabel, location);
        String frm = formattedLabel.replace("§", "%");
    }

    public static void handleNpc(String label, String formattedLabel, Location location) {
        if (!(label.equals("Sell, scrap and repair items") || label.equals("NPC"))) return;

        locationBaker.registerTypeLocation(BakerType.NPC, location);
    }

    public static void onWorldJoin(WynnWorldEvent.Join e) {
        // Remove data from the lobby when joining a world
        locationBaker.clearAll();
        detectedServices.clear();
    }

    public enum BakerType {
        NPC,
        DUNGEON,
        TERRITORY,
        MINIQUEST,
        BOOTH
    }
    public static class LocationBaker {
        public final Map<LabelLocation, String> nameMap = new HashMap<>();
        public final Map<LabelLocation, String> formattedNameMap = new HashMap<>();
        private final Map<BakerType, Map<LabelLocation, Location>> typeMaps = new HashMap<>();
        private final Map<LabelLocation, Location> npcLocationMap = new HashMap<>();
        public final Map<LabelLocation, Location> otherLocMap = new HashMap<>();

        public final Map<BakerType, Map<Location, String>> detectedTypes = new HashMap<>();

        public LocationBaker() {
            for (BakerType type : BakerType.values()) {
                typeMaps.put(type, new HashMap<>());
                detectedTypes.put(type, new HashMap<>());
            }
        }

        public void clearAll() {
            for (BakerType type : BakerType.values()) {
                typeMaps.get(type).clear();
                detectedTypes.get(type).clear();
            }
            nameMap.clear();
            formattedNameMap.clear();
            npcLocationMap.clear();
        }

        public void registerTypeLocation(BakerType type, Location location) {
            LabelLocation l = new LabelLocation(location);
            String name = nameMap.get(l);
            if (name != null) {
                finalizeType(type, location, name);
                nameMap.remove(l);
                formattedNameMap.remove(l);
                otherLocMap.remove(l);
            } else {
                Map<LabelLocation, Location> typeMap = typeMaps.get(type);
                typeMap.put(l, location);
            }
        }

        public void registerName(String name, String formattedLabel, Location location) {
            LabelLocation l = new LabelLocation(location);
            String cleanedName = name.replace("À", "");

            for (BakerType type : BakerType.values()) {
                Map<LabelLocation, Location> typeMap = typeMaps.get(type);
                Location typeLoc = typeMap.get(l);
                if (typeLoc != null) {
                    finalizeType(type, typeLoc, cleanedName);
                    typeMap.remove(l);
                    return;
                }
            }

            // If we had no matching type, just store it as a known name
            nameMap.put(l, cleanedName);
            formattedNameMap.put(l, formattedLabel);
            otherLocMap.put(l, location);
        }

        private void finalizeType(BakerType type, Location location, String name) {
            Map<Location, String> detectedTypeMap = detectedTypes.get(type);
            detectedTypeMap.put(location, name);
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
