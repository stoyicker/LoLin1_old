package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.utils.LoLin1Utils;

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
 * Created by JorgeAntonio on 07/01/14.
 */
public class InitialActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteDAO.setup(getApplicationContext());
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.settings, Boolean.FALSE);
        if (!Arrays.asList(LoLin1Utils.getStringArray(getApplicationContext(),
                "servers", null))
                .contains(LoLin1Utils.getRealm(getApplicationContext())) ||
                !Arrays.asList(LoLin1Utils.getStringArray(getApplicationContext(),
                        "langs_simplified", null))
                        .contains(LoLin1Utils.getLocale(
                                getApplicationContext()))) {
            final Intent serverAndLanguageChooserIntent =
                    new Intent(getApplicationContext(), ServerAndLanguageChooserActivity.class);
            startActivity(serverAndLanguageChooserIntent);
        }
        final Intent newsIntent = new Intent(getApplicationContext(), SplashActivity.class);
        finish();
        startActivity(newsIntent);
    }
}
