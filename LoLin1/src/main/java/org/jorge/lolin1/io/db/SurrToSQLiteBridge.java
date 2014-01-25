package org.jorge.lolin1.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jorge.lolin1.feeds.surr.SurrEntry;
import org.jorge.lolin1.utils.Utils;

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
 * Created by JorgeAntonio on 04/01/14.
 */
public class SurrToSQLiteBridge extends SQLiteOpenHelper {

    public static final String SURR_KEY_ID = "ARTICLE_ID", SURR_KEY_TITLE = "ARTICLE_TITLE",
            SURR_KEY_LINK = "ARTICLE_LINK", SURR_KEY_PUBLISHED = "ARTICLE_PUBLISHED",
            SURR_KEY_UPDATED = "ARTICLE_UPDATED", SURR_KEY_READ = "ARTICLE_READ";
    private static final String SURR_TABLE_NAME = "SURRENDER";
    private static SurrToSQLiteBridge singleton;
    private static Context mContext;

    private SurrToSQLiteBridge(Context _context) {
        super(_context, Utils.getString(_context, "db_name", "LoLin1_DB"), null, 1);
    }

    public static void setup(Context _context) {
        if (singleton == null) {
            singleton = new SurrToSQLiteBridge(_context);
            mContext = _context;
        }
    }

    public static SurrToSQLiteBridge getSingleton() {
        if (singleton == null) {
            throw new RuntimeException(
                    "Use method SurrToSQLiteBridge.setup(Context _context) at least once before calling this method.");
        }
        return singleton;
    }

    public Integer updateArticle(String title, ContentValues row) {
        SQLiteDatabase db = getWritableDatabase();
        Integer ret;

        db.beginTransaction();
        ret = db.update(SURR_TABLE_NAME, row, SURR_KEY_TITLE + " = '" + title + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    public final ArrayList<SurrEntry> getNewSurrs(int alreadyShown) {
        return getFilteredSurrs(SURR_KEY_ID + " > " + alreadyShown);
    }

    private final ArrayList<SurrEntry> getFilteredSurrs(String whereClause) {

        ArrayList<SurrEntry> ret = new ArrayList<>();
        String[] fields =
                new String[]{SURR_KEY_TITLE, SURR_KEY_LINK, SURR_KEY_PUBLISHED, SURR_KEY_UPDATED,
                        SURR_KEY_READ};
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));

        SQLiteDatabase db = getReadableDatabase();
        db.beginTransaction();
        Cursor result =
                db.query(SURR_TABLE_NAME, fields, whereClause, null, null, null,
                        SURR_KEY_ID + " DESC");
        StringBuilder data;
        final String separator = SurrEntry.getSEPARATOR();
        Boolean read = Boolean.FALSE;
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            data = new StringBuilder("");
            for (String x : fieldsAsList) {
                if (!x.contentEquals(SURR_KEY_READ)) {
                    data.append(result.getString(result.getColumnIndex(x))).append(separator);
                }
                else {
                    read = result.getInt(result.getColumnIndex(x)) == 1;
                }
            }
            ret.add(new SurrEntry(data.toString(), read));
        }
        result.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    /**
     * @param values {@link android.content.ContentValues} The values to insert. The most recent article is the latest one
     * @return The rowID of the newly inserted row, or -1 if any error happens
     */
    public long insertArticle(ContentValues values) {
        long ret;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ret = db.insert(SURR_TABLE_NAME, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    public SurrEntry getSurrByTitle(String surrTitle) {
        SurrEntry ret = null;
        String[] fields =
                new String[]{SURR_KEY_TITLE, SURR_KEY_LINK, SURR_KEY_PUBLISHED, SURR_KEY_UPDATED,
                        SURR_KEY_READ};
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));
        SQLiteDatabase db = getReadableDatabase();

        Cursor result =
                db.query(SURR_TABLE_NAME, fields, SURR_KEY_TITLE + " = '" + surrTitle + "'", null,
                        null, null,
                        SURR_KEY_ID + " DESC");
        StringBuilder data = null;
        String separator = SurrEntry.getSEPARATOR();
        Boolean read = Boolean.FALSE;
        for (String x : fieldsAsList) {
            if (!x.contentEquals(SURR_KEY_READ)) {
                data.append(result.getString(result.getColumnIndex(x))).append(separator);
            }
            else {
                read = result.getInt(result.getColumnIndex(x)) == 1;
            }
        }
        ret = new SurrEntry(data.toString(), read);
        result.moveToFirst();

        result.close();

        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.beginTransaction();

        sqLiteDatabase.execSQL(("CREATE TABLE IF NOT EXISTS " + SURR_TABLE_NAME + " ( " +
                SURR_KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                SURR_KEY_TITLE + " TEXT UNIQUE NOT NULL ON CONFLICT FAIL, " +
                SURR_KEY_PUBLISHED + " TEXT NOT NULL ON CONFLICT FAIL, " +
                SURR_KEY_UPDATED + " TEXT ON CONFLICT FAIL, " +
                SURR_KEY_LINK + " TEXT NOT NULL ON CONFLICT FAIL UNIQUE ON CONFLICT FAIL, " +
                SURR_KEY_READ + " BOOLEAN NOT NULL " +
                ")").toUpperCase());

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //At the moment no further database versions are planned.
    }
}