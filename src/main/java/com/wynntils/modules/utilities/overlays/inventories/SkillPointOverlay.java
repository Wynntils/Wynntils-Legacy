/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillPointOverlay implements Listener {

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if(e.getGuiInventory().getSlotUnderMouse() == null || !e.getGuiInventory().getSlotUnderMouse().getHasStack()) return;

        String lore = Utils.getStringLore(e.getGuiInventory().getSlotUnderMouse().getStack());
        if (e.getGuiInventory().getLowerInv().getName().contains("skill points remaining") && lore.contains("points")) {
            lore = lore.replace("§", "");
            String[] tokens = lore.split("[0-9]{1,3} points");
            for (int j = 0; j <= tokens.length - 1; j++) {
                lore = lore.replace(tokens[j], "");
            }
            String[] numbers = lore.split(" ");
            try {
                int count = Integer.parseInt(numbers[0]);
                e.getGuiInventory().getSlotUnderMouse().getStack().setCount(count == 0 ? 1 : count);
            } catch (Exception ex) {
                System.out.print(ex.toString());
            }
        }
    }

}
