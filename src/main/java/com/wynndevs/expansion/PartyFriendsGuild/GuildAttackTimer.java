package com.wynndevs.expansion.PartyFriendsGuild;

import com.wynndevs.expansion.ExpReference;
import com.wynndevs.expansion.Misc.Delay;
import com.wynndevs.expansion.Misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.text.DecimalFormat;

public class GuildAttackTimer extends ModGui{
	
	public static boolean AttackShadowTerritory = false;
	public static boolean AttackShadowTimer = false;
	public static boolean AttackColourTimer = false;
	
	public static int Timer = -1;
	public static Delay TimerDelay = new Delay(1.0f,true);
	private static int Colour = 0xff55FFFF;
	
	public GuildAttackTimer(Minecraft mc){
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		FontRenderer font = mc.fontRenderer;
		
		if (!ExpReference.inServer()) Timer = -1;
		
		if (Timer > -1){
			if (GuildAttack.CurrentTerritory.IsInside()){
				Colour = 0xff55FF55;
			}else{
				Colour = 0xff55FFFF;
			}
			
			if (AttackShadowTerritory) {
				this.drawCenteredString(font, GuildAttack.CurrentTerritory.Name + ": " + (!GuildAttack.CurrentTerritory.HasCoords() ? String.valueOf('\u00a7') + "cCoords Not Found" : GuildAttack.CurrentTerritory.GetFormatedCoords()), width / 2, 30, Colour);
			}else{
				this.drawCenteredStringPlain(font, GuildAttack.CurrentTerritory.Name + ": " + (!GuildAttack.CurrentTerritory.HasCoords() ? String.valueOf('\u00a7') + "cCoords Not Found" : GuildAttack.CurrentTerritory.GetFormatedCoords()), width / 2, 30, Colour);
			}
			
			int TimeColour = (!AttackColourTimer ? Colour : (255 << 24) + (((int) Math.floor(Timer > 120 ? 0 : (Timer > 60 ? ((((Timer-60)/60f)*-255)+255) : 255))) << 16) + (((int) Math.floor(Timer >= 60 ? 255 : (((Timer)/60f)*255))) << 8) + ((int) Math.floor(Timer > 240 ? 255 : (Timer >= 120 ? (((Timer - 120)/120f)*255) : 0))));
			
			if (AttackShadowTimer){
				this.drawCenteredString(font, new DecimalFormat("00").format(Math.round((Timer)/60)) + ":" + new DecimalFormat("00").format(Timer%60), width/2, 40, TimeColour);
			}else{
				this.drawCenteredStringPlain(font, new DecimalFormat("00").format(Math.round((Timer)/60)) + ":" + new DecimalFormat("00").format(Timer%60), width/2, 40, TimeColour);
			}
			
			if (TimerDelay.Passed()){
				Timer--;
			}
		}
	}
}
