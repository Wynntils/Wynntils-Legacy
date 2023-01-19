/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.instances.ShamanTotem;
import com.wynntils.modules.core.instances.MobTotemTracker;
import net.minecraftforge.fml.common.eventhandler.Event;


public class SpellEvent extends Event {

    public static class Cast extends SpellEvent {

        private final SpellType spell;

        public Cast(SpellType spell) {
            this.spell = spell;
        }

        public SpellType getSpell() {
            return spell;
        }
    }

    public static class Totem extends SpellEvent {
        private final int totemNumber;

        public Totem(int totemNumber) {
            this.totemNumber = totemNumber;
        }

        public int getTotemNumber() {
            return totemNumber;
        }
    }

    public static class TotemSummoned extends Totem {
        public TotemSummoned(int totemNumber) {
            super(totemNumber);
        }
    }

    public static class TotemActivated extends Totem {
        private final Location location;

        public TotemActivated(int totemNumber, Location location) {
            super(totemNumber);
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static class TotemUpdated extends Totem {
        private final int time;
        private final Location location;

        public TotemUpdated(int totemNumber, int time, Location location) {
            super(totemNumber);
            this.time = time;
            this.location = location;
        }

        public int getTime() {
            return time;
        }

        public Location getLocation() {
            return location;
        }
    }

    public static class TotemRemoved extends Totem {
        private final ShamanTotem totem;

        public TotemRemoved(int totemNumber, ShamanTotem totem) {
            super(totemNumber);
            this.totem = totem;
        }

        /**
         * @return the totem that was removed
         */
        public ShamanTotem getTotem() {
            return totem;
        }
    }

    public static class MobTotem extends SpellEvent {
        private final MobTotemTracker.MobTotem mobTotem;

        public MobTotem(MobTotemTracker.MobTotem mobTotem) {
            this.mobTotem = mobTotem;
        }

        public MobTotemTracker.MobTotem getMobTotem() {
            return mobTotem;
        }
    }

    public static class MobTotemActivated extends MobTotem {
        private final int time;

        public MobTotemActivated(MobTotemTracker.MobTotem mobTotem, int time) {
            super(mobTotem);
            this.time = time;
        }

        public int getTime() {
            return time;
        }
    }

    public static class MobTotemRemoved extends MobTotem {
        public MobTotemRemoved(MobTotemTracker.MobTotem mobTotem) {
            super(mobTotem);
        }
    }

}
