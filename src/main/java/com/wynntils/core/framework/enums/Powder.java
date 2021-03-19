/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public enum Powder {

    EARTH('✤', TextFormatting.DARK_GREEN),
    E1(3, 6, 17, 2, 1), E2(6, 9, 21, 4, 2), E3(8, 14, 25, 8, 3), E4(11, 16, 31, 14, 5), E5(15, 18, 38, 22, 9), E6(18, 22, 46, 30, 13),

    THUNDER('✦', TextFormatting.YELLOW),
    T1(1, 8, 9, 3, 1), T2(1, 13, 11, 5, 1), T3(2, 18, 14, 9, 2), T4(3, 24, 17, 14, 4), T5(3, 32, 22, 20, 7), T6(5, 40, 28, 28, 10),

    WATER('❉', TextFormatting.AQUA),
    W1(3, 4, 13, 3, 1), W2(4, 7, 15, 6, 1), W3(6, 10, 17, 11, 2), W4(8, 12, 21, 18, 4), W5(11, 14, 26, 28, 7), W6(13, 17, 32, 40, 10),

    FIRE('✹', TextFormatting.RED),
    F1(2, 5, 14, 3, 1), F2(4, 8, 16, 5, 2), F3(6, 10, 19, 9, 3), F4(9, 13, 24, 16, 5), F5(12, 16, 30, 25, 9), F6(15, 19, 37, 36, 13),

    AIR('❋', TextFormatting.WHITE),
    A1(2, 6, 11, 3, 1), A2(4, 9, 14, 6, 2), A3(7, 10, 17, 10, 3), A4(9, 13, 22, 16, 5), A5(13, 18, 28, 24, 9), A6(16, 18, 35, 34, 13);

    private static final Powder[] bases = {EARTH, THUNDER, WATER, FIRE, AIR};

    char symbol;
    String color;
    private final int minDmg;
    private final int maxDmg;
    private final int convPct;
    private final int defPlus;
    private final int defMinus;


    Powder(char symbol, TextFormatting color) {
        this(symbol, color, 0,0,0,0,0);
    }

    Powder(char symbol, TextFormatting color, int minDmg, int maxDmg, int convPct, int defPlus, int defMinus){

        this.symbol = symbol;
        this.color  = color.toString();

        this.minDmg   = minDmg;
        this.maxDmg   = maxDmg;
        this.convPct  = convPct;
        this.defPlus  = defPlus;
        this.defMinus = defMinus;
    }

    Powder(int minDmg, int maxDmg, int convPct, int defPlus, int defMinus){
        this(' ', TextFormatting.WHITE, minDmg, maxDmg, convPct, defPlus, defMinus);
    }

    public char getSymbol() {
        return symbol;
    }

    public String getLetterRepresentation() {
        return this.name().substring(0, 1).toLowerCase();
    }

    private static Powder[] getBases(){
        return bases;
    }

    private Powder getBase(){
        for (Powder p: getBases()){
            if (p.name().substring(0,1).equals(this.name().substring(0,1))){
                return p;
            }
        }
        return null;
    }

    public DamageType asDamage(){
        return DamageType.valueOf(this.getBase().name());
    }

    public DamageType getOppositeDamageType() {
        String s = String.valueOf(this.name().charAt(0));
        //Could maybe be optimized because the opposite is just the previous one
        switch (s){
            case "E":
                return DamageType.AIR;
            case "T":
                return DamageType.EARTH;
            case "W":
                return DamageType.THUNDER;
            case "F":
                return DamageType.WATER;
            case "A":
                return DamageType.FIRE;
        }
        return null;
    }

    private Powder[] getTiers(){
        Powder[] v = values();
        String s = this.name().substring(0,1);
        Powder[] out = new Powder[6];
        int i = 0;
        for (int n = 1;n < v.length;n++){
            if (v[n].name().startsWith(s) && v[n].name().length()==2){
                out[i] = v[n];
                i++;
            }
        }
        return out;
    }

    public static List<Powder> findPowders(String input) {
        List<Powder> foundPowders = new LinkedList<>();
        input.chars().forEach(ch -> {
            for (Powder powder : getBases()) {
                if (ch == powder.getSymbol()) {
                    foundPowders.add(powder);
                }
            }
        });

        return foundPowders;
    }


    public static List<Powder> findTieredPowders(ItemStack stack){
        String itemName =  WebManager.getTranslatedItemName(TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName())).replace("֎", "");
        ItemProfile baseItem = WebManager.getItems().get(itemName);
        ItemType t = ItemUtils.getItemType(stack);
        if (t == ItemType.BOW || t == ItemType.DAGGER || t == ItemType.WAND || t == ItemType.RELIK || t == ItemType.SPEAR){
            return findTieredPowdersWeapon(stack, baseItem);
        } else {
            return findTieredPowdersArmor(stack, baseItem);
        }
    }

    private static List<Powder> findTieredPowdersWeapon(ItemStack stack, ItemProfile baseItem){
        List<String> itemLore = ItemUtils.getUnformattedLore(stack);
        List<Powder> powderTypes = new LinkedList<>();

        Hashtable<DamageType, int[]> damageValues = new Hashtable<>();
        for( DamageType dt: DamageType.values()){ damageValues.put(dt, new int[]{0, 0}); }

        //Probably not optimized...
        //This loop finds the amount of powders used [(3 air powders 1 earth), (2 water 2 fire), (1 thunder), etc.]
        //As well as the elemental damages as displayed in the item lore
        itemLore.forEach(line -> {
            if (line.contains("Powder Slots [")) {
                //Get what types of powders there are (Not tiered)
                line.chars().forEach(ch -> {
                    for (Powder powder : getBases()) {
                        if (ch == powder.getSymbol()) {
                            powderTypes.add(powder);
                        }
                    }
                });
            } else {
                DamageType dt = null;

                //Get damages displayed on an item:
                if (line.startsWith("✤ Neutral Damage: ")) {
                    dt = DamageType.NEUTRAL;
                } else if (line.startsWith("✤")) {
                    dt = DamageType.EARTH;
                } else if (line.startsWith("✦")) {
                    dt = DamageType.THUNDER;
                } else if (line.startsWith("❉")) {
                    dt = DamageType.WATER;
                } else if (line.startsWith("✹")) {
                    dt = DamageType.FIRE;
                } else if (line.startsWith("❋")) {
                    dt = DamageType.AIR;
                }

                if (dt != null) {
                    //Get damage range as string("0-200"),("300-500"),etc.
                    String s = line.substring(line.indexOf(": ")+2);
                    //Split damage string into array with min & max [400, 500] and add to the hashtable
                    damageValues.put(dt, Arrays.stream(s.split("-")).mapToInt(Integer::parseInt).toArray());
                }
            }
        });

        //Number of different possible powder combinations
        int combinations = (int) Math.pow(6,powderTypes.size());
        if (combinations == 1) return null;

        //Start at limit because that is all tier 6 powders and that is most likely
        for (int i = combinations-1; i >= 0; i--){
            List<Powder> combo = new LinkedList<>();
            for (int n = 0; n < powderTypes.size(); n++){
                combo.add(powderTypes.get(n).getTiers()[((int) (i/ Math.pow(6,n))) % 6]);
            }

            if (matchDam(getDamages(combo, baseItem),damageValues)){
                return combo;
            }
        }

        return null;
    }

    private static Hashtable<DamageType, Integer> getDefences(List<Powder> combo, ItemProfile base){

        Hashtable<DamageType, Integer> defences = new Hashtable<>();

        //Initialize with all values 0
        for( DamageType dt: DamageType.values()){ defences.put(dt, 0); }

        //Add damages from the base unpowdered weapon
        base.getElementalDefenses().forEach(defences::put);

        combo.forEach(powder -> {
            defences.put(powder.asDamage(), defences.get(powder.asDamage()) + powder.defPlus);
            defences.put(powder.getOppositeDamageType(), defences.get(powder.getOppositeDamageType()) - powder.defMinus);
        });

        return defences;

    }

    private static Hashtable<DamageType, int[]> getDamages(List<Powder> combo, ItemProfile base){

        Hashtable<DamageType, int[]> damages = new Hashtable<>();

        //Initialize with all values 0
        for( DamageType dt: DamageType.values()){ damages.put(dt, new int[]{0, 0}); }

        //Add damages from the base unpowdered weapon
        base.getDamageTypes().forEach((dt, d) -> damages.put(DamageType.valueOf(dt.toUpperCase()), Arrays.stream(d.split("-")).mapToInt(Integer::parseInt).toArray()));

        //For percent convert calculations
        int[] baseNeutral = damages.get(DamageType.NEUTRAL).clone();

        combo.forEach(powder -> {
            int[] neutralDamage = damages.get(DamageType.NEUTRAL).clone();
            int[] elementalDamage = damages.get(powder.asDamage()).clone();

            float conversionRatio = powder.convPct/100f;
            if (neutralDamage[1] > 0) {
                //Integer math needed for calculations
                float min_diff = Math.min(neutralDamage[0], conversionRatio * baseNeutral[0]);
                float max_diff = Math.min(neutralDamage[1], conversionRatio * baseNeutral[1]);
                elementalDamage[0] = Math.round(Math.round(Math.floor(elementalDamage[0] + min_diff)));
                elementalDamage[1] = Math.round(Math.round(Math.floor(elementalDamage[1] + max_diff)));
                neutralDamage[0] = Math.round(Math.round(Math.floor(neutralDamage[0] - min_diff)));
                neutralDamage[1] = Math.round(Math.round(Math.floor(neutralDamage[1] - max_diff)));
            }
            elementalDamage[0] += powder.minDmg;
            elementalDamage[1] += powder.maxDmg;

            damages.put(powder.asDamage(), elementalDamage);
            damages.put(DamageType.NEUTRAL, neutralDamage);

        });

        return damages;

    }

    private static List<Powder> findTieredPowdersArmor (ItemStack stack, ItemProfile baseItem){
        List<String> itemLore = ItemUtils.getUnformattedLore(stack);
        List<Powder> powderTypes = new LinkedList<>();

        Hashtable<DamageType, Integer> defValues = new Hashtable<>();
        for( DamageType dt: DamageType.values()){ defValues.put(dt, 0); }

        itemLore.forEach(line -> {
            if (line.contains("Powder Slots [")) {
                //Get what types of powders there are (Not tiered)
                line.chars().forEach(ch -> {
                    for (Powder powder : getBases()) {
                        if (ch == powder.getSymbol()) {
                            powderTypes.add(powder);
                        }
                    }
                });
            } else {
                DamageType dt = null;

                //Get defences displayed on an item:
                if (line.startsWith("✤")) {
                    dt = DamageType.EARTH;
                } else if (line.startsWith("✦")) {
                    dt = DamageType.THUNDER;
                } else if (line.startsWith("❉")) {
                    dt = DamageType.WATER;
                } else if (line.startsWith("✹")) {
                    dt = DamageType.FIRE;
                } else if (line.startsWith("❋")) {
                    dt = DamageType.AIR;
                }

                if (dt != null) {
                    defValues.put(dt, Integer.parseInt(line.substring(line.indexOf(": ")+2)));
                }
            }
        });

        int combinations = (int) Math.pow(6,powderTypes.size());
        if (combinations == 1) return null;

        //Start at limit because that is all tier 6 powders and that is most likely
        for (int i = combinations-1; i >= 0; i--){
            List<Powder> combo = new LinkedList<>();
            for (int n = 0; n < powderTypes.size(); n++){
                combo.add(powderTypes.get(n).getTiers()[((int) (i/ Math.pow(6,n))) % 6]);
            }

            if (matchDef(getDefences(combo, baseItem),defValues)){
                return combo;
            }
        }

        return null;
    }

    private static boolean matchDam(Hashtable<DamageType, int[]> d1, Hashtable<DamageType, int[]> d2){
        AtomicBoolean b = new AtomicBoolean(true);

        d1.forEach(((damageType, ints) -> {
            if (!(d2.get(damageType)[0] == ints[0] && d2.get(damageType)[1] == ints[1])){
                b.set(false);
            }
        }));

        return b.get();
    }

    private static boolean matchDef(Hashtable<DamageType, Integer> d1, Hashtable<DamageType, Integer> d2){
        AtomicBoolean b = new AtomicBoolean(true);

        d1.forEach(((damageType, integer) -> {
            if (!(d2.get(damageType).equals(integer))){
                b.set(false);
            }
        }));

        return b.get();
    }
}
