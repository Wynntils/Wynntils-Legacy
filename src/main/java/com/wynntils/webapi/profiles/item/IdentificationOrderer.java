/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item;

import java.util.ArrayList;
import java.util.HashMap;

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

    private void organizeGroups() {
        organizedGroups = new HashMap<>();
        for(int id = 0; id < groups.size(); id++) {
            String groupRange = groups.get(id);

            String[] split = groupRange.split("-");

            int min = Integer.valueOf(split[0]);
            int max = Integer.valueOf(split[1]);

            //register each range into a reference
            for(int i = min; i <= max; i++) {
                organizedGroups.put(i, id);
            }
        }
    }

}
