package org.jorge.lolin1.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.activities.NewsActivity;
import org.jorge.lolin1.custom.NewsFragmentArrayAdapter;
import org.jorge.lolin1.io.db.NewsToSQLiteBridge;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.HeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

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
    private PullToRefreshLayout mPullToRefreshLayout;

    public NewsListFragment(Context context) {
        super();
        listAdapter = new NewsFragmentArrayAdapter(context);
        setListAdapter(listAdapter);
    }

    private void setUpPullToRefresh(View view) {

        Options.Builder optionsBuilder = Options.create();

        optionsBuilder.
                scrollDistance(.75f).headerLayout(R.layout.customised_header)
                .headerTransformer(new CustomisedHeaderTransformer());
//                .refreshOnUp(Boolean.TRUE)
//                .minimize(R.integer.ptr_minimize_delay_millis);

        Options options = optionsBuilder.build();

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        mPullToRefreshLayout.setEnabled(Boolean.TRUE);

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.SetupWizard setupWizard = ActionBarPullToRefresh.from(getActivity());

        // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
        setupWizard = setupWizard.insertLayoutInto(viewGroup);

        // We need to mark the ListView and its Empty View as pullable
        // This is because they are not direct children of the ViewGroup
        setupWizard =
                setupWizard.theseChildrenArePullable(getListView(), getListView().getEmptyView());

        // We can now complete the setup as desired
        setupWizard = setupWizard.listener(new OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
//                try {
//                    Thread.sleep(10000);
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Log.i("REFRESHED", "!!!!!!!");
            }
        });

        setupWizard = setupWizard.options(options);

        setupWizard.setup(mPullToRefreshLayout);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listAdapter.updateShownNews();
        ((NewsActivity) activity).onSectionAttached(
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpPullToRefresh(view);
    }

    static class CustomisedHeaderTransformer extends HeaderTransformer {

        private View mHeaderView;
        private TextView mMainTextView;
        private TextView mProgressTextView;

        @Override
        public void onViewCreated(Activity activity, View headerView) {
            mHeaderView = headerView;
            mMainTextView = (TextView) headerView.findViewById(R.id.ptr_text);
            mProgressTextView = (TextView) headerView.findViewById(R.id.ptr_text_secondary);
        }

        @Override
        public void onReset() {
            mMainTextView.setVisibility(View.VISIBLE);
            mMainTextView.setText(R.string.pull_to_refresh_pull_label);

            mProgressTextView.setVisibility(View.GONE);
            mProgressTextView.setText("");
        }

        @Override
        public void onPulled(float percentagePulled) {
            mProgressTextView.setVisibility(View.VISIBLE);
            mProgressTextView.setText(Math.round(100f * percentagePulled) + "%");
        }

        @Override
        public void onRefreshStarted() {
            mMainTextView.setText(R.string.pull_to_refresh_refreshing_label);
            mProgressTextView.setVisibility(View.GONE);
        }

        @Override
        public void onReleaseToRefresh() {
            mMainTextView.setText(R.string.pull_to_refresh_release_label);
        }

        @Override
        public void onRefreshMinimized() {
            // In this header transformer, we will ignore this call
        }

        @Override
        public boolean showHeaderView() {
            final boolean changeVis = mHeaderView.getVisibility() != View.VISIBLE;
            if (changeVis) {
                mHeaderView.setVisibility(View.VISIBLE);
            }
            return changeVis;
        }

        @Override
        public boolean hideHeaderView() {
            final boolean changeVis = mHeaderView.getVisibility() == View.VISIBLE;
            if (changeVis) {
                mHeaderView.setVisibility(View.GONE);
            }
            return changeVis;
        }
    }
}
