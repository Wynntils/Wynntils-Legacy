/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.instances;

import cf.wynntils.modules.questbook.enums.QuestSize;
import cf.wynntils.modules.questbook.enums.QuestStatus;

import java.util.ArrayList;
import java.util.List;

public class QuestInfo {

    private final String name;
    private final String lowerCaseName;
    private final QuestStatus status;
    private final int minLevel;
    private final QuestSize size;
    private final List<String> lore;
    private final ArrayList<String> splittedDescription;

    private final String currentDescription;

    public QuestInfo(String name, QuestStatus status, int minLevel, QuestSize size, String currentDescription, List<String> lore) {
        this.name = name; this.status = status; this.minLevel = minLevel; this.size = size; this.currentDescription = currentDescription; this.lore = lore;
        this.lowerCaseName = name.toLowerCase();

        ArrayList<String> splittedDescription = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        int chars = 0;
        for(String x : currentDescription.split(" ")) {
            if(chars + x.length() > 37) {
                splittedDescription.add(currentMessage.toString());
                currentMessage = new StringBuilder(x + " ");
                chars = x.length();
                continue;
            }
            chars+= x.length() ;
            currentMessage.append(x).append(" ");
        }
        splittedDescription.add(currentMessage.toString());

        this.splittedDescription = splittedDescription;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public QuestSize getSize() {
        return size;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public String getCurrentDescription() {
        return currentDescription;
    }

    public String getName() {
        return name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public ArrayList<String> getSplittedDescription() {
        return splittedDescription;
    }

    public String toString() {
        return name + ":" + minLevel + ":" + size.toString() + ":" + status.toString() + ":" + currentDescription;
    }

}
