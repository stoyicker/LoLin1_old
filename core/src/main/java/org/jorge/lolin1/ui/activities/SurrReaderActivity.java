package org.jorge.lolin1.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.surr.SurrEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.ui.frags.SurrListFragment;

import java.util.ArrayList;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 09/02/14.
 */
public final class SurrReaderActivity extends DrawerLayoutFragmentActivity implements
        SurrListFragment.SurrListFragmentListener {
    //    private static Boolean isDualPane = Boolean.FALSE;
    private ShowcaseView surrShowCase;
    private SurrListFragment SURR_FRAGMENT;
//    private WebViewerFragment WEB_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.getBoolean("showcase_surrs_done", Boolean.FALSE))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Boolean wasSavedInstanceStateNull = savedInstanceState == null;
        if (wasSavedInstanceStateNull) {
            savedInstanceState = new Bundle();
        }

        savedInstanceState
                .putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                        R.layout.activity_surr_reader);

        super.onCreate(savedInstanceState);
        if (!preferences.getBoolean("showcase_surrs_done", Boolean.FALSE))
            surrShowCase = new ShowcaseView.Builder(this).setContentTitle(R.string.tutorial_surr_title).setContentText(R.string.tutorial_surr_content).setStyle(R.style.CustomShowcaseThemePlusNoButton).setTarget(new ViewTarget(R.id.fragment_surr_list, this)).build();

        SURR_FRAGMENT =
                (SurrListFragment) getFragmentManager().findFragmentById(R.id.fragment_surr_list);
//        WEB_FRAGMENT =
//                (WebViewerFragment) getSupportFragmentManager()
//                        .findFragmentById(R.id.fragment_web_viewer);

//        getFragmentManager().executePendingTransactions();
//        getSupportFragmentManager().executePendingTransactions();

//        isDualPane = WEB_FRAGMENT != null && WEB_FRAGMENT.getView() != null &&
//                WEB_FRAGMENT.getView().getVisibility() == View.VISIBLE;

//        int index;
        if (!wasSavedInstanceStateNull) {
            restoreState(savedInstanceState);
        }
//        else if ((index = PreferenceManager.getDefaultSharedPreferences(this)
//                .getInt("lastSelectedSurrIndex", 0)) != -1 && isDualPane) {
//            onSurrArticleSelected(index);
//        }
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt("index", 0);
            if (index != -1) {
                SURR_FRAGMENT.setSelection(index);
//                onSurrArticleSelected(index);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", PreferenceManager.getDefaultSharedPreferences(this).getInt(
                "lastSelectedSurrIndex", -1));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSurrArticleSelected(int index) {
        showUrlInWebViewerFragment(index);
    }

    @Override
    public void onSurrRefreshed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (surrShowCase != null) {
            preferences.edit().putBoolean("showcase_surrs_done", Boolean.TRUE).apply();
            surrShowCase.hide();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

//    private void showUrlInWebViewerFragment(int index) {
//        ArrayList<SurrEntry> surrs;
//        if (isDualPane) {
//            if (!(surrs = SQLiteDAO.getSingleton().getSurrs()).isEmpty() && index > -1) {
//                WEB_FRAGMENT.loadUrl(surrs.get(index).getLink());
//            }
//        }
//        else {
//            Intent singleViewIntent = new Intent(getApplicationContext(), WebViewerActivity.class);
//            singleViewIntent.putExtra("index", index);
//            startActivity(singleViewIntent);
//        }
//    }

    private void showUrlInWebViewerFragment(int index) {
        ArrayList<SurrEntry> surrs;
        if (!(surrs = SQLiteDAO.getSingleton().getSurrs()).isEmpty() && index > -1) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(surrs.get(index).getLink())));
        }
    }
}
