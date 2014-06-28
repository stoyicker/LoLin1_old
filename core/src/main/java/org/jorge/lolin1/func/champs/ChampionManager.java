package org.jorge.lolin1.func.champs;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.io.local.CacheableBitmapLoader;
import org.jorge.lolin1.io.local.FileManager;
import org.jorge.lolin1.io.local.JsonManager;
import org.jorge.lolin1.utils.LoLin1Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 02/02/14.
 */
public final class ChampionManager {

    public List<Champion> getChampions() {
        return champions;
    }

    private List<Champion> champions;
    private final CacheableBitmapLoader bitmapLoader = new CacheableBitmapLoader();
    private static ChampionManager instance;

    private ChampionManager() {
    }

    public static ChampionManager getInstance() {
        if (instance == null) {
            instance = new ChampionManager();
        }
        return instance;
    }

    public void setChampions(Context context) {
        final String realm = LoLin1Utils.getRealm(context), locale = LoLin1Utils.getLocale(context),
                pathSeparator = LoLin1Utils.getString(context, "symbol_path_separator",
                        null);
        File targetFile = new File(context.getExternalFilesDir(
                LoLin1Utils.getString(context, "content_folder_name", null)) + pathSeparator +
                realm +
                LoLin1Utils.getString(context, "symbol_hyphen",
                        null) +
                PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("pref_version_" + realm + "-" + LoLin1Utils.getLocale(context.getApplicationContext()), "0") + pathSeparator + locale +
                pathSeparator +
                LoLin1Utils.getString(context, "list_file_name", null));
        try {
            champions = buildChampions(JsonManager
                    .getStringAttribute(FileManager.readFileAsString(targetFile), LoLin1Utils
                            .getString(context, "champion_list_key", null)));
        } catch (IOException e) {
            //It's fine, nothing will get shown
            champions = new ArrayList<>();
        }
    }

    public ArrayList<Champion> buildChampions(String list) {
        ArrayList<Champion> ret = new ArrayList<>();
        JSONArray rawChamps = null;
        try {
            rawChamps = new JSONArray(list);
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }

        int length = rawChamps.length();
        for (int i = 0; i < length; i++) {
            JSONObject currentRawChamp;
            try {
                currentRawChamp = rawChamps.getJSONObject(i);
                Champion currentChampion = new Champion(currentRawChamp);
                ret.add(currentChampion);
            } catch (JSONException e) {
                Crashlytics.logException(e);
            }
        }
        return ret;
    }

    public Bitmap getBustImageByChampion(int width, int height, Champion champion,
                                         Context context) {
        File root = context.getExternalFilesDir(
                LoLin1Utils.getString(context, "content_folder_name", null));
        final String realm = LoLin1Utils.getRealm(context), pathSeparator =
                LoLin1Utils.getString(context, "symbol_path_separator",
                        null);
        final StringBuilder absolutePathToBustBuilder =
                new StringBuilder(root.getPath()).append(pathSeparator).append(realm)
                        .append(LoLin1Utils.getString(context, "symbol_hyphen",
                                null))
                        .append(PreferenceManager.getDefaultSharedPreferences(context).getString(
                                "pref_version_" + realm, "0")).append(pathSeparator)
                        .append(LoLin1Utils.getLocale(context)).append(pathSeparator)
                        .append(LoLin1Utils.getString(context, "champion_image_folder",
                                null)).append(pathSeparator);

        absolutePathToBustBuilder
                .append(LoLin1Utils.getString(context, "bust_image_folder_name",
                        null)).append(pathSeparator)
                .append(champion.getBustImageName());

        return bitmapLoader
                .getBitmapFromCache(absolutePathToBustBuilder.toString(), width, height);
    }

    public Bitmap getPassiveImageByChampion(Champion target, Context context, int width,
                                            int height) {
        File root = context.getExternalFilesDir(
                LoLin1Utils.getString(context, "content_folder_name", null));
        final String realm = LoLin1Utils.getRealm(context), pathSeparator =
                LoLin1Utils.getString(context, "symbol_path_separator",
                        null);

        StringBuilder absolutePathToPassiveBuilder =
                new StringBuilder(root.getPath()).append(pathSeparator).append(realm)
                        .append(LoLin1Utils.getString(context, "symbol_hyphen",
                                null))
                        .append(PreferenceManager.getDefaultSharedPreferences(context).getString(
                                "pref_version_" + realm, "0")).append(pathSeparator)
                        .append(LoLin1Utils.getLocale(context)).append(pathSeparator)
                        .append(LoLin1Utils.getString(context, "champion_image_folder",
                                null)).append(pathSeparator);

        absolutePathToPassiveBuilder
                .append(LoLin1Utils.getString(context, "passive_image_folder_name",
                        null)).append(pathSeparator)
                .append(target.getPassive().getImageName());

        return bitmapLoader
                .getBitmapFromCache(absolutePathToPassiveBuilder.toString(), width, height);
    }

    public Bitmap getSpellImageByChampion(Champion target, Context context,
                                          Integer width, Integer height, int spellIndex) {
        File root = context.getExternalFilesDir(
                LoLin1Utils.getString(context, "content_folder_name", null));
        final String realm = LoLin1Utils.getRealm(context), pathSeparator =
                LoLin1Utils.getString(context, "symbol_path_separator",
                        null);

        StringBuilder absolutePathToPassiveBuilder =
                new StringBuilder(root.getPath()).append(pathSeparator).append(realm)
                        .append(LoLin1Utils.getString(context, "symbol_hyphen",
                                null))
                        .append(PreferenceManager.getDefaultSharedPreferences(context).getString(
                                "pref_version_" + realm, "0")).append(pathSeparator)
                        .append(LoLin1Utils.getLocale(context)).append(pathSeparator)
                        .append(LoLin1Utils.getString(context, "champion_image_folder",
                                null)).append(pathSeparator);

        absolutePathToPassiveBuilder
                .append(LoLin1Utils.getString(context, "spell_image_folder_name",
                        null)).append(pathSeparator)
                .append(target.getSpells()[spellIndex].getImageName());

        return bitmapLoader
                .getBitmapFromCache(absolutePathToPassiveBuilder.toString(), width, height);
    }

    public Bitmap getSplashImageByChampion(Champion target, Context context,
                                           Integer width, Integer height, int splashIndex) {
        File root = context.getExternalFilesDir(
                LoLin1Utils.getString(context, "content_folder_name", null));
        final String realm = LoLin1Utils.getRealm(context), pathSeparator =
                LoLin1Utils.getString(context, "symbol_path_separator",
                        null);

        StringBuilder absolutePathToPassiveBuilder =
                new StringBuilder(root.getPath()).append(pathSeparator).append(realm)
                        .append(LoLin1Utils.getString(context, "symbol_hyphen",
                                null))
                        .append(PreferenceManager.getDefaultSharedPreferences(context).getString(
                                "pref_version_" + realm, "0")).append(pathSeparator)
                        .append(LoLin1Utils.getLocale(context)).append(pathSeparator)
                        .append(LoLin1Utils.getString(context, "champion_image_folder",
                                null)).append(pathSeparator);

        absolutePathToPassiveBuilder
                .append(LoLin1Utils.getString(context, "splash_image_folder_name",
                        null)).append(pathSeparator)
                .append(target.getSimplifiedName() + "_" + splashIndex + "." +
                        LoLin1Utils.getString(context, "splash_image_extension", null));

        return bitmapLoader
                .getBitmapFromCache(absolutePathToPassiveBuilder.toString(), width, height);
    }
}
