/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.instances;

import com.google.common.collect.ImmutableMap;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelBake {

    private static final Pattern MINIQUEST_POST = Pattern.compile("^§2§a.* Post §2\\[.* Lv\\. [0-9]+\\]$");
    private static final Pattern GATHERING_LABEL = Pattern.compile("^. [ⒸⒷⒿⓀ] .* Lv. Min: [0-9]+$");
    private static final Pattern MINIQUEST_POST_PARSE = Pattern.compile("\\w+ Post \\[(\\w+) Lv. ([0-9]+)\\]");

    private static final Map<String, String> SERVICES_MAP = ImmutableMap.<String, String>builder().
        put("§dArmour Merchant", "Armour Merchant").
        put("§dBlacksmith", "Blacksmith").
        put("§dDungeon Merchant", "Dungeon Merchant").
        put("§dDungeon Scroll Merchant", "Dungeon Scroll Merchant").
        put("§dEmerald Merchant", "Emerald Merchant").
        put("§dItem Identifier", "Item Identifier").
        put("§dLiquid Merchant", "Liquid Merchant").
        put("§dParty Finder", "Party Finder").
        put("§dPotion Merchant", "Potion Merchant").
        put("§dPowder Master", "Powder Master").
        put("§dScroll Merchant", "Scroll Merchant").
        put("§dTool Merchant", "Tool Merchant").
        put("§dWeapon Merchant", "Weapon Merchant").
        put("§cTrade Market", "Trade Market").
        put("§fClick §7to go to your housing plot", "Housing Balloon").
        put("§6V.S.S. Seaskipper", "Boat Fast Travel").
        put("§fⒶ §6§lCooking§r§f Ⓐ", "Cooking Station").
        put("§fⒹ §6§lJeweling§r§f Ⓓ", "Jeweling Station").
        put("§fⒺ §6§lScribing§r§f Ⓔ", "Scribing Station").
        put("§fⒻ §6§lTailoring§r§f Ⓕ", "Tailoring Station").
        put("§fⒼ §6§lWeaponsmithing§r§f Ⓖ", "Weaponsmithing Station").
        put("§fⒽ §6§lArmouring§r§f Ⓗ", "Armouring Station").
        put("§fⒾ §6§lWoodworking§r§f Ⓘ", "Woodworking Station").
        put("§fⓁ §6§lAlchemism§r§f Ⓛ", "Alchemism Station").
        build();

    private static final List<String> IGNORE = Arrays.asList(
            "Buy & sell items",
            "on the market",
            "Looking for a group?",
            "Right-click to Sail",
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
            "Liquid Exchange",
            "Armour Shop",
            "Potion Shop",
            "Scroll Shop",
            "Weapon Shop",
            "EXIT"
    );

    public static final LocationBaker locationBaker = new LocationBaker();
    public static final Map<Location, String> detectedServices = new HashMap<>();
    public static final Map<Location, String> detectedMiniquests = new HashMap<>();
    public static final Map<Location, String> detectedMiniquestsLevel = new HashMap<>();

    public static void handleLabel(String label, String formattedLabel, Location location, Entity entity) {
        if (SERVICES_MAP.keySet().stream().anyMatch(l -> l.equals(formattedLabel))) {
            if (entity instanceof EntityArmorStand) {
                detectedServices.put(location, SERVICES_MAP.get(formattedLabel));
            }
            // If it's named as a service but is not an armor stand, we'll ignore it
            // (typically item frames outside the shop)
            return;
        }

        if (IGNORE.stream().anyMatch(l -> l.equals(label))) return;

        Matcher m = MINIQUEST_POST.matcher(formattedLabel);
        if (m.find()) {
            Matcher parse = MINIQUEST_POST_PARSE.matcher(label);
            // E.g. "Gathering Post [Fishing Lv. 8]" or
            // "Slaying Post [Combat Lv. 4]"
            if (parse.find()) {
                String type = parse.group(1);
                String level = parse.group(2);
                if (type.equals("Combat")) {
                    type = "Slaying";
                }
                detectedMiniquests.put(location, type);
                detectedMiniquestsLevel.put(location, level);
            }
            return;
        }

        Matcher m1 = GATHERING_LABEL.matcher(label);
        if (m1.find())         {
            Location offsetLocation = location.add(0, 1, 0);
            locationBaker.registerTypeLocation(BakerType.GATHER, offsetLocation);
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
            Location offsetLocation = location.add(0, -1, 0);
            locationBaker.registerTypeLocation(BakerType.BOOTH_LINE_2, offsetLocation);
            return;
        }

        if (label.matches ("Left-Click to set up booth")) {
            locationBaker.registerName("Unclaimed", "Unclaimed", location);
            locationBaker.registerTypeLocation(BakerType.BOOTH, location);
            return;
        }

        // Send the label on to the baker
        locationBaker.registerName(label, formattedLabel, location);
    }

    public static void handleNpc(String label, String formattedLabel, Location location) {
        if (!(label.equals("Sell, scrap and repair items") || label.equals("NPC"))) return;

        locationBaker.registerTypeLocation(BakerType.NPC, location);
    }

    public static class LabelInfo {
        private final String type;
        private final String name;
        private final String extraInfo;
        private final Location location;

        public LabelInfo(String type, String name, String extraInfo, Location location) {
            this.type = type;
            this.name = name;
            this.extraInfo = extraInfo;
            this.location = location;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static Set<LabelInfo> getAllLabelInfo() {
        Set<LabelInfo> allInfos = new HashSet<>();
        for (LabelBake.LabelLocation key : LabelBake.locationBaker.bakeMap.keySet()) {
            LabelBake.LocationBakeInfo info = LabelBake.locationBaker.bakeMap.get(key);
            if (info.name == null) {
                LabelInfo labelInfo = new LabelInfo("BROKEN", info.type.toString(), info.formattedName, info.location);
                allInfos.add(labelInfo);
            } else {
                // Booth line 2 is just a placeholder to hide the second line of booth ads
                if (info.type == LabelBake.BakerType.BOOTH_LINE_2) continue;

                String type = "Other";
                String name = info.name;
                String extraData = "...";

                if (info.type != null) {
                    type = info.type.toString();
                }
                if (info.type == LabelBake.BakerType.BOOTH) {
                    name = "..."; // hide current owner
                }
                if (info.type == LabelBake.BakerType.NPC) {
                    if (name.equals("Key Collector") || name.equals("Seaskipper Captain")) {
                        extraData = "Utility";
                    } else if (name.endsWith("Merchant")) {
                        extraData = "Merchant";
                    } else if (name.endsWith("Citizen")) {
                        extraData = "Citizen";
                    }
                }
                LabelInfo labelInfo = new LabelInfo(type, name, extraData, info.location);
                allInfos.add(labelInfo);
            }
        }

        for (Location key : LabelBake.detectedServices.keySet()) {
            String name = LabelBake.detectedServices.get(key);
            LabelInfo labelInfo = new LabelInfo("Service", name, "...", key);
            allInfos.add(labelInfo);
        }

        for (Location key : LabelBake.detectedMiniquests.keySet()) {
            String name = LabelBake.detectedMiniquests.get(key);
            String level = LabelBake.detectedMiniquestsLevel.get(key);
            LabelInfo labelInfo = new LabelInfo("Miniquest", name, level, key);
            allInfos.add(labelInfo);
        }
        return allInfos;
    }

    public static void onWorldJoin(WynnWorldEvent.Join e) {
        // Remove data from the lobby when joining a world
        locationBaker.clearAll();
        detectedServices.clear();
        detectedMiniquests.clear();
        detectedMiniquestsLevel.clear();
    }

    public enum BakerType {
        UNKNOWN("Unknown"),
        NPC("NPC"),
        DUNGEON("Dungeon"),
        TERRITORY("Territory Post"),
        GATHER("Gathering Spot"),
        BOOTH("Booth Shop"),
        BOOTH_LINE_2("Hidden Booth Data");

        private final String name;

        BakerType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class LocationBakeInfo {
        public String name;
        public String formattedName;
        public BakerType type;
        public Location location;
    }

    public static class LocationBaker {
        public final Map<LabelLocation, LocationBakeInfo> bakeMap = new HashMap<>();

        public LocationBaker() {
        }

        public void clearAll() {
            bakeMap.clear();
        }

        public void registerTypeLocation(BakerType type, Location location) {
            LabelLocation labelLocation = new LabelLocation(location);
            LocationBakeInfo info = bakeMap.get(labelLocation);

            if (info == null) {
                info = new LocationBakeInfo();
                info.type = type;
                info.location = location;
                bakeMap.put(labelLocation, info);
            } else {
                assert (info.type == null || info.type == type);
                info.type = type;
                updateLocation(info, location);
            }
        }

        private void updateLocation(LocationBakeInfo info, Location location) {
            if (location.y < info.location.y) {
                // Use the smallest found value (since NPCs are "falling" into existence)
                info.location = location;
            }
        }

        public void registerName(String name, String formattedLabel, Location location) {
            LabelLocation labelLocation = new LabelLocation(location);
            LocationBakeInfo info = bakeMap.get(labelLocation);
            String cleanedName = StringUtils.normalizeBadString(name);

            if (info == null) {
                info = new LocationBakeInfo();
                info.name = cleanedName;
                info.location = location;
                bakeMap.put(labelLocation, info);
            } else {
                assert (info.name == null || info.name.equals(cleanedName));
                info.name = cleanedName;
                updateLocation(info, location);
            }
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
