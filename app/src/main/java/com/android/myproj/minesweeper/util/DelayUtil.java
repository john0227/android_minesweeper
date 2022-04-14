package com.android.myproj.minesweeper.util;

import android.os.Handler;
import android.os.Looper;

public class DelayUtil {

    public static void delayTask(Runnable task, long millis) {
        new Handler(Looper.getMainLooper()).postDelayed(task, millis);
    }

}
