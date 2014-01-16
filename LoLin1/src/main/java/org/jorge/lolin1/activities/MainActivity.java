package org.jorge.lolin1.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.NavigationDrawerFragment;
import org.jorge.lolin1.ui.NewsListFragment;
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
implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
        setContentView(R.layout.main_activity);

        FragmentManager fragmentManager = getFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private final int commitReplaceAllBy(Fragment newFragment, String newFragmentTag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction removeTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        String[] fragment_tags =
                Utils.getStringArray(this, "main_fragments_tags", new String[]{"ERROR"});
        Fragment x;
        for (int i = 0; i < fragment_tags.length; i++) {
            x = fragmentManager.findFragmentByTag(fragment_tags[i]);
            if (x != null) {
                removeTransaction.remove(x);
                x = null;
            }
        }
        removeTransaction.commit();

        FragmentTransaction addTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        addTransaction.replace(R.id.main_container, newFragment, newFragmentTag);

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
        Fragment newFragment = null;
        String newFragmentTag = null;
        switch (position) {
            case 0:
                newFragment = new NewsListFragment(this);
                newFragmentTag = Utils.getString(this, "tag_fragment_news", "ERROR");
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            default:
                newFragment = new NewsListFragment(this);
                newFragmentTag = Utils.getString(this, "tag_fragment_news", "ERROR");
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

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p/>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        View icon = findViewById(R.id.action_ref_news);
        NewsListFragment newsListFragment = (NewsListFragment) getFragmentManager()
                .findFragmentByTag(Utils.getString(this, "tag_fragment_news", "ERROR"));
        if (icon != null)
        //At certain states the view will be null so we need this statement to avoid a NPE,
        //but it's not a problem because on such states the icon doesn't need to be animated.
        {
            if (newsListFragment.getUPDATE_RUNNING()) {
                icon.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.clockwise_rotate));
            }
            else {
                icon.clearAnimation();
            }
        }
        return super.onPrepareOptionsMenu(menu);
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
            case R.id.action_ref_news:
                final NewsListFragment ref = (NewsListFragment) getFragmentManager()
                        .findFragmentByTag(Utils.getString(this, "tag_fragment_news", "ERROR"));
                new AsyncTask<Void, Void, Boolean>() {
                    /**
                     * Runs on the UI thread before {@link #doInBackground}.
                     *
                     * @see #onPostExecute
                     * @see #doInBackground
                     */
                    @Override
                    protected void onPreExecute() {
                        ref.setUPDATE_RUNNING(Boolean.TRUE);
                        invalidateOptionsMenu();
                    }

                    /**
                     * <p>Runs on the UI thread after {@link #doInBackground}. The
                     * specified result is the value returned by {@link #doInBackground}.</p>
                     * <p/>
                     * <p>This method won't be invoked if the task was cancelled.</p>
                     *
                     * @param aBoolean The result of the operation computed by {@link #doInBackground}.
                     * @see #onPreExecute
                     * @see #doInBackground
                     * @see #onCancelled(Object)
                     */
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        ref.setUPDATE_RUNNING(Boolean.FALSE);
                        invalidateOptionsMenu();
                    }

                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        return ref.getNewsFeedProvider().requestFeedRefresh();
                    }
                }.execute();
                break;
            default: //Up button
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
