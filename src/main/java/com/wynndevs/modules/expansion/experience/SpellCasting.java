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
	
	private static List<String> spellList = new ArrayList<String>();
	private static int spellTimer = 0;
	private static int spellDelay = 2;
	private static int spellKey = 0;
	private static KeyBinding LMB = FMLClientHandler.instance().getClient().gameSettings.keyBindAttack;
	private static KeyBinding RMB = FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem;
	
	private static boolean isHunter = false;
	
	public static byte getCurrentClass(){
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
                                    isHunter = ClassTest.contains("Archer");
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
	
	public static void addSpell(String Spell){
		LMB = FMLClientHandler.instance().getClient().gameSettings.keyBindAttack;
		RMB = FMLClientHandler.instance().getClient().gameSettings.keyBindUseItem;
		spellList.add(Spell);
	}
	
	public static void castSpell() {
		if (!spellList.isEmpty()) {
			if (Boolean.logicalXor(Minecraft.getMinecraft().currentScreen instanceof GuiChat,true)) {
				if (spellTimer++ == spellDelay) {
					spellTimer = 0;
					if (Boolean.logicalXor(spellList.get(0).charAt(spellKey) == 'R', isHunter)) {
						KeyBinding.onTick(RMB.getKeyCode());
						if (spellKey == 2) {
							spellList.remove(0); spellKey =0;}else{
							spellKey++;}
					}else{
						KeyBinding.onTick(LMB.getKeyCode());
						if (spellKey == 2) {
							spellList.remove(0); spellKey =0;}else{
							spellKey++;}
					}
				}
			}
		}
	}
}
