package com.wynntils.core.utils.helpers;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class SimpleRelativeDateFormatter extends DateFormat {

    HashMap<Integer, String> MAPPINGS = new HashMap<Integer, String>(){{
        put(Calendar.DAY_OF_YEAR, "d");
        put(Calendar.HOUR_OF_DAY, "h");
        put(Calendar.MINUTE, "m");
        put(Calendar.SECOND, "s");
    }};

    public SimpleRelativeDateFormatter() {
        setCalendar(Calendar.getInstance());
        setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        calendar.setTime(date);
        StringBuffer sb = new StringBuffer();

        MAPPINGS.forEach((key, value) -> {
            int count = calendar.get(key);
            if(key == Calendar.DAY_OF_YEAR) count--;
            if (count > 0) {
                sb.append(count).append(value).append(" ");
            }
        });
        return sb;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        throw new UnsupportedOperationException("Parsing relative time is not supported.");
    }
}
