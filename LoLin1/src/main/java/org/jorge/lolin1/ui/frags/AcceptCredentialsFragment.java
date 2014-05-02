package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
 * Created by JorgeAntonio on 01/05/2014.
 */
public class AcceptCredentialsFragment extends Fragment {

    private AcceptCredentialsListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AcceptCredentialsListener) activity;
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement AcceptCredentialsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_account_authenticator_accept_credentials, container, false);

        Button acceptButton = (Button) ret.findViewById(R.id.authenticator_accept_button);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCredentialsAccepted();
            }
        });

        return ret;
    }

    public interface AcceptCredentialsListener {
        public void onCredentialsAccepted();
    }
}
