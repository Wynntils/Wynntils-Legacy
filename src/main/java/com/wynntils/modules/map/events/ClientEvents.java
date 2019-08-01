/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.events;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.managers.BeaconManager;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    Location lastLocation = null;

    @SubscribeEvent
    public void renderBeacon(RenderWorldLastEvent e) {
        if (!MapConfig.INSTANCE.showCompassBeam || CompassManager.getCompassLocation() == null) return;

        Location compass = CompassManager.getCompassLocation();
        BeaconManager.drawBeam(new Location(compass.getX(), compass.getY(), compass.getZ()), CommonColors.RED);
    }

    @SubscribeEvent
    public void openChest(PlayerInteractEvent.RightClickBlock e) {
        if(e.getPos() == null) return;
        lastLocation = new Location(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOpen(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if(lastLocation == null) return;

        if(e.getGuiInventory().getLowerInv().getName().contains("Loot Chest ")) {
            String tier = e.getGuiInventory().getLowerInv().getName().replace("Loot Chest ", "");
            if (!MapConfig.Waypoints.INSTANCE.chestTiers.isTierAboveThis(tier)) return;

            WaypointProfile wp = null;
            switch (tier) {
                case "IV":
                    wp = new WaypointProfile("Loot Chest T4", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T4, -1000);
                    break;
                case "III":
                    wp = new WaypointProfile("Loot Chest T3", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T3, -1000);
                    break;
                case "II":
                    wp = new WaypointProfile("Loot Chest T2", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T2, -1000);
                    break;
                case "I":
                    wp = new WaypointProfile("Loot Chest T1", lastLocation.getX(), lastLocation.getY(), lastLocation.getZ(), CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T1, -1000);
                    break;
            }
            if(wp != null) {
                if(MapConfig.Waypoints.INSTANCE.waypoints.stream().anyMatch(c -> c.getX() == lastLocation.getX() && c.getY() == lastLocation.getY() && c.getZ() == lastLocation.getZ())) return;

                MapConfig.Waypoints.INSTANCE.waypoints.add(wp);
                MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());

                ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "New Map Entry", "You found a tier " + tier.replace("IV", "4").replace("III", "3") + " chest!"));
            }
        }
    }

}
