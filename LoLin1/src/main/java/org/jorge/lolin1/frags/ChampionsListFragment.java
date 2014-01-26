package org.jorge.lolin1.frags;

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
 * Created by JorgeAntonio on 15/01/14.
 */

import android.app.Activity;
import android.app.ListFragment;

import org.jorge.lolin1.activities.MainActivity;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ChampionsListFragment extends ListFragment {

    public ChampionsListFragment() {
        //TODO -1 ChampionsListFragment
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(
                new ArrayList<>(
                        Arrays.asList(
                                Utils.getStringArray(
                                        getActivity().getApplicationContext(),
                                        "navigation_drawer_items", new String[]{""})
                        )
                ).indexOf(Utils.getString(getActivity().getApplicationContext(), "title_section",
                        "Champions")));
    }
}
