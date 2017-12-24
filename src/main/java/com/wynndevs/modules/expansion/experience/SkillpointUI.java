package com.wynndevs.modules.expansion.experience;

import com.wynndevs.modules.expansion.misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiScreenEvent;

public class SkillpointUI extends ModGui {
	
	public static boolean ShowSkillpoints = false;
	
	public static int Skillpoints = 0;
	
	public SkillpointUI(Minecraft mc){
		
		if (Skillpoints > 0 && ShowSkillpoints){
			if (Skillpoints > 64){
				mc.player.inventory.getStackInSlot(6).setCount(64);
			}else{
				mc.player.inventory.getStackInSlot(6).setCount(Skillpoints);
			}
			
		}else if (Skillpoints == 0 && mc.player.inventory.getStackInSlot(6).getCount() != 1){
			mc.player.inventory.getStackInSlot(6).setCount(1);
		}else if (!ShowSkillpoints){
			mc.player.inventory.getStackInSlot(6).setCount(1);
		}
		
	}
	
	public static void SkillpointUpdate(GuiScreenEvent.InitGuiEvent.Post event){
		String Name = ((GuiContainer) event.getGui()).inventorySlots.getSlot(0).inventory.getName();
		if (Name.contains("skill points remaining")){
			SkillpointUI.Skillpoints = Integer.parseInt(Name.substring(Name.indexOf(String.valueOf('\u00a7') + 'c') +2, Name.indexOf(String.valueOf('\u00a7') + '4')));
		}
	}
}
