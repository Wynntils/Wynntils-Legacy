package com.wynntils.modules.visual.managers;

import java.util.Map.Entry;

import com.wynntils.modules.visual.VisualModule;
import com.wynntils.modules.visual.configs.VisualConfig;

public class CharacterReorderManager {
    
    private static boolean reordering = false;
    
    public static void startReording() {
        reordering = true;
    }
    
    public static void stopReordering() {
        reordering = false;
    }
    
    public static void toggleReordering() {
        reordering = !reordering;
    }
    
    public static boolean isReordering() {
        return reordering;
    }
    
    public static void reorder(int slot1, int slot2) {
        // slot 1
        int rootSlot1 = slot1;
        if (VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.containsValue(slot1))
            rootSlot1 = getSwappedSlot(slot1);
        
        // slot 2
        int rootSlot2 = slot2;
        if (VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.containsValue(slot2))
            rootSlot2 = getSwappedSlot(slot2);
        
        // these must be done separately to avoid issues
        if (rootSlot1 == slot2)
            VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.remove(rootSlot1);
        else
            VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.put(rootSlot1, slot2);
        if (rootSlot2 == slot1)
            VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.remove(rootSlot2);
        else
            VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.put(rootSlot2, slot1);
        
        VisualConfig.CharacterSelector.INSTANCE.saveSettings(VisualModule.getModule());
    }
    
    public static int getSwappedSlot(int slot) {
        for(Entry<Integer, Integer> entry : VisualConfig.CharacterSelector.INSTANCE.swappedCharacters.entrySet()) {
            if (entry.getValue() == slot) {
                return entry.getKey();
            }
        }
        return -1;
    }

}