package org.jorge.lolin1.func.champs;

import android.util.Log;

import org.jorge.lolin1.func.champs.models.Champion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Collection;

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
 * Created by JorgeAntonio on 02/02/14.
 */
public final class ChampionManager {

    private Collection<Champion> champs;
    private static ChampionManager instance;

    private ChampionManager() {
    }

    public static ChampionManager getInstance() {
        if (instance == null) {
            instance = new ChampionManager();
        }
        return instance;
    }

    public Collection<Champion> buildChampions(String list) {
        ArrayDeque<Champion> ret = new ArrayDeque<>();
        JSONArray rawChamps = null;
        try {
            rawChamps = new JSONArray(list);
        }
        catch (JSONException e) {
            Log.wtf("debug", e.getClass().getName(), e);
        }
        assert rawChamps != null;
        int length = rawChamps.length();
        for (int i = 0; i < length; i++) {
            JSONObject currentRawChamp;
            try {
                currentRawChamp = rawChamps.getJSONObject(i);
                Champion currentChampion = new Champion(currentRawChamp);
                ret.add(currentChampion);
            }
            catch (JSONException e) {
                Log.wtf("debug", e.getClass().getName(), e);
            }
        }
        return ret;
    }

    public void resetChampions() {
        champs.clear();
    }

    public void addChampion(Champion newChamp) {
        if (champs == null) {
            champs = new ArrayDeque<>();
        }
        champs.add(newChamp);
    }
}
