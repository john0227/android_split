package com.android.split.util;

import java.text.DecimalFormat;

public class DecimalFormatUtil {

    public static String format(double d) {
        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(d);
        if (formatted.endsWith("00")) {
            formatted = formatted.substring(0, formatted.length() - 3);
        }
        return formatted;
    }

}
