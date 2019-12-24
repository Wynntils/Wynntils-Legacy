package com.wynntils.modules.core.managers;

import com.google.common.collect.Ordering;
import com.wynntils.Reference;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.reflections.ReflectionFields;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.Arrays;
import java.util.List;

public class TabManager {

    private static FastEntryOrdering entryOrdering;

    public static class FastEntryOrdering extends Ordering<NetworkPlayerInfo> {
        private Ordering<NetworkPlayerInfo> previousOrdering;
        private static boolean errored = false;

        private FastEntryOrdering(Ordering<NetworkPlayerInfo> previousOrdering) {
            this.previousOrdering = previousOrdering;
        }

        @Override
        public int compare(NetworkPlayerInfo left, NetworkPlayerInfo right) {
            return previousOrdering.compare(left, right);
        }

        @Override
        public <E extends NetworkPlayerInfo> List<E> sortedCopy(Iterable<E> elements) {
            if (errored || !Reference.onWorld) {
                return previousOrdering.sortedCopy(elements);
            }

            try {
                // Wynncraft tab names are '\0CRR', where \0 is ascii NUL, C is 1-4 (column), and RR is 01-20 (row)
                E[] result = (E[]) new NetworkPlayerInfo[80];
                int found = 0;
                for (E v : elements) {
                    String name = v.getGameProfile().getName();
                    if (name.length() != 4 || name.charAt(0) != 0) continue;
                    name = name.substring(1, 4);
                    if (!StringUtils.isValidInteger(name)) continue;
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
            } catch (NullPointerException e) {  // TODO: investigate this
                e.printStackTrace();
            }

            errored = true;
            Reference.LOGGER.error("Wynncraft has changed the tab names or incorrectly assumed on world");
            return previousOrdering.sortedCopy(elements);
        }
    }

    /**
     * Replaces the Minecraft tab gui ordering to be faster on Wynncraft
     */
    public static void replaceTabOrderer() {
        try {
            entryOrdering = new FastEntryOrdering(
                    (Ordering<NetworkPlayerInfo>) ReflectionFields.GuiPlayerTabOverlay_ENTRY_ORDERING
                            .getValue(GuiPlayerTabOverlay.class));

            ReflectionFields.GuiPlayerTabOverlay_ENTRY_ORDERING.setValue(GuiPlayerTabOverlay.class, entryOrdering);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the entry orderer
     */
    public static FastEntryOrdering getEntryOrdering() {
        return entryOrdering;
    }

}
