package org.jorge.lolin1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.feeds.news.NewsEntry;
import org.jorge.lolin1.frags.NewsListFragment;
import org.jorge.lolin1.frags.WebViewerFragment;
import org.jorge.lolin1.io.db.SQLiteDAO;

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
 * Created by JorgeAntonio on 07/02/14.
 */
public class NewsReaderActivity extends DrawerLayoutFragmentActivity implements
        NewsListFragment.NewsListFragmentListener {
    private static Boolean isDualPane = Boolean.FALSE;
    private NewsListFragment NEWS_FRAGMENT;
    private WebViewerFragment WEB_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean wasSavedInstanceStateNull = savedInstanceState == null;
        if (wasSavedInstanceStateNull) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt("layout", R.layout.news_reader);

        super.onCreate(savedInstanceState);

        NEWS_FRAGMENT =
                (NewsListFragment) getFragmentManager().findFragmentById(R.id.fragment_list);
        WEB_FRAGMENT =
                (WebViewerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_web_viewer);

        getFragmentManager().executePendingTransactions();
        getSupportFragmentManager().executePendingTransactions();

        isDualPane = WEB_FRAGMENT != null && WEB_FRAGMENT.getView() != null &&
                WEB_FRAGMENT.getView().getVisibility() == View.VISIBLE;

        int index;
        if (!wasSavedInstanceStateNull) {
            restoreState(savedInstanceState);
        }
        else if ((index = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt("lastSelectedNewsIndex", -1)) != -1 &&
                getResources().getBoolean(R.bool.feed_has_two_panes)) {
            Log.d("NX4", "I'm in");
            onNewsArticleSelected(index);
        }

    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt("index", 0);
            NEWS_FRAGMENT.setSelection(index);
            onNewsArticleSelected(index);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", PreferenceManager.getDefaultSharedPreferences(this).getInt(
                "lastSelectedNewsIndex", -1));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNewsArticleSelected(int index) {
        showUrlInWebViewerFragment(index);
    }

    private void showUrlInWebViewerFragment(int index) {
        ArrayList<NewsEntry> news;
        if (isDualPane) {
            if (!(news = SQLiteDAO.getSingleton().getNews()).isEmpty()) {
                WEB_FRAGMENT.loadUrl(news.get(index).getLink());
            }
        }
        else {
            Intent singleViewIntent = new Intent(this, WebViewerActivity.class);
            singleViewIntent.putExtra("index", index);
            startActivity(singleViewIntent);
        }
    }
}
