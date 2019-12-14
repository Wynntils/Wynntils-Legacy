/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item;

import java.util.*;
import java.util.stream.Collectors;

public class IdentificationOrderer {

    public static IdentificationOrderer INSTANCE = new IdentificationOrderer(null, null);

    HashMap<String, Integer> order = new HashMap<>();
    ArrayList<String> groups = new ArrayList<>();

    transient HashMap<Integer, Integer> organizedGroups = null;

    public IdentificationOrderer(HashMap<String, Integer> idOrders, ArrayList<String> groupRanges) {}

    /**
     * @param id the identification "short" name. Ex: rawMainAttackNeutralDamage
     * @return the priority level, if not present returns -1
     */
    public int getOrder(String id) {
        return order.getOrDefault(id, -1);
    }

    /**
     * @param id the identification "short" name. Ex: rawMainAttackNeutralDamage
     * @return the group id, if not present returns -1
     */
    public int getGroup(String id) {
        if(organizedGroups == null) organizeGroups();

        return organizedGroups.getOrDefault(getOrder(id), -1);
    }

    /**
     * Order and returns a list of string based on the provided ids
     *
     * @param holder a map containg as key the "short" id name and as value the id lore
     * @param groups if ids should be grouped
     * @return a list with the ordered lore
     */
    public List<String> order(HashMap<String, String> holder, boolean groups) {
        List<String> result = new ArrayList<>();
        if(holder.isEmpty()) return result;

        //order based on the priority first
        List<Map.Entry<String, String>> ordered = holder.entrySet().stream()
                .sorted(Comparator.comparingInt(c -> getOrder(c.getKey())))
                .collect(Collectors.toList());

        if(groups) {
            int lastGroup = getGroup(ordered.get(0).getKey()); //first key group to avoid wrong spaces
            for (Map.Entry<String, String> keys : ordered) {
                int currentGroup = getGroup(keys.getKey()); //next key group

                if (currentGroup != lastGroup) result.add(" "); //adds a space before if the group is different

                result.add(keys.getValue());
                lastGroup = currentGroup;
            }

            return result;
        }

        ordered.forEach(c -> result.add(c.getValue()));
        return result;
    }

    private void organizeGroups() {
        organizedGroups = new HashMap<>();
        for(int id = 0; id < groups.size(); id++) {
            String groupRange = groups.get(id);

            String[] split = groupRange.split("-");

            int min = Integer.parseInt(split[0]);
            int max = Integer.parseInt(split[1]);

            //register each range into a reference
            for(int i = min; i <= max; i++) {
                organizedGroups.put(i, id);
            }
        }
    }

}
