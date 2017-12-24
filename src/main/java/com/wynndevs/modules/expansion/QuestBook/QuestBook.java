package com.wynndevs.modules.expansion.QuestBook;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.Misc.Delay;
import com.wynndevs.modules.expansion.WebAPI.WebAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
			ExpReference.ConsoleOut("Gathering Quest Name Corrections");
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
			ExpReference.ConsoleOut("Quest Name Corrections at: " + (QuestCorrections.size()/2));
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
					
					System.out.println("Raw Data: " + page);
					
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
			selectedQuestDescription = "";
			selectedQuestTracking = false;
			//ignored.printStackTrace();
		}
	}
	
	public static Quest DecodePageIntoQuest(String questJson, boolean NewFormat) {
		String questName = "";
		String questLevel = "";
		Quest.QuestLength questLength = Quest.QuestLength.SHORT;
		Quest.QuestDifficulty questDifficulty = Quest.QuestDifficulty.EASY;
		Quest.QuestStatus questStatus = Quest.QuestStatus.NOT_STARTED;
		
		String builder = "";
		
		if (!NewFormat) {
			String reader = "";
			boolean build = false;
			for (Character chr : questJson.toCharArray()) {
				if (build) {
					if (chr.equals('"') && !builder.endsWith("\\\\\\")) {
						build = false;
						builder = builder.substring(0, builder.length() - 1);
					} else {
						if (builder.endsWith("\\\\\\")){
							builder = builder.substring(0, builder.length() - 3);
						}
						builder += chr;
					}
					
				} else {
					reader += chr;
					if (reader.endsWith("\\\"text\\\":\\\"")) {
						build = true;
						reader = "";
					}
				}
			}
		}else{
			if (questJson.contains(String.valueOf('\u00a7'))) {
				String reader = "";
				for (String read : questJson.split(String.valueOf('\u00a7'))) {
					if(read.length() > 1) {
						if (reader.equals("")) {
							reader = read;
						}else{
							reader = reader + read.substring(1);
						}
					}
				}
				builder = (questJson.startsWith(String.valueOf('\u00a7')) ? reader.substring(1) : reader).replace("\\\\n", "").replace("\\\\\\\"", "\"");
				builder = builder.replace("\"{\\\"extra\\\":[\\\"", "").replace("\\\"],\\\"text\\\":\\\"\\\"}\"", "");
			}
		}
		
		builder = builder.replace("\\\\u", "\\u");
		builder = ParseCharValues(builder);
		
		for (Character chr : builder.toCharArray()) {
			questName += chr;
			if (questName.endsWith("Lv. min: ")) {
				questName = questName.replace("  Lv. min: ", "");
				break;
			}
		}
		builder = builder.replace(questName + "  Lv. min: ", "");
		if (QuestCorrections.subList(0, (QuestCorrections.size()/2)).contains(questName)){
			for (int i=0;i<(QuestCorrections.size()/2);i++){
				if (QuestCorrections.get(i).equals(questName)){
					questName = QuestCorrections.get(i + (QuestCorrections.size()/2));
					break;
				}
			}
		}
		
		for (Character chr : builder.toCharArray()) {
			if (!"0123456789".contains(chr.toString())) {
				builder = builder.replaceFirst(questLevel, "");
				break;
			} else {
				questLevel += chr;
			}
		}
		
		for (byte i = 0; i < 3; i++){
			while (builder.charAt(0) == ' '){
				builder = builder.substring(1);
			}
			switch (builder.charAt(0)){
				case 'L':
					if (builder.startsWith("Length: ")) {
						if (builder.startsWith("Length: Short")){
							builder = builder.replace("Length: Short", "");
							questLength = Quest.QuestLength.SHORT;
						} else if (builder.startsWith("Length: Medium")) {
							builder = builder.replace("Length: Medium", "");
							questLength = Quest.QuestLength.MEDIUM;
						} else if (builder.startsWith("Length: Long")) {
							builder = builder.replace("Length: Long", "");
							questLength = Quest.QuestLength.LONG;
						}
					} break;
				
				case 'D':
					if (builder.startsWith("Difficulty: ")) {
						if (builder.startsWith("Difficulty: Easy")){
							builder = builder.replace("Difficulty: Easy", "");
							questDifficulty = Quest.QuestDifficulty.EASY;
						} else if (builder.startsWith("Difficulty: Medium")) {
							builder = builder.replace("Difficulty: Medium", "");
							questDifficulty = Quest.QuestDifficulty.MEDIUM;
						} else if (builder.startsWith("Difficulty: Hard")) {
							builder = builder.replace("Difficulty: Hard", "");
							questDifficulty = Quest.QuestDifficulty.HARD;
						}
					} break;
				
				case 'S':
					if (builder.startsWith("Status: ")) {
						if (builder.startsWith("Status: Not Started")){
							builder = builder.replace("Status: Not Started", "");
							questStatus = Quest.QuestStatus.NOT_STARTED;
						} else if (builder.startsWith("Status: Started")) {
							builder = builder.replace("Status: Started", "");
							questStatus = Quest.QuestStatus.STARTED;
						} else if (builder.startsWith("Status: Finished")) {
							builder = builder.replace("Status: Finished", "");
							questStatus = Quest.QuestStatus.FINISHED;
						}
					} break;
					
				default: break;
			}
		}
		
		System.out.println("Quest Data: " + questName + " - Lv. " + questLevel + " - " + questLength + " - " + questDifficulty + " - " + questStatus + " - " + builder);
		return new Quest(questName, Integer.parseInt(questLevel), questLength, questDifficulty, questStatus, builder);
	}
	
	private static String ParseCharValues(String Data){
		if (Data.contains("\\u")){
			String DataFormated = "";
			for (int i=0;i<Data.length();i++){
				if (Data.length() >= i+6 && Data.substring(i, i+2).equals("\\u")){
					DataFormated += String.valueOf((char) Integer.parseInt(Data.substring(i+2, (Data.length() > i+6? i+6 : Data.length())), 16));
					i = i+5;
				}else{
					DataFormated = DataFormated + Data.charAt(i);
				}
			}
			return DataFormated;
		}else{
			return Data;
		}
	}
}
