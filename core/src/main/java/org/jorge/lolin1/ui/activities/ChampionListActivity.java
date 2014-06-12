package org.jorge.lolin1.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.ui.frags.ChampionListFragment;
import org.jorge.lolin1.ui.frags.ExpandableSearchFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 14/04/2014.
 */
public final class ChampionListActivity extends DrawerLayoutFragmentActivity implements
        ExpandableSearchFragment.ExpandableSearchListener,
        ChampionListFragment.ChampionSelectionListener {

    private ExpandableSearchFragment SEARCH_FRAGMENT;
    private ChampionListFragment CHAMPION_LIST_FRAGMENT;
    private ShowcaseView championsShowcase;

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
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("showcase_champions_done", Boolean.FALSE))
            championsShowcase = new ShowcaseView.Builder(this).setContentText(R.string.tutorial_champions_contents).setContentTitle(R.string.tutorial_champions_title).setStyle(R.style.CustomShowcaseThemePlusNoButton).setTarget(new ViewTarget(R.id.champion_list_grid, this)).build();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);

        CHAMPION_LIST_FRAGMENT = (ChampionListFragment) getFragmentManager()
                .findFragmentById(R.id.champion_list_grid);

        SEARCH_FRAGMENT =
                (ExpandableSearchFragment) getFragmentManager()
                        .findFragmentById(R.id.champion_list_search);

        if (SEARCH_FRAGMENT != null && SEARCH_FRAGMENT.getQueryField() != null) {
            SEARCH_FRAGMENT.getQueryField().setHint(
                    LoLin1Utils.getString(getApplicationContext(), "champion_search_hint", null));
        }
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
        if (championsShowcase != null) {
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putBoolean("showcase_champions_done", Boolean.TRUE).commit();
            championsShowcase.hide();
        }
        Intent championDetailIntent =
                new Intent(getApplicationContext(), ChampionDetailFragmentActivity.class);
        championDetailIntent.putExtra(ChampionDetailFragmentActivity.SELECTED_CHAMPION, champion);
        startActivity(championDetailIntent);
    }
}
