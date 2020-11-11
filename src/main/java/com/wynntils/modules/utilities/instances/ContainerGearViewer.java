package com.wynntils.modules.utilities.instances;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerGearViewer extends Container {
    
    private final IInventory inventory;
    
    public ContainerGearViewer(IInventory inventory, EntityPlayer player) {
        this.inventory = inventory;
        inventory.openInventory(player);
        
        // add armor slots
        for (int i = 0; i < 4; i++) {
            int index = 3 - i;
            this.addSlotToContainer(new Slot(inventory, i, 8, 8 + i * 18) {
                    @Nullable
                    @SideOnly(Side.CLIENT)
                    public String getSlotTexture() {
                        return ItemArmor.EMPTY_SLOT_NAMES[index];
                    }
            });
        }
        
        // add weapon slot
        this.addSlotToContainer(new Slot(inventory, 4, 77, 62) {
            @Nullable
            @SideOnly(Side.CLIENT)
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.inventory.closeInventory(playerIn);
    }

}
