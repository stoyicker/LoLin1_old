package org.jorge.lolin1.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.activities.MainActivity;
import org.jorge.lolin1.custom.NewsFragmentArrayAdapter;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.io.net.NewsFeedProvider;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

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
 * Created by JorgeAntonio on 09/01/14.
 */
public class NewsListFragment extends ListFragment {

    private NewsFragmentArrayAdapter listAdapter;
    private NewsFeedProvider newsFeedProvider;
    private Boolean UPDATE_RUNNING = Boolean.FALSE;

    public NewsListFragment(Context context) {
        super();
        listAdapter = new NewsFragmentArrayAdapter(context);
        setListAdapter(listAdapter);
        newsFeedProvider = new NewsFeedProvider(context);
    }

    public NewsFeedProvider getNewsFeedProvider() {
        return newsFeedProvider;
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link android.app.Activity#onCreateOptionsMenu(android.view.Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news_default, menu);
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(android.app.Activity)} and before
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(Boolean.TRUE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String url = NewsToSQLiteBridge.getSingleton().getNewsUrl(position);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

//    /**
//     * Prepare the Screen's standard options menu to be displayed.  This is
//     * called right before the menu is shown, every time it is shown.  You can
//     * use this method to efficiently enable/disable items or otherwise
//     * dynamically modify the contents.  See
//     * {@link android.app.Activity#onPrepareOptionsMenu(android.view.Menu) Activity.onPrepareOptionsMenu}
//     * for more information.
//     *
//     * @param menu The options menu as last shown or first initialized by
//     *             onCreateOptionsMenu().
//     * @see #setHasOptionsMenu
//     * @see #onCreateOptionsMenu
//     */
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//
//    }

    @Override
    public void onAttach(Activity activity) {
        Log.d("NX4", "onAttach has been called.");
        super.onAttach(activity);

        Log.d("NX4", "onAttach is about to call updateShownNews.");
        listAdapter.updateShownNews();
        Log.d("NX4", "updateShownNews has finished.");
        ((MainActivity) activity).onSectionAttached(
                new ArrayList<>(
                        Arrays.asList(
                                Utils.getStringArray(
                                        getActivity().getApplicationContext(),
                                        "navigation_drawer_items", new String[]{""})
                        )
                ).indexOf(Utils.getString(getActivity().getApplicationContext(), "title_section1",
                        "Home"))
        );
    }

    public Boolean getUPDATE_RUNNING() {
        return UPDATE_RUNNING;
    }

    public final void setUPDATE_RUNNING(Boolean updateRunning) {
        this.UPDATE_RUNNING = updateRunning;
    }
}
