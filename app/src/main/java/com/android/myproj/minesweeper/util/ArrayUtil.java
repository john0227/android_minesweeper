package com.android.myproj.minesweeper.util;

public class ArrayUtil {

    public static int[] range(int endExclusive) {
        return range(0, endExclusive);
    }

    public static int[] range(int startInclusive, int endExclusive) {
        int[] values = new int[endExclusive - startInclusive];
        for (int i = startInclusive; i < endExclusive; i++) {
            values[i - startInclusive] = i;
        }
        return values;
    }

}
