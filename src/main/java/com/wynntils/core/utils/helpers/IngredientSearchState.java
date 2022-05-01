package com.wynntils.core.utils.helpers;

import com.wynntils.webapi.profiles.ingredient.IngredientProfile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IngredientSearchState implements Predicate<IngredientProfile>, Comparator<IngredientProfile> {
    public static IngredientSearchState parseSearchString(String search) throws IngredientFilter.FilteringException {
        // tokenize
        List<String> tokens = new ArrayList<>();
        StringBuilder buf = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < search.length(); i++) {
            char chr = search.charAt(i);
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

        if (inQuotes) throw new IngredientFilter.FilteringException("Mismatched quotes!");

        // pick up a last token, if any
        {
            String token = buf.toString().trim();
            if (!token.isEmpty()) tokens.add(token);
        }

        // parse filters
        IngredientSearchState searchState = new IngredientSearchState();
        for (String token : tokens) searchState.addFilter(IngredientFilter.parseFilterString(token));

        return searchState;
    }

    private final Map<IngredientFilter.Type<?>, IngredientFilter> filterTable = new LinkedHashMap<>();

    public void addFilter(IngredientFilter filter) throws IngredientFilter.FilteringException {
        IngredientFilter.Type<?> type = filter.getFilterType();
        if (filterTable.containsKey(type)) {
            if (type == IngredientFilter.ByName.TYPE) {
                getFilter(IngredientFilter.ByName.TYPE).adjoin((IngredientFilter.ByName) filter);
                return;
            }
            throw new IngredientFilter.FilteringException("Duplicate filters: " + type.getName());
        }
        filterTable.put(type, filter);
    }

    @SuppressWarnings("unchecked")
    public <T extends IngredientFilter> T getFilter(IngredientFilter.Type<T> type) {
        return (T) filterTable.get(type);
    }

    public String toSearchString() {
        return filterTable.values().stream().map(IngredientFilter::toFilterString).collect(Collectors.joining(" "));
    }

    @Override
    public boolean test(IngredientProfile item) {
        for (IngredientFilter filter : filterTable.values()) {
            if (!filter.test(item)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int compare(IngredientProfile a, IngredientProfile b) {
        for (IngredientFilter filter : filterTable.values()) {
            int result = filter.compare(a, b);
            if (result != 0) return result;
        }

        return Integer.compare(b.getLevel(), a.getLevel());
    }
}
