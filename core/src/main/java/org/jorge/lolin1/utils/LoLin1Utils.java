package org.jorge.lolin1.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.InitialActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.jorge.lolin1.utils.LoLin1DebugUtils.logString;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano on 03/01/14.
 * <p/>
 * Accessing resources through reflection is said to be ten times faster than through getResources(), and thus it's done when possible.
 */
public abstract class LoLin1Utils {

    private static final Map<String, Charset> charsetMap = new HashMap<>();

    public static String getCurrentForegroundActivityClass(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        logString("debug", "Class in top: " + taskInfo.get(0).topActivity.getClassName());
        return taskInfo.get(0).topActivity.getClassName();
    }

    public static void initCharsetMap() {
        charsetMap.put("tr_TR", Charset.forName("UTF-8"));
        charsetMap.put("en_US", Charset.forName("UTF-8"));
        charsetMap.put("es_ES", Charset.forName("UTF-8"));
        charsetMap.put("de_DE", Charset.forName("UTF-8"));
        charsetMap.put("fr_FR", Charset.forName("UTF-8"));
        charsetMap.put("it_IT", Charset.forName("UTF-8"));
        charsetMap.put("pt_PT", Charset.forName("UTF-8"));
        charsetMap.put("el_GR", Charset.forName("UTF-8"));
        charsetMap.put("pl_PL", Charset.forName("UTF-8"));
        charsetMap.put("ro_RO", Charset.forName("UTF-8"));
        charsetMap.put("cs_CZ", Charset.forName("UTF-8"));
        charsetMap.put("hu_HU", Charset.forName("UTF-8"));
        charsetMap.put("ko_KR", Charset.forName("UTF-8"));
        charsetMap.put("ru_RU", Charset.forName("UTF-8"));
    }

    /**
     * This is bad, very bad, but the problems only show on some devices and my time window is gone.
     */
    public static void configureStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static Charset getLocaleCharset(String locale) {
        return charsetMap.containsKey(locale) ? charsetMap.get(locale) :
                Charset.forName(locale);
    }

    public static int dpToPx(Resources res, int dp) {
        return (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static void restartApp(Activity activity) {
        activity.startActivity(new Intent(activity, InitialActivity.class));
        Intent i = activity.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
    }

    public static Boolean setLocale(Context baseContext, String newLocale) {
        if (!isLocaleSupported(baseContext.getApplicationContext(), newLocale)) {
            return Boolean.FALSE;
        }
        Locale locale = new Locale(newLocale
                .substring(0, LoLin1Utils.getInt(baseContext.getApplicationContext(), "locale_length", 4)));
        Locale.setDefault(locale);
        Configuration config = baseContext.getApplicationContext().getResources().getConfiguration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(baseContext.getApplicationContext()).edit();
        editor.putString("pref_title_locale", newLocale);
        editor.apply();
        return Boolean.TRUE;
    }

    public static String getRealm(Context context) {

        return context != null ? PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(
                "pref_title_server",
                "euw").toLowerCase(Locale.ENGLISH) : "";
    }

    public static String getLocale(Context context) {
        String ret;
        try {
            ret = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                    .getString("pref_title_locale", "en_US");
        } catch (NullPointerException ex) {
            ret = null;
        }

        return ret;
    }

    public static String[] getStringArray(Context context, String variableName,
                                          String[] defaultRet) {
        String[] ret = defaultRet;

        try {
            Field resourceField = R.array.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getResources().getStringArray(resourceId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Crashlytics.logException(e);
        }

        return ret;
    }

    public static String getString(Context context, String variableName, String defaultRet) {
        String ret = defaultRet;

        try {
            Field resourceField = R.string.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            if (context != null)
                ret = context.getString(resourceId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Crashlytics.logException(e);
        }

        return ret;
    }

    public static int getDrawableAsId(String variableName, int defaultRet) {
        int ret = defaultRet;

        try {
            Field resourceField = R.drawable.class.getDeclaredField(variableName);
            ret = resourceField.getInt(resourceField);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Crashlytics.logException(e);
        }

        return ret;
    }

    public static Boolean isInternetReachable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean ret;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI),
                dataNetworkInfo =
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isWifiConnected =
                (wifiNetworkInfo == null) ? Boolean.FALSE : wifiNetworkInfo.isConnected(),
                isDataConnected =
                        (dataNetworkInfo == null) ? Boolean.FALSE :
                                dataNetworkInfo.isConnected();
        ret = isWifiConnected || (preferences
                .getBoolean("pref_title_data",
                        Boolean.FALSE) && isDataConnected);

        return ret;
    }

    public static int getInt(Context context, String variableName, int defaultRet) {
        int ret = defaultRet;

        try {
            Field resourceField = R.integer.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getResources().getInteger(resourceId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Crashlytics.logException(e);
        }

        return ret;
    }

    public static Boolean setRealm(Context baseContext, String newRealm) {
        if (!isRealmSupported(baseContext.getApplicationContext(), newRealm.toLowerCase(Locale.ENGLISH))) {
            return Boolean.FALSE;
        }

        Log.d("realmisnull", "Setting realm " + newRealm);

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(baseContext.getApplicationContext()).edit();

        editor.putString(
                "pref_title_server",
                newRealm.toLowerCase(Locale.ENGLISH)
        );

        editor.apply();

        return Boolean.TRUE;
    }

    private static Boolean isRealmSupported(Context context, String realm) {
        String[] realms =
                LoLin1Utils
                        .getStringArray(context, "servers",
                                null);

        return Arrays.asList(realms).contains(realm);
    }

    private static Boolean isLocaleSupported(Context context, String locale) {
        String[] locales =
                LoLin1Utils
                        .getStringArray(context, "langs_simplified", null);

        return Arrays.asList(locales).contains(locale);
    }

    public static int pixelsAsDp(Context context, int sizeInPx) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInPx * scale + 0.5f);
    }

    public static String inputStreamAsString(InputStream is, String locale) throws IOException {
        java.util.Scanner s =
                new java.util.Scanner(is, LoLin1Utils.getLocaleCharset(locale).name());
        String ret;
        ret = s.useDelimiter("\\A").hasNext() ? s.next() : "";
        return ret;
    }
}