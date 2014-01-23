package org.jorge.lolin1.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.activities.MainActivity;
import org.jorge.lolin1.activities.WebViewerActivity;
import org.jorge.lolin1.custom.NewsFragmentArrayAdapter;
import org.jorge.lolin1.custom.TranslatableHeaderTransformer;
import org.jorge.lolin1.io.net.NewsFeedProvider;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
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
 * Created by JorgeAntonio on 09/01/14.
 */
public class NewsListFragment extends ListFragment implements OnRefreshListener {

    private static PullToRefreshLayout mPullToRefreshLayout;
    private NewsFragmentArrayAdapter listAdapter;
    private NewsFeedProvider newsFeedProvider;

    public NewsListFragment(Context context) {
        super();
        listAdapter = new NewsFragmentArrayAdapter(context);
        setListAdapter(listAdapter);
        newsFeedProvider = new NewsFeedProvider(context);
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
        String url = listAdapter.getItem(position).getLink();
        Intent inAppBrowserIntent = new Intent(getActivity(), WebViewerActivity.class);
        inAppBrowserIntent.putExtra("url", url);
        startActivity(inAppBrowserIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View ret = inflater.inflate(R.layout.fragment_news_feed, container, false);

        listAdapter.updateShownNews();

        return ret;
    }

    /**
     * Attach to list view once the view hierarchy has been created.
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        Options.Builder optionsBuilder = Options.create();

        int retrieved = Utils.getInt(getActivity(), "feed_refresh_distance_percentage",
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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d("NX4", "Refresh successful!");

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
                if (newsFeedProvider.requestFeedRefresh()) {
                    listAdapter.updateShownNews();
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
}
