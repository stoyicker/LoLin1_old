package org.jorge.lolin1.ui.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.auth.AccountAuthenticator;
import org.jorge.lolin1.ui.frags.AcceptCredentialsFragment;
import org.jorge.lolin1.ui.frags.AccountAuthenticatorRealmSelectorFragment;
import org.jorge.lolin1.ui.frags.AccountCredentialsComponentFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;

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
public class AccountAuthenticationActivity extends AccountAuthenticatorActivity implements
        AcceptCredentialsFragment.AcceptCredentialsListener,
        AccountCredentialsComponentFragment.AccountCredentialsComponentListener,
        AccountAuthenticatorRealmSelectorFragment.AccountAuthenticatorRealmSelectorListener {

    public static final String KEY_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String KEY_NEW_ACCOUNT = "NEW_ACCOUNT";
    public static final String KEY_RESPONSE = "RESPONSE";
    private String ACCOUNT_TYPE;
    private AccountCredentialsComponentFragment USERNAME_COMPONENT_FRAGMENT,
            PASSWORD_COMPONENT_FRAGMENT;
    private AcceptCredentialsFragment ACCEPT_CREDENTIALS_FRAGMENT;
    private AccountAuthenticatorRealmSelectorFragment REALM_SELECTION_FRAGMENT;

    @Override
    public void onFieldUpdated() {
        ACCEPT_CREDENTIALS_FRAGMENT.setEnabled(
                !TextUtils.isEmpty(USERNAME_COMPONENT_FRAGMENT.getContents()) &&
                        !TextUtils.isEmpty(PASSWORD_COMPONENT_FRAGMENT.getContents())
        );
    }

    @Override
    public void onDonePressed() {
        if (!TextUtils.isEmpty(USERNAME_COMPONENT_FRAGMENT.getContents()) &&
                !TextUtils.isEmpty(PASSWORD_COMPONENT_FRAGMENT.getContents())) {
            onCredentialsAccepted();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ACCOUNT_TYPE = LoLin1Utils.getString(getApplicationContext(), "account_type", null);
        setContentView(R.layout.activity_account_login);

        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);

        USERNAME_COMPONENT_FRAGMENT =
                (AccountCredentialsComponentFragment) getFragmentManager()
                        .findFragmentById(R.id.username_fragment);
        PASSWORD_COMPONENT_FRAGMENT =
                (AccountCredentialsComponentFragment) getFragmentManager()
                        .findFragmentById(R.id.password_fragment);
        ACCEPT_CREDENTIALS_FRAGMENT =
                (AcceptCredentialsFragment) getFragmentManager()
                        .findFragmentById(R.id.accept_credentials);
        REALM_SELECTION_FRAGMENT = (AccountAuthenticatorRealmSelectorFragment) getFragmentManager()
                .findFragmentById(R.id.realm_indicator);

        onNewRealmSelected();
    }

    @Override
    public void onNewRealmSelected() {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account account = null;
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        for (Account acc : accounts) {
            if (acc.name.contentEquals(REALM_SELECTION_FRAGMENT.getSelectedRealm())) {
                account = acc;
                break;
            }
        }
        if (account != null) {
            accountManager.getAuthToken(account, "none", null, this,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            String authToken = null;
                            try {
                                authToken =
                                        future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                            }
                            catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                Crashlytics.logException(e);
                            }
                            if (authToken != null) {
                                final String[] processedToken = authToken
                                        .split(AccountAuthenticator.TOKEN_GENERATION_JOINT);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        USERNAME_COMPONENT_FRAGMENT.setContents(processedToken[0]);
                                        PASSWORD_COMPONENT_FRAGMENT
                                                .setHint(processedToken[1].length());
                                    }
                                });
                            }
                        }
                    }, null
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up button
                finish();
                return Boolean.TRUE;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCredentialsAccepted() {
        CharSequence username = USERNAME_COMPONENT_FRAGMENT.getContents(), password =
                PASSWORD_COMPONENT_FRAGMENT.getContents(), authToken =
                username + AccountAuthenticator.TOKEN_GENERATION_JOINT + password;
        final Intent res = new Intent();
        String realm;
        res.putExtra(AccountManager.KEY_ACCOUNT_NAME,
                realm = REALM_SELECTION_FRAGMENT.getSelectedRealm());
        res.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
        Account[] LoLIn1Accounts = AccountManager.get(getApplicationContext()).getAccountsByType(
                ACCOUNT_TYPE);
        Collection<String> x = new ArrayDeque<>();
        for (Account acc : LoLIn1Accounts)
            x.add(acc.name);
        res.putExtra(KEY_NEW_ACCOUNT, !x.contains(realm));
        saveAccount(res);
    }

    private void saveAccount(Intent intent) {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword =
                ""; //We don't want the password field for anything, so we won't store any relevant data on it
        final Account account =
                new Account(accountName, ACCOUNT_TYPE);
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        if (intent.getBooleanExtra(KEY_NEW_ACCOUNT, Boolean.FALSE)) {
            accountManager.addAccountExplicitly(account, accountPassword, null);
        }
        accountManager.setAuthToken(account, "none", authToken);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        Toast.makeText(getApplicationContext(), R.string.account_save_success, Toast.LENGTH_SHORT)
                .show();
        finish();
    }
}
