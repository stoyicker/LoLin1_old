package org.jorge.lolin1.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.news.NewsEntry;
import org.jorge.lolin1.func.feeds.surr.SurrEntry;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 04/01/14.
 */
public class SQLiteDAO extends SQLiteOpenHelper {

    public static final String SURR_KEY_ID = "ARTICLE_ID", SURR_KEY_TITLE = "ARTICLE_TITLE",
            SURR_KEY_LINK = "ARTICLE_LINK", SURR_KEY_PUBLISHED = "ARTICLE_PUBLISHED",
            SURR_KEY_UPDATED = "ARTICLE_UPDATED", SURR_KEY_READ = "ARTICLE_READ", SURR_TABLE_NAME =
            "SURRENDER", LOLNEWS_FEED_HOST = "http://feed43.com/", LOLNEWS_FEED_EXTENSION =
            ".xml", NEWS_KEY_ID = "ARTICLE_ID", NEWS_KEY_TITLE = "ARTICLE_TITLE", NEWS_KEY_BLOB =
            "ARTICLE_BLOB", NEWS_KEY_URL = "ARTICLE_URL", NEWS_KEY_DESC = "ARTICLE_DESC",
            NEWS_KEY_IMG_URL =
                    "ARTICLE_IMG_URL";
    private static SQLiteDAO singleton;
    private static Context mContext;

    private SQLiteDAO(Context _context) {
        super(_context, LoLin1Utils.getString(_context, "db_name", "LoLin1_DB"), null, 1);
    }

    public static boolean tableExists(String tableName) {
        SQLiteDatabase db = SQLiteDAO.getSingleton().getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName +
                        "'",
                null
        );
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public static void setup(Context _context) {
        if (singleton == null) {
            singleton = new SQLiteDAO(_context);
            mContext = _context;
        }
    }

    public static SQLiteDAO getSingleton() {
        if (singleton == null) {
            throw new RuntimeException(
                    "Use method SQLiteDAO.setup(Context _context) at least once before calling this method.");
        }
        return singleton;
    }

    public static String getNewsTableName() {
        final String prefix =
                LoLin1Utils
                        .getString(mContext, "news_euw_en", "http://feed43.com/lolnews_euw_en.xml")
                        .replaceAll(SQLiteDAO.LOLNEWS_FEED_HOST, "")
                        .replaceAll(SQLiteDAO.LOLNEWS_FEED_EXTENSION, "")
                        .replaceAll("_(.*)", "") + "_";

        if (mContext == null) return "LOLNEWS_EUW_EN";

        return prefix.toUpperCase(Locale.ENGLISH) + LoLin1Utils.getRealm(mContext).toUpperCase(Locale.ENGLISH) + "_" +
                LoLin1Utils.getLocale(mContext).toUpperCase(Locale.ENGLISH);
    }

