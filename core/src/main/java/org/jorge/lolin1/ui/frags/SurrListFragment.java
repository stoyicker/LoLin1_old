package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.custom.SurrsAdapter;
import org.jorge.lolin1.func.custom.TranslatableHeaderTransformer;
import org.jorge.lolin1.func.feeds.surr.SurrEntry;
import org.jorge.lolin1.io.net.SurrFeedProvider;
import org.jorge.lolin1.ui.activities.DrawerLayoutFragmentActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 25/01/14.
 */
public class SurrListFragment extends ListFragment implements OnRefreshListener {

    private static PullToRefreshLayout mPullToRefreshLayout;
    private SurrsAdapter listAdapter;
    private SurrFeedProvider surrFeedProvider;
    private SurrListFragmentListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (listAdapter == null) {
            listAdapter = new SurrsAdapter(getActivity());
            setListAdapter(listAdapter);
        }
        if (surrFeedProvider == null) {
            surrFeedProvider = new SurrFeedProvider(getActivity());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

//        if (getResources().getBoolean(R.bool.feed_has_two_panes)) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putInt("lastSelectedSurrIndex", position).apply();
//        }

        getListView().setItemChecked(position, Boolean.TRUE);
        final SurrEntry selectedEntry = listAdapter.getItem(position);
        mCallback.onSurrArticleSelected(position);

        if (LoLin1Utils.isInternetReachable(getActivity())) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //It is "not possible" that the user comes back from the
                    // webview without the writing having been performed,
                    // so we take advantage of concurrency
                    selectedEntry.markAsRead();
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ret = inflater.inflate(R.layout.fragment_surr_feed, container, false);
        listAdapter.updateShownSurrs();

        return ret;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        listAdapter.updateShownSurrs();

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        Options.Builder optionsBuilder = Options.create();

        int retrieved = LoLin1Utils.getInt(getActivity(), "feed_refresh_distance_percentage",
                R.integer.feed_refresh_distance_percentage);
        float scrollDistance = (float) retrieved / 100;
        optionsBuilder = optionsBuilder
                .scrollDistance(scrollDistance);

        optionsBuilder = optionsBuilder.headerTransformer(new TranslatableHeaderTransformer());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())

                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)

                        // We need to mark the ListView and its empty view as pullable
                        // This is because they are not direct children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                        // Set the OnRefreshListener
                .listener(this).useViewDelegate(ImageView.class, new ViewDelegate() {
            @Override
            public boolean isReadyForPull(View view, float v, float v2) {
                return Boolean.TRUE;
            }
        }).options(optionsBuilder.build())
                // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (SurrListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SurrListFragmentListener");
        }

        ((DrawerLayoutFragmentActivity) activity)
                .onSectionAttached(Arrays.asList(
                                LoLin1Utils.getStringArray(getActivity(), "navigation_drawer_items",
                                        new String[]{""})
                        )
                                .indexOf(LoLin1Utils.getString(getActivity(), "title_section4",
                                        "Surrender@20"))
                );
    }

    @Override
    public void onRefreshStarted(View view) {
        new AsyncTask<Void, Void, Void>() {
            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p/>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Void doInBackground(Void... params) {
                if (surrFeedProvider.requestFeedRefresh()) {
                    listAdapter.updateShownSurrs();
                }
                return null;
            }

            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param aVoid The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                mPullToRefreshLayout.setRefreshComplete();
                mCallback.onSurrRefreshed();
            }

            /**
             * <p>Applications should preferably override {@link #onCancelled(Object)}.
             * This method is invoked by the default implementation of
             * {@link #onCancelled(Object)}.</p>
             * <p/>
             * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
             * {@link #doInBackground(Object[])} has finished.</p>
             *
             * @see #onCancelled(Object)
             * @see #cancel(boolean)
             * @see #isCancelled()
             */
            @Override
            protected void onCancelled() {
                mPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
    }


    public interface SurrListFragmentListener {
        public void onSurrArticleSelected(int index);

        public void onSurrRefreshed();
    }
}
