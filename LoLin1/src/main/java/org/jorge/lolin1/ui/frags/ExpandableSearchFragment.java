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
import org.jorge.lolin1.ui.activities.ChampionListActivity;
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

    private ExpandableSearchListener mCallback;
    private EditText queryField;
    private String lastQuery = "";
    private Timer filterUpdateTimer;
    private final int FILTER_UPDATE_DELAY_MILLIS = 0, FILTER_UPDATE_INTERVAL_MILLIS = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryField = (EditText) view.findViewById(R.id.query_field);

        queryField.setVisibility(View.GONE);

        setTimer();
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

        ((ChampionListActivity) activity).onSectionAttached(
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
            filterUpdateTimer.cancel();
            LoLin1Utils.slideViewUp(getActivity().getApplicationContext(), queryField);
            queryField.setVisibility(View.GONE);
            ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(queryField.getWindowToken(), 0);
            queryField.clearFocus();
            queryField.setEnabled(Boolean.FALSE);
        }
        else {
            setTimer();
            LoLin1Utils.slideViewDown(getActivity().getApplicationContext(), queryField);
            queryField.setEnabled(Boolean.TRUE);
            queryField.setVisibility(View.VISIBLE);
            queryField.requestFocus();
            ((InputMethodManager) getActivity().getApplicationContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(queryField, InputMethodManager.SHOW_FORCED);
        }
    }

    public interface ExpandableSearchListener {
        public void onNewQuery(String query);
    }
}