    public static Bitmap getNewsArticleBitmap(Context context, byte[] blob,
                                              final String imageLinkCallbackURL) {
        Bitmap ret = null;
        if (blob == null) {
            if (LoLin1Utils.isInternetReachable(context)) {
                try {
                    ret = BitmapFactory
                            .decodeStream(
                                    new URL(imageLinkCallbackURL).openConnection()
                                            .getInputStream()
                            );
                } catch (IOException e) {
                    ret = null;
                }
                ByteArrayOutputStream blobOS = new ByteArrayOutputStream();
                assert ret != null;
                ret.compress(Bitmap.CompressFormat.PNG, 0, blobOS);
                byte[] bmpAsByteArray = blobOS.toByteArray();
                SQLiteDAO.getSingleton()
                        .updateArticleBlob(bmpAsByteArray, imageLinkCallbackURL);
                return ret;
            }
        } else {
            ret = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return ret;
    }

    public final ArrayList<SurrEntry> getSurrs() {
        return getFilteredSurrs(null);
    }

    public int markSurrAsRead(String link) {
        SQLiteDatabase db = getWritableDatabase();
        int ret;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SURR_KEY_READ, Boolean.TRUE);

        assert db != null;
        db.beginTransaction();
        ret = db.update(SURR_TABLE_NAME, contentValues,
                SURR_KEY_LINK + "='" + link.replaceAll("http://", "httpxxx") + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    public SurrEntry getSurrByLink(String surrLink) {
        SurrEntry ret = null;
        String[] fields =
                new String[]{SURR_KEY_TITLE, SURR_KEY_LINK, SURR_KEY_PUBLISHED, SURR_KEY_UPDATED,
                        SURR_KEY_READ};
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));
        StringBuilder data = new StringBuilder("");
        String separator = SurrEntry.getSEPARATOR();
        Boolean read = Boolean.FALSE;
        SQLiteDatabase db = getReadableDatabase();

        assert db != null;
        db.beginTransaction();
        Cursor result =
                db.query(SURR_TABLE_NAME, fields,
                        SURR_KEY_LINK + " = '" + surrLink.replaceAll("http://", "httpxxx") + "'",
                        null,
                        null, null,
                        SURR_KEY_ID + " DESC");

        if (result.moveToFirst()) {

            for (String x : fieldsAsList) {
                if (!x.contentEquals(SURR_KEY_READ)) {
                    data.append(result.getString(result.getColumnIndex(x))).append(separator);
                } else {
                    read = result.getInt(result.getColumnIndex(x)) == 1;
                }
            }
            ret = new SurrEntry(data.toString(), read);
        }

        result.close();

        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    private ArrayList<SurrEntry> getFilteredSurrs(String whereClause) {

        ArrayList<SurrEntry> ret = new ArrayList<>();
        String[] fields =
                new String[]{SURR_KEY_TITLE, SURR_KEY_LINK, SURR_KEY_PUBLISHED, SURR_KEY_UPDATED,
                        SURR_KEY_READ};
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;
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
                } else {
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

    public Integer updateSurrArticleByLink(String link, ContentValues row) {
        SQLiteDatabase db = getWritableDatabase();
        Integer ret;

        assert db != null;
        db.beginTransaction();
        ret = db.update(SURR_TABLE_NAME, row,
                SURR_KEY_LINK + " = '" + link.replaceAll("http://", "httpxxx") + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return ret;
    }

    public byte[] getArticleBlob(String imageUrl) {
        byte[] ret;
        String tableName = getNewsTableName();
        SQLiteDatabase db = getReadableDatabase();
        Cursor result;

        assert db != null;
        db.beginTransaction();
        result = db.query(tableName, new String[]{NEWS_KEY_BLOB},
                NEWS_KEY_IMG_URL + "='" + imageUrl.replaceAll("http://", "httpxxx") + "'",
                null, null, null, null, null);
        result.moveToFirst();
        try {
            ret = result.getBlob(0);
        } catch (IndexOutOfBoundsException ex) { //Meaning there's no blob stored yet
            ret = null;
        }
        result.close();

        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    public final ArrayList<NewsEntry> getNews() {
        return getFilteredNews(null);
    }

    private ArrayList<NewsEntry> getFilteredNews(String whereClause) {

        ArrayList<NewsEntry> ret = new ArrayList<>();
        String[] fields =
                new String[]{NEWS_KEY_BLOB, NEWS_KEY_IMG_URL, NEWS_KEY_URL, NEWS_KEY_TITLE,
                        NEWS_KEY_DESC};
        ArrayList<String> fieldsAsList = new ArrayList<>(Arrays.asList(fields));

        SQLiteDatabase db = getReadableDatabase();
        assert db != null;
        db.beginTransaction();
        String tableName = getNewsTableName();
        Cursor result =
                db.query(tableName, fields, whereClause, null, null, null, NEWS_KEY_ID + " DESC");
        StringBuilder data = new StringBuilder("");
        final String separator = NewsEntry.getFieldSeparator();
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            for (String x : fieldsAsList) {
                if (!x.contentEquals(NEWS_KEY_BLOB)) {
                    if (x.contentEquals(NEWS_KEY_URL)) {
                        data.append(
                                result.getString(result.getColumnIndex(x))
                                        .replaceAll("httpxxx", "http://")
                        )
                                .append(separator);
                    } else {
                        data.append(result.getString(result.getColumnIndex(x))).append(separator);
                    }
                }
            }
            ret.add(new NewsEntry(data.toString()));
            data = new StringBuilder("");
        }
        result.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    public final void updateArticleBlob(byte[] blob, String imgUrl) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getNewsTableName();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NEWS_KEY_BLOB, blob);

        assert db != null;
        db.beginTransaction();
        db.update(tableName, contentValues,
                NEWS_KEY_IMG_URL + " = '" + imgUrl.replaceAll("http://", "httpxxx") + "'", null);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * @param values {@link android.content.ContentValues} The values to insert. The most recent article is the latest one
     * @return The rowID of the newly inserted row, or -1 if any error happens
     */
    public long insertNewsArticle(ContentValues values) {
        long ret;
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getNewsTableName();
        assert db != null;
        db.beginTransaction();
        ret = db.insert(tableName, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    /**
     * @param values {@link android.content.ContentValues} The values to insert. The most recent article is the latest one
     * @return The rowID of the newly inserted row, or -1 if any error happens
     */
    public long insertSurrArticle(ContentValues values) {
        long ret;
        SQLiteDatabase db = getWritableDatabase();

        assert db != null;
        db.beginTransaction();
        ret = db.insert(SURR_TABLE_NAME, null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
        return ret;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        HashSet<String> tableNames = new HashSet<>();
        ArrayList<String> langsInThisServer, servers = new ArrayList<>(
                Arrays.asList(mContext.getResources().getStringArray(R.array.servers)));
        String prefix =
                LoLin1Utils
                        .getString(mContext, "news_euw_en", "http://feed43.com/lolnews_euw_en.xml")
                        .replaceAll(LOLNEWS_FEED_HOST, "").replaceAll(LOLNEWS_FEED_EXTENSION, "")
                        .replaceAll("_(.*)", "") + "_";

        for (String server : servers) {
            langsInThisServer = new ArrayList<>();
            for (String locale : LoLin1Utils
                    .getStringArray(mContext, "lang_" + server.toLowerCase(Locale.ENGLISH) + "_simplified",
                            new String[]{""})) {
                langsInThisServer.add((prefix + server + "_" + locale).toUpperCase(Locale.ENGLISH));
            }
            tableNames.addAll(langsInThisServer);
        }

        sqLiteDatabase.beginTransaction();
        for (String tableName : tableNames) {
            sqLiteDatabase.execSQL(("CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
                    NEWS_KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                    NEWS_KEY_TITLE + " TEXT NOT NULL ON CONFLICT IGNORE, " +
                    NEWS_KEY_BLOB + " BLOB, " +
                    NEWS_KEY_URL + " TEXT NOT NULL ON CONFLICT IGNORE UNIQUE ON CONFLICT IGNORE, " +
                    NEWS_KEY_DESC + " TEXT, " +
                    NEWS_KEY_IMG_URL + " TEXT NOT NULL ON CONFLICT IGNORE " +
                    ")").toUpperCase(Locale.ENGLISH));
        }

        sqLiteDatabase.execSQL(("CREATE TABLE IF NOT EXISTS " + SURR_TABLE_NAME + " ( " +
                SURR_KEY_ID + " INTEGER PRIMARY KEY ASC AUTOINCREMENT, " +
                SURR_KEY_TITLE + " TEXT UNIQUE NOT NULL ON CONFLICT IGNORE, " +
                SURR_KEY_PUBLISHED +
                " TEXT NOT NULL ON CONFLICT IGNORE UNIQUE ON CONFLICT IGNORE, " +
                SURR_KEY_UPDATED + " TEXT, " +
                SURR_KEY_LINK + " TEXT NOT NULL ON CONFLICT IGNORE UNIQUE ON CONFLICT IGNORE, " +
                SURR_KEY_READ + " BOOLEAN NOT NULL " +
                ")").toUpperCase(Locale.ENGLISH));

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //At the moment no further database versions are planned.
    }
}