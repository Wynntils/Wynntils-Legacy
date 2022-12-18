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
    private ShamanTotem totem2 = null;

    private long totemCastTimestamp = 0;
    private int summonWeaponSlot = -1; // Weapon used to summon totem(s)

    private Integer pendingTotem1Id = null;
    private Integer pendingTotem2Id = null;
    private final String totemHighlightTeamBase = "wynntilsTH";

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
            pendingTotem1Id = null;
        } else {
            totem2 = null;
            pendingTotem2Id = null;
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

    @SubscribeEvent
    public void onTotemSpellCast(SpellEvent.Cast e) {
        if (SpellType.TOTEM != e.getSpell()) return;

        totemCastTimestamp = System.currentTimeMillis();
        summonWeaponSlot = McIf.player().inventory.currentItem;
    }

    @SubscribeEvent
    public void onTotemSpawn(PacketEvent<SPacketSpawnObject> e) {
        new Delay(() -> { // Delay because totem doesn't spawn instantly; server needs time
            if (Math.abs(totemCastTimestamp - System.currentTimeMillis()) > 450) return; // Not ours, ignore
            Entity entity = getBufferedEntity(e.getPacket().getEntityID());
            if (!(entity instanceof EntityArmorStand)) return;
            EntityArmorStand eas = (EntityArmorStand) entity;

            if (Math.abs(eas.getEyeHeight() - 1.7775f) > 0.00001f) return;
            if (Math.abs(eas.getHealth() - 1.0f) > 0.0001f) return;
            if (Math.abs(eas.getYOffset() - 0.10000000149011612f) > 0.00000000000000012f) return;

            List<ItemStack> inv = new ArrayList<>();
            eas.getArmorInventoryList().forEach(inv::add);
            if (inv.size() < 4 || inv.get(3).getItem() != Items.STONE_SHOVEL) return;

            int totemNumber = (totem1 != null && totem2 == null) ? 2 : 1;
            TextFormatting color = totemNumber == 1 ? UtilitiesConfig.INSTANCE.totem1Color : UtilitiesConfig.INSTANCE.totem2Color;

            // Create or get a colored team to set highlight color
            Scoreboard scoreboard = McIf.world().getScoreboard();
            if (!scoreboard.getTeamNames().contains(totemHighlightTeamBase + totemNumber)) {
                scoreboard.createTeam(totemHighlightTeamBase + totemNumber);
            }
            ScorePlayerTeam team = scoreboard.getTeam(totemHighlightTeamBase + totemNumber);
            team.setPrefix(color.toString()); // set color of team

            scoreboard.addPlayerToTeam(eas.getCachedUniqueIdString(), totemHighlightTeamBase + totemNumber);
            eas.setGlowing(true);
            if (totem1 != null && totem2 == null) {
                totem2 = new ShamanTotem(-1, -1, ShamanTotem.TotemState.SUMMONED, new Location(eas.posX, eas.posY, eas.posZ));
                pendingTotem2Id = eas.getEntityId();
            } else {
                totem1 = new ShamanTotem(-1, -1, ShamanTotem.TotemState.SUMMONED, new Location(eas.posX, eas.posY, eas.posZ));
                pendingTotem1Id = eas.getEntityId();
            }
        }, 1);
    }

    @SubscribeEvent
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
            // First check if this is actually one casted by us
            List<EntityArmorStand> toCheck = McIf.mc().world.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(
                    entity.posX - 0.5, entity.posY - 0.1, entity.posZ - 0.5,
                    entity.posX + 0.5, entity.posY + 0.1, entity.posZ + 0.5));
            for (EntityArmorStand eas : toCheck) {
                if (pendingTotem1Id != null && eas.getEntityId() == pendingTotem1Id) {
                    totem1 = new ShamanTotem(entityId, parsedTime, ShamanTotem.TotemState.ACTIVE, parsedLocation);
                    postEvent(new SpellEvent.TotemActivated(1, parsedTime, parsedLocation));
                    pendingTotem1Id = null;
                } else if (pendingTotem2Id != null && eas.getEntityId() == pendingTotem2Id) {
                    totem2 = new ShamanTotem(entityId, parsedTime, ShamanTotem.TotemState.ACTIVE, parsedLocation);
                    postEvent(new SpellEvent.TotemActivated(2, parsedTime, parsedLocation));
                    pendingTotem2Id = null;
                } else {
                    // No totem slots available?
                    Reference.LOGGER.warn("Received a new totem " + entityId + ", but no totem slots are available");
                }
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

    @SubscribeEvent
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

    @SubscribeEvent
    public void onTotemDestroy(SpellEvent.TotemRemoved e) {
        if (!UtilitiesConfig.INSTANCE.highlightShamanTotems) return;

        Scoreboard scoreboard = McIf.world().getScoreboard();
        if (scoreboard.getTeamNames().contains(totemHighlightTeamBase + e.getTotemNumber())) {
            scoreboard.removeTeam(scoreboard.getTeam(totemHighlightTeamBase + e.getTotemNumber()));
        }
    }

    @SubscribeEvent
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
}
