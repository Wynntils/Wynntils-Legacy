/*
 *  * Copyright © Wynntils - 2022.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShamanTotemTracker {
    private static final Pattern SHAMAN_TOTEM_TIMER = Pattern.compile("§c(\\d+)s");

    public enum TotemState { SUMMONED, ACTIVE }

    private ShamanTotem totem1 = null;
    private ShamanTotem totem2 = null;

    private long totemCastTimestamp = 0;
    private long totemCreatedTimestamp = Long.MAX_VALUE;

    private int potentialId = -1;
    private double potentialX, potentialY, potentialZ;

    private int summonWeaponSlot = -1; // Weapon used to summon totem(s)

    private int bufferedId = -1;
    private double bufferedX = -1;
    private double bufferedY = -1;
    private double bufferedZ = -1;

    private static boolean isClose(double a, double b) {
        double diff = Math.abs(a - b);
        return (diff < 3);
    }

    private void postEvent(Event event) {
        McIf.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
    }

    private Entity getBufferedEntity(int entityId) {
        Entity entity = McIf.world().getEntityByID(entityId);
        if (entity != null) return entity;

        if (entityId == bufferedId) {
            return new EntityArmorStand(McIf.world(), bufferedX, bufferedY, bufferedZ);
        }

        return null;
    }

    private void checkTotemSummoned() {
        // Check if we have both creation and spell cast at roughly the same time
        if (Math.abs(totemCreatedTimestamp - totemCastTimestamp) < 500) {
            ShamanTotem newTotem = new ShamanTotem(potentialId, -1, -1, TotemState.SUMMONED, new Location(potentialX, potentialY, potentialZ));
            if (totem1 == null) {
                totem1 = newTotem;
                postEvent(new SpellEvent.TotemSummoned(1));
            } else {
                totem2 = newTotem;
                postEvent(new SpellEvent.TotemSummoned(2));
            }
        } else if (Math.abs(totemCreatedTimestamp - totemCastTimestamp) < 3500 && totem1 != null) { // This is our flexible totem cast check
            // Only triggers if we have a totem1, and the totem was created within 3.5 seconds of the cast
            // This is because Wynn's server does not trigger another spell cast packet if you cast two totems quickly in succession
            totem2 = new ShamanTotem(potentialId, -1, -1, TotemState.SUMMONED, new Location(potentialX, potentialY, potentialZ));
            postEvent(new SpellEvent.TotemSummoned(2));
        }
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
     * Removes all totems from the list of totems.
     */
    private void removeAllTotems() {
        summonWeaponSlot = -1;
        removeTotem(1);
        removeTotem(2);
    }

    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        if (!Reference.onWorld || e.getPacket().getType() != 78) return;

        double packetX = e.getPacket().getX();
        double packetY = e.getPacket().getY();
        double packetZ = e.getPacket().getZ();

        bufferedId = e.getPacket().getEntityID();
        bufferedX = packetX;
        bufferedY = packetY;
        bufferedZ = packetZ;

        if (totem1 != null && e.getPacket().getEntityID() == totem1.getId() && totem1.getState() == TotemState.SUMMONED) {
            // Totems respawn with the same entityID when landing
            // Update with more precise coordinates
            totem1.setLocation(new Location(packetX, packetY, packetZ));
            totem1.setState(TotemState.ACTIVE);
        } else if (totem2 != null && e.getPacket().getEntityID() == totem2.getId() && totem2.getState() == TotemState.SUMMONED) {
            // Totems respawn with the same entityID when landing
            // Update with more precise coordinates
            totem2.setLocation(new Location(packetX, packetY, packetZ));
            totem2.setState(TotemState.ACTIVE);
        }

        // Is the new entity created close to us? If so, it could be a new totem
        // This check works because the totem flies out from the player when the totem spell is cast
        if (isClose(packetX, McIf.player().posX) &&
                isClose(packetY, McIf.player().posY) &&
                isClose(packetZ, McIf.player().posZ)) {
            potentialId = e.getPacket().getEntityID();
            potentialX = packetX;
            potentialY = packetY;
            potentialZ = packetZ;
            totemCreatedTimestamp = System.currentTimeMillis();
        }
        checkTotemSummoned(); // Go check if it actually is a totem

    }

    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (!SpellType.TOTEM.getName().equals(e.getSpell())) return;

        totemCastTimestamp = System.currentTimeMillis();
        summonWeaponSlot = McIf.player().inventory.currentItem;
    }

    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        System.out.println(totem1);
        System.out.println(totem2);
        if (!Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        int entityId = e.getPacket().getEntityId();
        Entity entity = getBufferedEntity(entityId);
        if (!(entity instanceof EntityArmorStand)) return;

        /*
        If the totem has this timer bound, process matcher
        OR
        If the totem does not have a timer bound and this timer is not bound, process matcher
        ELSE
        Skip this totem
        */
        if (totem1 != null && ((totem1.getTimerId() == entityId) || (!timerIdAlreadyBound(entityId) && totem1.getTimerId() == -1))) {
            Matcher m = SHAMAN_TOTEM_TIMER.matcher(name);
            if (m.matches()) {
                // This matcher matches when the totem is on the ground
                // This doesn't necessarily mean that the TotemState is ACTIVE, it could be the first time the timer has ticked after the totem landed
                // We also check if the timerId is -1 or the same as the timer entity's ID to make sure we don't confuse the two timers
                totem1.setState(TotemState.ACTIVE);
                totem1.setLocation(new Location(entity.posX, entity.posY - 2.8, entity.posZ));
                totem1.setTimerId(entityId);

                int parsedTime = Integer.parseInt(m.group(1));
                if (totem1.getTime() == -1) {
                    totem1.setTime(parsedTime);
                    postEvent(new SpellEvent.TotemActivated(1, totem1.getTime(), totem1.getLocation()));
                } else if (parsedTime != totem1.getTime()) {
                    if (parsedTime > totem1.getTime()) {
                        // Timer restarted using uproot
                        postEvent(new SpellEvent.TotemRenewed(1, parsedTime, totem1.getLocation()));
                    }
                    totem1.setTime(parsedTime); // Desync, reset to in-game time
                }
            }
        }

        if (totem2 != null && ((totem2.getTimerId() == entityId) || (!timerIdAlreadyBound(entityId) && totem2.getTimerId() == -1))) {
            Matcher m = SHAMAN_TOTEM_TIMER.matcher(name);
            if (m.matches()) {
                // This matcher matches when the totem is on the ground
                // This doesn't necessarily mean that the TotemState is ACTIVE, it could be the first time the timer has ticked after the totem landed
                // We also check if the timerId is -1 or the same as the timer entity's ID to make sure we don't confuse the two timers
                totem2.setState(TotemState.ACTIVE);
                totem2.setLocation(new Location(entity.posX, entity.posY - 2.8, entity.posZ));
                totem2.setTimerId(entityId);

                int parsedTime = Integer.parseInt(m.group(1));
                if (totem2.getTime() == -1) {
                    totem2.setTime(parsedTime);
                    postEvent(new SpellEvent.TotemActivated(2, totem2.getTime(), totem2.getLocation()));
                } else if (parsedTime != totem2.getTime()) {
                    if (parsedTime > totem2.getTime()) {
                        // Timer restarted using uproot
                        postEvent(new SpellEvent.TotemRenewed(2, parsedTime, totem2.getLocation()));
                    }
                    totem2.setTime(parsedTime); // Desync, reset to in-game time
                }
            }
        }
    }

    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        if (!Reference.onWorld) return;

        List<Integer> destroyedEntities = Arrays.stream(e.getPacket().getEntityIDs()).boxed().collect(Collectors.toList());

        if (totem1 != null && (destroyedEntities.contains(totem1.getId()) || destroyedEntities.contains(totem1.getTimerId()))) {
            removeTotem(1);
        }
        if (totem2 != null && (destroyedEntities.contains(totem2.getId()) || destroyedEntities.contains(totem2.getTimerId()))) {
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
     * Checks if the given entity ID is already bound as a totem's timer.
     * @param id The entity ID to check
     * @return true if the timer ID is bound, false otherwise
     */
    private boolean timerIdAlreadyBound(int id) {
        if (totem1 == null || totem2 == null) return false;
        return totem1.getTimerId() == id || totem2.getTimerId() == id;
    }

    public static class ShamanTotem {
        private final int id;
        private int timerId;
        private int time;
        private TotemState state;
        private Location location;

        public ShamanTotem(int id, int timerId, int time, TotemState state, Location location) {
            this.id = id;
            this.timerId = timerId;
            this.time = time;
            this.state = state;
            this.location = location;
        }

        public int getId() {
            return id;
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
