/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.map.events;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.*;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.LabelDetector;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.managers.BeaconManager;
import com.wynntils.modules.map.managers.GuildResourceManager;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.map.managers.NametagManager;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientEvents implements Listener {
    private static final Pattern MOB_LABEL = Pattern.compile("^.*\\[Lv. [0-9]+\\]$");
    private static final Pattern HEALTH_LABEL = Pattern.compile("^\\[\\|+[0-9]+\\|+\\]$");
    private static final Pattern[] IGNORE_PATTERNS = {
        // MOB_DAMAGE
        DamageType.compileDamagePattern(),
        // MOB_LABEL
        Pattern.compile("^.*\\[Lv. [0-9]+\\]$"),
        // HEALTH_LABEL
        Pattern.compile("^§4\\[(§c)?(\\||§0){5,6}[§0-9]+(§c)?(\\||§0){5,6}§4\\]$"),
        // ELEMENTAL_LABEL
        Pattern.compile("^§7\\[((§..)+(Weak|Dam|Def) ?)+§7\\]$"),
        // TOTEM_LABEL
        Pattern.compile("^§c[0-9]+s|\\§c+[0-9]+❤/§7s$"),
        // COMBAT_XP
        Pattern.compile("^(&bx[0-9.]+ )?§7\\[§f\\+§f[0-9]+§f (Combat)|(Guild) XP§7\\]$"),
        // GATHER_XP
        Pattern.compile("^§7\\[\\+[0-9]+§f [ⒸⒷⒿⓀ]§7 [A-Za-z ]+§7 XP\\] §6\\[[0-9]+%\\]$"),
        // GATHER_RESOURCE
        Pattern.compile("^§2\\[§a+[0-9]+§2 [A-Za-z ]+\\]$"),
        // RESOURCE_LABEL
        Pattern.compile("^§8(?:Right|Left)-Click for \\w+$"),
        // WYBEL_OWNER
        Pattern.compile("^§7\\[[A-Za-z0-9_]{3,16}\\]$"),
        // WYBEL_LEVEL
        Pattern.compile("^§2Lv. §a[0-9]+.*$"),
        // PET_NAME
        Pattern.compile("^§7[A-Za-z0-9_]{3,16}'s .*[A-Z].*$"),
        // TERRITORY_HOLDER
        Pattern.compile("^§7Controlled by §b§l.*§r§7 \\[Lv\\. [0-9]+\\]$"),
        // TERRITORY_MSG
        Pattern.compile("^§3< .* >$"),
        // BOSS_ALTAR_TRIBUTE
        Pattern.compile("^§7§lTribute: \\[.*\\]$")

    };

    BlockPos lastLocation = null;

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        LootRunManager.renderActivePaths();

        if (MapConfig.BeaconBeams.INSTANCE.showBeaconBeams) {
            Location playerLoc = new Location(McIf.player().posX, McIf.player().posY, McIf.player().posZ);
            for (WaypointProfile waypoint : MapConfig.Waypoints.INSTANCE.waypoints) {
                boolean groupValue = MapConfig.BeaconBeams.INSTANCE.groupSettings.getOrDefault(waypoint.getType(), false);
                if (!waypoint.shouldShowBeaconBeam() && !groupValue) continue;
                Location location = new Location(waypoint.getX(), waypoint.getY(), waypoint.getZ());
                boolean inRange = waypoint.getZoomNeeded() == MapWaypointIcon.ANY_ZOOM || location.distance(playerLoc) <= 60;
                if (!inRange) continue;
                BeaconManager.drawBeam(location, waypoint.getColor(), e.getPartialTicks());
                NametagManager.renderWaypointName(waypoint.getName(), location.x, location.y, location.z);
            }
        }

        if (!MapConfig.INSTANCE.showCompassBeam || CompassManager.getCompassLocation() == null) return;

        Location compass = CompassManager.getCompassLocation();
        BeaconManager.drawBeam(new Location(compass.getX(), compass.getY(), compass.getZ()), MapConfig.INSTANCE.compassBeaconColor, e.getPartialTicks());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openChest(PlayerInteractEvent.RightClickBlock e) {
        if (e.getPos() == null || e.isCanceled()) return;
        BlockPos pos = e.getPos();
        IBlockState state = e.getEntityPlayer().world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockContainer)) return;
        lastLocation = pos.toImmutable();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOpen(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (lastLocation == null) return;
        if (!e.getGui().getLowerInv().getName().contains("Loot Chest")) {
            lastLocation = null;
            return;
        }

        if (LootRunManager.isRecording())
            LootRunManager.addChest(lastLocation); // add chest to the current lootrun recording

        if(LootRunManager.isLootrunLoaded()){
            if(LootRunManager.isCheckALootrunChest(lastLocation)){
                LootRunManager.addOpenedChestToSession();
            }
        }

        String tier = e.getGui().getLowerInv().getName().replace("Loot Chest ", "");
        if (!MapConfig.Waypoints.INSTANCE.chestTiers.isTierAboveThis(tier)) return;

        WaypointProfile wp = null;
        switch (tier) {
            case "IV":
                wp = new WaypointProfile("Loot Chest T4", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T4, -1000);
                wp.setGroup(WaypointProfile.WaypointType.LOOTCHEST_T4);
                break;
            case "III":
                wp = new WaypointProfile("Loot Chest T3", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T3, -1000);
                wp.setGroup(WaypointProfile.WaypointType.LOOTCHEST_T3);
                break;
            case "II":
                wp = new WaypointProfile("Loot Chest T2", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T2, -1000);
                wp.setGroup(WaypointProfile.WaypointType.LOOTCHEST_T2);
                break;
            case "I":
                wp = new WaypointProfile("Loot Chest T1", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T1, -1000);
                wp.setGroup(WaypointProfile.WaypointType.LOOTCHEST_T1);
                break;
        }
        if (wp != null) {
            if (MapConfig.Waypoints.INSTANCE.waypoints.stream().anyMatch(c -> c.getX() == lastLocation.getX() && c.getY() == lastLocation.getY() && c.getZ() == lastLocation.getZ())) return;

            MapConfig.Waypoints.INSTANCE.waypoints.add(wp);
            MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());

            ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "New Map Entry", "You found a tier " + tier.replace("IV", "4").replace("III", "3") + " chest!"));
        }
    }

    @SubscribeEvent
    public void recordLootRun(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld || e.phase != TickEvent.Phase.END || !LootRunManager.isRecording()) return;

        EntityPlayerSP player = McIf.player();
        if (player == null) return;

        Entity lowestEntity = player.getLowestRidingEntity();

        LootRunManager.recordMovement(lowestEntity.posX, lowestEntity.posY, lowestEntity.posZ);
    }

    @SubscribeEvent
    public void sendGathering(GameEvent.ResourceGather e) {
        if (!MapConfig.Telemetry.INSTANCE.allowGatheringSpot) return;

        WebManager.getAccount().sendGatheringSpot(e.getType(), e.getMaterial(), e.getLocation());
    }

    @SubscribeEvent
    public void receiveAdvancements(PacketEvent.Incoming<SPacketAdvancementInfo> event) {
        // can be done async without problems
        GuildResourceManager.processAdvancements(event.getPacket());
    }

    @SubscribeEvent
    public void labelDetection(LocationEvent.LabelFoundEvent event) {
        if (!MapConfig.Telemetry.INSTANCE.enableLocationDetection) return;

        String formattedLabel = event.getLabel();
        String label = TextFormatting.getTextWithoutFormattingCodes(formattedLabel);
        Location location = event.getLocation();

        for (Pattern p : IGNORE_PATTERNS) {
            Matcher m = p.matcher(formattedLabel);
            if (m.find()) return;
        }

        LabelDetector.handleLabel(label, formattedLabel, location, event.getEntity());
    }

    @SubscribeEvent
    public void labelDetectEntity(LocationEvent.EntityLabelFoundEvent event) {
        if (!MapConfig.Telemetry.INSTANCE.enableLocationDetection) return;

        String name = TextFormatting.getTextWithoutFormattingCodes(event.getLabel());
        Location location = event.getLocation();
        Entity entity = event.getEntity();

        Matcher m = MOB_LABEL.matcher(name);
        if (m.find()) return;

        Matcher m2 = HEALTH_LABEL.matcher(name);
        if (m2.find()) return;

        if (!(entity instanceof EntityVillager)) return;

        LabelDetector.handleNpc(name, event.getLabel(), location);
    }

    @SubscribeEvent
    public void onWorldJoin(WynnWorldEvent.Join e) {
        LabelDetector.onWorldJoin(e);
    }

}
