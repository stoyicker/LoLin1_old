package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

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
public class AccountAuthenticatorRealmSelectorFragment extends Fragment {

    private AccountAuthenticatorRealmSelectorListener mCallback;

    public interface AccountAuthenticatorRealmSelectorListener {
        void onNewRealmSelected();
    }

    Spinner spinner;
    private int lastSelectedIndex = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AccountAuthenticatorRealmSelectorListener) activity;
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement AccountAuthenticatorRealmSelectorListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret =
                inflater.inflate(R.layout.fragment_account_authenticator_realm_selector, container,
                        false);
        spinner = (Spinner) ret.findViewById(R.id.authenticator_realm_selector_spinner);
        String[] servers =
                LoLin1Utils.getStringArray(getActivity().getApplicationContext(), "servers", null);
        for (int i = 0; i < servers.length; i++)
            servers[i] = servers[i].toUpperCase();
        spinner.setAdapter(new RealmSpinnerAdapter(getActivity().getApplicationContext(), servers));
        spinner.setSelection(Arrays.asList(
                LoLin1Utils.getStringArray(getActivity().getApplicationContext(), "servers", null))
                .indexOf(LoLin1Utils.getRealm(getActivity().getApplicationContext())));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != lastSelectedIndex) {
                    lastSelectedIndex = position;
                    mCallback.onNewRealmSelected();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return ret;
    }

    public String getSelectedRealm() {
        return spinner.getSelectedItem().toString().toUpperCase();
    }

    private class RealmSpinnerAdapter extends ArrayAdapter<String> {

        public RealmSpinnerAdapter(Context context, String[] servers) {
            super(context, R.layout.list_item_account_authenticator_realm_selector, servers);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);

            ((TextView) view.findViewById(R.id.entry_name))
                    .setTextColor(getResources().getColor(R.color.theme_black));

            return view;
        }
    }
}
