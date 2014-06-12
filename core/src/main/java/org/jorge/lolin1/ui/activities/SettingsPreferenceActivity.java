package org.jorge.lolin1.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import org.jorge.lolin1.ui.frags.SettingsPreferenceFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

import static org.jorge.lolin1.utils.LoLin1DebugUtils.logString;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 13/01/14.
 */
public final class SettingsPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsPreferenceFragment(),
                LoLin1Utils.getString(this, "title_activity_settings", "Settings"))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up button
                if (DrawerLayoutFragmentActivity.getLastSelectedNavDrawerIndex() == 1)
                    startActivity(
                            new Intent(getApplicationContext(), JungleTimersActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
