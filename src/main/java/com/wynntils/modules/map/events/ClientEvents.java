/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.events;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.utilities.instances.Toast;
import com.wynntils.modules.utilities.overlays.hud.ToastOverlay;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    int lastX, lastY, lastZ = 0;

    @SubscribeEvent()
    public void openChest(PlayerInteractEvent.RightClickBlock e) {
        if(e.getPos() == null) return;
        lastX = e.getPos().getX(); lastY = e.getPos().getY(); lastZ = e.getPos().getZ();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void guiOpen(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if(e.getGuiInventory().getLowerInv().getName().contains("Loot Chest ")) {
            String tier = e.getGuiInventory().getLowerInv().getName().replace("Loot Chest ", "");
            if (!MapConfig.INSTANCE.chestTiers.isTierAboveThis(tier)) return;

            WaypointProfile wp = null;
            switch (tier) {
                case "IV":
                    wp = new WaypointProfile("Loot Chest T4", lastX, lastY, lastZ, CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T4);
                    break;
                case "III":
                    wp = new WaypointProfile("Loot Chest T3", lastX, lastY, lastZ, CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T3);
                    break;
                case "II":
                    wp = new WaypointProfile("Loot Chest T2", lastX, lastY, lastZ, CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T2);
                    break;
                case "I":
                    wp = new WaypointProfile("Loot Chest T1", lastX, lastY, lastZ, CommonColors.WHITE, WaypointProfile.WaypointType.LOOTCHEST_T1);
                    break;
            }
            if(wp != null) {
                if(MapConfig.Waypoints.INSTANCE.waypoints.stream().anyMatch(c -> c.getX() == lastX && c.getY() == lastY && c.getZ() == lastZ)) return;

                MapConfig.Waypoints.INSTANCE.waypoints.add(wp);
                MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());

                ToastOverlay.addToast(new Toast(Toast.ToastType.DISCOVERY, "New Map Entry", "You found a tier " + tier.replace("IV", "4").replace("III", "3") + " chest!"));
            }
        }
    }

}
