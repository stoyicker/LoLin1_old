package org.jorge.lolin1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.SQLiteDAO;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

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
 * Created by JorgeAntonio on 03/01/14.
 * <p/>
 * Accessing resources through reflection is said to be ten times faster than through getResources(), and thus it's done when possible.
 */
public abstract class LoLin1Utils {

    public static Boolean setLocale(Context baseContext, String newLocale) {
        if (!isLocaleSupported(baseContext, newLocale)) {
            return Boolean.FALSE;
        }
        Locale locale = new Locale(newLocale
                .substring(0, LoLin1Utils.getInt(baseContext, "locale_length", 2)));
        Locale.setDefault(locale);
        Configuration config = baseContext.getResources().getConfiguration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config,
                baseContext.getResources().getDisplayMetrics());
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(baseContext).edit();
        editor.putString(getString(baseContext, "pref_title_lang", "error_pref_title_lang"),
                newLocale);
        editor.commit();
        return Boolean.TRUE;
    }

    public static String getRealm(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                LoLin1Utils.getString(context, "pref_title_server", "League of Legends server"),
                "null").toLowerCase();
    }

    public static String getLocale(Context context) {
        String ret;
        try {
            ret = LoLin1Utils.getStringArray(context, "langs_simplified", null)[
                    Arrays.asList(LoLin1Utils.getStringArray(context, "langs", null)).indexOf(
                            PreferenceManager.getDefaultSharedPreferences(context).getString(
                                    LoLin1Utils
                                            .getString(context, "pref_title_lang", "Language"),
                                    "null"
                            )
                    )];
        }
        catch (NullPointerException ex) {
            ret = "null";
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
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }

    public static String getString(Context context, String variableName, String defaultRet) {
        String ret = defaultRet;

        try {
            Field resourceField = R.string.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getString(resourceId);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }

    public static int getDrawableAsId(String variableName, int defaultRet) {
        int ret = defaultRet;

        try {
            Field resourceField = R.drawable.class.getDeclaredField(variableName);
            ret = resourceField.getInt(resourceField);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }

    public static Boolean isInternetReachable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean ret;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI), dataNetworkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isWifiConnected =
                (wifiNetworkInfo == null) ? Boolean.FALSE : wifiNetworkInfo.isConnected(),
                isDataConnected =
                        (dataNetworkInfo == null) ? Boolean.FALSE : dataNetworkInfo.isConnected();
        ret = isWifiConnected || (preferences
                .getBoolean(getString(context, "pref_title_data", "pref_title_data"),
                        Boolean.FALSE) && isDataConnected);

        return ret;
    }

    public static boolean tableExists(String tableName) {
        SQLiteDatabase db = SQLiteDAO.getSingleton().getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'",
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static int getInt(Context context, String variableName, int defaultRet) {
        int ret = defaultRet;

        try {
            Field resourceField = R.integer.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getResources().getInteger(resourceId);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }

    public static Boolean setRealm(Context baseContext, String newRealm) {
        if (!isRealmSupported(baseContext, newRealm.toLowerCase())) {
            return Boolean.FALSE;
        }

        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(baseContext).edit();

        editor.putString(
                LoLin1Utils.getString(baseContext, "pref_title_server", "League of Legends server"),
                newRealm.toLowerCase());

        editor.commit();

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
}