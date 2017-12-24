package com.wynndevs.modules.expansion.questbook;

public class Quest {
	private String questName;
	private int questLevel;
	private QuestLength questLength;
	private QuestDifficulty questDifficulty;
	private QuestStatus questStatus;
	private String questDescription;

	public String getQuestName() {
		return questName;
	}

	public int getQuestLevel() {
		return questLevel;
	}
	
	public QuestLength getQuestLength() {
		return questLength;
	}
	
	public QuestDifficulty getQuestDifficulty() {
		return questDifficulty;
	}

	public QuestStatus getQuestStatus() {
		return questStatus;
	}

	public String getQuestDescription() {
		return questDescription;
	}
	
	public enum QuestLength {
		SHORT, MEDIUM, LONG;
		public int getColor() {
			switch (this) {
				case SHORT:
					return Integer.parseInt("64c700", 16);
				case MEDIUM:
					return Integer.parseInt("CC5200", 16);
				case LONG:
					return Integer.parseInt("CF1715", 16);
			}
			return -1;
		}
		
		public String getName() {
			switch (this) {
				case SHORT:
					return "Short";
				case MEDIUM:
					return "Medium";
				case LONG:
					return "Long";
			}
			return "";
		}
	}
	
	public enum QuestDifficulty {
		EASY, MEDIUM, HARD;
		public int getColor() {
			switch (this) {
			case EASY:
				return Integer.parseInt("64c700", 16);
			case MEDIUM:
				return Integer.parseInt("CC5200", 16);
			case HARD:
				return Integer.parseInt("CF1715", 16);
			}
			return -1;
		}

		public String getName() {
			switch (this) {
			case EASY:
				return "Easy";
			case MEDIUM:
				return "Medium";
			case HARD:
				return "Hard";
			}
			return "";
		}
	}

	public enum QuestStatus {
		NOT_STARTED, STARTED, FINISHED;

		public int getColor() {
			switch (this) {
			case NOT_STARTED:
				return Integer.parseInt("64c700", 16);
			case STARTED:
				return Integer.parseInt("CC5200", 16);
			case FINISHED:
				return Integer.parseInt("FF1715", 16);
			}
			return -1;
		}

		public String getName() {
			switch (this) {
			case NOT_STARTED:
				return "Not Started";
			case STARTED:
				return "Started";
			case FINISHED:
				return "Finished";
			}
			return "";
		}
	}

	public Quest(String questName, int questLevel, QuestLength questLength, QuestDifficulty questDifficulty, QuestStatus status, String questDescription) {
		this.questName = questName;//.replace("\\\\u0027", "'");
		this.questLevel = questLevel;
		this.questLength = questLength;
		this.questStatus = status;
		this.questDifficulty = questDifficulty;
		this.questDescription = questDescription;//.replace("\\\\u0027", "'");

		//if (questName.contains("Macabre Masquerade"))
		//	this.questName = "Macabre Masquerade";
		//if (questName.contains("???"))
		//	this.questName = "???";
	}

	public void ConsoleQuest() {
		System.out.println("Quest:");
		System.out.println("Name:" + this.questName);
		System.out.println("Lv:" + this.questLevel);
		System.out.println("Difficulty:" + this.questDifficulty);
		System.out.println("Status:" + this.questStatus);
		System.out.println("Description:" + this.questDescription);
		System.out.println();
	}
}