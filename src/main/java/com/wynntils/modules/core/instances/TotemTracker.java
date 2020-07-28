/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.SpellEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class TotemTracker {
    public enum TotemState { NONE, SUMMONED, LANDING, PREPARING, ACTIVATING, ACTIVE}
    private TotemState totemState = TotemState.NONE;
    private int trackedTotemId = -1;
    private double trackedX, trackedY, trackedZ;
    private double potentialX, potentialY, potentialZ;
    private int potentialTrackedId = -1;
    private int trackedTime;
    private int heldWeaponSlot = -1;
    private long spellCastTimestamp = 0;
    private long totemCreatedTimestamp = Long.MAX_VALUE;

    private int bufferedId = -1;
    private double bufferedX = -1;
    private double bufferedY = -1;
    private double bufferedZ = -1;

    private static boolean isClose(double a, double b)
    {
        double diff = Math.abs(a - b);
        return  (diff < 3);
    }

    private void postEvent(Event event) {
        ModCore.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
    }

    private Entity getBufferedEntity(int entityId) {
        Entity entity = ModCore.mc().world.getEntityByID(entityId);
        if (entity != null) return entity;

        if (entityId == bufferedId) {
            return new EntityArmorStand(ModCore.mc().world, bufferedX, bufferedY, bufferedZ);
        }

        return null;
    }

    private void updateTotemPosition(double x, double y, double z) {
        trackedX = x;
        trackedY = y;
        trackedZ = z;
    }

    private void checkTotemSummoned() {
        // Check if we have both creation and spell cast at roughly the same time
        if (Math.abs(totemCreatedTimestamp - spellCastTimestamp) < 500) {
            // If we have an active totem already, first remove that one
            removeTotem(true);
            trackedTotemId = potentialTrackedId;
            trackedTime  = -1;

            updateTotemPosition(potentialX, potentialY, potentialZ);
            totemState = TotemState.SUMMONED;
            postEvent(new SpellEvent.TotemSummoned());
        }
    }

    private void removeTotem(boolean forcefullyRemoved) {
        if (totemState != TotemState.NONE) {
            totemState = TotemState.NONE;
            heldWeaponSlot = -1;
            trackedTotemId = -1;
            trackedTime = -1;
            trackedX = 0;
            trackedY = 0;
            trackedZ = 0;
            postEvent(new SpellEvent.TotemRemoved(forcefullyRemoved));
        }
    }

    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        if (e.getPacket().getType() == 78) {
            bufferedId = e.getPacket().getEntityID();
            bufferedX = e.getPacket().getX();
            bufferedY = e.getPacket().getY();
            bufferedZ = e.getPacket().getZ();

            if (e.getPacket().getEntityID() == trackedTotemId) {
                // Totems respawn with the same entityID when landing.
                // Update with more precise coordinates
                updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
                totemState = TotemState.LANDING;
                return;
            }

            // Is it created close to us? Then it's a potential new totem
            if (isClose(e.getPacket().getX(), Minecraft.getMinecraft().player.posX) &&
                    isClose(e.getPacket().getY(), Minecraft.getMinecraft().player.posY + 1.0) &&
                    isClose(e.getPacket().getZ(), Minecraft.getMinecraft().player.posZ)) {
                potentialTrackedId = e.getPacket().getEntityID();
                potentialX = e.getPacket().getX();
                potentialY = e.getPacket().getY();
                potentialZ = e.getPacket().getZ();
                totemCreatedTimestamp = System.currentTimeMillis();
                checkTotemSummoned();
            }
        }
    }

    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (e.getSpell().equals("Totem") || e.getSpell().equals("Sky Emblem")) {
            spellCastTimestamp = System.currentTimeMillis();
            heldWeaponSlot =  Minecraft.getMinecraft().player.inventory.currentItem;
            checkTotemSummoned();
        }
    }

    public void onTotemTeleport(PacketEvent<SPacketEntityTeleport> e) {
        int thisId = e.getPacket().getEntityId();

        if (thisId == trackedTotemId && (totemState == TotemState.SUMMONED || totemState == TotemState.LANDING)) {
            // Now the totem has gotten it's final coordinates
            updateTotemPosition(e.getPacket().getX(), e.getPacket().getY(), e.getPacket().getZ());
            totemState = TotemState.PREPARING;
        }
    }

    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onServer || !Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        Entity entity = getBufferedEntity(e.getPacket().getEntityId());
        if (!(entity instanceof EntityArmorStand)) return;

        Pattern shamanTotemTimer = Pattern.compile("^§c([0-9][0-9]?)s$");
        Matcher m = shamanTotemTimer.matcher(name);
        if (m.find()) {
            // We got a armor stand with a timer nametag
            double distanceXZ = Math.abs(entity.posX  - trackedX) +  Math.abs(entity.posZ  - trackedZ);
            if (distanceXZ < 3.0 && entity.posY <= (trackedY + 3.0) && entity.posY >= ((trackedY + 2.0))) {
                // ... and it's close to our totem; regard this as our timer
                int time = Integer.parseInt(m.group(1));

                if (trackedTime == -1) {
                    trackedTime = time;
                    totemState = TotemState.ACTIVE;
                    postEvent(new SpellEvent.TotemActivated(trackedTime, new Location(trackedX, trackedY, trackedZ)));
                } else if (time != trackedTime) {
                    trackedTime = time;
                }
            }
        }
    }

    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        IntStream entityIDs = Arrays.stream(e.getPacket().getEntityIDs());
        if (entityIDs.filter(id -> id == trackedTotemId).findFirst().isPresent()) {
            if (totemState == TotemState.ACTIVE && trackedTime == 0) {
                removeTotem(false);
            }
        }
    }

    public void onTotemClassChange(WynnClassChangeEvent e) {
        removeTotem(true);
    }

    public void onWeaponChange(PacketEvent<CPacketHeldItemChange> e) {
        if (e.getPacket().getSlotId() != heldWeaponSlot) {
            removeTotem(true);
        }
    }
}
