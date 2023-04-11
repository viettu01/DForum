package com.tuplv.dforum.until;

import android.annotation.SuppressLint;

public class Until {
    @SuppressLint("DefaultLocale")
    public static String formatNumber(long number){
        if (number >= 1000 && number < 1000000) {
            return String.format("%.1f", number / 1000.0) + " K";
        } else if (number >= 1000000 && number < 1000000000) {
            return String.format("%.1f", number / 1000000.0) + " Tr";
        } else if (number >= 1000000000) {
            return String.format("%.1f", number / 1000000000.0) + " B";
        }
        else {
            return String.valueOf(number);
        }
    }
}
