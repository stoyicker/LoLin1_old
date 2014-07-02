package org.jorge.lolin1.func.custom;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.ui.activities.ChampionDetailFragmentActivity;
import org.jorge.lolin1.ui.frags.ChampionSkinSupportFragment;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 21/04/2014.
 */
public class SkinsViewPagerAdapter extends FragmentPagerAdapter
        implements ViewPager.OnPageChangeListener {

    private final Fragment[] skins;
    private final Context context;
    private final Champion champion;
    private final FragmentManager fragmentManager;

    public SkinsViewPagerAdapter(Context _context, Champion thisChampion,
                                 FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        fragmentManager = supportFragmentManager;
        champion = thisChampion;
        context = _context;
        String[] skinNames = thisChampion.getSkinNames();
        skins = new ChampionSkinSupportFragment[skinNames.length];
        for (int i = 0; i < skinNames.length; i++) {
            skins[i] = ChampionSkinSupportFragment.newInstance(context, i, thisChampion,
                    i == 0 ? ScalableLinearLayout.BIG_SCALE : ScalableLinearLayout.SMALL_SCALE);
        }
    }

    @Override
    public int getCount() {
        return skins.length;
    }

    @Override
    public Fragment getItem(int position) {
        float scale;

        if (position == 0) {
            scale = ScalableLinearLayout.BIG_SCALE;
        } else {
            scale = ScalableLinearLayout.SMALL_SCALE;
        }

        Fragment ret = skins[position];
        if (ret.getView() == null) {
            return ChampionSkinSupportFragment.newInstance(context, position, champion,
                    position == 0 ? ScalableLinearLayout.BIG_SCALE :
                            ScalableLinearLayout.SMALL_SCALE
            );
        }
        ((ScalableLinearLayout) ret.getView().findViewById(R.id.skin_root_view))
                .setScaleBoth(scale);

        return skins[position];
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset >= 0f && positionOffset <= 1f) {
            getRootView(position).setScaleBoth(ScalableLinearLayout.BIG_SCALE
                    - ScalableLinearLayout.DIFF_SCALE * positionOffset);
            if (position < skins.length - 1) {
                getRootView(position + 1).setScaleBoth(ScalableLinearLayout.SMALL_SCALE
                        + ScalableLinearLayout.DIFF_SCALE * positionOffset);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private ScalableLinearLayout getRootView(int position) {
        return (ScalableLinearLayout)
                fragmentManager.findFragmentByTag(this.getFragmentTag(position))
                        .getView().findViewById(R.id.skin_root_view);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" +
                ((ChampionDetailFragmentActivity) context).getSkinsViewPager().getId() + ":" +
                position;
    }
}
