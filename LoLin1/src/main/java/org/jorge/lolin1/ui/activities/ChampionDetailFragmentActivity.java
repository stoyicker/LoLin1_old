package org.jorge.lolin1.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.jfeinstein.jazzyviewpager.JazzyViewPager;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.ui.frags.ChampionAbilitiesSupportFragment;
import org.jorge.lolin1.ui.frags.ChampionLoreSupportFragment;

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
 * Created by JorgeAntonio on 18/04/2014.
 */
public class ChampionDetailFragmentActivity extends FragmentActivity {

    public static final String SELECTED_CHAMPION = "SELECTED_CHAMPION";
    private static final JazzyViewPager.TransitionEffect TRANSITION_EFFECT =
            JazzyViewPager.TransitionEffect.CubeOut;
    private Champion selectedChampion;
    private JazzyViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);
        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);
        selectedChampion = getIntent().getParcelableExtra(SELECTED_CHAMPION);
        initPager();
    }

    private void initPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ChampionAbilitiesSupportFragment());
        fragments.add(new ChampionLoreSupportFragment());
        viewPager = (JazzyViewPager) findViewById(R.id.champion_detail_pager);
        viewPager.setTransitionEffect(TRANSITION_EFFECT);
        viewPager.setAdapter(new ChampionDetailPageAdapter(getSupportFragmentManager(), fragments));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up button
                finish();
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Champion getSelectedChampion() {
        return selectedChampion;
    }

    private final class ChampionDetailPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> items;

        public ChampionDetailPageAdapter(FragmentManager fm, List<Fragment> _items) {
            super(fm);
            items = _items;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            viewPager.setObjectForPosition(obj, position);
            return obj;
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}
