package org.jorge.lolin1.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.jorge.lolin1.io.db.NewsToSQLiteBridge;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;

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
 * Created by JorgeAntonio on 19/01/14.
 */
public abstract class DebugUtils {

    public static final String BufferedInputStreamToString(BufferedInputStream is) {
        String ret;

        is.mark(Integer.MAX_VALUE);

        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                Log.wtf("ERROR", "Should never happen!", e);
            }
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        catch (IOException e) {
            Log.wtf("ERROR", "Should never happen!", e);
        }
        finally {
            try {
                is.reset();
            }
            catch (IOException e) {
                Log.wtf("ERROR", "Should never happen!", e);
            }
        }
        ret = writer.toString();

        return ret;
    }

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

    public static final void writeToFile(String data, Context context) {
        try {
            @SuppressLint("WorldReadableFiles") OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(
                            context.openFileOutput("info.txt", Context.MODE_WORLD_READABLE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void debugSelectAllFromTable(String tag, String[] fields, String tableName) {
        SQLiteDatabase db = NewsToSQLiteBridge.getSingleton().getReadableDatabase();

        db.beginTransaction();
        Cursor cursor = db.query(tableName, fields, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Log.d(tag, fields[0] + ": " + cursor.getInt(0));
            Log.d(tag, fields[1] + ": " + cursor.getString(1));
            Log.d(tag, fields[2] + ": " + cursor.getString(2));
            Log.d(tag, fields[3] + ": " + cursor.getString(3));
            Log.d(tag, fields[4] + ": " + cursor.getString(4));
            Log.d(tag, fields[5] + ": " + cursor.getBlob(5));
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
