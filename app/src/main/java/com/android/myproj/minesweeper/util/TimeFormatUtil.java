package com.android.myproj.minesweeper.util;

import java.util.Locale;

public class TimeFormatUtil {

    public static String formatTime(int minute, int second) {
        return String.format(Locale.US, "%d:%02d", minute, second);
    }

    public static String formatTime(int minute, int second, int millis) {
        return String.format(Locale.US, "%d:%02d:%03d", minute, second, millis);
    }

}
