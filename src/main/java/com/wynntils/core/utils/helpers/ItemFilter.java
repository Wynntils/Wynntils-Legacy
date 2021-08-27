/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.helpers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wynntils.core.framework.enums.Comparison;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.enums.SortDirection;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemAttackSpeed;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;

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
            ImmutableMap.Builder<String, Type<?>> builder = new ImmutableMap.Builder<>();
            // there's a lot of em
            for (Class<?> filterClass : ItemFilter.class.getClasses()) {
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
                            // probably not an issue
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

            T extractKey(String str) throws FilteringException;

        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Alias {

            String[] value();

        }

    }

    class ByName implements ItemFilter {

        public static final Type<ByName> TYPE = new Type<ByName>("Name", "Item Name") {
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

        public static final Type<ByType> TYPE = new Type<ByType>("Type", "Item Type") {
            @Override
            public ByType parse(String filterStr) throws FilteringException {
                Pair<String, SortDirection> sortDir = parseSortDirection(filterStr);
                filterStr = sortDir.a;
                Set<ItemType> allowedTypes = EnumSet.noneOf(ItemType.class);
                for (String token : filterStr.split(",")) {
                    token = token.trim().toLowerCase(Locale.ROOT);
                    if (token.isEmpty()) continue;
                    ItemType type = ItemType.from(token);
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

        @Type.Alias("Tier")
        public static final Type<ByRarity> TYPE = new Type<ByRarity>("Rarity", "Item Rarity") {
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
        @Type.Alias({"Lvl", "CombatLevel", "CombatLvl"})
        public static final StatType TYPE_COMBAT_LEVEL = new StatType("Level", "Combat Level", i -> i.getRequirements().getLevel());
        @Type.Alias("StrMin")
        public static final StatType TYPE_STR_REQ = new StatType("StrReq", "Strength Min", i -> i.getRequirements().getStrength());
        @Type.Alias("DexMin")
        public static final StatType TYPE_DEX_REQ = new StatType("DexReq", "Dexterity Min", i -> i.getRequirements().getDexterity());
        @Type.Alias("IntMin")
        public static final StatType TYPE_INT_REQ = new StatType("IntReq", "Intelligence Min", i -> i.getRequirements().getIntelligence());
        @Type.Alias("DefMin")
        public static final StatType TYPE_DEF_REQ = new StatType("DefReq", "Defence Min", i -> i.getRequirements().getDefense());
        @Type.Alias("AgiMin")
        public static final StatType TYPE_AGI_REQ = new StatType("AgiReq", "Agility Min", i -> i.getRequirements().getAgility());
        @Type.Alias({"SumMin", "TotalReq", "TotalMin"})
        public static final StatType TYPE_SUM_REQ = StatType.sum("SumReq", "Total Skill Points Min", TYPE_STR_REQ, TYPE_DEX_REQ, TYPE_INT_REQ, TYPE_DEF_REQ, TYPE_AGI_REQ);

        // damages
        public static final StatType TYPE_NEUTRAL_DMG = new StatType("NeutralDmg", "Neutral Damage", i -> i.getAverageDamages().getOrDefault(DamageType.NEUTRAL, 0));
        public static final StatType TYPE_EARTH_DMG = new StatType("EarthDmg", "Earth Damage", i -> i.getAverageDamages().getOrDefault(DamageType.EARTH, 0));
        public static final StatType TYPE_THUNDER_DMG = new StatType("ThunderDmg", "Thunder Damage", i -> i.getAverageDamages().getOrDefault(DamageType.THUNDER, 0));
        public static final StatType TYPE_WATER_DMG = new StatType("WaterDmg", "Water Damage", i -> i.getAverageDamages().getOrDefault(DamageType.WATER, 0));
        public static final StatType TYPE_FIRE_DMG = new StatType("FireDmg", "Fire Damage", i -> i.getAverageDamages().getOrDefault(DamageType.FIRE, 0));
        public static final StatType TYPE_AIR_DMG = new StatType("AirDmg", "Air Damage", i -> i.getAverageDamages().getOrDefault(DamageType.AIR, 0));
        @Type.Alias("TotalDmg")
        public static final StatType TYPE_SUM_DMG = StatType.sum("SumDmg", "Total Damage", TYPE_NEUTRAL_DMG, TYPE_EARTH_DMG, TYPE_THUNDER_DMG, TYPE_WATER_DMG, TYPE_FIRE_DMG, TYPE_AIR_DMG);

        // defenses
        @Type.Alias("hp")
        public static final StatType TYPE_HEALTH = new StatType("Health", "Health", ItemProfile::getHealth);
        public static final StatType TYPE_EARTH_DEF = new StatType("EarthDef", "Earth Defence", i -> i.getElementalDefenses().getOrDefault(DamageType.EARTH, 0));
        public static final StatType TYPE_THUNDER_DEF = new StatType("ThunderDef", "Thunder Defence", i -> i.getElementalDefenses().getOrDefault(DamageType.THUNDER, 0));
        public static final StatType TYPE_WATER_DEF = new StatType("WaterDef", "Water Defence", i -> i.getElementalDefenses().getOrDefault(DamageType.WATER, 0));
        public static final StatType TYPE_FIRE_DEF = new StatType("FireDef", "Fire Defence", i -> i.getElementalDefenses().getOrDefault(DamageType.FIRE, 0));
        public static final StatType TYPE_AIR_DEF = new StatType("AirDef", "Air Defence", i -> i.getElementalDefenses().getOrDefault(DamageType.AIR, 0));
        @Type.Alias("TotalDef")
        public static final StatType TYPE_SUM_DEF = StatType.sum("SumDef", "Total Defence", TYPE_EARTH_DEF, TYPE_THUNDER_DEF, TYPE_WATER_DEF, TYPE_FIRE_DEF, TYPE_AIR_DEF);

        // attribute ids
        public static final StatType TYPE_STR = StatType.getIdStat("Str", "Strength", "rawStrength");
        public static final StatType TYPE_DEX = StatType.getIdStat("Dex", "Dexterity", "rawDexterity");
        public static final StatType TYPE_INT = StatType.getIdStat("Int", "Intelligence", "rawIntelligence");
        public static final StatType TYPE_DEF = StatType.getIdStat("Def", "Defence", "rawDefence");
        public static final StatType TYPE_AGI = StatType.getIdStat("Agi", "Agility", "rawAgility");
        @Type.Alias({"SkillPts", "Attributes", "Attrs"})
        public static final StatType TYPE_SKILL_POINTS = StatType.sum("SkillPoints", "Total Skill Points", TYPE_STR, TYPE_DEX, TYPE_INT, TYPE_DEF, TYPE_AGI);

        // damage ids
        @Type.Alias("MainAtkRawDmg")
        public static final StatType TYPE_MAIN_ATK_NEUTRAL_DMG = StatType.getIdStat("MainAtkNeutralDmg", "Main Attack Neutral Damage", "rawMainAttackNeutralDamage");
        @Type.Alias({"MainAtkDmg%", "%MainAtkDmg", "Melee%", "%Melee"})
        public static final StatType TYPE_MAIN_ATK_DMG = StatType.getIdStat("MainAtkDmg", "Main Attack Damage %", "mainAttackDamage");
        @Type.Alias("SpellRawDmg")
        public static final StatType TYPE_SPELL_NEUTRAL_DMG = StatType.getIdStat("SpellNeutralDmg", "Neutral Spell Damage", "rawNeutralSpellDamage");
        @Type.Alias({"SpellDmg%", "%SpellDmg", "Spell%", "%Spell", "sd"})
        public static final StatType TYPE_SPELL_DMG = StatType.getIdStat("SpellDmg", "Spell Damage %", "spellDamage");
        @Type.Alias({"EarthDmg%", "%EarthDmg"})
        public static final StatType TYPE_BONUS_EARTH_DMG = StatType.getIdStat("BonusEarthDmg", "Earth Damage %", "earthDamage");
        @Type.Alias({"ThunderDmg%", "%ThunderDmg"})
        public static final StatType TYPE_BONUS_THUNDER_DMG = StatType.getIdStat("BonusThunderDmg", "Thunder Damage %", "thunderDamage");
        @Type.Alias({"WaterDmg%", "%WaterDmg"})
        public static final StatType TYPE_BONUS_WATER_DMG = StatType.getIdStat("BonusWaterDmg", "Water Damage %", "waterDamage");
        @Type.Alias({"FireDmg%", "%FireDmg"})
        public static final StatType TYPE_BONUS_FIRE_DMG = StatType.getIdStat("BonusFireDmg", "Fire Damage %", "fireDamage");
        @Type.Alias({"AirDmg%", "%AirDmg"})
        public static final StatType TYPE_BONUS_AIR_DMG = StatType.getIdStat("BonusAirDmg", "Air Damage %", "airDamage");
        @Type.Alias({"SumDmg%", "%SumDmg", "BonusTotalDmg", "TotalDmg%", "%TotalDmg"})
        public static final StatType TYPE_BONUS_SUM_DMG = StatType.sum("BonusSumDmg", "Total Damage %", TYPE_BONUS_EARTH_DMG, TYPE_BONUS_THUNDER_DMG, TYPE_BONUS_WATER_DMG, TYPE_BONUS_FIRE_DMG, TYPE_BONUS_AIR_DMG);

        // defense ids
        @Type.Alias({"Health+", "hp+"})
        public static final StatType TYPE_BONUS_HEALTH = StatType.getIdStat("BonusHealth", "Bonus Health", "rawHealth");
        @Type.Alias({"SumHp", "TotalHealth", "TotalHp"})
        public static final StatType TYPE_SUM_HEALTH = StatType.sum("SumHealth", "Total Health", TYPE_HEALTH, TYPE_BONUS_HEALTH);
        @Type.Alias({"EarthDef%", "%EarthDef"})
        public static final StatType TYPE_BONUS_EARTH_DEF = StatType.getIdStat("BonusEarthDef", "Earth Defence %", "earthDefence");
        @Type.Alias({"ThunderDef%", "%ThunderDef"})
        public static final StatType TYPE_BONUS_THUNDER_DEF = StatType.getIdStat("BonusThunderDef", "Thunder Defence %", "thunderDefence");
        @Type.Alias({"WaterDef%", "%WaterDef"})
        public static final StatType TYPE_BONUS_WATER_DEF = StatType.getIdStat("BonusWaterDef", "Water Defence %", "waterDefence");
        @Type.Alias({"FireDef%", "%FireDef"})
        public static final StatType TYPE_BONUS_FIRE_DEF = StatType.getIdStat("BonusFireDef", "Fire Defence %", "fireDefence");
        @Type.Alias({"AirDef%", "%AirDef"})
        public static final StatType TYPE_BONUS_AIR_DEF = StatType.getIdStat("BonusAirDef", "Air Defence %", "airDefence");
        @Type.Alias({"SumDef%", "%SumDef", "BonusTotalDef", "TotalDef%", "%TotalDef"})
        public static final StatType TYPE_BONUS_SUM_DEF = StatType.sum("BonusSumDef", "Total Defence %", TYPE_BONUS_EARTH_DEF, TYPE_BONUS_THUNDER_DEF, TYPE_BONUS_WATER_DEF, TYPE_BONUS_FIRE_DEF, TYPE_BONUS_AIR_DEF);

        // resource regen ids
        @Type.Alias({"hpr", "hr"})
        public static final StatType TYPE_RAW_HEALTH_REGEN = StatType.getIdStat("RawHealthRegen", "Health Regen", "rawHealthRegen");
        @Type.Alias({"hr%", "%hr"})
        public static final StatType TYPE_HEALTH_REGEN = StatType.getIdStat("HealthRegen", "Health Regen %", "healthRegen");
        @Type.Alias("ls")
        public static final StatType TYPE_LIFE_STEAL = StatType.getIdStat("LifeSteal", "Life Steal", "lifeSteal");
        @Type.Alias("mr")
        public static final StatType TYPE_MANA_REGEN = StatType.getIdStat("ManaRegen", "Mana Regen", "manaRegen");
        @Type.Alias("ms")
        public static final StatType TYPE_MANA_STEAL = StatType.getIdStat("ManaSteal", "Mana Steal", "manaSteal");

        // movement ids
        @Type.Alias({"MoveSpeed", "ws"})
        public static final StatType TYPE_WALK_SPEED = StatType.getIdStat("WalkSpeed", "Walk Speed", "walkSpeed");
        public static final StatType TYPE_SPRINT = StatType.getIdStat("Sprint", "Sprint", "sprint");
        public static final StatType TYPE_SPRINT_REGEN = StatType.getIdStat("SprintRegen", "Sprint Regen", "sprintRegen");
        @Type.Alias("jh")
        public static final StatType TYPE_JUMP_HEIGHT = StatType.getIdStat("JumpHeight", "Jump Height", "rawJumpHeight");

        // spell cost ids
        public static final StatType TYPE_RAW_SPELL_COST_1 = StatType.getIdStat("RawSpellCost1", "1st Spell Cost", "raw1stSpellCost");
        public static final StatType TYPE_SPELL_COST_1 = StatType.getIdStat("SpellCost1", "1st Spell Cost %", "1stSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_2 = StatType.getIdStat("RawSpellCost2", "2nd Spell Cost", "raw2ndSpellCost");
        public static final StatType TYPE_SPELL_COST_2 = StatType.getIdStat("SpellCost2", "2nd Spell Cost %", "2ndSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_3 = StatType.getIdStat("RawSpellCost3", "3rd Spell Cost", "raw3rdSpellCost");
        public static final StatType TYPE_SPELL_COST_3 = StatType.getIdStat("SpellCost3", "3rd Spell Cost %", "3rdSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_4 = StatType.getIdStat("RawSpellCost4", "4th Spell Cost", "raw4thSpellCost");
        public static final StatType TYPE_SPELL_COST_4 = StatType.getIdStat("SpellCost4", "4th Spell Cost %", "4thSpellCost");
        public static final StatType TYPE_RAW_SPELL_COST_SUM = StatType.sum("RawSpellCostSum", "Total Spell Cost", TYPE_RAW_SPELL_COST_1, TYPE_RAW_SPELL_COST_2, TYPE_RAW_SPELL_COST_3, TYPE_RAW_SPELL_COST_4);
        public static final StatType TYPE_SPELL_COST_SUM = StatType.sum("SpellCostSum", "Total Spell Cost %", TYPE_SPELL_COST_1, TYPE_SPELL_COST_2, TYPE_SPELL_COST_3, TYPE_SPELL_COST_4);

        // other ids
        @Type.Alias("AtkSpd+")
        public static final StatType TYPE_BONUS_ATK_SPD = StatType.getIdStat("BonusAtkSpd", "Bonus Attack Speed", "attackSpeed");
        public static final StatType TYPE_EXPLODING = StatType.getIdStat("Exploding", "Exploding", "exploding");
        public static final StatType TYPE_POISON = StatType.getIdStat("Poison", "Poison", "poison");
        public static final StatType TYPE_THORNS = StatType.getIdStat("Thorns", "Thorns", "thorns");
        public static final StatType TYPE_REFLECTION = StatType.getIdStat("Reflection", "Reflection", "reflection");
        public static final StatType TYPE_SOUL_POINT_REGEN = StatType.getIdStat("SoulPointRegen", "Soul Point Regen", "soulPointRegen");
        @Type.Alias("lb")
        public static final StatType TYPE_LOOT_BONUS = StatType.getIdStat("LootBonus", "Loot Bonus", "lootBonus");
        public static final StatType TYPE_STEALING = StatType.getIdStat("Stealing", "Stealing", "stealing");
        @Type.Alias({"xp", "xb"})
        public static final StatType TYPE_XP_BONUS = StatType.getIdStat("XpBonus", "Xp Bonus", "xpBonus");

        // other stuff
        public static final StatType TYPE_ATTACK_SPEED = new StatType("AtkSpd", "Attack Speed", i -> {
            ItemAttackSpeed atkSpd = i.getAttackSpeed();
            return atkSpd != null ? atkSpd.getOffset() : 0;
        });
        @Type.Alias("TotalAtkSpd")
        public static final StatType TYPE_ATK_SPD_SUM = StatType.sum("SumAtkSpd", "Total Attack Speed", TYPE_BONUS_ATK_SPD, TYPE_ATTACK_SPEED);
        @Type.Alias("Powders")
        public static final StatType TYPE_POWDER_SLOTS = new StatType("PowderSlots", "Powder Slot Count", ItemProfile::getPowderAmount);

        // user-favorited
        @Type.Alias({"Favourited", "fav"})
        public static final StatType TYPE_FAVORITED = new StatType("Favorited", "Favorited", i -> {
            return i.isFavorited() ? 1 : 0;
        });

        public static class StatType extends Type<ByStat> {

            static StatType getIdStat(String name, String desc, String key) {
                return new StatType(name, desc, i -> {
                    IdentificationContainer id = i.getStatuses().get(key);
                    return id != null ? id.getMax() : 0;
                });
            }

            static StatType sum(String name, String desc, StatType... summands) {
                return new StatType(name, desc, i -> Arrays.stream(summands).mapToInt(s -> s.extractStat(i)).sum());
            }

            private final ToIntFunction<ItemProfile> statExtractor;

            StatType(String name, String desc, ToIntFunction<ItemProfile> statExtractor) {
                super(name, desc);
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
        public static final StringType TYPE_RESTRICTION = new StringType("Restriction", "Item Restriction", ItemProfile::getRestriction);

        public static class StringType extends Type<ByString> {

            private final Function<ItemProfile, String> stringExtractor;

            StringType(String name, String desc, Function<ItemProfile, String> stringExtractor) {
                super(name, desc);
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

        public static final Type<ByMajorId> TYPE = new Type<ByMajorId>("MajorId", "Major Identifications") {
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
