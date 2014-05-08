package org.jorge.lolin1.ui.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.custom.navdrawerfix.FixedDrawerLayout;
import org.jorge.lolin1.ui.frags.NavigationDrawerFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
 * Created by JorgeAntonio on 09/02/14.
 */
public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final ArrayList<Integer> navigatedItemsStack =
            new ArrayList<>();
    public static final String ACTIVITY_LAYOUT = "LAYOUT";
    public static final String ACTION_BAR_MENU_LAYOUT = "ACTION_BAR_MENU_LAYOUT";
    private static final long NEW_ACTIVITY_DELAY = 250;
    private FixedDrawerLayout drawerLayout;
    private CharSequence mTitle;

    public static void clearNavigation() {
        navigatedItemsStack.clear();
        navigatedItemsStack
                .add(0, 0);//By default say that the first thing we did was staying at Home
    }

    public static int getLastSelectedNavDavIndex() {
        return navigatedItemsStack.get(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean ret = Boolean.TRUE;
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(
                        new Intent(getApplicationContext(), SettingsPreferenceActivity.class));
                break;
            default: //Up button
                ret = super.onOptionsItemSelected(item);
        }
        restoreActionBar();
        return ret;
    }

    protected void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(Boolean.TRUE);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigatedItemsStack.add(0, navigatedItemsStack.get(0));
        recreate();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == getLastSelectedNavDavIndex()) {
            //We don't want to perform an unnecessary Activity reload
            //noinspection UnnecessaryReturnStatement
            return;
        }
        else {
            navigatedItemsStack.add(0, position);
        }

        Runnable task;
        switch (position) {
            case 0:
                task = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(getApplicationContext(), NewsReaderActivity.class));
                    }
                };
                break;
            case 1:
                task = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(getApplicationContext(), JungleTimersActivity.class));
                    }
                };
                break;
            case 2:
                task = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(getApplicationContext(),
                                        ChampionListActivity.class)
                        );
                    }
                };
                break;
            case 3:
                task = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(getApplicationContext(),
                                        SurrReaderActivity.class)
                        );
                    }
                };
                break;
            case 4:
                task = new Runnable() {
                    @Override
                    public void run() {
                        startActivity(
                                new Intent(getApplicationContext(),
                                        ChatOverviewActivity.class)
                        );
                    }
                };
                break;
            default:
                Log.wtf("debug", "Should never happen - Selected index - " + position);
                task = null;
        }
        ScheduledExecutorService newActivityExecutor =
                Executors.newSingleThreadScheduledExecutor();
        newActivityExecutor.schedule(task, NEW_ACTIVITY_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mTitle = LoLin1Utils
                    .getString(this, "title_section" + (getLastSelectedNavDavIndex() + 1), null);
        }
        catch (IndexOutOfBoundsException ex) {
            mTitle = LoLin1Utils
                    .getString(this, "title_section1", null);
        }
        restoreActionBar();
    }

    @Override
    public void onBackPressed() {
        if (navigatedItemsStack.size() > 1) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        navigatedItemsStack.remove(0);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(savedInstanceState.getInt(ACTIVITY_LAYOUT));

        if (navigatedItemsStack.isEmpty()) {
            navigatedItemsStack.add(0);
        }

        FragmentManager fragmentManager = getFragmentManager();

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setHasOptionsMenu(Boolean.TRUE);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout = (FixedDrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        mTitle = LoLin1Utils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }
}
