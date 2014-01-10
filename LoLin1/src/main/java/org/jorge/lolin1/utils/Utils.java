package org.jorge.lolin1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

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
public abstract class Utils {

    public static final String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder(Math.max(16, is.available()));
        char[] tmp = new char[4096];

        try {
            InputStreamReader reader = new InputStreamReader(is, Charset.forName("utf-8"));
            for (int cnt; (cnt = reader.read(tmp)) > 0; )
                sb.append(tmp, 0, cnt);
        }
        finally {
            is.close();
        }
        return sb.toString();
    }

    public static final String[] getStringArray(Context context, String variableName, String[] defaultRet) {
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

    public static final String getString(Context context, String variableName, String defaultRet) {
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

    public static final int getDrawableAsId(String variableName, int defaultRet) {
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

    public static final Boolean isInternetReachable(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean ret;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI), dataNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isWifiConnected = (wifiNetworkInfo == null) ? Boolean.FALSE : wifiNetworkInfo.isConnected(), isDataConnected = (dataNetworkInfo == null) ? Boolean.FALSE : dataNetworkInfo.isConnected();
        ret = isWifiConnected || (preferences.getBoolean(getString(context, "pref_title_data", "pref_title_data"), Boolean.FALSE) && isDataConnected);

        return ret;
    }

    public static String getTableName(Context context) {
        String prefix = Utils.getString(context, "news_euw_en", "http://feed43.com/lolnews_euw_en.xml").replaceAll(NewsToSQLiteBridge.LOLNEWS_FEED_HOST, "").replaceAll(NewsToSQLiteBridge.LOLNEWS_FEED_EXTENSION, "").replaceAll("_(.*)", "") + "_";
        String ret, server, lang, langSimplified;
        final String ERROR = "PREF_NOT_FOUND", defaultTableName = "EUW_ENGLISH", defaultLanguage = "ENGLISH";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        server = preferences.getString("pref_title_server", ERROR);
        lang = preferences.getString("pref_title_lang", ERROR);

        if (server.contentEquals(ERROR) || lang.contentEquals(ERROR)) {
            ret = prefix + defaultTableName;
        }
        else {
            langSimplified = getStringArray(context, "langs_simplified", new String[]{defaultLanguage})[new ArrayList<>(Arrays.asList(getStringArray(context, "langs", new String[]{defaultLanguage}))).indexOf(lang)];
            ret = prefix + server + "_" + langSimplified;
        }

        return ret.toUpperCase();
    }
}