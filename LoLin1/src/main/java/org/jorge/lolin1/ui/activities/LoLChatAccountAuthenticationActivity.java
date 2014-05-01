package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.frags.AcceptCredentialsFragment;

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
public class LoLChatAccountAuthenticationActivity extends Activity implements
        AcceptCredentialsFragment.AcceptCredentialsListener {

    public static final String KEY_REALM = "REALM";
    public static final String KEY_AUTH_TYPE = "AUTH_TYPE";
    public static final String KEY_NEW_ACCOUNT = "NEW_ACCOUNT";
    public static final String KEY_RESPONSE = "RESPONSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_account_login);
    }

    @Override
    public void onCredentialsAccepted() {

    }
}
