package com.wynntils.core.utils.helpers;

import com.google.common.collect.ImmutableMap;
import com.wynntils.core.framework.enums.Comparison;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.enums.SortDirection;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientTier;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemAttackSpeed;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public interface IngredientFilter extends Predicate<IngredientProfile>, Comparator<IngredientProfile> {

    Type<?> getFilterType();

    String toFilterString();

    static IngredientFilter parseFilterString(String filterString) throws FilteringException {
        int n = filterString.indexOf(':');
        return n == -1 ? ByName.TYPE.parse(filterString) : Type.getType(filterString.substring(0, n)).parse(filterString.substring(n + 1));
    }

    abstract class Type<T extends IngredientFilter> {
        private static Map<String, Type<?>> typeRegistry = null;

        private static void initTypeRegistry() {
            ImmutableMap.Builder<String, Type<?>> builder = new ImmutableMap.Builder<>();

            for (Class<?> filterClass : IngredientFilter.class.getClasses()) {
                for (Field field : filterClass.getFields()) {
                    if ((field.getModifiers() & Modifier.STATIC) != 0 && Type.class.isAssignableFrom(field.getType())) {
                        try {
                            Type<?> type = (Type<?>) field.get(null);
                            builder.put(type.getName().toLowerCase(Locale.ROOT), type);
                            for (Alias annot : field.getAnnotationsByType(Alias.class)) {
                                for (String alias : annot.value()) {
                                    builder.put(alias.toLowerCase(Locale.ROOT), type);
                                }
                            }
                        } catch (IllegalAccessException e) {

                        }
                    }
                }
            }

            typeRegistry = builder.build();
        }

        public static Type<?> getType(String name) throws FilteringException {
            if (typeRegistry == null) {
                initTypeRegistry();
            }

            Type<?> type = typeRegistry.get(name.toLowerCase(Locale.ROOT));
            if (type == null) throw new FilteringException("Unknown filter type: " + name);
            return type;
        }

        private final String name, desc;

        public Type(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public abstract T parse(String filterString) throws FilteringException;

        static Pair<String, SortDirection> parseSortDirection(String filterString) {
            if (!filterString.isEmpty()) {
                switch (filterString.charAt(0)) {
                    case '^':
                        return new Pair<>(filterString.substring(1), SortDirection.ASCENDING);
                    case '$':
                        return new Pair<>(filterString.substring(1), SortDirection.DESCENDING);
                }
            }
            return new Pair<>(filterString, SortDirection.NONE);
        }

        static <T extends Comparable<T>> List<Pair<Comparison, T>> parseComparisons(String filterStr, IngredientFilter.Type.KeyExtractor<T> keyExtractor) throws FilteringException {
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
                            continue;
                        case '>':
                            rels.add(new Pair<>(Comparison.GREATER_THAN, keyExtractor.extractKey(relStr.substring(1))));
                            continue;
                        case '=':
                            rels.add(new Pair<>(Comparison.EQUAL, keyExtractor.extractKey(relStr.substring(1))));
                            continue;
                    }
                    int rangeDelimNdx = relStr.indexOf("..");
                    if (rangeDelimNdx == -1) {
                        rels.add(new Pair<>(Comparison.EQUAL, keyExtractor.extractKey(relStr)));
                        continue;
                    }
                    rels.add(new Pair<>(Comparison.GREATER_THAN_OR_EQUAL, keyExtractor.extractKey(relStr.substring(0, rangeDelimNdx))));
                    rels.add(new Pair<>(Comparison.LESS_THAN_OR_EQUAL, keyExtractor.extractKey(relStr.substring(rangeDelimNdx + 2))));
                }
            }
            return rels;
        }

        @FunctionalInterface
        interface KeyExtractor<T> {
            T extractKey(String str) throws IngredientFilter.FilteringException, FilteringException;
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Alias {
            String[] value();
        }
    }

    class ByName implements IngredientFilter {
        public static final IngredientFilter.Type<IngredientFilter.ByName> TYPE = new IngredientFilter.Type<IngredientFilter.ByName>("Name", "Item Name") {
            @Override
            public IngredientFilter.ByName parse(String filterStr) {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                return new IngredientFilter.ByName(sortDir.a, false, sortDir.b);
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

        public void adjoin(IngredientFilter.ByName o) { // this is a hack; don't call this unless you're ItemSearchState!
            searchStr = searchStr.isEmpty() ? o.searchStr : (searchStr + " " + o.searchStr);
        }

        public String getSearchString() {
            return searchStr;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public IngredientFilter.Type<IngredientFilter.ByName> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            StringBuilder buf = new StringBuilder(TYPE.getName()).append(':').append(sortDir.prefix);
            if (!searchStr.isEmpty()) buf.append(StringUtils.quoteIfContainsSpace(searchStr));
            return buf.toString();
        }

        @Override
        public boolean test(IngredientProfile item) {
            return fuzzy ? StringUtils.fuzzyMatch(item.getDisplayName().toLowerCase(Locale.ROOT), searchStr)
                    : item.getDisplayName().toLowerCase(Locale.ROOT).contains(searchStr);
        }

        @Override
        public int compare(IngredientProfile a, IngredientProfile b) {
            return sortDir.modifyComparison(a.getDisplayName().compareToIgnoreCase(b.getDisplayName()));
        }
    }

    class ByProfession implements IngredientFilter {
        public static final Type<ByProfession> TYPE = new Type<ByProfession>("Profession", "Ingredient Profession") {
            @Override
            public ByProfession parse(String filterString) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterString);
                filterString = sortDir.a;
                Set<ProfessionType> allowedProfessions = EnumSet.noneOf(ProfessionType.class);
                for (String token : filterString.split(",")) {
                    token = token.trim().toLowerCase(Locale.ROOT);
                    if (token.isEmpty()) continue;
                    ProfessionType profession = ProfessionType.from(token);
                    if (profession != null) {
                        allowedProfessions.add(profession);
                    }
                }

                return new ByProfession(allowedProfessions, sortDir.b);
            }
        };

        private final Set<ProfessionType> allowedProfessions;
        private final SortDirection sortDir;

        public ByProfession(Collection<ProfessionType> allowedProfessions, SortDirection sortDir) {
            this.allowedProfessions = Collections.unmodifiableSet(EnumSet.copyOf(allowedProfessions));
            this.sortDir = sortDir;
        }

        public Set<ProfessionType> getAllowedProfessions() {
            return allowedProfessions;
        }

        @Override
        public Type<?> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            List<String> keys = new ArrayList<>();
            allowedProfessions.forEach((ProfessionType profession) -> {
                keys.add(profession.getDisplayName());
            });
            return TYPE.getName() + ":" + sortDir.prefix + StringUtils.quoteIfContainsSpace(String.join(",", keys));
        }

        @Override
        public boolean test(IngredientProfile item) {
            return allowedProfessions.stream().anyMatch(item.getProfessions()::contains);
        }

        @Override
        public int compare(IngredientProfile a, IngredientProfile b) {
            return sortDir.modifyComparison(a.getProfessions().get(0).compareTo(b.getProfessions().get(0)));
        }
    }

    class ByTier implements IngredientFilter {
        @Type.Alias("Rarity")
        public static final Type<ByTier> TYPE = new Type<ByTier>("Tier", "Ingredient Tier") {
            @Override
            public ByTier parse(String filterString) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterString);
                List<Pair<Comparison, IngredientTier>> comparisons = parseComparisons(sortDir.a, s -> {
                    IngredientTier tier = IngredientTier.matchText(s);
                    if (tier == null) throw new FilteringException("Unknown rarity: " + s);
                    return tier;
                });
                return new IngredientFilter.ByTier(comparisons, sortDir.b);
            }
        };

        private final List<Pair<Comparison, IngredientTier>> comparisons;
        private final SortDirection sortDir;

        public ByTier(List<Pair<Comparison, IngredientTier>> comparisons, SortDirection sortDir) {
            this.comparisons = comparisons;
            this.sortDir = sortDir;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public IngredientFilter.Type<?> getFilterType() {
            return TYPE;
        }

        @Override
        public String toFilterString() {
            return TYPE.getName() + ':' + sortDir.prefix + comparisons.stream()
                    .map(c -> (c.a == Comparison.EQUAL ? "" : c.a.symbol) + c.b.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(","));
        }

        @Override
        public boolean test(IngredientProfile item) {
            for (Pair<Comparison, IngredientTier> comp : comparisons) {
                if (!comp.a.test(item.getTier(), comp.b)) return false;
            }
            return true;
        }

        @Override
        public int compare(IngredientProfile a, IngredientProfile b) {
            return sortDir.modifyComparison(a.getTier().compareTo(b.getTier()));
        }
    }

    class ByStat implements IngredientFilter {
        // user-favorited
        @IngredientFilter.Type.Alias({"Favourited", "fav"})
        public static final IngredientFilter.ByStat.StatType TYPE_FAVORITED = new IngredientFilter.ByStat.StatType("Favorited", "Favorited", i -> {
            return i.isFavorited() ? 1 : 0;
        });

        public static class StatType extends IngredientFilter.Type<IngredientFilter.ByStat> {

//            static IngredientFilter.ByStat.StatType getIdStat(String name, String desc, String key) {
//                return new IngredientFilter.ByStat.StatType(name, desc, i -> {
//                    IdentificationContainer id = i.getStatuses().get(key);
//                    return id != null ? id.getMax() : 0;
//                });
//            }

            static IngredientFilter.ByStat.StatType sum(String name, String desc, IngredientFilter.ByStat.StatType... summands) {
                return new IngredientFilter.ByStat.StatType(name, desc, i -> Arrays.stream(summands).mapToInt(s -> s.extractStat(i)).sum());
            }

            private final ToIntFunction<IngredientProfile> statExtractor;

            StatType(String name, String desc, ToIntFunction<IngredientProfile> statExtractor) {
                super(name, desc);
                this.statExtractor = statExtractor;
            }

            public int extractStat(IngredientProfile item) {
                return statExtractor.applyAsInt(item);
            }

            @Override
            public IngredientFilter.ByStat parse(String filterStr) throws IngredientFilter.FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                List<Pair<Comparison, Integer>> comparisons = parseComparisons(sortDir.a, s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        throw new IngredientFilter.FilteringException("Not a number: " + s);
                    }
                });
                return new IngredientFilter.ByStat(this, comparisons, sortDir.b);
            }

        }

        private final IngredientFilter.ByStat.StatType type;
        private final List<Pair<Comparison, Integer>> comps;
        private final SortDirection sortDir;

        public ByStat(IngredientFilter.ByStat.StatType type, List<Pair<Comparison, Integer>> comps, SortDirection sortDir) {
            this.type = type;
            this.comps = comps;
            this.sortDir = sortDir;
        }

        public SortDirection getSortDirection() {
            return sortDir;
        }

        @Override
        public IngredientFilter.Type<?> getFilterType() {
            return type;
        }

        @Override
        public String toFilterString() {
            return type.getName() + ':' + sortDir.prefix + comps.stream()
                    .map(c -> (c.a == Comparison.EQUAL ? "" : c.a.symbol) + c.b)
                    .collect(Collectors.joining(","));
        }

        @Override
        public boolean test(IngredientProfile item) {
            for (Pair<Comparison, Integer> comp : comps) {
                if (!comp.a.test(type.extractStat(item), comp.b)) return false;
            }
            return true;
        }

        @Override
        public int compare(IngredientProfile a, IngredientProfile b) {
            return sortDir.modifyComparison(Integer.compare(type.extractStat(a), type.extractStat(b)));
        }
    }

    class ByString implements IngredientFilter {

        // info about sets is missing in the current wynntils item dataset
        //public static final StringType TYPE_SET = new StringType("Set", i -> i.getItemInfo().getSet());

        public static class StringType extends Type<ByString> {

            private final Function<IngredientProfile, String> stringExtractor;

            StringType(String name, String desc, Function<IngredientProfile, String> stringExtractor) {
                super(name, desc);
                this.stringExtractor = stringExtractor;
            }

            public String extractString(IngredientProfile item) {
                return stringExtractor.apply(item);
            }

            @Override
            public ByString parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                return new ByString(this, sortDir.a, sortDir.b);
            }

        }

        private final ByString.StringType type;
        private final String matchStr;
        private final SortDirection sortDir;

        public ByString(ByString.StringType type, String matchStr, SortDirection sortDir) {
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
        public boolean test(IngredientProfile item) {
            String s = type.extractString(item);
            return s != null && s.toLowerCase(Locale.ROOT).contains(matchStr);
        }

        @Override
        public int compare(IngredientProfile a, IngredientProfile b) {
            return sortDir.modifyComparison(type.extractString(a).compareToIgnoreCase(type.extractString(b)));
        }

    }

    class FilteringException extends Exception {
        public FilteringException(String message) {
            super(message);
        }
    }
}
