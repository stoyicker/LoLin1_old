package org.jorge.lolin1.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
 * Created by JorgeAntonio on 04/01/14.
 */
public class NewsToSQLiteBridge extends SQLiteOpenHelper {

    public static final String LOLNEWS_FEED_HOST = "http://feed43.com/", LOLNEWS_FEED_EXTENSION = ".xml", KEY_ID = "ARTICLE_ID", KEY_TITLE = "ARTICLE_TITLE", KEY_BLOB = "ARTICLE_BLOB", KEY_URL = "ARTICLE_URL", KEY_DESC = "ARTICLE_DESC", KEY_IMG_URL = "ARTICLE_IMG_URL";
    private static NewsToSQLiteBridge singleton;
    private static Context mContext;


//    public void asyncExecute(String command) {
//        String cmdType;
//        switch (cmdType = new StringTokenizer(command).nextToken(" ").toUpperCase()) {
//            case "INSERT":
//            case "UPDATE":
//            case "DELETE":
//                try {
//                    throw new IllegalArgumentException("Method " + NewsToSQLiteBridge.class.getName() + " " + NewsToSQLiteBridge.class.getDeclaredMethod("asyncExecute", String.class) + " should not be used for " + cmdType + "-type requests. See http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#execSQL(java.lang.String)");
//                } catch (NoSuchMethodException e) {
//                    Log.e("ERROR", "Exception", e);
//                }
//            default:
//                new AsyncTask<String, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(String... command) {
//                        NewsToSQLiteBridge.this.getWritableDatabase().execSQL(command[0].toUpperCase());
//                        return null;
//                    }
//                }.execute();
//        }
//    }

    private NewsToSQLiteBridge(Context _context) {
        super(_context, Utils.getString(_context, "db_name", "LoLin1_DB"), null, 1);
    }

    public static void setup(Context _context) {
        if (singleton == null) {
            singleton = new NewsToSQLiteBridge(_context);
            mContext = _context;
        }
    }

    public static NewsToSQLiteBridge getSingleton() {
        if (singleton == null) {
            throw new RuntimeException("Use method NewsToSQLiteBridge.setup(Context _context) at least once before calling this method.");
        }
        return singleton;
    }

    public byte[] getNewsBlob(String imgUrl) {
        byte[] ret;
        String tableName = Utils.getTableName(mContext);
        SQLiteDatabase db = getReadableDatabase();
        Cursor result;

        db.beginTransaction();
        result = getReadableDatabase().query(tableName, new String[]{KEY_BLOB}, "?s = ?s", new String[]{KEY_IMG_URL, imgUrl}, null, null, null, null);
        result.moveToFirst();
        ret = result.getBlob(0);
        db.setTransactionSuccessful();

        db.endTransaction();
        return ret;
    }

    public final ArrayList<HashMap<String, String>> getNews() {
        return getFilteredNews(new String[]{KEY_TITLE, KEY_DESC, KEY_IMG_URL}, null);
    }

    public final ArrayList<HashMap<String, String>> getNewNews(int alreadyShown) {
        return getFilteredNews(new String[]{KEY_TITLE, KEY_DESC, KEY_IMG_URL}, KEY_ID + " > " + alreadyShown);
    }

    private final ArrayList<HashMap<String, String>> getFilteredNews(String[] fields, String whereClause) {
        ArrayList<HashMap<String, String>> ret = new ArrayList<>();
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));

        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        String tableName = Utils.getTableName(mContext);
        Cursor result = db.query(tableName, fields, whereClause, null, null, null, KEY_ID + " DESC");
        HashMap<String, String> row;
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            row = new HashMap<>();
            for (String x : fieldsAsList) {
                row.put(x, result.getString(result.getColumnIndex(x)));
            }
            ret.add(row);
        }
        result.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    public final String getNewsUrl(int position) {
        long count, interestIndex;
        String ret, queryUrl, tableName = Utils.getTableName(mContext);

        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        count = DatabaseUtils.queryNumEntries(db, tableName);
        interestIndex = count - position;
        queryUrl = "SELECT " + KEY_URL + " FROM " + tableName + " WHERE " + KEY_ID + " = " + interestIndex;
        Cursor cursorUrl = db.rawQuery(queryUrl, null);
        cursorUrl.moveToFirst();
        ret = cursorUrl.getString(0);
        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    public final void updateNewsBlob(byte[] blob, String callbackUrl) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = Utils.getTableName(mContext);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_BLOB, blob);

        db.beginTransaction();
        db.update(tableName, contentValues, KEY_URL + " = " + callbackUrl, null);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * @param values {@link android.content.ContentValues} The values to insert. The most recent article is the latest one
     * @return The rowID of the newly inserted row, or -1 if any error happens
     */
    public long insertNews(ContentValues values) {
        long ret;
        SQLiteDatabase db = getWritableDatabase();
        String tableName = Utils.getTableName(mContext);
        db.beginTransaction();
        ret = db.insert(tableName, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    //    /**
//     * Simplified 'DELETE FROM TABLE' implementation that allows only for a single field matching check.
//     *
//     * @param fromTable   {@link String} The name of the table
//     * @param paramName   {@link String} The parameter to be checked
//     * @param targetValue {@link String} The value to check the parameter against
//     * @return the number of rows affected, or -1 if any error happens
//     */
//    public long simpleDelete(String fromTable, String paramName, String targetValue) {
//        long ret;
//        SQLiteDatabase db = getWritableDatabase();
//        db.beginTransaction();
//        ret = db.delete(fromTable, "WHERE ? = ?", new String[]{paramName.toUpperCase(), targetValue.toUpperCase()});
//        db.setTransactionSuccessful();
//        db.endTransaction();
//        return ret;
//    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        HashSet<String> tableNames = new HashSet<>();
        ArrayList<String> langsInThisServer, servers = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.servers)));
        String prefix = Utils.getString(mContext, "news_euw_en", "http://feed43.com/lolnews_euw_en.xml").replaceAll(LOLNEWS_FEED_HOST, "").replaceAll(LOLNEWS_FEED_EXTENSION, "").replaceAll("_(.*)", "") + "_";

        for (String x : servers) {
            langsInThisServer = new ArrayList<>();
            for (String y : Utils.getStringArray(mContext, "lang_" + x.toLowerCase() + "_simplified", new String[]{""})) {
                langsInThisServer.add((prefix + x + "_" + y).toUpperCase());
            }
            tableNames.addAll(langsInThisServer);
        }

        sqLiteDatabase.beginTransaction();
        for (Iterator<String> it = tableNames.iterator(); it.hasNext(); ) {
            String tableName = it.next();
            sqLiteDatabase.execSQL(("CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                    KEY_TITLE + " TEXT NOT NULL ON CONFLICT FAIL, " +
                    KEY_BLOB + " BLOB, " +
                    KEY_URL + " TEXT NOT NULL ON CONFLICT FAIL UNIQUE ON CONFLICT IGNORE, " +
                    KEY_DESC + " TEXT, " +
                    KEY_IMG_URL + " TEXT NOT NULL ON CONFLICT FAIL " +
                    ")").toUpperCase());
        }

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //At the moment no further database versions are planned.
    }
}