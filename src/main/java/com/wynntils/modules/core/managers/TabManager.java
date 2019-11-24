package com.wynntils.modules.core.managers;

import com.google.common.collect.Ordering;
import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionClasses;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.*;

import static com.wynntils.core.utils.reflections.ReflectionFields.GuiPlayerTabOverlay_ENTRY_ORDERING;

public class TabManager {
    private static class FastEntryOrdering extends Ordering<NetworkPlayerInfo> {
        private static final Ordering<NetworkPlayerInfo> DEFAULT_ORDERING = Ordering.from((Comparator<NetworkPlayerInfo>) ReflectionClasses.GuiPlayerTabOverlay$PlayerComparator.construct());
        private static boolean errored = false;

        @Override
        public int compare(NetworkPlayerInfo left, NetworkPlayerInfo right) {
            return DEFAULT_ORDERING.compare(left, right);
        }

        @Override
        public <E extends NetworkPlayerInfo> List<E> sortedCopy(Iterable<E> elements) {
            if (errored || !Reference.onWorld) {
                return super.sortedCopy(elements);
            }

            // Wynncraft tab names are '\0CRR', where \0 is ascii NUL, C is 1-4 (column), and R is 1-20 (row)
            E[] result = (E[]) new NetworkPlayerInfo[80];
            int found = 0;
            for (E v : elements) {
                String name = v.getGameProfile().getName();
                if (name.length() != 4 || name.charAt(0) != 0) continue;
                name = name.substring(1, 4);
                if (!Utils.isValidInteger(name)) continue;
                int x = Integer.parseInt(name);
                int col = x / 100 - 1;
                if (!(0 <= col && col < 4)) continue;
                int row = x % 100 - 1;
                if (!(0 <= row && row < 20)) continue;
                int i = col * 20 + row;
                if (result[i] != null) {
                    break;
                }
                result[i] = v;
                if (++found == 80) {
                    return Arrays.asList(result);
                }
            }

            errored = true;
            Reference.LOGGER.error("Wynncraft has changed the tab names or incorrectly assumed on world");
            return super.sortedCopy(elements);
        }
    }

    /**
     * Replaces the Minecraft tab gui ordering to be faster on Wynncraft
     */
    public static void replaceTabOrderer() {
        try {
            GuiPlayerTabOverlay_ENTRY_ORDERING.setValue(GuiPlayerTabOverlay.class, new FastEntryOrdering());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
