package com.wynndevs.modules.expansion.misc;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TpsMonitor {
	
	public static List<double[]> TpsTimes = new ArrayList<double[]>();
	private static boolean InWar = false;
	
	public static void SetFooter() {
		if (!InWar && Reference.onWars()) {
			TpsTimes.clear();
			InWar = true;
		}else if (InWar && !Reference.onWars()){
			TpsTimes.clear();
			InWar = false;
		}
		
		double[] Times = {System.currentTimeMillis(), ModCore.mc().world.getWorldTime()};
		//if (TpsTimes.isEmpty()) {
			TpsTimes.add(0, Times);
		//}
		
		//double TpsSystemTime1 = 0;
		//double TpsWorldTime1 = 0;
		//int TickCount1 = 1;
		double TpsSystemTime5 = 0;
		double TpsWorldTime5 = 0;
		int TickCount5 = 1;
		double TpsSystemTime20 = 0;
		double TpsWorldTime20 = 0;
		int TickCount20 = 1;
		double TpsSystemTime60 = 0;
		double TpsWorldTime60 = 0;
		int TickCount60 = 1;
		
		for (int i=0;i<TpsTimes.size();i++) {
			if (i >= 2400 /*TpsTimes.get(i)[0] + 60250 < Times[0]*/) {
				TpsTimes.remove(i);
				i--;
			}else if (i > 0 && TpsTimes.get(i-1)[1] <= TpsTimes.get(i)[1]) {
				TpsTimes.remove(i);
				i--;
			}else{
				//if (i >= 5 && i <= 25/*TpsTimes.get(i)[0] + 1250 > Times[0]*/) {
				//	TpsSystemTime1 = TpsSystemTime1 + (TpsTimes.get(i-1)[0] - TpsTimes.get(i)[0]);
				//	TpsWorldTime1 = TpsWorldTime1 + (TpsTimes.get(i-1)[1] - TpsTimes.get(i)[1]);
				//	TickCount1++;
				//}
				if (i >= 5 && i <= 105/*TpsTimes.get(i)[0] + 5250 > Times[0]*/) {
					TpsSystemTime5 = TpsSystemTime5 + (TpsTimes.get(i-1)[0] - TpsTimes.get(i)[0]);
					TpsWorldTime5 = TpsWorldTime5 + (TpsTimes.get(i-1)[1] - TpsTimes.get(i)[1]);
					TickCount5++;
				}
				if (i >= 5 && i <= 405 /*TpsTimes.get(i)[0] + 20250 > Times[0]*/) {
					TpsSystemTime20 = TpsSystemTime20 + (TpsTimes.get(i-1)[0] - TpsTimes.get(i)[0]);
					TpsWorldTime20 = TpsWorldTime20 + (TpsTimes.get(i-1)[1] - TpsTimes.get(i)[1]);
					TickCount20++;
				}
				if (i >= 5) {
					TpsSystemTime60 = TpsSystemTime60 + (TpsTimes.get(i-1)[0] - TpsTimes.get(i)[0]);
					TpsWorldTime60 = TpsWorldTime60 + (TpsTimes.get(i-1)[1] - TpsTimes.get(i)[1]);
					TickCount60++;
				}
			}
		}
		
		//double TPS1 = (TpsWorldTime1 / (TickCount1 <= 1 ? TickCount1 : TickCount1-1)) / ((TpsSystemTime1 / 1000) / (TickCount1 <= 1 ? TickCount1 : TickCount1-1));
		double TPS5 = (TpsWorldTime5 / (TickCount5 <= 1 ? TickCount5 : TickCount5-1)) / ((TpsSystemTime5 / 1000) / (TickCount5 <= 1 ? TickCount5 : TickCount5-1));
		double TPS20 = (TpsWorldTime20 / (TickCount20 <= 1 ? TickCount20 : TickCount20-1)) / ((TpsSystemTime20 / 1000) / (TickCount20 <= 1 ? TickCount20 : TickCount20-1));
		double TPS60 = (TpsWorldTime60 / (TickCount60 <= 1 ? TickCount60 : TickCount60-1)) / ((TpsSystemTime60 / 1000) / (TickCount60 <= 1 ? TickCount60 : TickCount60-1));
		
		String FooterText = String.valueOf('\u00a7') + "aEstimated TPS      ";
		
		//if (TPS1 >= 19) {
		//	FooterText = FooterText + String.valueOf('\u00a7') + "a";
		//}else if (TPS1 >= 17) {
		//	FooterText = FooterText + String.valueOf('\u00a7') + "e";
		//}else if (TPS1 >= 15) {
		//	FooterText = FooterText + String.valueOf('\u00a7') + "c";
		//}else{
		//	FooterText = FooterText + String.valueOf('\u00a7') + "4";
		//}
		//FooterText = FooterText + new DecimalFormat("00.0").format(TPS1) + "/1s      ";
		
		if (TPS5 >= 19) {
			FooterText = FooterText + String.valueOf('\u00a7') + "a";
		}else if (TPS5 >= 17) {
			FooterText = FooterText + String.valueOf('\u00a7') + "e";
		}else if (TPS5 >= 15) {
			FooterText = FooterText + String.valueOf('\u00a7') + "c";
		}else{
			FooterText = FooterText + String.valueOf('\u00a7') + "4";
		}
		FooterText = FooterText + new DecimalFormat("00.0").format(TPS5) + "/5s      ";
		
		if (TPS20 >= 19) {
			FooterText = FooterText + String.valueOf('\u00a7') + "a";
		}else if (TPS20 >= 17) {
			FooterText = FooterText + String.valueOf('\u00a7') + "e";
		}else if (TPS20 >= 15) {
			FooterText = FooterText + String.valueOf('\u00a7') + "c";
		}else{
			FooterText = FooterText + String.valueOf('\u00a7') + "4";
		}
		FooterText = FooterText + new DecimalFormat("00.0").format(TPS20) + "/20s     ";
		
		if (TPS60 >= 19) {
			FooterText = FooterText + String.valueOf('\u00a7') + "a";
		}else if (TPS60 >= 17) {
			FooterText = FooterText + String.valueOf('\u00a7') + "e";
		}else if (TPS60 >= 15) {
			FooterText = FooterText + String.valueOf('\u00a7') + "c";
		}else{
			FooterText = FooterText + String.valueOf('\u00a7') + "4";
		}
		FooterText = FooterText + new DecimalFormat("00.0").format(TPS60) + "/60s";
		
		ITextComponent Footer = new TextComponentString(FooterText);
		
		if (TpsTimes.size() > 5) ModCore.mc().ingameGUI.getTabList().setFooter(Footer);
	}
}
