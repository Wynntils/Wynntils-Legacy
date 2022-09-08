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
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class TotemTracker {
    private static final Pattern SHAMAN_TOTEM_TIMER = Pattern.compile("§c(\\d+)s");
    private static final Pattern MOB_TOTEM_NAME = Pattern.compile("^§f§l(.*)'s§6§l Mob Totem$");
    private static final Pattern MOB_TOTEM_TIMER = Pattern.compile("^§c§l([0-9]+):([0-9]+)$");

    public enum TotemState { NONE, SUMMONED, LANDING, PREPARING, ACTIVE }
    private TotemState totemState = TotemState.NONE;

    private int totemId = -1;
    private double totemX, totemY, totemZ;
    private int totemTime = -1;

    private long totemCastTimestamp = 0;
    private long totemCreatedTimestamp = Long.MAX_VALUE;
    private long totemPreparedTimestamp = 0;

    private int potentialId = -1;
    private double potentialX, potentialY, potentialZ;

    private int heldWeaponSlot = -1;

    Map<Integer, MobTotem> mobTotemUnstarted = new HashMap<>();
    Map<Integer, MobTotem> mobTotemStarted = new HashMap<>();

    private int bufferedId = -1;
    private double bufferedX = -1;
    private double bufferedY = -1;
    private double bufferedZ = -1;

    private static boolean isClose(double a, double b)
    {
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

    private void updateTotemPosition(double x, double y, double z) {
        totemX = x;
        totemY = y;
        totemZ = z;
    }

    private void checkTotemSummoned() {
        // Check if we have both creation and spell cast at roughly the same time
        if (Math.abs(totemCreatedTimestamp - totemCastTimestamp) < 500) {
            // If we have an active totem already, first remove that one
            removeTotem(true);
            totemId = potentialId;
            totemTime = -1;

            updateTotemPosition(potentialX, potentialY, potentialZ);
            totemState = TotemState.SUMMONED;
            postEvent(new SpellEvent.TotemSummoned());
        }
    }

    private void removeTotem(boolean forcefullyRemoved) {
        if (totemState != TotemState.NONE) {
            totemState = TotemState.NONE;
            heldWeaponSlot = -1;
            totemId = -1;
            totemTime = -1;
            totemX = 0;
            totemY = 0;
            totemZ = 0;
            postEvent(new SpellEvent.TotemRemoved(forcefullyRemoved));
        }
    }

    private void removeAllMobTotems() {
        for (MobTotem mobTotem : mobTotemStarted.values()) {
            postEvent(new SpellEvent.MobTotemRemoved(mobTotem));
        }
        mobTotemUnstarted.clear();
        mobTotemStarted.clear();
    }

    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        if (!Reference.onWorld) return;

        if (e.getPacket().getType() == 78) {
            bufferedId = e.getPacket().getEntityID();
            bufferedX = e.getPacket().getX();
            bufferedY = e.getPacket().getY();
            bufferedZ = e.getPacket().getZ();

            if (e.getPacket().getEntityID() == totemId && totemState == TotemState.SUMMONED) {
                // Totems respawn with the same entityID when landing.
                // Update with more precise coordinates
                updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
                totemState = TotemState.LANDING;
                return;
            }

            // Is it created close to us? Then it's a potential new totem
            if (isClose(e.getPacket().getX(), McIf.player().posX) &&
                    isClose(e.getPacket().getY(), McIf.player().posY + 1.0) &&
                    isClose(e.getPacket().getZ(), McIf.player().posZ)) {
                potentialId = e.getPacket().getEntityID();
                potentialX = e.getPacket().getX();
                potentialY = e.getPacket().getY();
                potentialZ = e.getPacket().getZ();
                totemCreatedTimestamp = System.currentTimeMillis();
                checkTotemSummoned();
            }
        }
    }

    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (SpellType.TOTEM.getName().equals(e.getSpell())) {
            totemCastTimestamp = System.currentTimeMillis();
            heldWeaponSlot = McIf.player().inventory.currentItem;
            checkTotemSummoned();
        } else if (SpellType.UPROOT.getName().equals(e.getSpell())) {
            totemCastTimestamp = System.currentTimeMillis();
        }
    }

    public void onTotemTeleport(PacketEvent<SPacketEntityTeleport> e) {
        if (!Reference.onWorld) return;

        int thisId = e.getPacket().getEntityId();
        if (thisId == totemId) {
            if (totemState == TotemState.SUMMONED || totemState == TotemState.LANDING) {
                // Now the totem has gotten it's final coordinates
                updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
                totemState = TotemState.PREPARING;
                totemPreparedTimestamp = System.currentTimeMillis();
            }
            if (totemState == TotemState.ACTIVE) {
                // Uproot; update our location
                updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
            }
        }
    }

    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        Entity entity = getBufferedEntity(e.getPacket().getEntityId());
        if (!(entity instanceof EntityArmorStand)) return;

        if (totemState == TotemState.PREPARING || totemState == TotemState.SUMMONED || totemState == TotemState.ACTIVE) {
            Matcher m = SHAMAN_TOTEM_TIMER.matcher(name);
            if (m.matches()) {
                // We got a armor stand with a timer nametag
                if (totemState == TotemState.PREPARING) {
                    totemX = entity.posX;
                    totemY = entity.posY - 2.8;
                    totemZ = entity.posZ;
                }

                int time = Integer.parseInt(m.group(1));
                if (totemTime == -1) {
                    totemTime = time;
                    totemState = TotemState.ACTIVE;
                    postEvent(new SpellEvent.TotemActivated(totemTime, new Location(totemX, totemY, totemZ)));
                } else if (time != totemTime) {
                    if (time > totemTime) {
                        // Timer restarted using uproot
                        postEvent(new SpellEvent.TotemRenewed(time, new Location(totemX, totemY, totemZ)));
                    }
                    totemTime = time;
                }
                return;
            }
        }

        Matcher m2 = MOB_TOTEM_NAME.matcher(name);
        if (m2.find()) {
            int mobTotemId = e.getPacket().getEntityId();

            MobTotem mobTotem = new MobTotem(mobTotemId,
                    new Location(entity.posX, entity.posY - 4.5, entity.posZ), m2.group(1));

            mobTotemUnstarted.put(mobTotemId, mobTotem);
            return;
        }

        for (MobTotem mobTotem : mobTotemUnstarted.values()) {
            if (entity.posX == mobTotem.getLocation().getX() && entity.posZ == mobTotem.getLocation().getZ()
                    && entity.posY == mobTotem.getLocation().getY() + 4.7) {
                Matcher m3 = MOB_TOTEM_TIMER.matcher(name);
                if (m3.find()) {
                    int minutes = Integer.parseInt(m3.group(1));
                    int seconds = Integer.parseInt(m3.group(2));

                    mobTotemStarted.put(mobTotem.totemId, mobTotem);
                    mobTotemUnstarted.remove(mobTotem.totemId);

                    postEvent(new SpellEvent.MobTotemActivated(mobTotem, minutes * 60 + seconds + 1));
                    return;
                }
            }
        }

    }

    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        if (!Reference.onWorld) return;

        IntStream entityIDs = Arrays.stream(e.getPacket().getEntityIDs());
        if (entityIDs.filter(id -> id == totemId).findFirst().isPresent()) {
            if (totemState == TotemState.ACTIVE && totemTime == 0) {
                removeTotem(false);
            }
        }

        for (int id : e.getPacket().getEntityIDs()) {
            mobTotemUnstarted.remove(id);
            MobTotem mobTotem = mobTotemStarted.get(id);
            if (mobTotem == null) continue;
            mobTotemStarted.remove(id);

            postEvent(new SpellEvent.MobTotemRemoved(mobTotem));
        }
    }

    public void onTotemClassChange(WynnClassChangeEvent e) {
        removeTotem(true);
        removeAllMobTotems();
    }

    public void onWeaponChange(PacketEvent<CPacketHeldItemChange> e) {
        if (!Reference.onWorld) return;

        if (e.getPacket().getSlotId() != heldWeaponSlot) {
            removeTotem(true);
        }
    }

    public static class MobTotem {
        private final int totemId;
        private final Location location;
        private final String owner;

        public MobTotem(int totemId, Location location, String owner) {
            this.totemId = totemId;
            this.location = location;
            this.owner = owner;
        }

        public int getTotemId() {
            return totemId;
        }

        public Location getLocation() {
            return location;
        }

        public String getOwner() {
            return owner;
        }

        @Override
        public String toString() {
            return "Mob Totem (" + owner + ") at " + location;
        }
    }

}
