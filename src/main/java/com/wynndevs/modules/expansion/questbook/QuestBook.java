package com.wynndevs.modules.expansion.questbook;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.Delay;
import com.wynndevs.modules.expansion.webapi.WebAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestBook {
	public static ItemStack questBook = null;
	public static List<Quest> quests = new ArrayList<Quest>();
	public static String selectedQuest = "";
	public static String selectedQuestDescription = "";
	public static boolean selectedQuestTracking = false;
	public static Delay xyzDelay = new Delay(5.01f);
	public static Delay refreshDelay = new Delay(2f, true);
	
	public static List<String> QuestCorrections = new ArrayList<String>();
	
	public static void SetupQuestCorrections(){
		try {
			ExpReference.consoleOut("Gathering Quest Name Corrections");
			QuestCorrections.clear();
			// Retrieve Quest Fixes from file
			BufferedReader DataFile = new BufferedReader(new InputStreamReader(new URL(WebAPI.QuestCorrectionsURL).openConnection().getInputStream()));
			String DataLine;
			int QuestCorrectionMarker = 0;
			DataLine = DataFile.readLine();
			while (DataLine != null) {
				QuestCorrections.add(QuestCorrectionMarker, ParseCharValues(DataLine.substring(0, DataLine.indexOf(" - "))));
				QuestCorrections.add(ParseCharValues(DataLine.substring(DataLine.indexOf(" - ") +3)));
				QuestCorrectionMarker++;
				DataLine = DataFile.readLine();
			}
			DataFile.close();
			ExpReference.consoleOut("Quest Name Corrections at: " + (QuestCorrections.size()/2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Quest GetSelectedQuest() {
		for (Quest quest : quests) {
			if (quest.getQuestName().equals(selectedQuest))
				return quest;
		}
		
		return null;
	}
	
	public static int GetSelectedQuestIndex() {
		int i = -1;
		for (Quest quest : quests) {
			i++;
			if (quest.getQuestName().equals(selectedQuest))
				return i;
		}
		
		return -1;
	}
	
	public static boolean XyzEnabled(String description) {
		if (xyzDelay.Passed()) {
			int stage = -1;
			for (char chr : description.toCharArray()) {
				if (chr == '[') {
					stage = 0;
				}
				if (chr == ',' && (stage == 0 || stage == 1)) {
					stage++;
				}
				if (chr == ']' && stage == 2) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void ReloadBook() {
		try {
			ItemStack book = ModCore.mc().player.inventory.getStackInSlot(7);
			if (book.hasDisplayName() && book.getDisplayName().contains("Quest Book")) {
				NBTTagCompound nbttagcompound = book.getTagCompound();
				NBTTagList pages = nbttagcompound.getTagList("pages", 8).copy();
				int pageCount = pages.tagCount();
				
				quests.clear();
				for (int i = 0; i < pageCount; i++) {
					String page = pages.get(i).toString();
					
					//System.out.println("Raw Data: " + page);
					
					if (page.contains("\\\"color\\\":\\\"")) {
						quests.add(QuestBook.DecodePageIntoQuest(page, false));
					}else{
						quests.add(QuestBook.DecodePageIntoQuest(page, true));
					}
				}
				
				selectedQuest = quests.get(0).getQuestName();
				/*
				if (selectedQuest == "") {
					selectedQuest = quests.get(0).getQuestName();
				}else if (GetSelectedQuest().getQuestStatus() == Quest.QuestStatus.FINISHED){
					selectedQuest = quests.get(0).getQuestName();
					if (selectedQuestTracking) {selectedQuestTracking = false;}
				}*/
				
				selectedQuestDescription = GetSelectedQuest().getQuestDescription();
			} else {
				selectedQuestDescription = "";
				selectedQuestTracking = false;
			}
		} catch (Exception ignored) {
			ignored.printStackTrace();
			selectedQuestDescription = "";
			selectedQuestTracking = false;
			//ignored.printStackTrace();
		}
	}
	
	public static Quest DecodePageIntoQuest(String questJson, boolean NewFormat) {
		StringBuilder questName = new StringBuilder();
		StringBuilder questLevel = new StringBuilder();
		Quest.QuestLength questLength = Quest.QuestLength.SHORT;
		Quest.QuestDifficulty questDifficulty = Quest.QuestDifficulty.EASY;
		Quest.QuestStatus questStatus = Quest.QuestStatus.NOT_STARTED;
		
		StringBuilder builder = new StringBuilder();
		
		if (!NewFormat) {
			StringBuilder reader = new StringBuilder();
			boolean build = false;
			for (Character chr : questJson.toCharArray()) {
				if (build) {
					if (chr.equals('"') && !builder.toString().endsWith("\\\\\\")) {
						build = false;
						builder = new StringBuilder(builder.substring(0, builder.length() - 1));
					} else {
						if (builder.toString().endsWith("\\\\\\")){
							builder = new StringBuilder(builder.substring(0, builder.length() - 3));
						}
						builder.append(chr);
					}
					
				} else {
					reader.append(chr);
					if (reader.toString().endsWith("\\\"text\\\":\\\"")) {
						build = true;
						reader = new StringBuilder();
					}
				}
			}
		}else{
			if (questJson.contains(String.valueOf('\u00a7'))) {
				StringBuilder reader = new StringBuilder();
				for (String read : questJson.split(String.valueOf('\u00a7'))) {
					if(read.length() > 1) {
						if (reader.toString().equals("")) {
							reader = new StringBuilder(read);
						}else{
							reader.append(read.substring(1));
						}
					}
				}
				builder = new StringBuilder((questJson.startsWith(String.valueOf('\u00a7')) ? reader.substring(1) : reader.toString()).replace("\\\\n", "").replace("\\\\\\\"", "\""));
				builder = new StringBuilder(builder.toString().replace("\"{\\\"extra\\\":[\\\"", "").replace("\\\"],\\\"text\\\":\\\"\\\"}\"", ""));
			}
		}
		
		builder = new StringBuilder(builder.toString().replace("\\\\u", "\\u"));
		builder = new StringBuilder(ParseCharValues(builder.toString()));
		
		for (Character chr : builder.toString().toCharArray()) {
			questName.append(chr);
			if (questName.toString().endsWith("Lv. min: ")) {
				questName = new StringBuilder(questName.toString().replace("  Lv. min: ", ""));
				break;
			}
		}
		builder = new StringBuilder(builder.toString().replace(questName + "  Lv. min: ", ""));
		if (QuestCorrections.subList(0, (QuestCorrections.size()/2)).contains(questName.toString())){
			for (int i=0;i<(QuestCorrections.size()/2);i++){
				if (QuestCorrections.get(i).equals(questName.toString())){
					questName = new StringBuilder(QuestCorrections.get(i + (QuestCorrections.size() / 2)));
					break;
				}
			}
		}
		
		for (Character chr : builder.toString().toCharArray()) {
			if (!"0123456789".contains(chr.toString())) {
				builder = new StringBuilder(builder.toString().replaceFirst(questLevel.toString(), ""));
				break;
			} else {
				questLevel.append(chr);
			}
		}
		
		for (byte i = 0; i < 3; i++){
			while (builder.charAt(0) == ' '){
				builder = new StringBuilder(builder.substring(1));
			}
			switch (builder.charAt(0)){
				case 'L':
					if (builder.toString().startsWith("Length: ")) {
						if (builder.toString().startsWith("Length: Short")){
							builder = new StringBuilder(builder.toString().replace("Length: Short", ""));
							questLength = Quest.QuestLength.SHORT;
						} else if (builder.toString().startsWith("Length: Medium")) {
							builder = new StringBuilder(builder.toString().replace("Length: Medium", ""));
							questLength = Quest.QuestLength.MEDIUM;
						} else if (builder.toString().startsWith("Length: Long")) {
							builder = new StringBuilder(builder.toString().replace("Length: Long", ""));
							questLength = Quest.QuestLength.LONG;
						}
					} break;
				
				case 'D':
					if (builder.toString().startsWith("Difficulty: ")) {
						if (builder.toString().startsWith("Difficulty: Easy")){
							builder = new StringBuilder(builder.toString().replace("Difficulty: Easy", ""));
							questDifficulty = Quest.QuestDifficulty.EASY;
						} else if (builder.toString().startsWith("Difficulty: Medium")) {
							builder = new StringBuilder(builder.toString().replace("Difficulty: Medium", ""));
							questDifficulty = Quest.QuestDifficulty.MEDIUM;
						} else if (builder.toString().startsWith("Difficulty: Hard")) {
							builder = new StringBuilder(builder.toString().replace("Difficulty: Hard", ""));
							questDifficulty = Quest.QuestDifficulty.HARD;
						}
					} break;
				
				case 'S':
					if (builder.toString().startsWith("Status: ")) {
						if (builder.toString().startsWith("Status: Not Started")){
							builder = new StringBuilder(builder.toString().replace("Status: Not Started", ""));
							questStatus = Quest.QuestStatus.NOT_STARTED;
						} else if (builder.toString().startsWith("Status: Started")) {
							builder = new StringBuilder(builder.toString().replace("Status: Started", ""));
							questStatus = Quest.QuestStatus.STARTED;
						} else if (builder.toString().startsWith("Status: Finished")) {
							builder = new StringBuilder(builder.toString().replace("Status: Finished", ""));
							questStatus = Quest.QuestStatus.FINISHED;
						}
					} break;
					
				default: break;
			}
		}
		
		Pattern pattern = Pattern.compile("\\[-?[0-9]+,[0-9]{1,3},-?[0-9]+]");
		Matcher Matcher = pattern.matcher(builder.toString());
		int[] questCoords = new int[] {0,0,0};
		if (Matcher.find()){
			String[] Coords = builder.substring(Matcher.start(), Matcher.end()).split(",");
			questCoords = new int[]{Integer.parseInt(Coords[0].substring(1)), Integer.parseInt(Coords[1]), Integer.parseInt(Coords[2].substring(0, Coords[2].length() - 1))};
		}
		
		//System.out.println("Quest Data: " + questName + " - Lv. " + questLevel + " - " + questLength + " - " + questDifficulty + " - " + questStatus + " - " + builder + " - [" + questCoords[0] + "," + questCoords[1] + "," + questCoords[2] + "]");
		return new Quest(questName.toString(), Integer.parseInt(questLevel.toString()), questLength, questDifficulty, questStatus, builder.toString(), questCoords);
	}
	
	private static String ParseCharValues(String Data){
		if (Data.contains("\\u")){
			StringBuilder DataFormated = new StringBuilder();
			for (int i=0;i<Data.length();i++){
				if (Data.length() >= i+6 && Data.substring(i, i+2).equals("\\u")){
					DataFormated.append(String.valueOf((char) Integer.parseInt(Data.substring(i + 2, (Data.length() > i + 6 ? i + 6 : Data.length())), 16)));
					i = i+5;
				}else{
					DataFormated.append(Data.charAt(i));
				}
			}
			return DataFormated.toString();
		}else{
			return Data;
		}
	}
}
