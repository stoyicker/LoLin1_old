package com.github.amlcurran.showcaseview;

import android.os.Build;
import android.view.View;

public class ApiUtils {

    public boolean isCompatWith(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

//    public boolean isCompatWithHoneycomb() {
//        return isCompatWith(Build.VERSION_CODES.HONEYCOMB);
//    }

    public void setFitsSystemWindowsCompat(View view) {
        if (isCompatWith(Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            view.setFitsSystemWindows(true);
        }
    }
}
