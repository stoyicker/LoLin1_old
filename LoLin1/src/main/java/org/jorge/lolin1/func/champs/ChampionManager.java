package org.jorge.lolin1.func.champs;

import android.content.Context;
import android.preference.PreferenceManager;

import org.jorge.lolin1.utils.LoLin1Utils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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

    private static final LinkedList<Champion> champs = new LinkedList<>();
    private static final String champsFileName = "champs_", champsFileExtension = ".json";
    private static Context context;

    public static void setContext(Context context) {
        ChampionManager.context = context;
    }

    public static void readInfo() {
        LinkedList<JSONObject> stats = readStats();
        final String[] langs_simplified = LoLin1Utils.getStringArray(context, "langs_simplified",
                new String[]{"en"}), langs = LoLin1Utils.getStringArray(context, "langs",
                new String[]{"error"});
        final String selectedLang =
                PreferenceManager.getDefaultSharedPreferences(context).getString(
                        LoLin1Utils.getString(context, "pref_title_lang", "Language"), "English");
        final String fileName =
                champsFileName + langs_simplified[new ArrayList<>(
                        Arrays.asList(langs))
                        .indexOf(selectedLang)].toLowerCase() +
                        champsFileExtension;
        //TODO Parse champions
        //TODO Set stats to each champions
    }

    private static LinkedList<JSONObject> readStats() {
        final LinkedList<JSONObject> ret = new LinkedList<>();
        //TODO Read champ_stats.json into ret
        return ret;
    }
}
