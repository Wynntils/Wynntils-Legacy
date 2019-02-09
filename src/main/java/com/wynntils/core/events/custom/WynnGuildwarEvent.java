package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WynnGuildwarEvent extends Event {

    String territoryName;
    String attackerName;
    String defenderName;
    WarUpdateType type;

    public WynnGuildwarEvent(String territoryName, String attackerName, String defenderName, WarUpdateType type) {
        this.territoryName = territoryName;
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.type = type;
    }

    public String getTerritoryName() {
        return territoryName;
    }

    public String getAttackerName() {
        return attackerName;
    }

    public String getDefenderName() {
        return defenderName;
    }

    public WarUpdateType getType() {
        return type;
    }

    public enum WarUpdateType {
        ATTACKED,
        CAPTURED,
        DEFENDED
    }
}
