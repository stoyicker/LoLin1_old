package org.jorge.lolin1.activities;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.NavigationDrawerFragment;
import org.jorge.lolin1.utils.DebugUtils;
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
 * Created by JorgeAntonio on 09/02/14.
 */
public abstract class DrawerLayoutFragmentActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static int lastSelectedNavDrawerItem = 0;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout drawerLayout;
    private CharSequence mTitle;

    public static int getLastSelectedNavDavIndex() {
        return lastSelectedNavDrawerItem;
    }

    /**
     * Called when activity start-up is complete (after {@link #onStart}
     * and {@link #onRestoreInstanceState} have been called).  Applications will
     * generally not implement this method; it is intended for system
     * classes to do final initialization after application code has run.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onCreate
     */
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
                startActivity(new Intent(this, SettingsPreferenceActivity.class));
                break;
            default: //Up button
                ret = super.onOptionsItemSelected(item);
        }
        restoreActionBar();
        return ret;
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(Boolean.TRUE);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    /**
     * Called when an item in the navigation drawer is selected.
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == lastSelectedNavDrawerItem) {
            //We don't want to perform an unnecessary Activity reload
            //noinspection UnnecessaryReturnStatement
            return;
        }
        else {
            lastSelectedNavDrawerItem = position;
        }

        DebugUtils.showTrace("NX4", new Exception());

        Class target = null;
        switch (position) {
            case 0:
                target = NewsReaderActivity.class;
                break;
            case 5:
                target = SurrReaderActivity.class;
                break;
            default:
                Log.wtf("NX4", "Should never happen - Selected index - " + position);
        }
        startActivity(new Intent(this, target));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(savedInstanceState.getInt("layout"));

        FragmentManager fragmentManager = getFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setHasOptionsMenu(Boolean.TRUE);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void onSectionAttached(int number) {
        int shiftedPos = number + 1;
        mTitle = Utils.getString(this, "title_section" + shiftedPos, "");
        if (mTitle.toString().isEmpty()) {
            mTitle = getString(R.string.title_section1);
        }
    }
}
