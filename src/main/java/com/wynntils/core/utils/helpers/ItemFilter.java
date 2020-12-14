/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.utils.helpers;

import com.google.common.collect.ImmutableList;
import com.wynntils.core.framework.enums.Comparison;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;

import java.util.*;
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
            putType(ByName.TYPE);
            putType(ByType.TYPE);
            putType(ByRarity.TYPE);
            putType(ByStat.TYPE_COMBAT_LEVEL);
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
            if (!searchStr.isEmpty()) {
                buf.append('"').append(searchStr).append('"');
            }
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
            return TYPE.getName() + ':' + sortDir.prefix + String.join(",", keys);
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

        public static final StatType TYPE_COMBAT_LEVEL = new StatType("Level", i -> i.getRequirements().getLevel());

        public static class StatType extends Type<ByStat> {

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

    class FilteringException extends Exception {

        public FilteringException(String message) {
            super(message);
        }

    }

}
