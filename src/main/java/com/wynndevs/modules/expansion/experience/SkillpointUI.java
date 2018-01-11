package com.wynndevs.modules.expansion.experience;

import com.wynndevs.modules.expansion.misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.GuiScreenEvent;

public class SkillpointUI extends ModGui {
	
	public static boolean showSkillpoints = false;
	
	public static int skillpoints = 0;
	
	public SkillpointUI(Minecraft mc){
		
		if (skillpoints > 0 && showSkillpoints){
			if (skillpoints > 64){
				mc.player.inventory.getStackInSlot(6).setCount(64);
			}else{
				mc.player.inventory.getStackInSlot(6).setCount(skillpoints);
			}
			
		}else if (skillpoints == 0 && mc.player.inventory.getStackInSlot(6).getCount() != 1){
			mc.player.inventory.getStackInSlot(6).setCount(1);
		}else if (!showSkillpoints){
			mc.player.inventory.getStackInSlot(6).setCount(1);
		}
		
	}
	
	public static void skillpointUpdate(GuiScreenEvent.InitGuiEvent.Post event){
		String Name = ((GuiContainer) event.getGui()).inventorySlots.getSlot(0).inventory.getName();
		if (Name.contains("skill points remaining")){
			SkillpointUI.skillpoints = Integer.parseInt(Name.substring(Name.indexOf(String.valueOf('\u00a7') + 'c') +2, Name.indexOf(String.valueOf('\u00a7') + '4')));
		}
	}
}
