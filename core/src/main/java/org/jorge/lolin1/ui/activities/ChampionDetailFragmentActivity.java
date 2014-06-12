package org.jorge.lolin1.ui.activities;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.viewpagerindicator.PageIndicator;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.func.custom.SkinsViewPagerAdapter;
import org.jorge.lolin1.func.custom.TransitionViewPager;
import org.jorge.lolin1.ui.frags.ChampionAbilitiesSupportFragment;
import org.jorge.lolin1.ui.frags.ChampionLoreSupportFragment;
import org.jorge.lolin1.ui.frags.ChampionStatsSupportFragment;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 18/04/2014.
 */
public final class ChampionDetailFragmentActivity extends FragmentActivity {

    public static final String SELECTED_CHAMPION = "SELECTED_CHAMPION";
    private static final TransitionViewPager.TransitionEffect TRANSITION_EFFECT =
            TransitionViewPager.TransitionEffect.CubeOut;
    private Champion selectedChampion;
    private TransitionViewPager viewPager;
    private ViewPager skinsViewPager;
    private ShowcaseView detailShowcase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("showcase_champion_detail_done", Boolean.FALSE))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        selectedChampion = getIntent().getParcelableExtra(SELECTED_CHAMPION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);
        ActionBar actionBar;
        if (findViewById(R.id.champion_title) != null) {
            //Portrait layout
            if (!(actionBar = getActionBar()).isShowing()) {
                actionBar.show();
            }
            actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
            ((TextView) findViewById(R.id.champion_name)).setText(selectedChampion.getName());
            ((TextView) findViewById(R.id.champion_title)).setText(selectedChampion.getTitle());
            initChampionInfoPager();
            if (!preferences.getBoolean("showcase_champion_detail_done", Boolean.FALSE))
                detailShowcase = new ShowcaseView.Builder(this).setContentText(R.string.tutorial_detail_contents).setContentTitle(R.string.tutorial_detail_title).setStyle(R.style.CustomShowcaseThemePlusNoButton).setTarget(new ViewTarget(R.id.champion_detail_pager, this)).build();

            new AsyncTask<Void, Void, Void>(

            ) {
                @Override
                protected Void doInBackground(Void... params) {
                    ((ImageView) findViewById(R.id.champion_bust))
                            .setImageDrawable(
                                    new BitmapDrawable(getResources(), ChampionManager.getInstance()
                                            .getBustImageByChampion(200, 200, selectedChampion,
                                                    getApplicationContext()))
                            );
                    return null;
                }
            }.execute();
        } else {
            //Landscape layout
            if ((actionBar = getActionBar()).isShowing()) {
                actionBar.hide();
            }
            skinsViewPager = ((ViewPager) findViewById(R.id.skins_view_pager));
            initChampionSkinsPager();
        }
    }

    private void initChampionSkinsPager() {
        PagerAdapter pagerAdapter =
                new SkinsViewPagerAdapter(this, selectedChampion,
                        getSupportFragmentManager());
        skinsViewPager.setPageTransformer(Boolean.TRUE, new ZoomOutPageTransformer());
        skinsViewPager.setAdapter(pagerAdapter);
        skinsViewPager.setOnPageChangeListener((ViewPager.OnPageChangeListener) pagerAdapter);
        skinsViewPager.setCurrentItem(0);
        skinsViewPager.setOffscreenPageLimit(selectedChampion.getSkinNames().length);
    }

    private void initChampionInfoPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ChampionStatsSupportFragment());
        fragments.add(new ChampionAbilitiesSupportFragment());
        fragments.add(new ChampionLoreSupportFragment());
        viewPager = (TransitionViewPager) findViewById(R.id.champion_detail_pager);
        viewPager.setTransitionEffect(TRANSITION_EFFECT);
        viewPager.setAdapter(new ChampionDetailPageAdapter(getSupportFragmentManager(), fragments));
        ((PageIndicator) findViewById(R.id.champion_detail_pager_indicator))
                .setViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (detailShowcase != null) {
                    SharedPreferences preferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    preferences.edit().putBoolean("showcase_champion_detail_done", Boolean.TRUE).commit();
                    detailShowcase.hide();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }
        });
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

    public ViewPager getSkinsViewPager() {
        return skinsViewPager;
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

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
