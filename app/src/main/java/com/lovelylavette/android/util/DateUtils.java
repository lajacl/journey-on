package com.lovelylavette.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_PATTERN = "EEE, MMM dd";

    public static String getFormattedRange(Calendar date1, Calendar date2) {
        return String.format("%s - %s", formatDate(date1),
                formatDate(date2));
    }

    public static String getFormattedDate(Calendar date) {
        return String.format("%s", formatDate(date));
    }

    private static String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
