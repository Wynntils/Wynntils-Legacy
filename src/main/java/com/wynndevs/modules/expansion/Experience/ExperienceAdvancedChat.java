package com.wynndevs.modules.expansion.Experience;

import java.text.DecimalFormat;

public class ExperienceAdvancedChat {
	
	
	private static boolean Quest = false;
	private static boolean Discovery = false;
	private static boolean Area = false;
	private static String Name = "";
	
	public static void ChatHandler(String msg, String msgRaw) {
		
		if (msgRaw.contains(String.valueOf('\u00a7') + 'e') && Quest && Name.equals("")) {
			for (int i=0;i<msg.length();i++){
				if (msg.charAt(i) != ' '){
					Name = msg.substring(i);
					break;
				}
			}
		}else if(msg.contains("Experience Points") && Quest){
			String Exp = msg.substring(0,msg.indexOf(" Experience")).replace("-", "").replace("+", "").replace(" ", "");
			String ExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(Exp)/Experience.getCurrentWynncraftMaxXp())*100);
			Exp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(Exp));
			String[] Message = {String.valueOf('\u00a7') + "e" + Name, String.valueOf('\u00a7') + "6[Quest]", Exp, ExpPer};
			Experience.Exp.add(Message);
			Name = "";
			Quest = false;
		}else if (msg.contains("[Quest Completed]")){
			Quest = true;
		}
		
		if (msgRaw.contains(String.valueOf('\u00a7') + 'b') && Discovery && Name.equals("")) {
			for (int i=0;i<msg.length();i++){
				if (msg.charAt(i) != ' '){
					Name = msg.substring(i);
					break;
				}
			}
		}else if(msg.contains("Experience Points") && Discovery){
			String Exp = msg.substring(0,msg.indexOf(" Experience")).replace("-", "").replace("+", "").replace(" ", "");
			String ExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(Exp)/Experience.getCurrentWynncraftMaxXp())*100);
			Exp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(Exp));
			String[] Message = {String.valueOf('\u00a7') + "b" + Name, String.valueOf('\u00a7') + "3[Discovery]", Exp, ExpPer};
			Experience.Exp.add(Message);
			Name = "";
			Discovery = false;
		}else if (msg.contains("[Discovery Found]")){
			Discovery = true;
		}
		
		if (msgRaw.contains(String.valueOf('\u00a7') + 'e') && Area && Name.equals("")) {
			for (int i=0;i<msg.length();i++){
				if (msg.charAt(i) != ' '){
					Name = msg.substring(i);
					break;
				}
			}
			Name = String.valueOf('\u00a7') + "e" + Name;
		}else if (Area && Name.equals("")) {
			for (int i=0;i<msg.length();i++){
				if (msg.charAt(i) != ' '){
					Name = msg.substring(i);
					break;
				}
			}
			Name = String.valueOf('\u00a7') + "f" + Name;
		}else if(msg.contains("Experience Points") && Area){
			String Exp = msg.substring(0,msg.indexOf(" Experience")).replace("-", "").replace("+", "").replace(" ", "");
			String ExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(Exp)/Experience.getCurrentWynncraftMaxXp())*100);
			Exp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(Exp));
			String[] Message = {Name, String.valueOf('\u00a7') + (Name.charAt(1) == 'e' ? "6[Area]" : "7[Area]"), Exp, ExpPer};
			Experience.Exp.add(Message);
			Name = "";
			Area = false;
		}else if (msg.contains("[Area Discovered]")){
			Area = true;
		}
		
		if (msg.startsWith("Great job! You've completed the ")){
			Name = "";
			boolean Skip = false;
			for (Character chr : msg.substring(msg.indexOf("completed the ") +14, msg.indexOf(" Dungeon!")).toCharArray()) {
				if (!Skip) {
					if (chr.equals('\u00C0')) {
						Skip = true;
						Name += " ";
					}else{
						Name += chr;
					}
				}else{
					if (!chr.equals('\u00C0')) {
						Skip = false;
						Name += chr;
					}
				}
			}
		}else if (msg.startsWith("[+") && msg.endsWith(" XP]") && !Quest && !Discovery && !Name.equals("")){
			String Exp = msg.substring(msg.indexOf("[+") +2, msg.indexOf(" XP]"));
			String ExpPer = new DecimalFormat("#,###,#00.00").format((Float.parseFloat(Exp)/Experience.getCurrentWynncraftMaxXp())*100);
			Exp = new DecimalFormat("#,###,###,##0").format(Integer.parseInt(Exp));
			if (Name.startsWith("Corrupted")){
				String[] Message = {String.valueOf('\u00a7') + "c" + Name, String.valueOf('\u00a7') + "4[Dungeon]", Exp, ExpPer};
				Experience.Exp.add(Message);
			}else{
				String[] Message = {String.valueOf('\u00a7') + "e" + Name, String.valueOf('\u00a7') + "6[Dungeon]", Exp, ExpPer};
				Experience.Exp.add(Message);
			}
			Name = "";
		}
	}
}
