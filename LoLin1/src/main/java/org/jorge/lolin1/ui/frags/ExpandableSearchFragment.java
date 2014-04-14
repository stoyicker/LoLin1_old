package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.ChampionListActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

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
 * Created by JorgeAntonio on 14/04/2014.
 */
public class ExpandableSearchFragment extends Fragment {

    private ExpandableSearchListener mCallback;
    private EditText queryField;
    private String lastQuery = "";

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

        queryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newQuery;
                if (!lastQuery.contentEquals((newQuery = s.toString()))) {
                    mCallback.onNewQuery(newQuery);
                    lastQuery = newQuery;
                }
            }
        });
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
            LoLin1Utils.slideViewUp(getActivity().getApplicationContext(), queryField);
            queryField.setVisibility(View.GONE);
            queryField.setText("");
            queryField.setEnabled(Boolean.FALSE);
        }
        else {
            LoLin1Utils.slideViewDown(getActivity().getApplicationContext(), queryField);
            queryField.setEnabled(Boolean.TRUE);
            queryField.setVisibility(View.VISIBLE);
        }
    }

    public interface ExpandableSearchListener {
        public void onNewQuery(String query);
    }
}
