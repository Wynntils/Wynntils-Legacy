/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.events;

import cf.wynntils.ModCore;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.ReflectionFields;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.core.overlays.inventories.ChestReplacer;
import cf.wynntils.modules.core.overlays.inventories.HorseReplacer;
import cf.wynntils.modules.core.overlays.inventories.IngameMenuReplacer;
import cf.wynntils.modules.core.overlays.inventories.InventoryReplacer;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onGuiOpened(GuiOpenEvent e) {
        if(e.getGui() instanceof GuiInventory) {
            if(e.getGui() instanceof InventoryReplacer) return;

            e.setGui(new InventoryReplacer(ModCore.mc().player));
            return;
        }
        if(e.getGui() instanceof GuiChest) {
            if(e.getGui() instanceof ChestReplacer) return;

            e.setGui(new ChestReplacer(ModCore.mc().player.inventory, (IInventory) ReflectionFields.GuiChest_lowerChestInventory.getValue(e.getGui())));
            return;
        }
        if(e.getGui() instanceof GuiScreenHorseInventory) {
            if(e.getGui() instanceof HorseReplacer) return;

            e.setGui(new HorseReplacer(ModCore.mc().player.inventory, (IInventory) ReflectionFields.GuiScreenHorseInventory_horseInventory.getValue(e.getGui()), (AbstractHorse) ReflectionFields.GuiScreenHorseInventory_horseEntity.getValue(e.getGui())));
        }
        if(e.getGui() instanceof GuiIngameMenu) {
            if(e.getGui() instanceof IngameMenuReplacer) return;

            e.setGui(new IngameMenuReplacer());
        }
    }

    @SubscribeEvent
    public void changeClass(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if(e.getGuiInventory().getLowerInv().getName().contains("Select a Class")) {
            if(e.getMouseButton() == 0 && e.getSlotIn() != null &&  e.getSlotIn().getHasStack() && e.getSlotIn().getStack().hasDisplayName() && e.getSlotIn().getStack().getDisplayName().contains("[>] Select")) {
                PlayerInfo.getPlayerInfo().setClassId(e.getSlotId());

                String classS = Utils.getLore(e.getSlotIn().getStack()).get(1).split(": §f")[1];

                ClassType selectedClass = ClassType.NONE;

                try{
                    selectedClass = ClassType.valueOf(classS.toUpperCase());
                }catch (Exception ex) {
                    switch(classS) {
                        case "Hunter":
                            selectedClass = ClassType.ARCHER;
                            break;
                        case "Knight":
                            selectedClass = ClassType.WARRIOR;
                            break;
                        case "Dark Mage":
                            selectedClass = ClassType.MAGE;
                            break;
                        case "Ninja":
                            selectedClass = ClassType.ASSASSIN;
                    }
                }

                PlayerInfo.getPlayerInfo().updatePlayerClass(selectedClass);
            }
        }
    }

}
