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
import org.jorge.lolin1.func.custom.NewsAdapter;
import org.jorge.lolin1.func.custom.TranslatableHeaderTransformer;
import org.jorge.lolin1.io.net.NewsFeedProvider;
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
 * Created by JorgeAntonio on 09/01/14.
 */
public class NewsListFragment extends ListFragment implements OnRefreshListener {

    private static PullToRefreshLayout mPullToRefreshLayout;
    private NewsAdapter listAdapter;
    private NewsFeedProvider newsFeedProvider;
    private NewsListFragmentListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (listAdapter == null) {
            listAdapter = new NewsAdapter(getActivity());
            setListAdapter(listAdapter);
        }
        if (newsFeedProvider == null) {
            newsFeedProvider = new NewsFeedProvider(getActivity());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        getListView().setItemChecked(position, Boolean.TRUE);
        if (getResources().getBoolean(R.bool.feed_has_two_panes)) {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                    .putInt("lastSelectedNewsIndex", position).commit();
        }
        mCallback.onNewsArticleSelected(position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (NewsListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NewsListFragmentListener");
        }

        ((DrawerLayoutFragmentActivity) activity).onSectionAttached(
                Arrays.asList(LoLin1Utils.getStringArray(getActivity(), "navigation_drawer_items",
                        new String[]{""}))
                        .indexOf(LoLin1Utils.getString(getActivity(), "title_section1", "Home"))
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listAdapter.updateShownNews();

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

        getListView().setChoiceMode(
                ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onRefreshStarted(View view) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                newsFeedProvider.requestFeedRefresh();
                listAdapter.updateShownNews();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mPullToRefreshLayout.setRefreshComplete();
            }

            @Override
            protected void onCancelled() {
                mPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
    }


    public interface NewsListFragmentListener {
        public void onNewsArticleSelected(int index);
    }
}
