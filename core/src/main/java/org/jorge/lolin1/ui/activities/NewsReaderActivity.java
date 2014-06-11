package org.jorge.lolin1.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.news.NewsEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.ui.frags.NewsListFragment;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 07/02/14.
 */
public final class NewsReaderActivity extends DrawerLayoutFragmentActivity implements
        NewsListFragment.NewsListFragmentListener {
    ShowcaseView newsShowcase, navigationShowcase;
    //    private static Boolean isDualPane = Boolean.FALSE;
//    private NewsListFragment NEWS_FRAGMENT;
//    private WebViewerProgressFragment WEB_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Boolean wasSavedInstanceStateNull = savedInstanceState == null;
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState
                .putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                        R.layout.activity_news_reader);

        super.onCreate(savedInstanceState);
        newsShowcase = new ShowcaseView.Builder(this).setContentText(R.string.tutorial_news_content).setContentTitle(R.string.tutorial_news_title).setStyle(R.style.CustomShowcaseTheme).setTarget(new ViewTarget(R.id.fragment_news_list, this)).build();

//  NEWS_FRAGMENT =
//                (NewsListFragment) getFragmentManager().findFragmentById(R.id.fragment_news_list);
//        WEB_FRAGMENT =
//                (WebViewerProgressFragment) getFragmentManager()
//                        .findFragmentById(R.id.fragment_web_viewer);

//        getFragmentManager().executePendingTransactions();

//        isDualPane = WEB_FRAGMENT != null && WEB_FRAGMENT.getView() != null &&
//                WEB_FRAGMENT.getView().getVisibility() == View.VISIBLE;
//        int index;
//        if (!wasSavedInstanceStateNull) {
//            restoreState(savedInstanceState);
//        } else if ((index = PreferenceManager.getDefaultSharedPreferences(this)
//                .getInt("lastSelectedNewsIndex", 0)) != -1 && isDualPane) {
//            onNewsArticleSelected(index);
//        }
    }

//    private void restoreState(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            int index = savedInstanceState.getInt("index", 0);
//            if (index != -1) {
//                NEWS_FRAGMENT.setSelection(index);
//                onNewsArticleSelected(index);
//            }
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("index", PreferenceManager.getDefaultSharedPreferences(this).getInt(
//                "lastSelectedNewsIndex", 0));
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public void onNewsArticleSelected(int index) {
        showUrlInBrowser(index);
    }

    @Override
    public void onNewsRefreshed() {
        if (newsShowcase != null) {
            newsShowcase.hide();
        }

        navigationShowcase = new ShowcaseView.Builder(this).setContentText(R.string.tutorial_navigation_contents).setContentTitle(R.string.tutorial_navigation_title).setStyle(R.style.CustomShowcaseTheme).setTarget(new ActionViewTarget(this, ActionViewTarget.Type.TITLE)).build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (navigationShowcase != null)
                    navigationShowcase.hide();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    private void showUrlInWebViewerFragment(int index) {
//        ArrayList<NewsEntry> news;
//        if (isDualPane) {
//            if (!(news = SQLiteDAO.getSingleton().getNews()).isEmpty() && index > -1) {
//                WEB_FRAGMENT.loadUrl(news.get(index).getLink());
//            }
//        } else {
//            Intent singleViewIntent = new Intent(getApplicationContext(), WebViewerActivity.class);
//            singleViewIntent.putExtra("index", index);
//            startActivity(singleViewIntent);
//        }
//    }

    private void showUrlInBrowser(int index) {
        ArrayList<NewsEntry> news;
        news = SQLiteDAO.getSingleton().getNews();
        if (!news.isEmpty() && index > -1)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(news.get(index).getLink())));
    }
}
