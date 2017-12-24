package com.wynndevs.modules.expansion.update;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Update {
	public static boolean newUpdate = false;
	public static String latest = "";

	private static String getLatest() {
		String LATEST_VERSION = ExpReference.VERSION;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/qy1uwVBD").openConnection().getInputStream()));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				LATEST_VERSION = strLine;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		latest = LATEST_VERSION;
		return LATEST_VERSION;
	}
	
	public static void checkLatest() {
		String[] Current = ExpReference.VERSION.split("\\.");
		String[] Latest = getLatest().split("\\.");
		
		for (int i=0;i<Math.min(Current.length,Latest.length);i++){
			if (!Current[i].equals(Latest[i])){
				if (Current[i].matches("[0-9a-zA-Z]+") && Latest[i].matches("[0-9a-zA-Z]+")){
					for (int j=0;j<Math.min(Current[i].length(),Latest[i].length());j++) {
						if (Current[i].charAt(j) < Latest[i].charAt(j)){
							newUpdate = true;
							return;
						}
					}
				}
			}
		}
		if (Latest.length > Current.length){
			newUpdate = true;
		}else{
			newUpdate = false;
		}
	}
	
	public static void SetHeader() {
		ITextComponent Header = new TextComponentString( String.valueOf('\u00a7') + "6Wynn Expansion v" + ExpReference.VERSION + (newUpdate ? String.valueOf('\u00a7') + "a   update Availible v" + latest + "!" : ""));
		ModCore.mc().ingameGUI.getTabList().setHeader(Header);
	}
}
