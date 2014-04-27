package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.io.local.FileManager;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.File;

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
 * Created by JorgeAntonio on 07/01/14.
 */
public final class InitialActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, Boolean.TRUE);
        LoLin1Utils.initCharsetMap();
        SQLiteDAO.setup(getApplicationContext());
        flushCacheIfNecessary();
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        LoLin1Utils
                .setLocale(getApplicationContext(), LoLin1Utils.getLocale(getApplicationContext()));
        preferences.edit().putInt("lastSelectedNewsIndex", 0).commit();
        if (!preferences.getBoolean("initial_setup_done", Boolean.FALSE)) {
            final Intent serverAndLanguageChooserIntent =
                    new Intent(getApplicationContext(), ServerAndLanguageChooserActivity.class);
            startActivity(serverAndLanguageChooserIntent);
        }
        else {
            final Intent splashIntent = new Intent(getApplicationContext(), SplashActivity.class);
            finish();
            startActivity(splashIntent);
        }
    }

    private void flushCacheIfNecessary() {
        File cacheDir;
        int CACHE_SIZE_LIMIT_BYTES = 1048576;
        if ((cacheDir = getApplicationContext().getCacheDir()).length() > CACHE_SIZE_LIMIT_BYTES) {
            FileManager.recursiveDelete(cacheDir);
        }
    }

    @Override
    public void onBackPressed() {
    }
}
