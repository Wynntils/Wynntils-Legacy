/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */
package com.wynntils.modules.core.instances;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.SpellEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShamanTotemTracker {
    private static final Pattern SHAMAN_TOTEM_TIMER = Pattern.compile("§c(\\d+)s");

    public enum TotemState {
        SUMMONED,
        ACTIVE
    }

    private ShamanTotem totem1 = null;
    private ShamanTotem totem2 = null;

    private long totemCastTimestamp = 0;
    private int summonWeaponSlot = -1; // Weapon used to summon totem(s)

    private void postEvent(Event event) {
        McIf.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
    }

    private Entity getBufferedEntity(int entityId) {
        Entity entity = McIf.world().getEntityByID(entityId);
        if (entity != null) return entity;

        if (entityId == -1) {
            return new EntityArmorStand(McIf.world(), 0, 0, 0);
        }

        return null;
    }

    /**
     * Removes the given totem from the list of totems.
     * @param totem The totem to remove. Must be 1 or 2.
     */
    private void removeTotem(int totem) {
        if (totem != 1 && totem != 2) {
            throw new IllegalArgumentException("Totem must be 1 or 2");
        }
        postEvent(new SpellEvent.TotemRemoved(totem, totem == 1 ? totem1 : totem2));
        if (totem == 1) {
            totem1 = null;
        } else {
            totem2 = null;
        }
    }

    /**
     * Resets both totem variables.
     */
    private void removeAllTotems() {
        summonWeaponSlot = -1;
        removeTotem(1);
        removeTotem(2);
    }

    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (SpellType.TOTEM != e.getSpell()) return;

        totemCastTimestamp = System.currentTimeMillis();
        summonWeaponSlot = McIf.player().inventory.currentItem;
    }

    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
    }

    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        int entityId = e.getPacket().getEntityId();
        Entity entity = getBufferedEntity(entityId);
        if (!(entity instanceof EntityArmorStand)) return;

        /*
        Logic flow for the following bits:
        - First, the given entity is checked to see if it is a totem timer
        - If the given timerId (int entityId) is not already a totem, assign it to the lowest # totem slot
          - Additionally, assign location, state, and time
        - If the given timerId is already a totem, update the time and location instead
          - Location is updated because there are now totems that can move
         */
        Matcher m = SHAMAN_TOTEM_TIMER.matcher(name);
        if (!m.find()) return;

        int parsedTime = Integer.parseInt(m.group(1));
        Location parsedLocation = new Location(entity.posX, entity.posY, entity.posZ);

        if (getBoundTotem(entityId) == null && Math.abs(totemCastTimestamp - System.currentTimeMillis()) < 15000) {
            // Given timerId is not a totem, make a new totem (assuming regex matches and we are within 15s of casting)
            ShamanTotem newTotem = new ShamanTotem(entityId, parsedTime, TotemState.ACTIVE, parsedLocation);
            if (totem1 == null) {
                totem1 = newTotem;
                postEvent(new SpellEvent.TotemActivated(1, parsedTime, parsedLocation));
            } else if (totem2 == null) {
                totem2 = newTotem;
                postEvent(new SpellEvent.TotemActivated(2, parsedTime, parsedLocation));
            } else {
                // No totem slots available?
                Reference.LOGGER.warn("Received a new totem " + entityId + ", but no totem slots are available");
            }
        } else if (getBoundTotem(entityId) == totem1 && totem1 != null) {
            totem1.setTime(parsedTime);
            totem1.setLocation(parsedLocation);
            postEvent(new SpellEvent.TotemActivated(1, parsedTime, parsedLocation));
        } else if (getBoundTotem(entityId) == totem2 && totem2 != null) {
            totem2.setTime(parsedTime);
            totem2.setLocation(parsedLocation);
            postEvent(new SpellEvent.TotemActivated(2, parsedTime, parsedLocation));
        }
    }

    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        if (!Reference.onWorld) return;

        List<Integer> destroyedEntities = Arrays.stream(e.getPacket().getEntityIDs()).boxed().collect(Collectors.toList());

        if (totem1 != null && destroyedEntities.contains(totem1.getTimerId())) {
            removeTotem(1);
        }
        if (totem2 != null && destroyedEntities.contains(totem2.getTimerId())) {
            removeTotem(2);
        }
    }

    public void onClassChange(WynnClassChangeEvent e) {
        removeAllTotems();
    }

    public void onWeaponChange(PacketEvent<CPacketHeldItemChange> e) {
        if (!Reference.onWorld) return;

        if (e.getPacket().getSlotId() != summonWeaponSlot) {
            removeAllTotems();
        }
    }

    /**
     * Gets the totem bound to the given timerId.
     * @param timerId The timerId that is checked against the totems
     * @return The totem bound to the given timerId, or null if no totem is bound
     */
    private ShamanTotem getBoundTotem(int timerId) {
        if (totem1 != null && totem1.getTimerId() == timerId) return totem1;
        if (totem2 != null && totem2.getTimerId() == timerId) return totem2;
        return null;
    }

    public static class ShamanTotem {
        private int timerId;
        private int time;
        private TotemState state;
        private Location location;

        public ShamanTotem(int timerId, int time, TotemState totemState, Location location) {
            this.timerId = timerId;
            this.time = time;
            this.state = totemState;
            this.location = location;
        }

        public int getTimerId() {
            return timerId;
        }

        public void setTimerId(int timerId) {
            this.timerId = timerId;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public TotemState getState() {
            return state;
        }

        public void setState(TotemState state) {
            this.state = state;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }
}
