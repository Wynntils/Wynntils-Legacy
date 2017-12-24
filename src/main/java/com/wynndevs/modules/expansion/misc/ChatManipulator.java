package com.wynndevs.modules.expansion.misc;


import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.experience.SkillpointUI;
import com.wynndevs.modules.expansion.webapi.ItemDB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class ChatManipulator {
	public static boolean tipDisabler = false;
	
	private static SoundEvent PopOffSound = new SoundEvent(new ResourceLocation("minecraft", "entity.blaze.hurt"));
	
	private static boolean RemoveBlank = false;
	
	public static void ChatHandler(ClientChatReceivedEvent event) {
		String msg = event.getMessage().getUnformattedText();
		
		if (tipDisabler && (msg.startsWith("[Tip]") || msg.startsWith("[Info]") || msg.startsWith("You don't have permission for this area.") || msg.startsWith("You don't have enough mana to do that spell!"))) {
			event.setCanceled(true);
		}
		if (msg.startsWith("Warning! You do not have the Wynncraft Resource Pack.") || msg.startsWith("enable server resource packs")){
			event.setCanceled(true);
			RemoveBlank = true;
		}else if (RemoveBlank && msg.isEmpty()){
			event.setCanceled(true);
			RemoveBlank = false;
		}else if (msg.startsWith("To fix this, add play.wynncraft.com") || msg.startsWith("Still not sure how to do it?")){
			event.setCanceled(true);
		}
		
		if (msg.startsWith("VoxelMap: Unknown world! Please select your current world in the Multiworld screen.")){
			event.setCanceled(true);
		}
		
		if (msg.endsWith("unused skill points! Click with your compass to use them!") || msg.endsWith("unused skill point! Click with your compass to use them!")){
			SkillpointUI.Skillpoints = Integer.parseInt(msg.substring(msg.indexOf("You still have ") +15, msg.indexOf(" unused skill points!")));
			event.setCanceled(true);
		}
		
		if (msg.contains(" requires your ") && msg.contains(" skill to be at least ")){
			String Item = msg.substring(0, msg.indexOf(" requires your "));
			for (int i=0;i<ItemDB.ItemDB.size();i++){
				if (ItemDB.ItemDB.get(i).Name.equals(Item)){
					if (ItemDB.ItemDB.get(i).GetCategory() != 0){
						ModCore.mc().player.playSound(PopOffSound, 1.0f, 1.0f);
						Announcments.AnnounceMessage = String.valueOf('\u00a7') + "cOne or more of your Items have Popped off";
						Announcments.AnnounceTime.Reset();
					}
					break;
				}
			}
		}
	}
}
