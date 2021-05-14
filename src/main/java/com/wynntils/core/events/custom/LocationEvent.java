/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LocationEvent extends Event {

    /**
     * Emitted when a static label is encountered in the map.
     */
    public static class LabelFoundEvent extends LocationEvent {
        private final String label;
        private final Location location;
        private final Entity entity;

        public LabelFoundEvent(String label, Location location, Entity entity) {
            this.label = label;
            this.location = location;
            this.entity = entity;
        }

        public String getLabel() {
            return label;
        }

        public Location getLocation() {
            return location;
        }

        /**
         * @return the entity this label is associated with
         */
        public Entity getEntity() {
            return entity;
        }
    }

    /**
     * Emitted when a labeled LivingEntity is encountered in the map.
     */
    public static class EntityLabelFoundEvent extends LocationEvent {
        private final String label;
        private final Location location;
        private final EntityLiving entity;

        public EntityLabelFoundEvent(String label, Location location, EntityLiving entity) {
            this.label = label;
            this.location = location;
            this.entity = entity;
        }

        public String getLabel() {
            return label;
        }

        public Location getLocation() {
            return location;
        }

        public EntityLiving getEntity() {
            return entity;
        }
    }
}
