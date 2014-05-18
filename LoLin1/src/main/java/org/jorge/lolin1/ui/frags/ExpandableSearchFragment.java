package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.DrawerLayoutFragmentActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
 * Created by JorgeAntonio on 14/04/2014.
 */
public class ExpandableSearchFragment extends Fragment {

    private static final String WAS_SHOWN = "wasShown";
    private ExpandableSearchListener mCallback;

    public EditText getQueryField() {
        return queryField;
    }

    private EditText queryField;
    private String lastQuery = "";
    private Timer filterUpdateTimer;
    private static final int DEFAULT_FILTER_UPDATE_DELAY_MILLIS = 0,
            DEFAULT_FILTER_UPDATE_INTERVAL_MILLIS = 1000;
    private final int FILTER_UPDATE_DELAY_MILLIS, FILTER_UPDATE_INTERVAL_MILLIS;

    @SuppressWarnings("unused")
    public ExpandableSearchFragment() {
        this(-1, -1);
    }

    public ExpandableSearchFragment(int filterUpdateDelayMillis, int filterUpdateIntervalMillis) {
        FILTER_UPDATE_DELAY_MILLIS = filterUpdateDelayMillis > 0 ? filterUpdateDelayMillis :
                DEFAULT_FILTER_UPDATE_DELAY_MILLIS;
        FILTER_UPDATE_INTERVAL_MILLIS =
                filterUpdateIntervalMillis > 0 ? filterUpdateIntervalMillis :
                        DEFAULT_FILTER_UPDATE_INTERVAL_MILLIS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_search, container, false);
    }

    private void cancelTimer() {
        if (filterUpdateTimer != null) {
            filterUpdateTimer.cancel();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryField = (EditText) view.findViewById(R.id.query_field);

        queryField.setVisibility(View.GONE);
    }

    private void setTimer() {
        filterUpdateTimer = new Timer();
        filterUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String thisQuery;
                if (!(thisQuery = queryField.getText().toString()).contentEquals(lastQuery) &&
                        queryField.isShown()) {
                    lastQuery = thisQuery;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCallback.onNewQuery(lastQuery);
                        }
                    });
                }
            }
        }, FILTER_UPDATE_DELAY_MILLIS, FILTER_UPDATE_INTERVAL_MILLIS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ExpandableSearchListener) getActivity();
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement ExpandableSearchListener");
        }

        ((DrawerLayoutFragmentActivity) activity).onSectionAttached(
                new ArrayList<>(
                        Arrays.asList(
                                LoLin1Utils.getStringArray(
                                        getActivity(),
                                        "navigation_drawer_items", new String[]{""})
                        )
                ).indexOf(LoLin1Utils.getString(getActivity(), "title_section3",
                        "Champions"))
        );
    }

    public void toggleVisibility() {
        if (queryField.isShown()) {
            cancelTimer();
            queryField.setVisibility(View.GONE);
            ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(queryField.getWindowToken(), 0);
            queryField.clearFocus();
            queryField.setEnabled(Boolean.FALSE);
        }
        else {
            setTimer();
            queryField.setEnabled(Boolean.TRUE);
            queryField.setVisibility(View.VISIBLE);
            queryField.requestFocus();
            ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(queryField, InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Boolean wasShown;
        if (savedInstanceState != null &&
                (wasShown = savedInstanceState.getBoolean(WAS_SHOWN)) != null) {
            if (!wasShown) {
                cancelTimer();
                queryField.setVisibility(View.GONE);
                ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(queryField.getWindowToken(), 0);
                queryField.clearFocus();
                queryField.setEnabled(Boolean.FALSE);
                if (getView() != null) {
                    getView().setVisibility(View.VISIBLE);
                }
            }
            else {
                setTimer();
                queryField.setEnabled(Boolean.TRUE);
                queryField.setVisibility(View.VISIBLE);
                queryField.requestFocus();
                ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE))
                        .showSoftInput(queryField, InputMethodManager.SHOW_FORCED);
                if (getView() != null) {
                    getView().setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (queryField.isShown()) {
            toggleVisibility();
        }
        ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(queryField.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putBoolean(WAS_SHOWN, queryField.isShown());
        super.onSaveInstanceState(outState);
    }

    public interface ExpandableSearchListener {
        public void onNewQuery(String query);
    }
}
