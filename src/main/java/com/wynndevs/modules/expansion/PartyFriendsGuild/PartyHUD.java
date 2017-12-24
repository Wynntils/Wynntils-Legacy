package com.wynndevs.modules.expansion.PartyFriendsGuild;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.Misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PartyHUD extends ModGui {
	
	public static boolean ShowHealthHUD = false;
	public static boolean ShowOutOfRange = false;
	public static boolean AllignRight = false;
	public static boolean ColourHealthBar = false;
	public static boolean ColourName = false;
	public static boolean ShowPercentage = false;
	public static boolean UseProportionalDisplay = false;
	
	static List<String> PartyList = new ArrayList<String>();
	static List<int[]> PartyValues = new ArrayList<int[]>();
	private static int Offset = 5;
	private static int HealthScale = 2;
	
	public PartyHUD(Minecraft mc) {
		if (ShowHealthHUD) {
			ScaledResolution scaled = new ScaledResolution(mc);
			int width = scaled.getScaledWidth();
			//int height = scaled.getScaledHeight();
			FontRenderer font = mc.fontRenderer;
			
			for (int i=0;i<PartyList.size();i++) {
				//float HealthPercentage = (float) ((Math.ceil(PartyList.get(i).getHealth()) / Math.ceil(PartyList.get(i).getMaxHealth())) * 100);
				//int ManaPercentage = (int) Math.ceil((Math.ceil(PartyList.get(i).getFoodStats().getFoodLevel()) / 20) * 100);
				//int ManaPercentage2 = (int) Math.ceil((Math.ceil(PartyList.get(i).getFoodStats().getSaturationLevel()) / 20) * 100);
				//int LevelPercentage = (int) Math.ceil((Math.ceil(PartyList.get(i).experienceLevel) / 100) * 100);
				//int XPPercentage = (int) Math.ceil((Math.ceil(PartyList.get(i).experience) / 1) * 100);
				int Allign[] = {Offset, Offset + PartyValues.get(i)[3], Offset + PartyValues.get(i)[4]};
				if (AllignRight) {
					Allign = new int[] {width - Offset - PartyValues.get(i)[3], width -Offset, width - Offset - PartyValues.get(i)[4]};
				}
				// Draw Health
				drawGradientRect(Allign[0], (i*15) +Offset, Allign[1], (i*15) +10 +Offset, 0xff999999, 0xff4F4F4F);
				drawGradientRect((AllignRight ? Allign[2] : Allign[0]), (i*15) +Offset, (AllignRight ? Allign[1] : Allign[2]), (i*15) + 10 +Offset, (ColourHealthBar ? PartyValues.get(i)[1] : 0xffEA1800) , (ColourHealthBar ? PartyValues.get(i)[2] : 0xff850E00));//0xffEA1800, 0xff850E00);
				
				// Draw Mana
				//drawRect(width -100, (i*15) +15, width, (i*15) +20, 0xff00757E);
				//drawRect(width -ManaPercentage, (i*15) +15, width, (i*15) +20, 0xff00D8EA);
				
				// Draw Username
				if (UseProportionalDisplay) {
					this.drawString(font, PartyList.get(i), (AllignRight ? Allign[1] -2 - ExpReference.GetMsgLength(PartyList.get(i), 1.0f) : Allign[0] +2), (i*15) +1 +Offset, (ColourName ? PartyValues.get(i)[1] : 0xffFFFFFF));//0xffEAE000);
				}else{
					this.drawCenteredString(font, PartyList.get(i), Allign[0] + (int) Math.floor((Allign[1] - Allign[0])/2), (i*15) +1 +Offset, (ColourName ? PartyValues.get(i)[1] : 0xffFFFFFF));//0xffEAE000);
				}
				
				if (PartyValues.get(i)[0] == -1) {
					int OffsetTag = (PartyValues.get(i)[3] > ExpReference.GetMsgLength(PartyList.get(i), 1.0f) +2 ? PartyValues.get(i)[3] : ExpReference.GetMsgLength(PartyList.get(i), 1.0f) +2);
					this.drawString(font, "OOR", (AllignRight ? Allign[1] - 2 - OffsetTag - ExpReference.GetMsgLength("OOR", 1.0f) : Allign[0] + 2 + OffsetTag), (i*15) +1 +Offset, PartyValues.get(i)[1]);
				}else if (ShowPercentage) {
					int OffsetTag = (PartyValues.get(i)[3] > ExpReference.GetMsgLength(PartyList.get(i), 1.0f) +2 ? PartyValues.get(i)[3] : ExpReference.GetMsgLength(PartyList.get(i), 1.0f) +2);
					this.drawString(font, new DecimalFormat("00").format(PartyValues.get(i)[0]) + "%", (AllignRight ? Allign[1] - 2 - OffsetTag - ExpReference.GetMsgLength(new DecimalFormat("00").format(PartyValues.get(i)[0]) + "%", 1.0f) : Allign[0] + 2 + OffsetTag), (i*15) +1 +Offset, PartyValues.get(i)[1]);
				}
			}
		}
	}
	
	public static void CapturePlayerEntities(Minecraft mc) {
		if (ShowOutOfRange) {
			for (int i=0;i<PartyList.size();i++) {
				if (!PlayerGlow.PartyList.contains(PartyList.get(i))) {
					PartyList.remove(i);
					PartyValues.remove(i);
					i--;
				}
			}
			for (String PartyPlayer : PlayerGlow.PartyList) {
				boolean FoundPlayer = false;
				for (EntityPlayer Player : mc.world.playerEntities) {
					if (mc.world.playerEntities == null)
						break;
					if (Player.getName().equals(PartyPlayer)) {
						FoundPlayer = true;
						
						int PartyIndex = -1;
						for (int i=0;i<PartyList.size();i++) {
							if (PartyList.get(i).equals(PartyPlayer)) {
								PartyIndex = i;
								break;
							}
						}
						
						if (PartyIndex == -1) {
							PartyList.add(Player.getName());
							
							float HealthPercentage = (float) ((Math.ceil(Player.getHealth()) / Math.ceil(Player.getMaxHealth())) * 100);
							if (HealthPercentage > 100) HealthPercentage = 100;
							int Colour1 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-255)+255) : 255) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 255 : (((HealthPercentage)/50)*255)) << 8) + (0);
							int Colour2 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-127)+127) : 127) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 127 : (((HealthPercentage)/50)*127)) << 8) + (0);
							PartyValues.add(new int[] {(int) Math.floor(HealthPercentage), Colour1, Colour2, (int) Math.floor((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale), Math.round((float) ((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale) * (HealthPercentage / 100f))});
						}else{
							float HealthPercentage = (float) ((Math.ceil(Player.getHealth()) / Math.ceil(Player.getMaxHealth())) * 100);
							if (HealthPercentage > 100) HealthPercentage = 100;
							int Colour1 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-255)+255) : 255) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 255 : (((HealthPercentage)/50)*255)) << 8) + (0);
							int Colour2 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-127)+127) : 127) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 127 : (((HealthPercentage)/50)*127)) << 8) + (0);
							PartyValues.set(PartyIndex, new int[] {(int) Math.floor(HealthPercentage), Colour1, Colour2, (int) Math.floor((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale), Math.round((float) ((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale) * (HealthPercentage / 100f))});
						}
					}
				}
				if (!FoundPlayer) {
					int PartyIndex = -1;
					for (int i=0;i<PartyList.size();i++) {
						if (PartyList.get(i).equals(PartyPlayer)) {
							PartyIndex = i;
							break;
						}
					}
					if (PartyIndex == -1) {
						PartyList.add(PartyPlayer);
						
						int Colour1 = (255 << 24) + (0 << 16) + (0 << 8) + (0);
						int Colour2 = (255 << 24) + (0 << 16) + (0 << 8) + (0);
						PartyValues.add(new int[] {-1, Colour1, Colour2, (int) Math.floor(50 * HealthScale), 0});
					}else{
						int[] OldPartyPlayer = PartyValues.get(PartyIndex);
						OldPartyPlayer[0] = -1;
						OldPartyPlayer[1] = (255 << 24) + (195 << 16) + (195 << 8) + (195);
						OldPartyPlayer[4] = 0;
						PartyValues.set(PartyIndex, OldPartyPlayer);
					}
				}
			}
		}else{
			PartyList.clear();
			PartyValues.clear();
			for (EntityPlayer Player : mc.world.playerEntities) {
				if (mc.world.playerEntities == null)
					break;
				if (PlayerGlow.PartyList.contains(Player.getName())) {
					PartyList.add(Player.getName());
					
					float HealthPercentage = (float) ((Math.ceil(Player.getHealth()) / Math.ceil(Player.getMaxHealth())) * 100);
					if (HealthPercentage > 100) HealthPercentage = 100;
					int Colour1 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-255)+255) : 255) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 255 : (((HealthPercentage)/50)*255)) << 8) + (0);
					int Colour2 = (255 << 24) + ((int) Math.floor(HealthPercentage > 50 ? ((((HealthPercentage-50)/50)*-127)+127) : 127) << 16) + ((int) Math.floor(HealthPercentage >= 50 ? 127 : (((HealthPercentage)/50)*127)) << 8) + (0);
					PartyValues.add(new int[] {(int) Math.floor(HealthPercentage), Colour1, Colour2, (int) Math.floor((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale), Math.round((float) ((UseProportionalDisplay ? Player.getMaxHealth() : 50) * HealthScale) * (HealthPercentage / 100f))});
				}
			}
		}
	}
}
