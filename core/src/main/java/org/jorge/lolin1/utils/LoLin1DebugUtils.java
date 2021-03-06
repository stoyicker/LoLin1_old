package org.jorge.lolin1.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.BuildConfig;
import org.jorge.lolin1.io.db.SQLiteDAO;

import java.io.IOException;
import java.io.OutputStreamWriter;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 19/01/14.
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public abstract class LoLin1DebugUtils {

    public static void showTrace(String tag, Exception source) {
        if (!BuildConfig.DEBUG) return;
        StackTraceElement[] trace = source.getStackTrace();
        String toPrint = "";
        for (StackTraceElement x : trace) {
            toPrint += "Class " + x.getClassName() + " -  " + x.getMethodName() + ":" +
                    x.getLineNumber();
            toPrint += "\n";
        }
        Log.d(tag, toPrint);
    }

    public static void writeToFile(String data, Context context) {
        if (!BuildConfig.DEBUG) return;
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(
                            context.openFileOutput("info.txt", Context.MODE_WORLD_READABLE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    public static void debugSelectAllFromTable(String tag, String[] fields, String tableName) {
        if (!BuildConfig.DEBUG) return;
        SQLiteDatabase db = SQLiteDAO.getSingleton().getReadableDatabase();

        db.beginTransaction();
        Cursor cursor = db.query(tableName, fields, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.d(tag, fields[0] + ": " + cursor.getInt(0));
            Log.d(tag, fields[1] + ": " + cursor.getString(1));
            Log.d(tag, fields[2] + ": " + cursor.getString(2));
            Log.d(tag, fields[3] + ": " + cursor.getString(3));
            Log.d(tag, fields[4] + ": " + cursor.getString(4));
            Log.d(tag, fields[5] + ": " + Arrays.toString(cursor.getBlob(5)));
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static void logArray(String tag, String arrayName, Object[] array) {
        if (!BuildConfig.DEBUG) return;
        Log.d(tag, "Logging array " + arrayName);
        for (Object x : array)
            Log.d(tag, x + "\n");
    }

    public static void logString(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.d(tag, msg);
    }
}
