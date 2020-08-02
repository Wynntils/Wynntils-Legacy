package com.wynntils.core.framework.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SpellType {
    ARROW_STORM(ClassType.ARCHER, 1, "Arrow Storm", "Bolt Blizzard", 6, 0),
    ESCAPE(ClassType.ARCHER, 2, "Escape", "Spider Jump", 3, 0),
    BOMB(ClassType.ARCHER, 3, "Bomb", "Creeper Dart", 8, 0),
    ARROW_SHIELD(ClassType.ARCHER, 4, "Arrow Shield", "Dagger Aura", 8, 1),

    SPIN_ATTACK(ClassType.ASSASSIN, 1, "Spin Attack", "Whirlwind", 6, 0),
    VANISH(ClassType.ASSASSIN, 2, "Vanish", "Shadow Clone", 1, 0),
    MULTI_HIT(ClassType.ASSASSIN, 3, "Multi Hit", "Leopard Punches", 8, 0),
    SMOKE_BOMB(ClassType.ASSASSIN, 4, "Smoke Bomb", "Blinding Cloud", 8, 0),

    BASH(ClassType.WARRIOR, 1, "Bash", "Holy Blast", 6, 0),
    CHARGE(ClassType.WARRIOR, 2, "Charge", "Leap", 4, 0),
    UPPERCUT(ClassType.WARRIOR, 3, "Uppercut", "Heaven Jolt", 10, 0),
    WAR_SCREAM(ClassType.WARRIOR, 4, "War Scream", "Cry of the Gods", 7, -1),

    HEAL(ClassType.MAGE, 1, "Heal", "Remedy", 8, -1),
    TELEPORT(ClassType.MAGE, 2, "Teleport", "Blink", 4, 0),
    METEOR(ClassType.MAGE, 3, "Meteor", "Dead Star", 8, 0),
    ICE_SNAKE(ClassType.MAGE, 4, "Ice Snake", "Crystal Reptile", 6, -1),

    TOTEM(ClassType.SHAMAN, 1, "Totem", "Sky Emblem", 4, 0),
    HAUL(ClassType.SHAMAN, 2, "Haul", "Soar", 3, -1),
    AURA(ClassType.SHAMAN, 3, "Aura", "Wind Surge", 8, 0),
    UPROOT(ClassType.SHAMAN, 4, "Uproot", "Gale Funnel", 6, 0),

    // Unspecified spells
    FIRST_SPELL(null, 1, "1st Spell", "1st Spell", 0, 0),
    SECOND_SPELL(null, 2, "2nd Spell", "2nd Spell", 0, 0),
    THIRD_SPELL(null, 3, "3rd Spell", "3rd Spell", 0, 0),
    FOURTH_SPELL(null, 4, "4th Spell", "4th Spell", 0, 0);

    static final int[][] MANA_REDUCTION_LEVELS = new int[][] {
        {},
        {68},
        {41, 105},
        {29, 68, 129},
        {23, 51, 89, 147},
        {19, 41, 68, 105},
        {16, 34, 55, 82, 118},
        {14, 29, 47, 68, 94, 129},
        {12, 26, 41, 58, 79, 105, 139},
        {11, 23, 36, 51, 68, 89, 114, 147}
    };

    private ClassType classType;
    private int spellNumber;
    private String name;
    private String vipName;
    private int startManaCost;
    private int gradeManaChange;

    public ClassType getClassType() {
        return classType;
    }

    public int getSpellNumber() {
        return spellNumber;
    }

    public String getName() {
        return name;
    }

    public String getVipName() {
        return vipName;
    }

    public int getUnlockLevel(int grade) {
        int unlockLevel = (spellNumber - 1) * 10 + 1;
        if (grade == 1) return unlockLevel;
        if (grade == 2) return unlockLevel + 15;
        if (grade == 3) return unlockLevel + 35;
        assert(false);
        return 0;
    }

    public int getGrade(int level) {
        int compareLevel = level - (spellNumber - 1) * 10;
        if (compareLevel  >= 36) {
            return 3;
        } else if (compareLevel >= 16) {
            return 2;
        } else if (compareLevel >= 1) {
            return 1;
        } else {
            // not unlocked
            return 0;
        }
    }

    private int getUnreducedManaCost(int level) {
        return startManaCost + (getGrade(level)-1) * gradeManaChange;
    }

    public int getManaCost(int level, int intelligenceLevel) {
        int manaReduction = 0;
        for (int i : MANA_REDUCTION_LEVELS[getUnreducedManaCost(level)-1]) {
            if (intelligenceLevel >= i) {
                manaReduction++;
            } else {
                break;
            }
        }
        return getUnreducedManaCost(level) - manaReduction;
    }

    public int getNextManaReduction(int level, int intelligenceLevel) {
        for (int i : MANA_REDUCTION_LEVELS[getUnreducedManaCost(level)-1]) {
            if (i > intelligenceLevel) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    SpellType(ClassType classType, int spellNumber, String name, String vipName, int startManaCost, int gradeManaChange) {
        this.classType = classType;
        this.spellNumber = spellNumber;
        this.name = name;
        this.vipName = vipName;
        this.startManaCost = startManaCost;
        this.gradeManaChange = gradeManaChange;
    }

    public static SpellType fromName(String name) {
        for (SpellType spellType : values()) {
            if (spellType.name.matches("^" + name + "\\b") || spellType.vipName.matches("^" + name + "\\b")) {
                return spellType;
            }
        }
        return null;
    }

    public static SpellType forClass(ClassType classRequired, int spellNumber) {
        for (SpellType spellType : values()) {
            if (spellType.classType.equals(classRequired)
                    && spellType.spellNumber ==spellNumber) {
                return spellType;
            }
        }
        return null;
    }

    private UpdatableSpellType getUpdatableSpellType() {
        return UpdatableSpellType.values()[spellNumber-1];
    }

    public String replaceWithShortAndCurrentName(String id) {
        return getUpdatableSpellType().replaceWithShortAndCurrentName(id);
    }

    public String replaceWithShortName(String id) {
        return getUpdatableSpellType().replaceWithShortName(id);
    }

    public void updateCurrentName(String id) {
        getUpdatableSpellType().updateCurrentName(id);
    }

    private enum UpdatableSpellType {

        FIRST_SPELL  ("1st Spell", "^(?:Arrow Storm|Bolt Blizzard|Bash|Holy Blast|Heal|Remedy|Spin Attack|Whirlwind|Totem|Sky Emblem|1st Spell)\\b"),
        SECOND_SPELL ("2nd Spell", "^(?:Escape|Spider Jump|Charge|Leap|Teleport|Blink|Vanish|Shadow Clone|Haul|Soar|2nd Spell)\\b"),
        THIRD_SPELL  ("3rd Spell", "^(?:Bomb(?: Arrow)?|Creeper Dart|Uppercut|Heaven Jolt|Meteor|Dead Star|Multi Hit|Leopard Punches|Aura|Wind Surge|3rd Spell)\\b"),
        FOURTH_SPELL ("4th Spell", "^(?:Arrow Shield|Dagger Aura|War Scream|Cry of the Gods|Ice Snake|Crystal Reptile|Smoke Bomb|Blinding Cloud|Uproot|Gale Funnel|4th Spell)\\b");

        private final String shortName;
        private final Pattern regex;
        private String currentName;

        UpdatableSpellType(String shortName, String regex) {
            this.shortName = shortName;
            this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            this.currentName = shortName;
        }

        public void updateCurrentName(String id) {
            Matcher m = regex.matcher(id);
            if (!m.find()) return;
            String currentName = m.group(0);
            if (currentName.equalsIgnoreCase(shortName)) return;
            this.currentName = currentName;
        }

        public String replaceWithShortName(String id) {
            return regex.matcher(id).replaceFirst(shortName);
        }

        public String replaceWithShortAndCurrentName(String id) {
            return regex.matcher(id).replaceFirst(shortName + " (" + currentName + ")");
        }

    }
}
