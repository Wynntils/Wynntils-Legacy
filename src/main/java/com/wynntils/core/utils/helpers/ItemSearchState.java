/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.helpers;

import com.wynntils.webapi.profiles.item.ItemProfile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemSearchState implements Predicate<ItemProfile>, Comparator<ItemProfile> {

    public static ItemSearchState parseSearchString(String searchStr) throws ItemFilter.FilteringException {
        // tokenize
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < searchStr.length(); i++) {
            char chr = searchStr.charAt(i);
            switch (chr) {
                case '"':
                    inQuotes = !inQuotes;
                    break;
                case ' ':
                    if (inQuotes) {
                        buf.append(' ');
                    } else {
                        String token = buf.toString().trim();
                        if (!token.isEmpty()) tokens.add(token);
                        buf = new StringBuilder();
                    }
                    break;
                default:
                    buf.append(chr);
                    break;
            }
        }

        if (inQuotes) throw new ItemFilter.FilteringException("Mismatched quotes!");

        // pick up a last token, if any
        {
            String token = buf.toString().trim();
            if (!token.isEmpty()) tokens.add(token);
        }

        // parse filters
        ItemSearchState searchState = new ItemSearchState();
        for (String token : tokens) searchState.addFilter(ItemFilter.parseFilterString(token));

        return searchState;
    }

    private final Map<ItemFilter.Type<?>, ItemFilter> filterTable = new LinkedHashMap<>();

    public void addFilter(ItemFilter filter) throws ItemFilter.FilteringException {
        ItemFilter.Type<?> type = filter.getFilterType();
        if (filterTable.containsKey(type)) {
            if (type == ItemFilter.ByName.TYPE) { // special-case: adjoin multiple by-name filters
                getFilter(ItemFilter.ByName.TYPE).adjoin((ItemFilter.ByName)filter);
                return;
            }
            throw new ItemFilter.FilteringException("Duplicate filters: " + type.getName());
        }
        filterTable.put(type, filter);
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemFilter> T getFilter(ItemFilter.Type<T> type) {
        return (T) filterTable.get(type);
    }

    public String toSearchString() {
        return filterTable.values().stream().map(ItemFilter::toFilterString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean test(ItemProfile item) {
        for (ItemFilter filter : filterTable.values()) {
            if (!filter.test(item)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compare(ItemProfile a, ItemProfile b) {
        for (ItemFilter filter : filterTable.values()) {
            int result = filter.compare(a, b);
            if (result != 0) return result;
        }

        // default to combat level, descending
        return Integer.compare(b.getRequirements().getLevel(), a.getRequirements().getLevel());
    }

}
