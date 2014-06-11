package org.jorge.lolin1.ui.frags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.AccountAuthenticationActivity;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 06/05/2014.
 */
public class WrongChatCredentialsSupportFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_chat_overview_wrong_credentials, container,
                Boolean.FALSE);

        if (ret != null) {
            ret.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent accoutManagementIntent =
                            new Intent(getActivity().getApplicationContext(),
                                    AccountAuthenticationActivity.class);
                    startActivity(accoutManagementIntent);
                }
            });
        }
        return ret;
    }
}
