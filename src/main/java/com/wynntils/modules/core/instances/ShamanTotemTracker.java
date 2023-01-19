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
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShamanTotemTracker implements Listener {
    private static final Pattern SHAMAN_TOTEM_TIMER = Pattern.compile("§c(\\d+)s");

    private ShamanTotem totem1 = null;
    private Integer pendingTotem1VisibleId = null;

    private ShamanTotem totem2 = null;
    private Integer pendingTotem2VisibleId = null;

    private ShamanTotem totem3 = null;
    private Integer pendingTotem3VisibleId = null;

    private long totemCastTimestamp = 0;
    private int nextTotemSlot = 1;
    private int summonWeaponSlot = -1; // Weapon used to summon totem(s)

    private final String totemHighlightTeamBase = "wynntilsTH";
    private final int CAST_DELAY_MAX = 450;
    private final double SEARCH_RADIUS = 1.0;

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
        switch (totem) {
            case 1:
                postEvent(new SpellEvent.TotemRemoved(1, totem1));
                totem1 = null;
                pendingTotem1VisibleId = null;
                nextTotemSlot = 1;
                return;
            case 2:
                postEvent(new SpellEvent.TotemRemoved(2, totem2));
                totem2 = null;
                pendingTotem2VisibleId = null;
                if (nextTotemSlot != 1) {
                    nextTotemSlot = 2;
                }
                return;
            case 3:
                postEvent(new SpellEvent.TotemRemoved(3, totem3));
                totem3 = null;
                pendingTotem3VisibleId = null;
                if (nextTotemSlot != 1 && nextTotemSlot != 2) {
                    nextTotemSlot = 3;
                }
                return;
            default:
                throw new IllegalArgumentException("Totem must be 1, 2, or 3");
        }
    }

    /**
     * Resets all three totem variables.
     */
    private void removeAllTotems() {
        summonWeaponSlot = -1;
        removeTotem(1);
        removeTotem(2);
        removeTotem(3);
        nextTotemSlot = 1;
    }

    @SubscribeEvent
    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (SpellType.TOTEM != e.getSpell()) return;

        totemCastTimestamp = System.currentTimeMillis();
        summonWeaponSlot = McIf.player().inventory.currentItem;
    }

    @SubscribeEvent
    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        new Delay(() -> { // Delay because totem doesn't spawn instantly; server needs time
            Entity entity = getBufferedEntity(e.getPacket().getEntityID());
            if (!(entity instanceof EntityArmorStand)) return;
            EntityArmorStand eas = (EntityArmorStand) entity;
            if (Math.abs(totemCastTimestamp - System.currentTimeMillis()) > CAST_DELAY_MAX) return; // Not ours, ignore

            if (Math.abs(eas.getHealth() - 1.0f) > 0.0001f) return;
            List<ItemStack> inv = new ArrayList<>();
            eas.getArmorInventoryList().forEach(inv::add);
            if (inv.size() < 4 || inv.get(3).getItem() != Items.STONE_SHOVEL) return;

            int totemNumber = nextTotemSlot;
            nextTotemSlot = updateNextTotemSlot();

            postEvent(new SpellEvent.TotemSummoned(totemNumber));

            TextFormatting color;
            switch (totemNumber) {
                case 1:
                    color = UtilitiesConfig.ShamanTotemTracking.INSTANCE.totem1Color;
                    break;
                case 2:
                    color = UtilitiesConfig.ShamanTotemTracking.INSTANCE.totem2Color;
                    break;
                case 3:
                    color = UtilitiesConfig.ShamanTotemTracking.INSTANCE.totem3Color;
                    break;
                default:
                    throw new IllegalArgumentException("totemNumber should be 1, 2, or 3! (color switch in #onTotemSpawn in ShamanTotemTracker.java");
            }

            // Create or get a colored team to set highlight color
            Scoreboard scoreboard = McIf.world().getScoreboard();
            if (!scoreboard.getTeamNames().contains(totemHighlightTeamBase + totemNumber)) {
                scoreboard.createTeam(totemHighlightTeamBase + totemNumber);
            }
            ScorePlayerTeam team = scoreboard.getTeam(totemHighlightTeamBase + totemNumber);
            team.setPrefix(color.toString()); // set color of team

            scoreboard.addPlayerToTeam(eas.getCachedUniqueIdString(), totemHighlightTeamBase + totemNumber);
            eas.setGlowing(true);
            ShamanTotem newTotem = new ShamanTotem(totemNumber, eas.getEntityId(), -1, -1, ShamanTotem.TotemState.SUMMONED, new Location(eas.posX, eas.posY, eas.posZ));
            switch (totemNumber) {
                case 1:
                    totem1 = newTotem;
                    pendingTotem1VisibleId = eas.getEntityId();
                    break;
                case 2:
                    totem2 = newTotem;
                    pendingTotem2VisibleId = eas.getEntityId();
                    break;
                case 3:
                    totem3 = newTotem;
                    pendingTotem3VisibleId = eas.getEntityId();
                    break;
                default:
                    throw new IllegalArgumentException("totemNumber should be 1, 2, or 3! (totem variable switch in #onTotemSpawn in ShamanTotemTracker.java");
            }
        }, 1);
    }

    @SubscribeEvent
    public void onTimerSpawn(PacketEvent<SPacketSpawnObject> e) {
        if (pendingTotem1VisibleId == null && pendingTotem2VisibleId == null && pendingTotem3VisibleId == null) return;

        int entityId = e.getPacket().getEntityID();

        if (getBoundTotem(entityId) != null) return;

        new Delay(() -> {
            Entity possibleTimer = getBufferedEntity(entityId);
            if (!(possibleTimer instanceof EntityArmorStand)) return;

            List<EntityArmorStand> toCheck = McIf.world().getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(
                    possibleTimer.posX - SEARCH_RADIUS,
                    possibleTimer.posY - 0.3, // Do not modify unless you are certain it is causing issues
                    possibleTimer.posZ - SEARCH_RADIUS,
                    possibleTimer.posX + SEARCH_RADIUS,
                    possibleTimer.posY + SEARCH_RADIUS * 5,
                    possibleTimer.posZ + SEARCH_RADIUS
            ));

            for (EntityArmorStand eas : toCheck) {
                Location parsedLocation = new Location(eas.posX, eas.posY, eas.posZ);
                if (pendingTotem1VisibleId != null && eas.getEntityId() == pendingTotem1VisibleId) {
                    totem1.setTimerEntityId(entityId);
                    totem1.setLocation(parsedLocation);
                    totem1.setState(ShamanTotem.TotemState.ACTIVE);
                    postEvent(new SpellEvent.TotemActivated(1, parsedLocation));
                } else if (pendingTotem2VisibleId != null && eas.getEntityId() == pendingTotem2VisibleId) {
                    totem2.setTimerEntityId(entityId);
                    totem2.setLocation(parsedLocation);
                    totem2.setState(ShamanTotem.TotemState.ACTIVE);
                    postEvent(new SpellEvent.TotemActivated(2, parsedLocation));
                } else if (pendingTotem3VisibleId != null && eas.getEntityId() == pendingTotem3VisibleId) {
                    totem3.setTimerEntityId(entityId);
                    totem3.setLocation(parsedLocation);
                    totem3.setState(ShamanTotem.TotemState.ACTIVE);
                    postEvent(new SpellEvent.TotemActivated(3, parsedLocation));
                } else {
                    // No totem slots available?
                    // System.out.println("Reeceived a new totem " + entityId + " but no totem slots available");
                }
            }
        }, 0);
    }

    @SubscribeEvent
    public void onTotemRename(PacketEvent<SPacketEntityMetadata> e) {
        if (!Reference.onWorld) return;

        String name = Utils.getNameFromMetadata(e.getPacket().getDataManagerEntries());
        if (name == null || name.isEmpty()) return;

        int entityId = e.getPacket().getEntityId();
        Entity entity = getBufferedEntity(entityId);
        if (!(entity instanceof EntityArmorStand)) return;

        Matcher m = SHAMAN_TOTEM_TIMER.matcher(name);
        if (!m.find()) return;

        int parsedTime = Integer.parseInt(m.group(1));
        Location parsedLocation = new Location(entity.posX, entity.posY, entity.posZ);

        if (getBoundTotem(entityId) == null) return;

        if (getBoundTotem(entityId) == totem1 && totem1 != null) {
            totem1.setTime(parsedTime);
            totem1.setLocation(parsedLocation);
            postEvent(new SpellEvent.TotemUpdated(1, parsedTime, parsedLocation));
        } else if (getBoundTotem(entityId) == totem2 && totem2 != null) {
            totem2.setTime(parsedTime);
            totem2.setLocation(parsedLocation);
            postEvent(new SpellEvent.TotemUpdated(2, parsedTime, parsedLocation));
        } else if (getBoundTotem(entityId) == totem3 && totem3 != null) {
            totem3.setTime(parsedTime);
            totem3.setLocation(parsedLocation);
            postEvent(new SpellEvent.TotemUpdated(3, parsedTime, parsedLocation));
        }
    }

    @SubscribeEvent
    public void onTotemDestroy(PacketEvent<SPacketDestroyEntities> e) {
        if (!Reference.onWorld) return;

        List<Integer> destroyedEntities = Arrays.stream(e.getPacket().getEntityIDs()).boxed().collect(Collectors.toList());

        if (totem1 != null && (destroyedEntities.contains(totem1.getVisibleEntityId()) || destroyedEntities.contains(totem1.getTimerEntityId()))) {
            removeTotem(1);
        }
        if (totem2 != null && (destroyedEntities.contains(totem2.getVisibleEntityId()) || destroyedEntities.contains(totem2.getTimerEntityId()))) {
            removeTotem(2);
        }
        if (totem3 != null && (destroyedEntities.contains(totem3.getVisibleEntityId()) || destroyedEntities.contains(totem3.getTimerEntityId()))) {
            removeTotem(3);
        }
    }

    @SubscribeEvent
    public void onTotemDestroy(SpellEvent.TotemRemoved e) {
        if (!UtilitiesConfig.ShamanTotemTracking.INSTANCE.highlightShamanTotems) return;

        Scoreboard scoreboard = McIf.world().getScoreboard();
        if (scoreboard.getTeamNames().contains(totemHighlightTeamBase + e.getTotemNumber())) {
            scoreboard.removeTeam(scoreboard.getTeam(totemHighlightTeamBase + e.getTotemNumber()));
        }
    }

    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        removeAllTotems();
    }

    @SubscribeEvent
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
        if (totem1 != null && totem1.getTimerEntityId() == timerId) return totem1;
        if (totem2 != null && totem2.getTimerEntityId() == timerId) return totem2;
        if (totem3 != null && totem3.getTimerEntityId() == timerId) return totem3;
        return null;
    }

    private int updateNextTotemSlot() {
        if (nextTotemSlot == 3) return 1;
        return nextTotemSlot += 1;
    }
}
