package org.jorge.lolin1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.ByteBuffer;
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

    public static final String convertStreamToString(BufferedInputStream in) throws IOException {
        StringBuilder sb = new StringBuilder(Math.max(16, in.available()));
        char[] tmp = new char[4096];

        in.mark(Integer.MAX_VALUE);

        InputStreamReader reader = new InputStreamReader(in, Charset.forName("utf-8"));
        for (int cnt; (cnt = reader.read(tmp)) > 0; )
            sb.append(tmp, 0, cnt);

        in.reset();

        return sb.toString();
    }

    public static final String[] getStringArray(Context context, String variableName,
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

    public static final String[] getStringArrayRegular(Context context, int variableId,
                                                       String[] defaultRet) {
        String[] ret = defaultRet;

        try {
            ret = context.getResources().getStringArray(variableId);
        }
        catch (Resources.NotFoundException e) {
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

    public static String getTableName(Context context) {
        String prefix =
                Utils.getString(context, "news_euw_en", "http://feed43.com/lolnews_euw_en.xml")
                        .replaceAll(NewsToSQLiteBridge.LOLNEWS_FEED_HOST, "")
                        .replaceAll(NewsToSQLiteBridge.LOLNEWS_FEED_EXTENSION, "")
                        .replaceAll("_(.*)", "") + "_";
        String ret, server, lang, langSimplified;
        final String ERROR = "PREF_NOT_FOUND", defaultTableName = "EUW_EN", defaultLanguage =
                "ENGLISH";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        server = preferences.getString(Utils.getString(context, "pref_title_server", "nvm"), ERROR);
        lang = preferences.getString(Utils.getString(context, "pref_title_lang", "nvm"), ERROR);

        if (server.contentEquals(ERROR) || lang.contentEquals(ERROR)) {
            ret = prefix + defaultTableName;
        }
        else {
            langSimplified = getStringArray(context, "langs_simplified",
                    new String[]{defaultLanguage})[new ArrayList<>(
                    Arrays.asList(getStringArray(context, "langs",
                            new String[]{defaultLanguage})))
                    .indexOf(lang)];
            ret = prefix + server + "_" + langSimplified;
        }

        return ret.toUpperCase();
    }

    public static final Bitmap getArticleBitmap(Context context, byte[] blob,
                                                final String callbackURL) {
        Bitmap ret = null;
        if (blob == null) {
            if (Utils.isInternetReachable(context)) {
                try {
                    ret = BitmapFactory
                            .decodeStream(
                                    new URL(callbackURL).openConnection()
                                            .getInputStream());
                }
                catch (IOException e) {
                    ret = null;
                }
                int size = ret.getRowBytes() * ret.getHeight();
                ByteBuffer b = ByteBuffer.allocate(size);
                byte[] downloadedBitmapAsByteArray = new byte[b.remaining()];
                ret.copyPixelsToBuffer(b);
                b.rewind();
                b.get(downloadedBitmapAsByteArray, 0,
                        downloadedBitmapAsByteArray.length);
                NewsToSQLiteBridge.getSingleton()
                        .updateArticleBlob(downloadedBitmapAsByteArray, callbackURL);
                return ret;
            }
        }
        else {
            ret = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return ret;
    }

    public static boolean tableExists(String tableName) {
        SQLiteDatabase db = NewsToSQLiteBridge.getSingleton().getReadableDatabase();

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
}