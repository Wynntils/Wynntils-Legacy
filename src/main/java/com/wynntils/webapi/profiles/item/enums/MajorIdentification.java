/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles.item.enums;

import net.minecraft.util.text.TextFormatting;

public enum MajorIdentification {

    PLAGUE("Plague", "Poisoned mobs also poison nearby mobs"),
    HAWKEYE("Hawkeye", "Arrow storm fires 5 powerful arrows"),
    GREED("Greed", "Picking up emeralds heals you and nearby players for 20% max health"),
    CAVALRYMAN("Cavalryman", "Can attack mobs while riding a horse"),
    GUARDIAN("Guardian", "50% of the damage taken by players nearby is redirected to you"),
    ALTRUISM("Heart of the Pack", "Nearby players gain 30% of the health you naturally regenerate"),
    HERO("Saviour's Sacrifice", "While under 25% maximum health, nearby allies gain 30% bonus damage and defense"),
    ARCANES("Transcendence", "50% chance for spells to cost no mana when casted"),
    ENTROPY("Entropy", "Meteor falls significantly faster"),
    ROVINGASSASSIN("Roving Assassin", "Vanish no longer drains mana while invisible"),
    MAGNET("Magnet", "Pull every items on the ground nearby"),
    MADNESS("Madness", "Cast a random ability every 10s"),
    LIGHTWEIGHT("Lightweight", "Removes fall damage"),
    SORCERY("Sorcery", "Spells may cast a second time at no additional cost"),
    TAUNT("Taunt", "War Scream makes nearby mobs target you");

    String name, description;

    MajorIdentification(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String asLore() {
        return TextFormatting.AQUA + "+" + name + ": " + TextFormatting.DARK_AQUA + description;
    }

}
