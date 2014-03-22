package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.ArrayList;

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
 * Created by JorgeAntonio on 22/03/2014.
 */
public class RealmListFragment extends Fragment {

    private RealmListFragmentListener mCallback;
    private ArrayList<TextView> views = new ArrayList<>();

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_realm_list, container, Boolean.FALSE);
        ViewGroup viewAsViewGroup = (ViewGroup) ret;

        String[] realms =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(), "servers", null);

        int realmCounter = 0;
        for (String realm : realms) {
            final TextView textView =
                    new TextView(getActivity().getApplicationContext()) {
                        @Override
                        public boolean onTouchEvent(MotionEvent event) {
                            for (TextView x : views)
                                if (x != this) {
                                    x.setShadowLayer(0, 0, 0, R.color.theme_white);
                                    x.setTypeface(null, Typeface.NORMAL);
                                }
                            this.setTypeface(null, Typeface.BOLD);
                            this.setShadowLayer(3, 3, 3, R.color.theme_strong_orange);
                            mCallback.onRealmSelected((String) this.getTag());
                            return Boolean.TRUE;
                        }
                    };
            views.add(textView);
            textView.setText(realm.toUpperCase());
            textView.setTextSize(LoLin1Utils
                    .getInt(getActivity().getApplicationContext(), "server_chooser_text_size", 25));
            textView.setTextColor(getResources().getColor(R.color.theme_black));
            textView.setTag(realms[realmCounter].toLowerCase());
            viewAsViewGroup.addView(textView);
            realmCounter++;
        }

        return ret;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (RealmListFragmentListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement RealmListFragmentListener.");
        }
    }

    public interface RealmListFragmentListener {
        public void onRealmSelected(String newSelectedRealm);
    }
}
