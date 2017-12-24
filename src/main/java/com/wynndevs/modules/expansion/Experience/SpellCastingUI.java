package com.wynndevs.modules.expansion.Experience;

import com.wynndevs.ModCore;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.Misc.Delay;
import com.wynndevs.modules.expansion.Misc.ModGui;
import com.wynndevs.modules.expansion.WebAPI.WebAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpellCastingUI extends ModGui{
	
	private static final ResourceLocation SpellBook = new ResourceLocation(ExpReference.MOD_ID, "textures/spells/spell_book.png");
	
	public static boolean ShowSpellCastingHUD = false;
	
	private static Delay ClassLookup = new Delay(30.0f,true);
	private static boolean ClassChosen = false;
	
	private static List<String> ButtonRename = new ArrayList<String>();
	private static String ButtonName1 = "";
	private static String ButtonName2 = "";
	private static String ButtonName3 = "";
	private static String ButtonName4 = "";
	private static int Button1 = 0;
	private static int Button2 = 0;
	private static int Button3 = 0;
	private static int Button4 = 0;
	
	public static void SetupButtonRenaming(){
		try {
			ExpReference.ConsoleOut("Gathering Button Renaming List");
			ButtonRename.clear();
			// Retrieve Quest Fixes from file
			BufferedReader DataFile = new BufferedReader(new InputStreamReader(new URL(WebAPI.SpellKeyRenaimingURL).openConnection().getInputStream()));
			String DataLine;
			int ButtonRenameMarker = 0;
			DataLine = DataFile.readLine();
			while (DataLine != null) {
				ButtonRename.add(ButtonRenameMarker, DataLine.substring(0, DataLine.indexOf(" - ")));
				ButtonRename.add(DataLine.substring(DataLine.indexOf(" - ") +3));
				ButtonRenameMarker++;
				DataLine = DataFile.readLine();
			}
			DataFile.close();
			ExpReference.ConsoleOut("Button Renamings at: " + (ButtonRename.size()/2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SpellCastingUI(Minecraft mc) {
		if (ExpReference.Class > 0) {
			if (ShowSpellCastingHUD){
				ScaledResolution scaled = new ScaledResolution(mc);
				int width = scaled.getScaledWidth();
				int height = scaled.getScaledHeight();
				FontRenderer font = mc.fontRenderer;
				int Lvl = ModCore.mc().player.experienceLevel;
				
				if (Button1 != KeyBindings.SPELL_1.getKeyCode() || Button2 != KeyBindings.SPELL_2.getKeyCode() || Button3 != KeyBindings.SPELL_3.getKeyCode() || Button4 != KeyBindings.SPELL_4.getKeyCode()){
					Button1 = KeyBindings.SPELL_1.getKeyCode();
					Button2 = KeyBindings.SPELL_2.getKeyCode();
					Button3 = KeyBindings.SPELL_3.getKeyCode();
					Button4 = KeyBindings.SPELL_4.getKeyCode();
					ButtonName1 = KeyBindings.SPELL_1.getDisplayName();
					ButtonName2 = KeyBindings.SPELL_2.getDisplayName();
					ButtonName3 = KeyBindings.SPELL_3.getDisplayName();
					ButtonName4 = KeyBindings.SPELL_4.getDisplayName();
					String ButtonName1Start = "";
					String ButtonName2Start = "";
					String ButtonName3Start = "";
					String ButtonName4Start = "";
					
					if (ButtonName1.contains(" + ")){
						ButtonName1Start = ButtonName1.substring(0, ButtonName1.indexOf(" + "));
						ButtonName1 = ButtonName1.substring(ButtonName1.indexOf(" + ") +3);
					}
					
					if (ButtonName2.contains(" + ")){
						ButtonName2Start = ButtonName2.substring(0, ButtonName2.indexOf(" + "));
						ButtonName2 = ButtonName2.substring(ButtonName2.indexOf(" + ") +3);
					}
					
					if (ButtonName3.contains(" + ")){
						ButtonName3Start = ButtonName3.substring(0, ButtonName3.indexOf(" + "));
						ButtonName3 = ButtonName3.substring(ButtonName3.indexOf(" + ") +3);
					}
					
					if (ButtonName4.contains(" + ")){
						ButtonName4Start = ButtonName4.substring(0, ButtonName4.indexOf(" + "));
						ButtonName4 = ButtonName4.substring(ButtonName4.indexOf(" + ") +3);
					}
					
					if (ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName1) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName2) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName3) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName4)){
						
						for (int i=0;i<(ButtonRename.size()/2);i++){
							if (ButtonRename.get(i).equals(ButtonName1)){
								ButtonName1 = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName2)){
								ButtonName2 = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName3)){
								ButtonName3 = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName4)){
								ButtonName4 = ButtonRename.get(i + (ButtonRename.size()/2));
							}
						}
					}
					if (ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName1Start) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName2Start) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName3Start) || ButtonRename.subList(0, (ButtonRename.size()/2)).contains(ButtonName4Start)){
						for (int i=0;i<(ButtonRename.size()/2);i++){
							if (ButtonRename.get(i).equals(ButtonName1Start)){
								ButtonName1Start = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName2Start)){
								ButtonName2Start = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName3Start)){
								ButtonName3Start = ButtonRename.get(i + (ButtonRename.size()/2));
							}
							if (ButtonRename.get(i).equals(ButtonName4Start)){
								ButtonName4Start = ButtonRename.get(i + (ButtonRename.size()/2));
							}
						}
					}
					
					ButtonName1 = ButtonName1Start + ButtonName1;
					ButtonName2 = ButtonName2Start + ButtonName2;
					ButtonName3 = ButtonName3Start + ButtonName3;
					ButtonName4 = ButtonName4Start + ButtonName4;
				}
				
				for (int i=0;i<4;i++){
					int SpellLvl = 0;
					if ((i*10) < Lvl){
						SpellLvl++;
					}
					if ((i*10)+10 < Lvl){
						SpellLvl++;
					}
					if ((i*10)+30 < Lvl){
						SpellLvl++;
					}
					
					int Position = 0;
					if (i < 2) {
						Position = (width/2) - 155 + (i*32);
					}else{
						Position = (width/2) + 27 + (i*32);
					}
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(SpellBook);
					SpellCastingUI.drawModalRectWithCustomSizedTexture(Position, height-32, 0, ((((((ExpReference.Class - 1) * 4) + i) * 4) + SpellLvl) * 32), 32, 32, 32, 2048);
					
					if (SpellLvl == 0){
						this.drawCenteredString(font," " + String.valueOf((i*10)+1), Position+14, height-19, 1.0f, Integer.parseInt("f0f0f0",16));
					}else{
						switch (i) {
						case 0 : 
							this.drawCenteredString(font, ButtonName1, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_1.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 1 : 
							this.drawCenteredString(font, ButtonName2, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_2.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 2 : 
							this.drawCenteredString(font, ButtonName3, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_3.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 3 : 
							this.drawCenteredString(font, ButtonName4, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_4.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						}
					}
				}
			}
			
			if (ModCore.mc().player.experienceLevel == 0){
				ExpReference.Class = 0;
			}
		}else{
			if (!ClassChosen && ModCore.mc().player.experienceLevel > 0){
				ExpReference.Class = SpellCasting.GetCurrentClass();
				//ExpReference.Loaded = true;
				ClassChosen = true;
			}else if(ClassChosen && ModCore.mc().player.experienceLevel == 0){
				ClassChosen = false;
			}
			if (ClassLookup.Passed() && ClassChosen && ModCore.mc().player.experienceLevel > 0){
				ExpReference.Class = SpellCasting.GetCurrentClass();
			}
		}
	}
}
