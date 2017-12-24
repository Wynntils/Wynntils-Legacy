package com.wynndevs.expansion.Experience;


import com.wynndevs.ModCore;
import com.wynndevs.expansion.ExpReference;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Experience {
	private static int[] WynncraftXPLevels = new int[101];

	public static void PreInit() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(ModCore.mc().getResourceManager().getResource(new ResourceLocation(ExpReference.MOD_ID, "misc/wynncraftxplevels")).getInputStream()));
			String strLine;
			for(int j = 0; j < 101; j++){
				strLine = br.readLine();
				WynncraftXPLevels[j] = Integer.parseInt(strLine);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static int getXpNeededForWynncraftLevel(int level) {
		return ((level > 0 && level < 102) ? (WynncraftXPLevels[level - 1]) : (-1));
	}
	
	public static float getCurrentWynncraftXp(){
		return (getCurrentWynncraftMaxXp() * ModCore.mc().player.experience);
	}
	
	public static int getCurrentWynncraftMaxXp(){
		return getXpNeededForWynncraftLevel(ModCore.mc().player.experienceLevel);
	}
	
	public static String getPercentage(){
		return new DecimalFormat("##.##").format(ModCore.mc().player.experience * 100);
	}
	
	private static float oldXPb = -1;
	private static int oldXPl = -1;
	public static boolean ExperienceChanged() {
		if (oldXPb != -1)
		{
			if (oldXPb == getCurrentWynncraftXp()  && oldXPl == ModCore.mc().player.experienceLevel)
			{
				return false;
			}
			else
			{
				oldXPb = getCurrentWynncraftXp();
				oldXPl = ModCore.mc().player.experienceLevel;
				return true;
			}
		}
		else
		{
			oldXPb = getCurrentWynncraftXp();
			oldXPl = ModCore.mc().player.experienceLevel;
			return false;
		}
	}
	
	
	public static List<String[]> Exp = new ArrayList<String[]>();
	public static boolean GainedExp = false;
	
	private static int oldXPe = 0;
	private static float oldXPeP = 0;
	private static int oldXPlevel = 0;
	public static void getAddedAmounts()
	{
		if (GainedExp){GainedExp=false;}
		
		// Add new Exp entries
		if (ExperienceChanged()) {
			int ExpLevelTmp = ModCore.mc().player.experienceLevel;
			if (ExpLevelTmp >= oldXPlevel) {
				int ExpTmp = 0;
				float ExpPerTmp = 0;
				if (oldXPlevel < ExpLevelTmp){
					for(int i=oldXPlevel;i<ExpLevelTmp;i++){
						ExpTmp = ExpTmp + getXpNeededForWynncraftLevel(i);
						ExpPerTmp++;
					}
				}
				if (oldXPlevel > 0){
					GainedExp = true;
					
					ExpTmp = ExpTmp - oldXPe + Math.round(getCurrentWynncraftXp());
					ExpPerTmp = ExpPerTmp - oldXPeP + ModCore.mc().player.experience;
					
					ExperienceUI.ExpHUDHang.Reset();
					ExperienceUI.ExpHUD += ExpTmp;
					ExperienceUI.ExpHUDPer += ExpPerTmp*100;
					ExperienceUI.ExpHUDAnimation = 0;
					ExperienceUI.ExpHUDLength = 1;
					
					if (!((ExperienceUI.EnableScrollingSidebar && (ExperienceUI.ExpFlowShowNames || ExperienceUI.ExpFlowShowLevel)) || ExperienceUI.KillStreak)){
						String[] ExpValues = {"", "", new DecimalFormat("#,###,###,##0").format(ExpTmp), new DecimalFormat("##,###,#00.00").format(ExpPerTmp * 100)};
						Exp.add(ExpValues);
					}
				}
			}
		}
		oldXPe = Math.round(getCurrentWynncraftXp());
		oldXPeP = ModCore.mc().player.experience;
		oldXPlevel = ModCore.mc().player.experienceLevel;
	}
}

