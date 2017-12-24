package com.wynndevs.modules.expansion.experience;

import com.wynndevs.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.ArrayList;
import java.util.List;

public class SpellCasting {
	
	private static List<String> SpellList = new ArrayList<String>();
	private static int SpellTimer = 0;
	private static int SpellDelay = 2;
	private static int SpellKey = 0;
	private static KeyBinding LMB = FMLClientHandler.instance().getClient().gameSettings.keyBindAttack;
	private static KeyBinding RMB = FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem;
	
	private static boolean isHunter = false;
	
	public static byte GetCurrentClass(){
		if (ModCore.mc().player.experienceLevel > 0) {
			try {
				ItemStack book = ModCore.mc().player.inventory.getStackInSlot(7);
				if (book.hasDisplayName() && book.getDisplayName().contains("Quest Book")) {
					for (int i=0;i<36;i++) {
						try {
							ItemStack ItemTest = ModCore.mc().player.inventory.getStackInSlot(i);
							NBTTagList Lore = ItemTest.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
							for (int j = 1; j < Lore.tagCount(); j++) {
								String ClassTest = Lore.get(j).toString();
								if (ClassTest.contains("Class Req:") && ClassTest.charAt(2) == 'a'){
									if (ClassTest.contains("Archer")){isHunter=true;}else{isHunter=false;}
									switch (ClassTest.substring(18,ClassTest.lastIndexOf('/'))) {
										case "Archer": return 1;
										case "Warrior": return 2;
										case "Mage": return 3;
										case "Assassin": return 4;
										default: return 0;
									}
								}
							}
						}
						catch (Exception ignored){
						}
					}
				}
				else
				{
					return 0;
				}
			}
			catch (Exception ignored) {
				return 0;
			}
		}
		return 0;
	}
	
	public static void AddSpell(String Spell){
		LMB = FMLClientHandler.instance().getClient().gameSettings.keyBindAttack;
		RMB = FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem;
		SpellList.add(Spell);
	}
	
	public static void CastSpell() {
		if (!SpellList.isEmpty()) {
			if (Boolean.logicalXor(Minecraft.getMinecraft().currentScreen instanceof GuiChat,true)) {
				if (SpellTimer++ == SpellDelay) {
					SpellTimer = 0;
					if (Boolean.logicalXor(SpellList.get(0).charAt(SpellKey) == 'R', isHunter)) {
						KeyBinding.onTick(RMB.getKeyCode());
						if (SpellKey == 2) {SpellList.remove(0); SpellKey=0;}else{SpellKey++;}
					}else{
						KeyBinding.onTick(LMB.getKeyCode());
						if (SpellKey == 2) {SpellList.remove(0); SpellKey=0;}else{SpellKey++;}
					}
				}
			}
		}
	}
}
