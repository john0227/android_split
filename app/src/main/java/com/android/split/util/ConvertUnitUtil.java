package com.android.split.util;

import android.app.Activity;
import android.util.TypedValue;

public class ConvertUnitUtil {

    public static float convertPxToDp(Activity activity, float px) {
        return px / activity.getResources().getDisplayMetrics().density;
    }

    public static float convertDpToPx(Activity activity, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, dp, activity.getResources().getDisplayMetrics()
        );
    }

}
