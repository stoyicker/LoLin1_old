package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.jorge.lolin1.R;

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
public class VerificationFragment extends Fragment {

    private VerificationFragmentListener mCallback;
    private ImageButton imageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_verification, container, Boolean.FALSE);

        imageButton = (ImageButton) ret.findViewById(R.id.verification_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onVerificationFired();
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                        .edit().putBoolean("initial_setup_done", Boolean.TRUE).commit();
            }
        });

        imageButton.setEnabled(Boolean.FALSE);

        return ret;
    }

    public void setButton(Boolean enabled) {
        imageButton.setEnabled(enabled);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (VerificationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement VerificationFragmentListener.");
        }
    }

    public interface VerificationFragmentListener {
        public void onVerificationFired();
    }
}
