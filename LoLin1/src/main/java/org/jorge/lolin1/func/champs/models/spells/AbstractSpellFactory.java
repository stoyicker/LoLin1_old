/**
 * This file is part of lolin1-data-provider.

 lolin1-data-provider is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 lolin1-data-provider is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with lolin1-data-provider.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jorge.lolin1.func.champs.models.spells;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractSpellFactory {

    private static final String DUMMY_ERROR_MESSAGE = "The Riot API is still a little bit buggy...";

    public static PassiveSpell createPassiveSpell(JSONObject contentDescriptor) {
        String name, imageName, detail;

        try {
            name = contentDescriptor.getString("name");
        }
        catch (JSONException e) {
            name = DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }

        try {
            imageName = contentDescriptor.getString("imageName");
        }
        catch (JSONException e) {
            imageName = DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }

        try {
            detail = contentDescriptor.getString("detail");
        }
        catch (JSONException e) {
            detail = DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }

        return new PassiveSpell(name, imageName, detail);
    }

    public static ActiveSpell createActiveSpell(JSONObject contentDescriptor) throws JSONException {
        ActiveSpell ret;
        String name, imageName, cooldown, range, cost = null, detail;

        // It's key to manage the exceptions in different try-catch blocks to
        // make sure that one variable not being found doesn't forbid the others
        // from being parsed

        try {
            name = contentDescriptor.getString("name");
        }
        catch (JSONException e) {
            name = AbstractSpellFactory.DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            detail = contentDescriptor.getString("detail");
        }
        catch (JSONException e) {
            detail = AbstractSpellFactory.DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            imageName = contentDescriptor.getString("imageName");
        }
        catch (JSONException e) {
            imageName = AbstractSpellFactory.DUMMY_ERROR_MESSAGE;
            Log.wtf("debug", e.getClass().getName(), e);
        }
        try {
            cooldown = contentDescriptor.getString("cooldownBurn");
        }
        catch (JSONException e) {
            cooldown = AbstractSpellFactory.DUMMY_ERROR_MESSAGE;
        }
        try {
            range = contentDescriptor.getString("rangeBurn");
        }
        catch (JSONException e) {
            range = AbstractSpellFactory.DUMMY_ERROR_MESSAGE;
        }
        try {
            cost = contentDescriptor.getString("costBurn");
        }
        catch (JSONException e) {
            // No explicitly stated cost, it's already reported to Riot Games
        }
        ret = new ActiveSpell(name, detail, imageName, cooldown, range, cost);
        return ret;
    }
}
