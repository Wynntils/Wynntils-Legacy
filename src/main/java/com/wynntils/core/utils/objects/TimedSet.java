/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.utils.objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class TimedSet<T> implements Iterable<T> {

    private final HashSet<TimedEntry<T>> ENTRIES = new HashSet<>();

    long timeJump;
    boolean autoClear;

    public TimedSet(long duration, TimeUnit unit, boolean autoClear) {
        timeJump = unit.toMillis(duration);
        this.autoClear = autoClear;
    }

    public TimedSet(long duration, TimeUnit unit) {
        this(duration, unit, false);
    }

    public void releaseEntries() {
        Iterator<TimedEntry<T>> it = ENTRIES.iterator();
        while (it.hasNext()) {
            TimedEntry<T> entry = it.next();
            if (!entry.shouldRelease()) continue;

            it.remove();
        }
    }

    public void put(T entry) {
        ENTRIES.add(new TimedEntry<>(entry, System.currentTimeMillis() + timeJump));
    }

    public void clear() {
        ENTRIES.clear();
    }

    public int size() {
        return ENTRIES.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        if (autoClear) releaseEntries();

        return new Iterator<T>() {
            final Iterator<TimedEntry<T>> ORIGINAL = ENTRIES.iterator();

            @Override
            public boolean hasNext() {
                return ORIGINAL.hasNext();
            }

            @Override
            public T next() {
                return ORIGINAL.next().getEntry();
            }
        };
    }

    private class TimedEntry<G> {

        T entry;
        long expiration;

        public TimedEntry(T entry, long expiration) {
            this.entry = entry;
            this.expiration = expiration;
        }

        public long getExpiration() {
            return expiration;
        }

        public T getEntry() {
            return entry;
        }

        public boolean shouldRelease() {
            return System.currentTimeMillis() >= expiration;
        }

    }

}
