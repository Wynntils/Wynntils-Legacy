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
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobTotemTracker implements Listener {
    private static final Pattern MOB_TOTEM_NAME = Pattern.compile("^§f§l(.*)'s§6§l Mob Totem$");
    private static final Pattern MOB_TOTEM_TIMER = Pattern.compile("^§c§l([0-9]+):([0-9]+)$");

    Map<Integer, MobTotem> mobTotemUnstarted = new HashMap<>();
    Map<Integer, MobTotem> mobTotemStarted = new HashMap<>();

    private int bufferedId = -1;
    private double bufferedX = -1;
    private double bufferedY = -1;
    private double bufferedZ = -1;

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

    private void removeAllMobTotems() {
        for (MobTotem mobTotem : mobTotemStarted.values()) {
            postEvent(new SpellEvent.MobTotemRemoved(mobTotem));
        }
        mobTotemUnstarted.clear();
        mobTotemStarted.clear();
    }

    @SubscribeEvent
    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        Entity entity = getBufferedEntity(e.getPacket().getEntityId());
        if (!(entity instanceof EntityArmorStand)) return;

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

                    mobTotemStarted.put(mobTotem.getTotemId(), mobTotem);
                    mobTotemUnstarted.remove(mobTotem.getTotemId());

                    postEvent(new SpellEvent.MobTotemActivated(mobTotem, minutes * 60 + seconds + 1));
                    return;
                }
            }
        }

    }

    @SubscribeEvent
    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        if (!Reference.onWorld) return;

        for (int id : e.getPacket().getEntityIDs()) {
            mobTotemUnstarted.remove(id);
            MobTotem mobTotem = mobTotemStarted.get(id);
            if (mobTotem == null) continue;
            mobTotemStarted.remove(id);

            postEvent(new SpellEvent.MobTotemRemoved(mobTotem));
        }
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        removeAllMobTotems();
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
