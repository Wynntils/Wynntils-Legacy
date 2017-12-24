package com.wynndevs.modules.expansion.misc;

import com.wynndevs.ModCore;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.options.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import org.lwjgl.input.Keyboard;

public class StickyItems {
	
	//public static List<Integer> StickySlots = new ArrayList<Integer>();
	public static boolean UseClassLocks = true;
	
	public static boolean[][] StickySlots = new boolean[5][41];
	private static boolean Press = true;
	private static boolean InBank = false;
	
	@SubscribeEvent
	public void InputEventKeyInputEvent(GuiScreenEvent.KeyboardInputEvent.Pre event){
		if (ExpReference.inGame()){
			if ((event.getGui() instanceof GuiContainer) == false || event.getGui().mc == null || event.getGui().mc.player == null){
				return;
			}
			Slot InvSlot = ((GuiContainer) event.getGui()).getSlotUnderMouse();
			
			if (InvSlot != null && InvSlot.inventory.getName().equals("container.inventory")){
				if (Keyboard.getEventKey() == KeyBindings.TOGGLE_LOCK.getKeyCode() && InvSlot.getHasStack() && (InvSlot.getSlotIndex() < 6 || InvSlot.getSlotIndex() > 8) && InvSlot.getStack().getItem() != Item.getByNameOrId("minecraft:snow_layer")){
					if (!InBank) {
						if (Press){
							if (CheckLock(InvSlot.getSlotIndex())){
								SetLock(InvSlot.getSlotIndex(), false);
								if (ItemName[InvSlot.getSlotIndex()]) {
									InvSlot.getStack().setStackDisplayName(GetRawName(InvSlot.getStack().getDisplayName()) + String.valueOf('\u00a7') + "a UNLOCKED");
									LockedDisplay.Reset();
									ItemsLocked = true;
									Config.setStickyItemLock(ExpReference.Class, InvSlot.getSlotIndex(), false);
								}else{
									ItemName[InvSlot.getSlotIndex()] = true;
									InvSlot.getStack().setStackDisplayName(GetRawName(InvSlot.getStack().getDisplayName()) + String.valueOf('\u00a7') + "a UNLOCKED");
									LockedDisplay.Reset();
									ItemsLocked = true;
									Config.setStickyItemLock(ExpReference.Class, InvSlot.getSlotIndex(), false);
								}
							}else{
								if (ItemName[InvSlot.getSlotIndex()]) {
									InvSlot.getStack().setStackDisplayName(GetRawName(InvSlot.getStack().getDisplayName()) + String.valueOf('\u00a7') + "c LOCKED");
									LockedDisplay.Reset();
									ItemsLocked = true;
									Config.setStickyItemLock(ExpReference.Class, InvSlot.getSlotIndex(), true);
								}else{
									ItemName[InvSlot.getSlotIndex()] = true;
									InvSlot.getStack().setStackDisplayName(GetRawName(InvSlot.getStack().getDisplayName()) + String.valueOf('\u00a7') + "c LOCKED");
									LockedDisplay.Reset();
									ItemsLocked = true;
									Config.setStickyItemLock(ExpReference.Class, InvSlot.getSlotIndex(), true);
								}
								SetLock(InvSlot.getSlotIndex(), true);
							}
							Press = false;
						}else{
							Press = true;
						}
					}
				}
				if (Keyboard.getEventKey() == Minecraft.getMinecraft().gameSettings.keyBindDrop.getKeyCode() && CheckLock(InvSlot.getSlotIndex())){
					if (!InBank) event.setCanceled(true);
				}
			}
		}
	}
	
