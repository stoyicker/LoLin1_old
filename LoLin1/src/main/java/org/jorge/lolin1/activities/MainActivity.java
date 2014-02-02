package org.jorge.lolin1.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.ChampionListFragment;
import org.jorge.lolin1.frags.NavigationDrawerFragment;
import org.jorge.lolin1.frags.NewsListFragment;
import org.jorge.lolin1.frags.SurrListFragment;
import org.jorge.lolin1.frags.WebViewerFragment;
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
 */
public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        NewsListFragment.NewsListFragmentListener, SurrListFragment.SurrListFragmentListener {

    private Fragment FRAGMENT_NEWS, FRAGMENT_SURR, FRAGMENT_CHAMPS;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int lastSelectedNavDrawerItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private int commitReplaceAllBy(Fragment newFragment, String newFragmentTag) {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment webViewerFragment = fragmentManager.findFragmentById(R.id.web_viewer);
        FragmentTransaction removeTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        String[] fragment_tags =
                Utils.getStringArray(this, "main_fragments_tags", new String[]{"ERROR"});
        Fragment x;
        for (String fragment_tag : fragment_tags) {
            x = fragmentManager.findFragmentByTag(fragment_tag);
            if (x != null) {
                removeTransaction.remove(x);
            }
        }

        if (webViewerFragment != null) {
            removeTransaction.remove(webViewerFragment);
        }

        removeTransaction.addToBackStack(null);

        removeTransaction.commit();

        FragmentTransaction addTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        addTransaction.replace(R.id.main_container, newFragment, newFragmentTag);

        addTransaction.addToBackStack(null);

        return addTransaction.commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == lastSelectedNavDrawerItem) {
            //We don't want to perform a useless fragment reload
            return;
        }
        else {
            lastSelectedNavDrawerItem = position;
        }
        Fragment newFragment;
        String newFragmentTag;
        switch (position) {
            case 0:
                if (FRAGMENT_NEWS == null) {
                    FRAGMENT_NEWS = new NewsListFragment(this);
                }
                newFragment = FRAGMENT_NEWS;
                newFragmentTag = Utils.getString(this, "tag_fragment_news", "ERROR");
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                if (FRAGMENT_CHAMPS == null) {
                    FRAGMENT_CHAMPS = new ChampionListFragment(this);
                }
                newFragment = FRAGMENT_CHAMPS;
                newFragmentTag = Utils.getString(this, "tag_fragment_champs", "ERROR");
                break;
            case 5:
                if (FRAGMENT_SURR == null) {
                    FRAGMENT_SURR = new SurrListFragment(this);
                }
                newFragment = FRAGMENT_SURR;
                newFragmentTag = Utils.getString(this, "tag_fragment_surr", "ERROR");
                break;
            case 6:
            default:
                newFragment =
                        FRAGMENT_NEWS; //FIXME Remove the default once all the sections have been done
                newFragmentTag = Utils.getString(this, "tag_fragment_news",
                        getString(R.string.tag_fragment_news));
                Log.wtf("ERROR", "Should never happen");
        }
        commitReplaceAllBy(newFragment, newFragmentTag);
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        mTitle = Utils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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

    @Override
    public void onNewsArticleSelected(String url) {
        showUrlInWebViewerFragment(url);
    }

    private void showUrlInWebViewerFragment(String url) {
        WebViewerFragment webViewerFragment =
                (WebViewerFragment) getFragmentManager().findFragmentById(R.id.web_viewer);

        if (webViewerFragment != null) {
            webViewerFragment.showUrl(url);
        }
        else {
            Bundle urlBundle = new Bundle();
            urlBundle.putString("url", url);

            webViewerFragment = new WebViewerFragment();
            webViewerFragment.setArguments(urlBundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, webViewerFragment);

            transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    public void onSurrArticleSelected(String url) {
        showUrlInWebViewerFragment(url);
    }
}
