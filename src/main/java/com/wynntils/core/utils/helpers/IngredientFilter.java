package com.wynntils.core.utils.helpers;

import com.google.common.collect.ImmutableMap;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.Comparison;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.enums.SortDirection;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.ingredient.IngredientIdentificationContainer;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientTier;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
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
                        } catch (IllegalAccessException ignored) {
                            // Could not infer filter type, ignore it
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
                    ProfessionType profession = ProfessionType.fromString(token);
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
        @Type.Alias({"Lvl", "CombatLevel", "CombatLvl"})
        public static final ByStat.StatType TYPE_LEVEL = new ByStat.StatType("Level", "Combat Level", IngredientProfile::getLevel);

        // Defense ids
        @Type.Alias("hp")
        public static final ByStat.StatType TYPE_HEALTH = StatType.getIdStat("Health", "Health", "rawHealth");
        public static final ByStat.StatType TYPE_EARTH_DEF = StatType.getIdStat("EarthDef", "Earth Defence", "earthDefence");
        public static final ByStat.StatType TYPE_THUNDER_DEF = StatType.getIdStat("ThunderDef", "Thunder Defence", "thunderDefence");
        public static final ByStat.StatType TYPE_WATER_DEF = StatType.getIdStat("WaterDef", "Water Defence", "waterDefence");
        public static final ByStat.StatType TYPE_FIRE_DEF = StatType.getIdStat("FireDef", "Fire Defence", "fireDefence");
        public static final ByStat.StatType TYPE_AIR_DEF = StatType.getIdStat("AirDef", "Air Defence", "airDefence");
        @Type.Alias("TotalDef")
        public static final ByStat.StatType TYPE_SUM_DEF = ByStat.StatType.sum("SumDef", "Total Defence", TYPE_EARTH_DEF, TYPE_THUNDER_DEF, TYPE_WATER_DEF, TYPE_FIRE_DEF, TYPE_AIR_DEF);

        // Attribute ids
        public static final ByStat.StatType TYPE_STR = ByStat.StatType.getIdStat("Str", "Strength", "rawStrength");
        public static final ByStat.StatType TYPE_DEX = ByStat.StatType.getIdStat("Dex", "Dexterity", "rawDexterity");
        public static final ByStat.StatType TYPE_INT = ByStat.StatType.getIdStat("Int", "Intelligence", "rawIntelligence");
        public static final ByStat.StatType TYPE_DEF = ByStat.StatType.getIdStat("Def", "Defence", "rawDefence");
        public static final ByStat.StatType TYPE_AGI = ByStat.StatType.getIdStat("Agi", "Agility", "rawAgility");
        @Type.Alias({"SkillPts", "Attributes", "Attrs"})
        public static final ByStat.StatType TYPE_SKILL_POINTS = ByStat.StatType.sum("SkillPoints", "Total Skill Points", TYPE_STR, TYPE_DEX, TYPE_INT, TYPE_DEF, TYPE_AGI);

        // Damage ids
        @Type.Alias("MainAtkRawDmg")
        public static final ByStat.StatType TYPE_MAIN_ATK_NEUTRAL_DMG = ByStat.StatType.getIdStat("MainAtkNeutralDmg", "Main Attack Neutral Damage", "rawMainAttackNeutralDamage");
        @Type.Alias({"MainAtkDmg%", "%MainAtkDmg", "Melee%", "%Melee"})
        public static final ByStat.StatType TYPE_MAIN_ATK_DMG = ByStat.StatType.getIdStat("MainAtkDmg", "Main Attack Damage %", "mainAttackDamage");
        @Type.Alias("SpellRawDmg")
        public static final ByStat.StatType TYPE_SPELL_NEUTRAL_DMG = ByStat.StatType.getIdStat("SpellNeutralDmg", "Neutral Spell Damage", "rawNeutralSpellDamage");
        @Type.Alias({"SpellDmg%", "%SpellDmg", "Spell%", "%Spell", "sd"})
        public static final ByStat.StatType TYPE_SPELL_DMG = ByStat.StatType.getIdStat("SpellDmg", "Spell Damage %", "spellDamage");
        @Type.Alias({"EarthDmg%", "%EarthDmg"})
        public static final ByStat.StatType TYPE_BONUS_EARTH_DMG = ByStat.StatType.getIdStat("BonusEarthDmg", "Earth Damage %", "earthDamage");
        @Type.Alias({"ThunderDmg%", "%ThunderDmg"})
        public static final ByStat.StatType TYPE_BONUS_THUNDER_DMG = ByStat.StatType.getIdStat("BonusThunderDmg", "Thunder Damage %", "thunderDamage");
        @Type.Alias({"WaterDmg%", "%WaterDmg"})
        public static final ByStat.StatType TYPE_BONUS_WATER_DMG = ByStat.StatType.getIdStat("BonusWaterDmg", "Water Damage %", "waterDamage");
        @Type.Alias({"FireDmg%", "%FireDmg"})
        public static final ByStat.StatType TYPE_BONUS_FIRE_DMG = ByStat.StatType.getIdStat("BonusFireDmg", "Fire Damage %", "fireDamage");
        @Type.Alias({"AirDmg%", "%AirDmg"})
        public static final ByStat.StatType TYPE_BONUS_AIR_DMG = ByStat.StatType.getIdStat("BonusAirDmg", "Air Damage %", "airDamage");
        @Type.Alias({"SumDmg%", "%SumDmg", "BonusTotalDmg", "TotalDmg%", "%TotalDmg"})
        public static final ByStat.StatType TYPE_BONUS_SUM_DMG = ByStat.StatType.sum("BonusSumDmg", "Total Damage %", TYPE_BONUS_EARTH_DMG, TYPE_BONUS_THUNDER_DMG, TYPE_BONUS_WATER_DMG, TYPE_BONUS_FIRE_DMG, TYPE_BONUS_AIR_DMG);

        // Resource regen ids
        @Type.Alias({"hpr", "hr"})
        public static final ByStat.StatType TYPE_RAW_HEALTH_REGEN = ByStat.StatType.getIdStat("RawHealthRegen", "Health Regen", "rawHealthRegen");
        @Type.Alias({"hr%", "%hr"})
        public static final ByStat.StatType TYPE_HEALTH_REGEN = ByStat.StatType.getIdStat("HealthRegen", "Health Regen %", "healthRegen");
        @Type.Alias("ls")
        public static final ByStat.StatType TYPE_LIFE_STEAL = ByStat.StatType.getIdStat("LifeSteal", "Life Steal", "lifeSteal");
        @Type.Alias("mr")
        public static final ByStat.StatType TYPE_MANA_REGEN = ByStat.StatType.getIdStat("ManaRegen", "Mana Regen", "manaRegen");
        @Type.Alias("ms")
        public static final ByStat.StatType TYPE_MANA_STEAL = ByStat.StatType.getIdStat("ManaSteal", "Mana Steal", "manaSteal");

        // Movement ids
        @Type.Alias({"MoveSpeed", "ws"})
        public static final ByStat.StatType TYPE_WALK_SPEED = ByStat.StatType.getIdStat("WalkSpeed", "Walk Speed", "walkSpeed");
        public static final ByStat.StatType TYPE_SPRINT = ByStat.StatType.getIdStat("Sprint", "Sprint", "sprint");
        public static final ByStat.StatType TYPE_SPRINT_REGEN = ByStat.StatType.getIdStat("SprintRegen", "Sprint Regen", "sprintRegen");
        @Type.Alias("jh")
        public static final ByStat.StatType TYPE_JUMP_HEIGHT = ByStat.StatType.getIdStat("JumpHeight", "Jump Height", "rawJumpHeight");

        // Gathering ids
        @Type.Alias("GatherSpeed")
        public static final ByStat.StatType TYPE_GATHER_SPEED = ByStat.StatType.getIdStat("GatheringSpeed", "Bonus Gather Speed", "gatherSpeed");
        @Type.Alias("GatherXP")
        public static final ByStat.StatType TYPE_GATHER_XP = ByStat.StatType.getIdStat("GatheringXP", "Bonus Gather XP", "gatherXPBonus");

        // Other ids
        @Type.Alias("AtkSpd+")
        public static final ByStat.StatType TYPE_BONUS_ATK_SPD = ByStat.StatType.getIdStat("BonusAtkSpd", "Bonus Attack Speed", "attackSpeed");
        public static final ByStat.StatType TYPE_EXPLODING = ByStat.StatType.getIdStat("Exploding", "Exploding", "exploding");
        public static final ByStat.StatType TYPE_POISON = ByStat.StatType.getIdStat("Poison", "Poison", "poison");
        public static final ByStat.StatType TYPE_THORNS = ByStat.StatType.getIdStat("Thorns", "Thorns", "thorns");
        public static final ByStat.StatType TYPE_REFLECTION = ByStat.StatType.getIdStat("Reflection", "Reflection", "reflection");
        public static final ByStat.StatType TYPE_SOUL_POINT_REGEN = ByStat.StatType.getIdStat("SoulPointRegen", "Soul Point Regen", "soulPointRegen");
        @Type.Alias("lb")
        public static final ByStat.StatType TYPE_LOOT_BONUS = ByStat.StatType.getIdStat("LootBonus", "Loot Bonus", "lootBonus");
        @Type.Alias("lq")
        public static final ByStat.StatType TYPE_LOOT_QUALITY = ByStat.StatType.getIdStat("LootQuality", "Loot Quality", "lootQuality");
        public static final ByStat.StatType TYPE_STEALING = ByStat.StatType.getIdStat("Stealing", "Stealing", "stealing");
        @Type.Alias({"xp", "xb"})
        public static final ByStat.StatType TYPE_XP_BONUS = ByStat.StatType.getIdStat("XpBonus", "Xp Bonus", "xpBonus");

        // user-favorited
        @IngredientFilter.Type.Alias({"Favourited", "fav"})
        public static final IngredientFilter.ByStat.StatType TYPE_FAVORITED = new IngredientFilter.ByStat.StatType("Favorited", "Favorited", i -> {
            return i.isFavorited() ? 1 : 0;
        });

        public static class StatType extends IngredientFilter.Type<IngredientFilter.ByStat> {
            static IngredientFilter.ByStat.StatType sum(String name, String desc, IngredientFilter.ByStat.StatType... summands) {
                return new IngredientFilter.ByStat.StatType(name, desc, i -> Arrays.stream(summands).mapToInt(s -> s.extractStat(i)).sum());
            }

            static ByStat.StatType getIdStat(String name, String desc, String key) {
                return new IngredientFilter.ByStat.StatType(name, desc, i -> {
                    IngredientIdentificationContainer id = i.getStatuses().get(key);
                    return id != null ? id.getMax() : 0;
                });
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
            // If we are 'sorting' by favorited, only show favorited items
            if (type == TYPE_FAVORITED) {
                return item.isFavorited();
            }

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

    class FilteringException extends Exception {
        public FilteringException(String message) {
            super(message);
        }
    }
}
