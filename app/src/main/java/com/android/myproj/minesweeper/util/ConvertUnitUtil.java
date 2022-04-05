package com.android.myproj.minesweeper.util;

import android.app.Activity;
import android.util.TypedValue;

public class ConvertUnitUtil {

    public static float convertPxToDP(Activity activity, float px) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px, activity.getResources().getDisplayMetrics()
        );
    }

    public static float convertDpToPx(Activity activity, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, dp, activity.getResources().getDisplayMetrics()
        );
    }

}
