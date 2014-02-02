package org.jorge.lolin1.champs;

import android.content.Context;
import android.preference.PreferenceManager;

import org.jorge.lolin1.utils.Utils;

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


    public static void readChampions() {
        final String fileName = champsFileName + Utils.getStringArray(context, "langs_simplified",
                new String[]{"en"})[new ArrayList<>(
                Arrays.asList(Utils.getStringArray(context, "langs",
                        new String[]{"error"})))
                .indexOf(PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("pref_title_lang", "english"))].toLowerCase() +
                champsFileExtension;
        //TODO Parsing champions
    }
}
