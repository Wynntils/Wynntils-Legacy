package com.wynndevs.modules.expansion.misc;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.options.Config;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiScreenEvent;

public class DailyChestReminder {
	
	public static boolean DailyChestReminder = false;
	
	private static Delay ChestTimer = new Delay(5.0f, false);
	private static boolean Reminded = false;
	public static Long Alarm = 0L;
	
	public static void CheckDailyChest(){
		if (ChestTimer.Passed() && Alarm < System.currentTimeMillis() && DailyChestReminder){
			ExpReference.PostToChat(new TextComponentString(String.valueOf('\u00a7') + "aDaily Rewards are available!"));
			Alarm = System.currentTimeMillis() + 1800000;
			Reminded = true;
		}
	}
	
	public static void ChatHandler(String msg){
		if (msg.startsWith("[Daily Rewards:")){
			Alarm = System.currentTimeMillis() + 86400000;
			Config.ResetDailyTimer(Alarm);
			Reminded = false;
		}
	}
	
	public static void DailyChestReseter(GuiScreenEvent.InitGuiEvent.Post event){
		String Name = ((GuiContainer) event.getGui()).inventorySlots.getSlot(0).inventory.getName();
		if (Name.contains("skill points remaining")){
			if (Reminded && DailyChestReminder && !((GuiContainer) event.getGui()).inventorySlots.getSlot(22).getHasStack()){
				Config.ResetDailyTimer(Long.MAX_VALUE);
				Alarm = Long.MAX_VALUE;
				Reminded = false;
			}
		}
	}
}