	private static boolean[] ItemName = new boolean[41];
	private static Delay LockedDisplay = new Delay(2.0f, true);
	private static boolean ItemsLocked = false;
	private static int LastSlot = -1;
	@SubscribeEvent
	public void onMouseInputEventPre(GuiScreenEvent.MouseInputEvent.Pre event){
		if (ExpReference.inGame()){
			if ((event.getGui() instanceof GuiContainer) == false || event.getGui().mc == null || event.getGui().mc.player == null){
				return;
			}
			Slot InvSlot = ((GuiContainer) event.getGui()).getSlotUnderMouse();
			
			if (InvSlot != null && InvSlot.inventory.getName().equals("container.inventory") && CheckLock(InvSlot.getSlotIndex()) && InvSlot.getHasStack() && InvSlot.getStack().getItem() != Item.getByNameOrId("minecraft:snow_layer")){
				if (!InBank) {
					if (LastSlot != InvSlot.getSlotIndex() && (InvSlot.getSlotIndex() < 6 || InvSlot.getSlotIndex() > 8)) {
						ItemName[InvSlot.getSlotIndex()] = true;
						InvSlot.getStack().setStackDisplayName(GetRawName(InvSlot.getStack().getDisplayName()) + String.valueOf('\u00a7') + "c LOCKED");
						ItemsLocked = true;
						LockedDisplay.Reset();
					}
					event.setCanceled(true);
				}
			}
			if (InvSlot != null && InvSlot.inventory.getName().equals("container.inventory") && LastSlot != InvSlot.getSlotIndex()) {
				LastSlot = InvSlot.getSlotIndex();
			}else if(InvSlot == null) {
				LastSlot = -1;
			}
		}
	}
	
	public static int SavedDropKeyCode = 0;
	public static int DropKeyCode = 0;
	@SubscribeEvent
	public void eventHandler(MouseInputEvent event){
		if (ExpReference.inServer()){
			if (DropKeyCode == 0 && CheckLock(ModCore.mc().player.inventory.currentItem) && ModCore.mc().inGameHasFocus){
				DropKeyCode = ModCore.mc().gameSettings.keyBindDrop.getKeyCode();
				ModCore.mc().gameSettings.keyBindDrop.setKeyCode(0);
				if (DropKeyCode != SavedDropKeyCode){
					SavedDropKeyCode = DropKeyCode;
					Config.setStickyItemDropKey(SavedDropKeyCode);
				}
			}else if (DropKeyCode != 0 && (!CheckLock(ModCore.mc().player.inventory.currentItem) || !ModCore.mc().inGameHasFocus)){
				ModCore.mc().gameSettings.keyBindDrop.setKeyCode(DropKeyCode);
				DropKeyCode = 0;
			}
			
			if (LockedDisplay.Passed() && ItemsLocked) {
				for (int i=0;i<ItemName.length;i++){
					if (ItemName[i]) {
						String Name = GetRawName(ModCore.mc().player.inventory.getStackInSlot(i).getDisplayName());
						ModCore.mc().player.inventory.getStackInSlot(i).setStackDisplayName(Name);
						ItemName[i] = false;
					}
				}
				ItemsLocked = false;
			}
		}else if (DropKeyCode != 0){
			ModCore.mc().gameSettings.keyBindDrop.setKeyCode(DropKeyCode);
			DropKeyCode = 0;
		}
	}
	
	public static void BankCheck(GuiScreenEvent.InitGuiEvent.Post event) {
		String Name = ((GuiContainer) event.getGui()).inventorySlots.getSlot(0).inventory.getName();
		if (Name.endsWith(ModCore.mc().player.getName() + "'s" + String.valueOf('\u00a7') + "0 Bank")) {
			InBank = true;
		}else{
			InBank = false;
		}
	}
	
	private static String GetRawName(String DisplayName) {
		if (DisplayName.endsWith("LOCKED")) {
			DisplayName = DisplayName.replace(String.valueOf('\u00a7') + "c LOCKED", "");
			DisplayName = DisplayName.replace(String.valueOf('\u00a7') + "a UNLOCKED", "");
		}
		if (!DisplayName.startsWith(String.valueOf('\u00a7'))) {
			DisplayName = String.valueOf('\u00a7') + "r" + DisplayName;
		}
		return DisplayName;
	}
	
	private static boolean CheckLock(int Slot) {
		return (ExpReference.Loaded ? StickySlots[ExpReference.Class][Slot] : true);
	}
	
	private static void SetLock(int Slot, boolean Lock) {
		if (ExpReference.Loaded) {
			StickySlots[(UseClassLocks ? ExpReference.Class : 0)][Slot] = Lock;
		}
	}
}
