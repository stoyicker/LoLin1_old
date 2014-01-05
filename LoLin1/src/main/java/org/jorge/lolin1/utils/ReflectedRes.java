package org.jorge.lolin1.utils;

import android.content.Context;
import android.util.Log;

import org.jorge.lolin1.R;

import java.lang.reflect.Field;

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
 * Accessing resources through reflection is said to be ten times faster than through getResources().
 */
public abstract class ReflectedRes {

    private ReflectedRes() {
    }

    public static String string(Context context, String variableName, String def) {
        String ret = def;

        try {
            Field resourceField = R.string.class.getDeclaredField(variableName);
            int resourceId = resourceField.getInt(resourceField);
            ret = context.getString(resourceId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }

    public static int drawableAsId(String variableName, int def) {
        int ret = def;

        try {
            Field resourceField = R.drawable.class.getDeclaredField(variableName);
            ret = resourceField.getInt(resourceField);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e("ERROR", "Exception", e);
        }

        return ret;
    }
}