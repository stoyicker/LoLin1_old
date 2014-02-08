package org.jorge.lolin1.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.NavigationDrawerFragment;
import org.jorge.lolin1.frags.NewsListFragment;
import org.jorge.lolin1.frags.WebViewerFragment;
import org.jorge.lolin1.io.db.SQLiteBridge;
import org.jorge.lolin1.utils.Utils;

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
public class NewsReaderActivity extends FragmentActivity implements
        NewsListFragment.NewsListFragmentListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks {
    private Boolean isDualPane = Boolean.FALSE;
    private NewsListFragment NEWS_FRAGMENT;
    private WebViewerFragment WEB_FRAGMENT;
    private int lastSelectedNavDrawerItem = 0;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == lastSelectedNavDrawerItem) {
            //We don't want to perform a useless fragment reload
            return;
        }
        else {
            lastSelectedNavDrawerItem = position;
        }
        //TODO The rest of the stuff
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.standard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsPreferenceActivity.class));
                break;
            default: //Up button
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        mTitle = Utils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        FragmentManager fragmentManager = getFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        NEWS_FRAGMENT =
                (NewsListFragment) getFragmentManager().findFragmentById(R.id.fragment_news);
        WEB_FRAGMENT =
                (WebViewerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_web_viewer);

        View webView = findViewById(R.id.fragment_web_viewer);
        isDualPane = webView != null && webView.getVisibility() == View.VISIBLE;
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
    public void onNewsArticleSelected(int index) {
        showUrlInWebViewerFragment(index);
    }

    private void showUrlInWebViewerFragment(int index) {
        if (isDualPane) {
            Log.d("NX4", "Double pane mode");
            WEB_FRAGMENT.loadUrl(SQLiteBridge.getSingleton().getNews().get(index).getLink());
        }
        else {
            Log.d("NX4", "Single pane mode");
            Intent singleViewIntent = new Intent(this, WebViewerActivity.class);
            singleViewIntent.putExtra("index", index);
            startActivity(singleViewIntent);
        }
    }
}
