/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

import javax.annotation.Nonnull;
import java.util.Comparator;

public abstract class TimerContainer implements Comparable<TimerContainer> {

    private String prefix; // The prefix to display before the name. Not included in identifying name. A good place to put color codes.
    private final String name; // The name of the consumable (also used to identify it)
    private String suffix; // The suffix to display after the name. Not included in identifying name.
    private final boolean persistent; // If the consumable is should persist through death and character changes

    public TimerContainer(String prefix, String name, String suffix, boolean persistent) {
        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;
        this.persistent = persistent;
    }

    /**
     * @return The name of the consumable
     */
    public String getName() {
        return name;
    }

    /**
     * @return If the consumable should persist through death and character changes
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * @return The prefix to display before the name
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param newPrefix The new prefix to display before the name
     */
    public void setPrefix(String newPrefix) {
        this.prefix = newPrefix;
    }

    /**
     * @return The suffix to display after the name
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param newSuffix The new suffix to display after the name
     */
    public void setSuffix(String newSuffix) {
        this.suffix = newSuffix;
    }

    @Override
    public int compareTo(@Nonnull TimerContainer other) {
        return Comparator.comparing(TimerContainer::getName).compare(this, other);
    }
}
