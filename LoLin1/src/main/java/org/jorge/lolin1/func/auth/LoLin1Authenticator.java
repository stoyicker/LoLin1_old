package org.jorge.lolin1.func.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import org.jorge.lolin1.ui.activities.LoLChatAccountAuthenticationActivity;
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
public class LoLin1Authenticator extends AbstractAccountAuthenticator {

    private final Context mContext;
    public static final String TOKEN_GENERATION_JOINT = "acuwRQZChu"; //Generated through Random.org

    public LoLin1Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoLChatAccountAuthenticationActivity.class);
        intent.putExtra(LoLChatAccountAuthenticationActivity.KEY_REALM, accountType);
        intent.putExtra(LoLChatAccountAuthenticationActivity.KEY_NEW_ACCOUNT, Boolean.TRUE);
        intent.putExtra(LoLChatAccountAuthenticationActivity.KEY_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                     Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                               String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(mContext);
        String username, password;
        final Bundle bundle = new Bundle();

        username = accountManager.peekAuthToken(account, authTokenType);
        if (!TextUtils.isEmpty(username)) {
            //Account found!
            password = accountManager.getPassword(account);
            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, username);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, authTokenType);
            bundle.putString(AccountManager.KEY_AUTH_TOKEN_LABEL,
                    username + TOKEN_GENERATION_JOINT + password);
        }
        else {
            //Account not found
            final Intent intent = new Intent(mContext, LoLChatAccountAuthenticationActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        }

        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return LoLin1Utils.getString(mContext, "auth_token_label", null);
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                    String authTokenType, Bundle options)
            throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                              String[] features) throws NetworkErrorException {
        return null;
    }
}
