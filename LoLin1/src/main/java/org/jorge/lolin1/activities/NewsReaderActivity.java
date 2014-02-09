package org.jorge.lolin1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.NewsListFragment;
import org.jorge.lolin1.frags.WebViewerFragment;
import org.jorge.lolin1.io.db.SQLiteBridge;

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
        savedInstanceState.putInt("layout", R.layout.activity_feed);

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
        if (wasSavedInstanceStateNull) {
            savedInstanceState = null;
        }
        restoreState(savedInstanceState);
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
        outState.putInt("index", NewsListFragment.getSelectedIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNewsArticleSelected(int index) {
        showUrlInWebViewerFragment(index);
    }

    private void showUrlInWebViewerFragment(int index) {
        if (isDualPane) {
            WEB_FRAGMENT.loadUrl(SQLiteBridge.getSingleton().getNews().get(index).getLink());
        }
        else {
            Intent singleViewIntent = new Intent(this, WebViewerActivity.class);
            singleViewIntent.putExtra("index", index);
            startActivity(singleViewIntent);
        }
    }
}
