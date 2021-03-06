package org.jorge.lolin1.io.local;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 29/03/2014.
 */
public abstract class JsonManager {

    public static String getStringAttribute(String response, String attributeName) {
        JSONObject responseAsObject;
        try {
            responseAsObject = new JSONObject(response);
            return responseAsObject.getString(attributeName);
        } catch (JSONException e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    public static boolean getResponseStatus(String response) {
        return getStringAttribute(response, "status").contentEquals("ok");
    }
}
