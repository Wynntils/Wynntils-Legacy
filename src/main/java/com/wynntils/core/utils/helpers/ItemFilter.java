/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.utils.helpers;

import com.google.common.collect.ImmutableList;
import com.wynntils.core.framework.enums.Comparison;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemAttackSpeed;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import com.wynntils.webapi.profiles.item.enums.MajorIdentification;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public interface ItemFilter extends Predicate<ItemProfile>, Comparator<ItemProfile> {

    Type<?> getFilterType();

    String toFilterString();

    static ItemFilter parseFilterString(String filterStr) throws FilteringException {
        int n = filterStr.indexOf(':');
        return n == -1 ? ByName.TYPE.parse(filterStr) : Type.getType(filterStr.substring(0, n)).parse(filterStr.substring(n + 1));
    }

    abstract class Type<T extends ItemFilter> {

        private static Map<String, Type<?>> typeRegistry = null;

        private static void initTypeRegistry() {
            typeRegistry = new HashMap<>();
            // there's a lot of em
            for (Class<?> filterClass : ItemFilter.class.getClasses()) {
                for (Field field : filterClass.getFields()) {
                    if ((field.getModifiers() & Modifier.STATIC) != 0 && Type.class.isAssignableFrom(field.getType())) {
                        try {
                            putType((Type<?>)field.get(null));
                        } catch (IllegalAccessException e) {
                            // probably not an issue
                        }
                    }
                }
            }
            typeRegistry = Collections.unmodifiableMap(typeRegistry);
        }

        private static void putType(Type<?> type) {
            typeRegistry.put(type.getName().toLowerCase(Locale.ROOT), type);
        }

        public static Type<?> getType(String name) throws FilteringException {
            if (typeRegistry == null) {
                initTypeRegistry();
            }
            Type<?> type = typeRegistry.get(name.toLowerCase(Locale.ROOT));
            if (type == null) throw new FilteringException("Unknown filter type: " + name);
            return type;
        }

        private final String name;

        public Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public abstract T parse(String filterStr) throws FilteringException;

        static Pair<String, SortDirection> parseSortDirection(String filterStr) {
            if (!filterStr.isEmpty()) {
                switch (filterStr.charAt(0)) {
                    case '^':
                        return new Pair<>(filterStr.substring(1), SortDirection.ASCENDING);
                    case '$':
                        return new Pair<>(filterStr.substring(1), SortDirection.DESCENDING);
                }
            }
            return new Pair<>(filterStr, SortDirection.NONE);
        }

        static <T extends Comparable<T>> List<Pair<Comparison, T>> parseComparisons(String filterStr, KeyExtractor<T> keyExtractor) throws FilteringException {
            if (filterStr.isEmpty()) return Collections.emptyList();
            List<Pair<Comparison, T>> rels = new ArrayList<>();
            for (String relStr : filterStr.split(",")) {
                relStr = relStr.trim();
                if (relStr.isEmpty()) continue;
                if (relStr.startsWith("<=")) {
                    rels.add(new Pair<>(Comparison.LESS_THAN_OR_EQUAL, keyExtractor.extractKey(relStr.substring(2))));
                } else if (relStr.startsWith(">=")) {
                    rels.add(new Pair<>(Comparison.GREATER_THAN_OR_EQUAL, keyExtractor.extractKey(relStr.substring(2))));
                } else if (relStr.startsWith("!=")) {
                    rels.add(new Pair<>(Comparison.NOT_EQUAL, keyExtractor.extractKey(relStr.substring(2))));
                } else {
                    switch (relStr.charAt(0)) {
                        case '<':
                            rels.add(new Pair<>(Comparison.LESS_THAN, keyExtractor.extractKey(relStr.substring(1))));
                            break;
                        case '>':
                            rels.add(new Pair<>(Comparison.GREATER_THAN, keyExtractor.extractKey(relStr.substring(1))));
                            break;
                        case '=':
                            rels.add(new Pair<>(Comparison.EQUAL, keyExtractor.extractKey(relStr.substring(1))));
                            break;
                        default:
                            rels.add(new Pair<>(Comparison.EQUAL, keyExtractor.extractKey(relStr)));
                            break;
                    }
                }
            }
            return rels;
        }

        @FunctionalInterface
        interface KeyExtractor<T> {

            T extractKey(String str) throws FilteringException;

        }

    }

    enum SortDirection {

        ASCENDING("^") {
            @Override
            public int modifyComparison(int cmp) {
                return cmp;
            }
        },

        DESCENDING("$") {
            @Override
            public int modifyComparison(int cmp) {
                return -cmp;
            }
        },

        NONE("") {
            @Override
            public int modifyComparison(int cmp) {
                return 0;
            }
        };

        public final String prefix;

        SortDirection(String prefix) {
            this.prefix = prefix;
        }

        public abstract int modifyComparison(int cmp);

    }

    class ByName implements ItemFilter {

        public static final Type<ByName> TYPE = new Type<ByName>("Name") {
            @Override
            public ByName parse(String filterStr) {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                return new ByName(sortDir.a, false, sortDir.b);
            }
        };

        private String searchStr;
        private final boolean fuzzy;
        private final SortDirection sortDir;

        public ByName(String searchStr, boolean fuzzy, SortDirection sortDir) {
            this.searchStr = searchStr.toLowerCase(Locale.ROOT);
            this.fuzzy = fuzzy;
            this.sortDir = sortDir;
        }

        public void adjoin(ByName o) { // this is a hack; don't call this unless you're ItemSearchState!
            searchStr = searchStr.isEmpty() ? o.searchStr : (searchStr + " " + o.searchStr);
        }

        public String getSearchString() {
            return searchStr;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public Type<ByName> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            StringBuilder buf = new StringBuilder(TYPE.getName()).append(':').append(sortDir.prefix);
            if (!searchStr.isEmpty()) buf.append(StringUtils.quoteIfContainsSpace(searchStr));
            return buf.toString();
        }

        @Override
        public boolean test(ItemProfile item) {
            return fuzzy ? StringUtils.fuzzyMatch(item.getDisplayName().toLowerCase(Locale.ROOT), searchStr)
                    : item.getDisplayName().toLowerCase(Locale.ROOT).contains(searchStr);
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return sortDir.modifyComparison(a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        }

    }

    class ByType implements ItemFilter {

        public static final Type<ByType> TYPE = new Type<ByType>("Type") {
            @Override
            public ByType parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                filterStr = sortDir.a;
                Set<ItemType> allowedTypes = EnumSet.noneOf(ItemType.class);
                for (String token : filterStr.split(",")) {
                    token = token.trim().toLowerCase(Locale.ROOT);
                    if (token.isEmpty()) continue;
                    ItemType type = ItemType.matchText(token);
                    if (type != null) {
                        allowedTypes.add(type);
                    } else {
                        switch (token) {
                            case "armor":
                            case "armour":
                                allowedTypes.addAll(ARMOURS);
                                break;
                            case "weapon":
                                allowedTypes.addAll(WEAPONS);
                                break;
                            case "accessory":
                            case "bauble":
                                allowedTypes.addAll(ACCESSORIES);
                                break;
                            default:
                                throw new FilteringException("Unknown item type: " + token.trim());
                        }
                    }
                }
                return new ByType(allowedTypes, sortDir.b);
            }
        };

        private static final List<ItemType> ARMOURS = ImmutableList.of(ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS);
        private static final List<ItemType> WEAPONS = ImmutableList.of(ItemType.WAND, ItemType.DAGGER, ItemType.SPEAR, ItemType.BOW, ItemType.RELIK);
        private static final List<ItemType> ACCESSORIES = ImmutableList.of(ItemType.NECKLACE, ItemType.RING, ItemType.BRACELET);

        private final Set<ItemType> allowedTypes;
        private final SortDirection sortDir;

        public ByType(Collection<ItemType> allowedTypes, SortDirection sortDir) {
            this.allowedTypes = Collections.unmodifiableSet(EnumSet.copyOf(allowedTypes));
            this.sortDir = sortDir;
        }

        public Set<ItemType> getAllowedTypes() {
            return allowedTypes;
        }

        @Override
        public Type<?> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            List<String> keys = new ArrayList<>();
            categoryToFilterString(ARMOURS, "armor", keys);
            categoryToFilterString(WEAPONS, "weapon", keys);
            categoryToFilterString(ACCESSORIES, "accessory", keys);
            return TYPE.getName() + ':' + sortDir.prefix + StringUtils.quoteIfContainsSpace(String.join(",", keys));
        }

        private void categoryToFilterString(List<ItemType> types, String categoryName, List<String> dest) {
            if (allowedTypes.containsAll(types)) {
                dest.add(categoryName);
            } else {
                for (ItemType type : types) {
                    if (allowedTypes.contains(type)) {
                        dest.add(type.name().toLowerCase(Locale.ROOT));
                    }
                }
            }
        }

        @Override
        public boolean test(ItemProfile item) {
            return allowedTypes.contains(item.getItemInfo().getType());
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return sortDir.modifyComparison(a.getItemInfo().getType().compareTo(b.getItemInfo().getType()));
        }

    }

    class ByRarity implements ItemFilter {

        public static final Type<ByRarity> TYPE = new Type<ByRarity>("Rarity") {
            @Override
            public ByRarity parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                List<Pair<Comparison, ItemTier>> comparisons = parseComparisons(sortDir.a, s -> {
                    ItemTier rarity = ItemTier.matchText(s);
                    if (rarity == null) throw new FilteringException("Unknown rarity: " + s);
                    return rarity;
                });
                return new ByRarity(comparisons, sortDir.b);
            }
        };

        private final List<Pair<Comparison, ItemTier>> comps;
        private final SortDirection sortDir;

        public ByRarity(List<Pair<Comparison, ItemTier>> comps, SortDirection sortDir) {
            this.comps = comps;
            this.sortDir = sortDir;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public Type<?> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            return TYPE.getName() + ':' + sortDir.prefix + comps.stream()
                    .map(c -> (c.a == Comparison.EQUAL ? "" : c.a.symbol) + c.b.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(","));
        }

        @Override
        public boolean test(ItemProfile item) {
            for (Pair<Comparison, ItemTier> comp : comps) {
                if (!comp.a.test(item.getTier(), comp.b)) return false;
            }
            return true;
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return sortDir.modifyComparison(a.getTier().compareTo(b.getTier()));
        }

    }

    class ByStat implements ItemFilter {

        // requirements
        public static final StatType TYPE_COMBAT_LEVEL = new StatType("Level", i -> i.getRequirements().getLevel());
        public static final StatType TYPE_STR_REQ = new StatType("StrReq", i -> i.getRequirements().getStrength());
        public static final StatType TYPE_DEX_REQ = new StatType("DexReq", i -> i.getRequirements().getDexterity());
        public static final StatType TYPE_INT_REQ = new StatType("IntReq", i -> i.getRequirements().getIntelligence());
        public static final StatType TYPE_DEF_REQ = new StatType("DefReq", i -> i.getRequirements().getDefense());
        public static final StatType TYPE_AGI_REQ = new StatType("AgiReq", i -> i.getRequirements().getAgility());
        public static final StatType TYPE_SUM_REQ = StatType.sum("SumReq", TYPE_STR_REQ, TYPE_DEX_REQ, TYPE_INT_REQ, TYPE_DEF_REQ, TYPE_AGI_REQ);

        // damages
        public static final StatType TYPE_NEUTRAL_DMG = new StatType("NeutralDmg", i -> i.getAverageDamages().getOrDefault(DamageType.NEUTRAL, 0));
        public static final StatType TYPE_EARTH_DMG = new StatType("EarthDmg", i -> i.getAverageDamages().getOrDefault(DamageType.EARTH, 0));
        public static final StatType TYPE_THUNDER_DMG = new StatType("ThunderDmg", i -> i.getAverageDamages().getOrDefault(DamageType.THUNDER, 0));
        public static final StatType TYPE_WATER_DMG = new StatType("WaterDmg", i -> i.getAverageDamages().getOrDefault(DamageType.WATER, 0));
        public static final StatType TYPE_FIRE_DMG = new StatType("FireDmg", i -> i.getAverageDamages().getOrDefault(DamageType.FIRE, 0));
        public static final StatType TYPE_AIR_DMG = new StatType("AirDmg", i -> i.getAverageDamages().getOrDefault(DamageType.AIR, 0));
        public static final StatType TYPE_SUM_DMG = StatType.sum("SumDmg", TYPE_NEUTRAL_DMG, TYPE_EARTH_DMG, TYPE_THUNDER_DMG, TYPE_WATER_DMG, TYPE_FIRE_DMG, TYPE_AIR_DMG);

        // defenses
        public static final StatType TYPE_HEALTH = new StatType("Health", ItemProfile::getHealth);
        public static final StatType TYPE_EARTH_DEF = new StatType("EarthDef", i -> i.getElementalDefenses().getOrDefault(DamageType.EARTH, 0));
        public static final StatType TYPE_THUNDER_DEF = new StatType("ThunderDef", i -> i.getElementalDefenses().getOrDefault(DamageType.THUNDER, 0));
        public static final StatType TYPE_WATER_DEF = new StatType("WaterDef", i -> i.getElementalDefenses().getOrDefault(DamageType.WATER, 0));
        public static final StatType TYPE_FIRE_DEF = new StatType("FireDef", i -> i.getElementalDefenses().getOrDefault(DamageType.FIRE, 0));
        public static final StatType TYPE_AIR_DEF = new StatType("AirDef", i -> i.getElementalDefenses().getOrDefault(DamageType.AIR, 0));
        public static final StatType TYPE_SUM_DEF = StatType.sum("SumDef", TYPE_EARTH_DEF, TYPE_THUNDER_DEF, TYPE_WATER_DEF, TYPE_FIRE_DEF, TYPE_AIR_DEF);

        // attribute ids
        public static final StatType TYPE_STR = StatType.getIdStat("Str", "rawStrength");
        public static final StatType TYPE_DEX = StatType.getIdStat("Dex", "rawDexterity");
        public static final StatType TYPE_INT = StatType.getIdStat("Int", "rawIntelligence");
        public static final StatType TYPE_DEF = StatType.getIdStat("Def", "rawDefence");
        public static final StatType TYPE_AGI = StatType.getIdStat("Agi", "rawAgility");
        public static final StatType TYPE_SKILL_POINTS = StatType.sum("SkillPoints", TYPE_STR, TYPE_DEX, TYPE_INT, TYPE_DEF, TYPE_AGI);

        // damage ids
        public static final StatType TYPE_MAIN_ATK_NEUTRAL_DMG = StatType.getIdStat("MainAtkNeutralDmg", "rawMainAttackNeutralDamage");
        public static final StatType TYPE_MAIN_ATK_DMG = StatType.getIdStat("MainAtkDmg", "mainAttackDamage");
        public static final StatType TYPE_SPELL_NEUTRAL_DMG = StatType.getIdStat("SpellNeutralDmg", "rawNeutralSpellDamage");
        public static final StatType TYPE_SPELL_DMG = StatType.getIdStat("SpellDmg", "spellDamage");
        public static final StatType TYPE_BONUS_EARTH_DMG = StatType.getIdStat("BonusEarthDmg", "earthDamage");
        public static final StatType TYPE_BONUS_THUNDER_DMG = StatType.getIdStat("BonusThunderDmg", "thunderDamage");
        public static final StatType TYPE_BONUS_WATER_DMG = StatType.getIdStat("BonusWaterDmg", "waterDamage");
        public static final StatType TYPE_BONUS_FIRE_DMG = StatType.getIdStat("BonusFireDmg", "fireDamage");
        public static final StatType TYPE_BONUS_AIR_DMG = StatType.getIdStat("BonusAirDmg", "airDamage");
        public static final StatType TYPE_BONUS_SUM_DMG = StatType.sum("BonusSumDmg", TYPE_BONUS_EARTH_DMG, TYPE_BONUS_THUNDER_DMG, TYPE_BONUS_WATER_DMG, TYPE_BONUS_FIRE_DMG, TYPE_BONUS_AIR_DMG);

        // defense ids
        public static final StatType TYPE_BONUS_HEALTH = StatType.getIdStat("BonusHealth", "rawHealth");
        public static final StatType TYPE_BONUS_EARTH_DEF = StatType.getIdStat("BonusEarthDef", "earthDefence");
        public static final StatType TYPE_BONUS_THUNDER_DEF = StatType.getIdStat("BonusThunderDef", "thunderDefence");
        public static final StatType TYPE_BONUS_WATER_DEF = StatType.getIdStat("BonusWaterDef", "waterDefence");
        public static final StatType TYPE_BONUS_FIRE_DEF = StatType.getIdStat("BonusFireDef", "fireDefence");
        public static final StatType TYPE_BONUS_AIR_DEF = StatType.getIdStat("BonusAirDef", "airDefence");
        public static final StatType TYPE_BONUS_SUM_DEF = StatType.sum("BonusSumDef", TYPE_BONUS_EARTH_DEF, TYPE_BONUS_THUNDER_DEF, TYPE_BONUS_WATER_DEF, TYPE_BONUS_FIRE_DEF, TYPE_BONUS_AIR_DEF);

        // resource regen ids
        public static final StatType TYPE_RAW_HEALTH_REGEN = StatType.getIdStat("RawHealthRegen", "rawHealthRegen");
        public static final StatType TYPE_HEALTH_REGEN = StatType.getIdStat("HealthRegen", "healthRegen");
        public static final StatType TYPE_LIFE_STEAL = StatType.getIdStat("LifeSteal", "lifeSteal");
        public static final StatType TYPE_MANA_REGEN = StatType.getIdStat("ManaRegen", "manaRegen");
        public static final StatType TYPE_MANA_STEAL = StatType.getIdStat("ManaSteal", "manaSteal");

        // movement ids
        public static final StatType TYPE_WALK_SPEED = StatType.getIdStat("WalkSpeed", "walkSpeed");
        public static final StatType TYPE_SPRINT = StatType.getIdStat("Sprint", "sprint");
        public static final StatType TYPE_SPRINT_REGEN = StatType.getIdStat("SprintRegen", "sprintRegen");
        public static final StatType TYPE_JUMP_HEIGHT = StatType.getIdStat("JumpHeight", "jumpHeight");

        // spell cost ids
        public static final StatType TYPE_RAW_SPELL_COST_1 = StatType.getIdStat("RawSpellCost1", "raw1stSpellCost");
        public static final StatType TYPE_SPELL_COST_1 = StatType.getIdStat("SpellCost1", "1stSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_2 = StatType.getIdStat("RawSpellCost2", "raw2ndSpellCost");
        public static final StatType TYPE_SPELL_COST_2 = StatType.getIdStat("SpellCost2", "2ndSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_3 = StatType.getIdStat("RawSpellCost3", "raw3rdSpellCost");
        public static final StatType TYPE_SPELL_COST_3 = StatType.getIdStat("SpellCost3", "3rdSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_4 = StatType.getIdStat("RawSpellCost4", "raw4thSpellCost");
        public static final StatType TYPE_SPELL_COST_4 = StatType.getIdStat("SpellCost4", "4thSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_SUM = StatType.sum("RawSpellCostSum", TYPE_RAW_SPELL_COST_1, TYPE_RAW_SPELL_COST_2, TYPE_RAW_SPELL_COST_3, TYPE_RAW_SPELL_COST_4);
        public static final StatType TYPE_SPELL_COST_SUM = StatType.sum("SpellCostSum", TYPE_SPELL_COST_1, TYPE_SPELL_COST_2, TYPE_SPELL_COST_3, TYPE_SPELL_COST_4);

        // other ids
        public static final StatType TYPE_BONUS_ATK_SPD = StatType.getIdStat("BonusAtkSpd", "attackSpeed");
        public static final StatType TYPE_EXPLODING = StatType.getIdStat("Exploding", "exploding");
        public static final StatType TYPE_POISON = StatType.getIdStat("Poison", "poison");
        public static final StatType TYPE_THORNS = StatType.getIdStat("Thorns", "thorns");
        public static final StatType TYPE_REFLECTION = StatType.getIdStat("Reflection", "reflection");
        public static final StatType TYPE_SOUL_POINT_REGEN = StatType.getIdStat("SoulPointRegen", "soulPointRegen");
        public static final StatType TYPE_LOOT_BONUS = StatType.getIdStat("LootBonus", "lootBonus");
        public static final StatType TYPE_STEALING = StatType.getIdStat("Stealing", "emeraldStealing");
        public static final StatType TYPE_XP_BONUS = StatType.getIdStat("XpBonus", "xpBonus");

        // other stuff
        public static final StatType TYPE_ATTACK_SPEED = new StatType("AtkSpd", i -> {
            ItemAttackSpeed atkSpd = i.getAttackSpeed();
            return atkSpd != null ? atkSpd.getOffset() : 0;
        });
        public static final StatType TYPE_POWDER_SLOTS = new StatType("PowderSlots", ItemProfile::getPowderAmount);

        public static class StatType extends Type<ByStat> {

            static StatType getIdStat(String name, String key) {
                return new StatType(name, i -> {
                    IdentificationContainer id = i.getStatuses().get(key);
                    return id != null ? id.getMax() : 0;
                });
            }

            static StatType sum(String name, StatType... summands) {
                return new StatType(name, i -> Arrays.stream(summands).mapToInt(s -> s.extractStat(i)).sum());
            }

            private final ToIntFunction<ItemProfile> statExtractor;

            StatType(String name, ToIntFunction<ItemProfile> statExtractor) {
                super(name);
                this.statExtractor = statExtractor;
            }

            public int extractStat(ItemProfile item) {
                return statExtractor.applyAsInt(item);
            }

            @Override
            public ByStat parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                List<Pair<Comparison, Integer>> comparisons = parseComparisons(sortDir.a, s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new FilteringException("Not a number: " + s);
                    }
                });
                return new ByStat(this, comparisons, sortDir.b);
            }

        }

        private final StatType type;
        private final List<Pair<Comparison, Integer>> comps;
        private final SortDirection sortDir;

        public ByStat(StatType type, List<Pair<Comparison, Integer>> comps, SortDirection sortDir) {
            this.type = type;
            this.comps = comps;
            this.sortDir = sortDir;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public Type<?> getFilterType() {
            return type;
        }

        @Override
        public String toFilterString() {
            return type.getName() + ':' + sortDir.prefix + comps.stream()
                    .map(c -> (c.a == Comparison.EQUAL ? "" : c.a.symbol) + c.b)
                    .collect(Collectors.joining(","));
        }

        @Override
        public boolean test(ItemProfile item) {
            for (Pair<Comparison, Integer> comp : comps) {
                if (!comp.a.test(type.extractStat(item), comp.b)) return false;
            }
            return true;
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return sortDir.modifyComparison(Integer.compare(type.extractStat(a), type.extractStat(b)));
        }

    }

    class ByString implements ItemFilter {

        // info about sets is missing in the current wynntils item dataset
        //public static final StringType TYPE_SET = new StringType("Set", i -> i.getItemInfo().getSet());
        public static final StringType TYPE_RESTRICTION = new StringType("Restriction", ItemProfile::getRestriction);

        public static class StringType extends Type<ByString> {

            private final Function<ItemProfile, String> stringExtractor;

            StringType(String name, Function<ItemProfile, String> stringExtractor) {
                super(name);
                this.stringExtractor = stringExtractor;
            }

            public String extractString(ItemProfile item) {
                return stringExtractor.apply(item);
            }

            @Override
            public ByString parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                return new ByString(this, sortDir.a, sortDir.b);
            }

        }

        private final StringType type;
        private final String matchStr;
        private final SortDirection sortDir;

        public ByString(StringType type, String matchStr, SortDirection sortDir) {
            this.type = type;
            this.matchStr = matchStr.toLowerCase(Locale.ROOT);
            this.sortDir = sortDir;
        }

        @Override
        public Type<ByString> getFilterType() {
            return type;
        }

        @Override
        public String toFilterString() {
            return type.getName() + ':' + sortDir.prefix + StringUtils.quoteIfContainsSpace(matchStr);
        }

        @Override
        public boolean test(ItemProfile item) {
            String s = type.extractString(item);
            return s != null && s.toLowerCase(Locale.ROOT).contains(matchStr);
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return sortDir.modifyComparison(type.extractString(a).compareToIgnoreCase(type.extractString(b)));
        }

    }

    class ByMajorId implements ItemFilter {

        public static final Type<ByMajorId> TYPE = new Type<ByMajorId>("MajorId") {
            @Override
            public ByMajorId parse(String filterStr) {
                return new ByMajorId(filterStr.split(","));
            }
        };

        private final Set<String> majorIds;

        public ByMajorId(String... majorIds) {
            this.majorIds = Arrays.stream(majorIds).map(s -> s.toLowerCase(Locale.ROOT)).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        }

        @Override
        public Type<ByMajorId> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            return TYPE.getName() + ':' + StringUtils.quoteIfContainsSpace(String.join(",", majorIds));
        }

        @Override
        public boolean test(ItemProfile item) {
            List<MajorIdentification> itemIds = item.getMajorIds();
            if (itemIds == null) return false;
            // quadratic-time subset check is bad, but items generally don't have many major IDs so it should be fine in practice
            iter_ids:
            for (String expectedId : majorIds) {
                for (MajorIdentification itemId : itemIds) {
                    if (itemId.getName().toLowerCase(Locale.ROOT).contains(expectedId)) continue iter_ids;
                }
                return false;
            }
            return true;
        }

        @Override
        public int compare(ItemProfile a, ItemProfile b) {
            return 0;
        }

    }

    class FilteringException extends Exception {

        public FilteringException(String message) {
            super(message);
        }

    }

}
