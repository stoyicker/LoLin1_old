package org.jorge.lolin1.io.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.jorge.lolin1.utils.ReflectedRes;

import java.util.StringTokenizer;

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
public class SQLiteBridge extends SQLiteOpenHelper {

    private static SQLiteBridge singleton;

    public void asyncExecute(String command) {
        String cmdType;
        switch (cmdType = new StringTokenizer(command).nextToken(" ").toUpperCase()) {
            case "INSERT":
            case "UPDATE":
            case "DELETE":
                try {
                    throw new IllegalArgumentException("Method " + SQLiteBridge.class.getName() + " " + SQLiteBridge.class.getDeclaredMethod("asyncExecute", String.class) + " should not be used for " + cmdType + "-type requests. See http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#execSQL(java.lang.String)");
                } catch (NoSuchMethodException e) {
                    Log.e("Error", "Exception", e);
                }
            default:
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... command) {
                        SQLiteBridge.this.getWritableDatabase().execSQL(command[0].toUpperCase());
                        return null;
                    }
                }.execute();
        }
    }

    private SQLiteBridge(Context _context) {
        super(_context, ReflectedRes.string(_context, "db_name", "LoLin1_DB"), null, 1);
    }

    public static void setup(Context _context) {
        singleton = new SQLiteBridge(_context);
    }

    public static SQLiteBridge getSingleton() {
        return singleton;
    }

    public void select() {
        //TODO Use the 'query(...)' methods
    }

    /**
     * @param tableName
     * @param values    {@link android.content.ContentValues}
     * @return The rowID of the inserted column, or -1 if any error happens
     */
    public long insert(String tableName, ContentValues values) {
        long ret;
        getWritableDatabase().beginTransaction();
        ret = getWritableDatabase().insert(tableName, null, values);
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
        return ret;
    }

    /**
     * Simplified 'DELETE FROM TABLE' implementation that allows only for a single field matching check.
     *
     * @param fromTable   {@link String} The name of the table
     * @param paramName   {@link String} The parameter to be checked
     * @param targetValue {@link String} The value to check the parameter against
     * @return the number of rows affected
     */
    public long simpleDelete(String fromTable, String paramName, String targetValue) {
        return getWritableDatabase().delete(fromTable, "WHERE ? = ?", new String[]{paramName.toUpperCase(), targetValue.toUpperCase()});
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //TODO Create the tables
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //At the moment no further database versions are planned.
    }
}
