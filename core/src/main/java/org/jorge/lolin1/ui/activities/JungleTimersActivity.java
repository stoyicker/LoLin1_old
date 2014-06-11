package org.jorge.lolin1.ui.activities;

import android.os.Bundle;

import org.jorge.lolin1.R;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 25/04/2014.
 */
public final class JungleTimersActivity extends DrawerLayoutFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                R.layout.activity_jungle_timers);
        super.onCreate(savedInstanceState);
        onSectionAttached(1);
    }
}
