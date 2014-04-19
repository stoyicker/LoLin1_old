package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.ui.activities.ChampionDetailFragmentActivity;

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
 * Created by JorgeAntonio on 19/04/2014.
 */
public abstract class ChampionDetailSupportFragment extends Fragment {

    private Champion selectedChampion;
    private int layout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        selectedChampion = ((ChampionDetailFragmentActivity) getActivity()).getSelectedChampion();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout, container);
    }

    protected Champion getSelectedChampion() {
        return selectedChampion;
    }

    protected void setLayout(int layout) {
        this.layout = layout;
    }
}
