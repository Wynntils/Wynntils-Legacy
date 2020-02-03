/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.managers.BeaconManager;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents implements Listener {

    BlockPos lastLocation = null;

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent e) {
        LootRunManager.renderActivePaths();

        if (!MapConfig.INSTANCE.showCompassBeam || CompassManager.getCompassLocation() == null) return;

        Location compass = CompassManager.getCompassLocation();
        BeaconManager.drawBeam(new Location(compass.getX(), compass.getY(), compass.getZ()), MapConfig.INSTANCE.compassBeaconColor);
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

        LootRunManager.addChest(lastLocation); // add chest to the current lootrun recording

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

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        LootRunManager.recordMovement(player.posX, player.posY, player.posZ);
    }

}
