package org.jorge.lolin1.ui.frags;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class ChatAccountCredentialsComponentFragment extends Fragment {

    private TextView contentsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String aux = getId() == R.id.username_fragment ? "username" : "password";
        View ret = inflater.inflate(R.layout.fragment_chat_credentials_component, container, false);
        ((TextView) ret.findViewById(R.id.chat_credentials_component_title))
                .setText(LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "chat_credentials_" + aux + "_component_title", null));
        contentsView = (TextView) ret.findViewById(R.id.chat_credentials_component_content);
        //TODO Set the username or the hint somehow
        return ret;
    }

    public CharSequence getContents() {
        return contentsView.getText();
    }
}
