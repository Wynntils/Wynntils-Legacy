package com.wynndevs.modules.expansion.experience;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.Delay;
import com.wynndevs.modules.expansion.misc.ModGui;
import com.wynndevs.modules.expansion.webapi.WebAPI;
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
	
	private static final ResourceLocation spellBook = new ResourceLocation(Reference.MOD_ID, "textures/spells/spell_book.png");
	
	public static boolean showSpellCastingHUD = false;
	
	private static Delay classLookup = new Delay(30.0f,true);
	private static boolean classChosen = false;
	
	private static List<String> buttonRename = new ArrayList<String>();
	private static String buttonName1 = "";
	private static String buttonName2 = "";
	private static String buttonName3 = "";
	private static String buttonName4 = "";
	private static int button1 = 0;
	private static int button2 = 0;
	private static int button3 = 0;
	private static int button4 = 0;
	
	public static void setupButtonRenaming(){
		try {
			ExpReference.consoleOut("Gathering Button Renaming List");
			buttonRename.clear();
			// Retrieve Quest Fixes from file
			BufferedReader DataFile = new BufferedReader(new InputStreamReader(new URL(WebAPI.SpellKeyRenaimingURL).openConnection().getInputStream()));
			String DataLine;
			int ButtonRenameMarker = 0;
			DataLine = DataFile.readLine();
			while (DataLine != null) {
				buttonRename.add(ButtonRenameMarker, DataLine.substring(0, DataLine.indexOf(" - ")));
				buttonRename.add(DataLine.substring(DataLine.indexOf(" - ") +3));
				ButtonRenameMarker++;
				DataLine = DataFile.readLine();
			}
			DataFile.close();
			ExpReference.consoleOut("Button Renamings at: " + (buttonRename.size()/2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SpellCastingUI(Minecraft mc) {
		if (ExpReference.Class > 0) {
			if (showSpellCastingHUD){
				ScaledResolution scaled = new ScaledResolution(mc);
				int width = scaled.getScaledWidth();
				int height = scaled.getScaledHeight();
				FontRenderer font = mc.fontRenderer;
				int Lvl = ModCore.mc().player.experienceLevel;
				
				if (button1 != KeyBindings.SPELL_1.getKeyCode() || button2 != KeyBindings.SPELL_2.getKeyCode() || button3 != KeyBindings.SPELL_3.getKeyCode() || button4 != KeyBindings.SPELL_4.getKeyCode()){
					button1 = KeyBindings.SPELL_1.getKeyCode();
					button2 = KeyBindings.SPELL_2.getKeyCode();
					button3 = KeyBindings.SPELL_3.getKeyCode();
					button4 = KeyBindings.SPELL_4.getKeyCode();
					buttonName1 = KeyBindings.SPELL_1.getDisplayName();
					buttonName2 = KeyBindings.SPELL_2.getDisplayName();
					buttonName3 = KeyBindings.SPELL_3.getDisplayName();
					buttonName4 = KeyBindings.SPELL_4.getDisplayName();
					String ButtonName1Start = "";
					String ButtonName2Start = "";
					String ButtonName3Start = "";
					String ButtonName4Start = "";
					
					if (buttonName1.contains(" + ")){
						ButtonName1Start = buttonName1.substring(0, buttonName1.indexOf(" + "));
						buttonName1 = buttonName1.substring(buttonName1.indexOf(" + ") +3);
					}
					
					if (buttonName2.contains(" + ")){
						ButtonName2Start = buttonName2.substring(0, buttonName2.indexOf(" + "));
						buttonName2 = buttonName2.substring(buttonName2.indexOf(" + ") +3);
					}
					
					if (buttonName3.contains(" + ")){
						ButtonName3Start = buttonName3.substring(0, buttonName3.indexOf(" + "));
						buttonName3 = buttonName3.substring(buttonName3.indexOf(" + ") +3);
					}
					
					if (buttonName4.contains(" + ")){
						ButtonName4Start = buttonName4.substring(0, buttonName4.indexOf(" + "));
						buttonName4 = buttonName4.substring(buttonName4.indexOf(" + ") +3);
					}
					
					if (buttonRename.subList(0, (buttonRename.size()/2)).contains(buttonName1) || buttonRename.subList(0, (buttonRename.size()/2)).contains(buttonName2) || buttonRename.subList(0, (buttonRename.size()/2)).contains(buttonName3) || buttonRename.subList(0, (buttonRename.size()/2)).contains(buttonName4)){
						
						for (int i = 0; i<(buttonRename.size()/2); i++){
							if (buttonRename.get(i).equals(buttonName1)){
								buttonName1 = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(buttonName2)){
								buttonName2 = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(buttonName3)){
								buttonName3 = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(buttonName4)){
								buttonName4 = buttonRename.get(i + (buttonRename.size()/2));
							}
						}
					}
					if (buttonRename.subList(0, (buttonRename.size()/2)).contains(ButtonName1Start) || buttonRename.subList(0, (buttonRename.size()/2)).contains(ButtonName2Start) || buttonRename.subList(0, (buttonRename.size()/2)).contains(ButtonName3Start) || buttonRename.subList(0, (buttonRename.size()/2)).contains(ButtonName4Start)){
						for (int i = 0; i<(buttonRename.size()/2); i++){
							if (buttonRename.get(i).equals(ButtonName1Start)){
								ButtonName1Start = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(ButtonName2Start)){
								ButtonName2Start = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(ButtonName3Start)){
								ButtonName3Start = buttonRename.get(i + (buttonRename.size()/2));
							}
							if (buttonRename.get(i).equals(ButtonName4Start)){
								ButtonName4Start = buttonRename.get(i + (buttonRename.size()/2));
							}
						}
					}
					
					buttonName1 = ButtonName1Start + buttonName1;
					buttonName2 = ButtonName2Start + buttonName2;
					buttonName3 = ButtonName3Start + buttonName3;
					buttonName4 = ButtonName4Start + buttonName4;
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

                    int Position;
					if (i < 2) {
						Position = (width/2) - 155 + (i*32);
					}else{
						Position = (width/2) + 27 + (i*32);
					}
					
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(spellBook);
					SpellCastingUI.drawModalRectWithCustomSizedTexture(Position, height-32, 0, ((((((ExpReference.Class - 1) * 4) + i) * 4) + SpellLvl) * 32), 32, 32, 32, 2048);
					
					if (SpellLvl == 0){
						this.drawCenteredString(font," " + String.valueOf((i*10)+1), Position+14, height-19, 1.0f, Integer.parseInt("f0f0f0",16));
					}else{
						switch (i) {
						case 0 : 
							this.drawCenteredString(font, buttonName1, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_1.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 1 : 
							this.drawCenteredString(font, buttonName2, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_2.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 2 : 
							this.drawCenteredString(font, buttonName3, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_3.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						case 3 : 
							this.drawCenteredString(font, buttonName4, Position+16, height-36, 1.0f, Integer.parseInt((KeyBindings.SPELL_4.isKeyDown() ? "858585" : "ffffff"),16));
							break;
						}
					}
				}
			}
			
			if (ModCore.mc().player.experienceLevel == 0){
				ExpReference.Class = 0;
			}
		}else{
			if (!classChosen && ModCore.mc().player.experienceLevel > 0){
				ExpReference.Class = SpellCasting.getCurrentClass();
				//ExpReference.Loaded = true;
				classChosen = true;
			}else if(classChosen && ModCore.mc().player.experienceLevel == 0){
				classChosen = false;
			}
			if (classLookup.Passed() && classChosen && ModCore.mc().player.experienceLevel > 0){
				ExpReference.Class = SpellCasting.getCurrentClass();
			}
		}
	}
}
