package com.wynndevs.core;

import java.util.ArrayList;

public class Utils {

    public static String arrayWithCommas(ArrayList<String> values) {
        String total = "";

        for (String value : values) {
            if(total.equals("")) {
                total = value;
                continue;
            }
            total = total + ", " + value;
        }

        return total.endsWith(", ") ? total.substring(0, total.length() - 2) + "." : total + ".";
    }

}
