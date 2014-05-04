package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.custom.ChatFilterableListAdapter;
import org.jorge.lolin1.ui.activities.ChatOverviewActivity;
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
 * Created by JorgeAntonio on 01/05/2014.
 */
public class ChatOverviewFragment extends Fragment {

    private ChatRoomSelectionListener mCallback;
    private ListView mListView;
    private ChatFilterableListAdapter listAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ChatRoomSelectionListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ChatRoomSelectionListener");
        }

        listAdapter = new ChatFilterableListAdapter(activity);

        ((ChatOverviewActivity) activity).onSectionAttached(
                new ArrayList<>(
                        Arrays.asList(
                                LoLin1Utils.getStringArray(
                                        getActivity(),
                                        "navigation_drawer_items", new String[]{""})
                        )
                ).indexOf(LoLin1Utils.getString(getActivity(), "title_section5", null))
        );

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onRoomSelected(((Friend) listAdapter.getItem(position)).getName());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_chat_overview, container, false);

        mListView = (ListView) ret.findViewById(android.R.id.list);

        mListView.setChoiceMode(
                ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onRoomSelected(
                        ((Friend) mListView.getAdapter().getItem(position)).getName());
            }
        });

        mListView.setAdapter(listAdapter);

        return ret;
    }

    public void applyFilter(CharSequence constraint) {
        listAdapter.getFilter().filter(constraint);
    }

    public interface ChatRoomSelectionListener {
        void onRoomSelected(String friendName);
    }
}
