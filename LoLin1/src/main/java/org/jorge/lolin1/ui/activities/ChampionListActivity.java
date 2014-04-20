package org.jorge.lolin1.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.ui.frags.ChampionListFragment;
import org.jorge.lolin1.ui.frags.ExpandableSearchFragment;

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
 * Created by JorgeAntonio on 14/04/2014.
 */
public class ChampionListActivity extends DrawerLayoutFragmentActivity implements
        ExpandableSearchFragment.ExpandableSearchListener,
        ChampionListFragment.ChampionSelectionListener {

    private ExpandableSearchFragment SEARCH_FRAGMENT;
    private ChampionListFragment CHAMPION_LIST_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntent().putExtra(DrawerLayoutFragmentActivity.ACTION_BAR_MENU_LAYOUT,
                R.menu.menu_champion_list);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                R.layout.activity_champion_list);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);

        SEARCH_FRAGMENT =
                (ExpandableSearchFragment) getFragmentManager()
                        .findFragmentById(R.id.champion_list_search);

        CHAMPION_LIST_FRAGMENT = (ChampionListFragment) getFragmentManager()
                .findFragmentById(R.id.champion_list_grid);

        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean ret = Boolean.TRUE;
        switch (item.getItemId()) {
            case R.id.action_champion_search:
                SEARCH_FRAGMENT.toggleVisibility();
                break;
            default: //Up or Settings buttons
                ret = super.onOptionsItemSelected(item);
        }
        super.restoreActionBar();
        return ret;
    }

    @Override
    public void onNewQuery(String query) {
        CHAMPION_LIST_FRAGMENT.applyFilter(query);
    }

    @Override
    public void onChampionSelected(Champion champion) {
        Intent championDetailIntent =
                new Intent(getApplicationContext(), ChampionDetailFragmentActivity.class);
        championDetailIntent.putExtra(ChampionDetailFragmentActivity.SELECTED_CHAMPION, champion);
        startActivity(championDetailIntent);
    }
}
