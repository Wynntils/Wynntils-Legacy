package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WynnGuildWarEvent extends Event {

    String territoryName;
    String attackerName;
    String defenderName;
    String attackerTag;
    String defenderTag;
    WarUpdateType type;

    public WynnGuildWarEvent(String territoryName, String attackerName, String defenderName, String attackerTag, String defenderTag, WarUpdateType type) {
        this.territoryName = territoryName;
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.attackerTag = attackerTag;
        this.defenderTag = defenderTag;
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

    public String getAttackerTag() {
        return attackerTag;
    }

    public String getDefenderTag() {
        return defenderTag;
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
