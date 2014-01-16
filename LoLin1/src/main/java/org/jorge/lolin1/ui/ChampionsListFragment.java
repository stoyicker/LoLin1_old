package org.jorge.lolin1.ui;

import android.app.Activity;
import android.app.ListFragment;

import org.jorge.lolin1.activities.MainActivity;
import org.jorge.lolin1.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChampionsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChampionsListFragment extends ListFragment {

    public ChampionsListFragment() {
        //TODO ChampionsListFragment
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(
                new ArrayList<>(
                        Arrays.asList(
                                Utils.getStringArray(
                                        getActivity().getApplicationContext(),
                                        "navigation_drawer_items", new String[]{""})
                        )
                ).indexOf(Utils.getString(getActivity().getApplicationContext(), "title_section",
                        "Champions")));
    }
}
