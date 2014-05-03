package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

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
public class AccountCredentialsComponentFragment extends Fragment {

    private final char PASSWORD_HINT_CHARACTER = '*';

    public interface AccountCredentialsComponentListener {

        public void onFieldUpdated();

        public void onDonePressed();
    }

    private AccountCredentialsComponentListener mCallback;
    private TextView contentsView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AccountCredentialsComponentListener) activity;
        }
        catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoLin1AccountCredentialsComponentListener");
        }
    }

    public void setContents(String contents) {
        contentsView.setText(contents);
    }

    public void setHint(int hintLength) {
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < hintLength; i++)
            stringBuilder.append(PASSWORD_HINT_CHARACTER);
        contentsView.setHint(stringBuilder.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret =
                inflater.inflate(R.layout.fragment_account_credentials_component, container, false);
        contentsView = (EditText) ret.findViewById(R.id.chat_credentials_component_contents);
        String aux;
        if (getId() == R.id.username_fragment) {
            aux = "username";
            contentsView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        else {
            aux = "password";
            contentsView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            contentsView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        mCallback.onDonePressed();
                    }
                    return Boolean.FALSE; //I still want Android to run its thingies
                }
            });
        }
        contentsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mCallback.onFieldUpdated();
            }
        });
        ((TextView) ret.findViewById(R.id.chat_credentials_component_title))
                .setText(LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "chat_credentials_" + aux + "_component_title", null));

        //TODO Set the username or the hint somehow
        return ret;
    }

    public CharSequence getContents() {
        return contentsView.getText();
    }
}
