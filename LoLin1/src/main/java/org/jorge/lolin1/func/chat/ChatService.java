package org.jorge.lolin1.func.chat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jorge.lolin1.func.auth.AccountAuthenticator;
import org.jorge.lolin1.ui.activities.ChatOverviewActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
 * Created by JorgeAntonio on 05/05/2014.
 */
public class ChatService extends Service {

    private static final long LOG_IN_DELAY_MILLIS = 3000;
    private final IBinder mBinder = new ChatBinder();
    private LoLChat api;
    private BroadcastReceiver mChatBroadcastReceiver;
    private SmackAndroid mSmackAndroid;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ChatBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    private void runChatOverviewBroadcastReceiver() {
        mChatBroadcastReceiver = ChatOverviewActivity.instantiateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_failed", null));
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_chat_overview", null));
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_successful", null));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mChatBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        mSmackAndroid = LoLChat.init(getApplicationContext());
        Boolean loginSuccess = login(LoLin1Utils.getRealm(getApplicationContext()).toUpperCase());
        Log.d("debug", "Log-in finished");
        if (loginSuccess) {
            Log.d("debug", "Log-in successful");
            runChatOverviewBroadcastReceiver();
            launchBroadcastLoginSuccessful();
            setUpChatOverviewListener();
        }
        else {
            Log.d("debug", "Log-in failed");
            launchBroadcastLoginUnsuccessful();
        }
        return ret;
    }

    private void setUpChatOverviewListener() {
        api.addFriendListener(new FriendListener() {

            @Override
            public void onFriendLeave(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendJoin(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAvailable(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAway(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendBusy(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendStatusChange(Friend friend) {
                ChatService.this.launchBroadcastFriendEvent();
            }
        });
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void launchBroadcastLoginSuccessful() {
        Intent intent = new Intent();
        intent.setAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_successful", null));
        sendLocalBroadcast(intent);
    }

    private void launchBroadcastLoginUnsuccessful() {
        Intent intent = new Intent();
        intent.setAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_failed", null));
        sendLocalBroadcast(intent);
    }

    private void launchBroadcastFriendEvent() {
        Intent intent = new Intent();
        intent.setAction(LoLin1Utils
                .getString(getApplicationContext(), "event_chat_overview", null));
        sendLocalBroadcast(intent);
    }

    private void launchBroadcastLostConnection() {
        Intent intent = new Intent();
        intent.setAction(LoLin1Utils
                .getString(getApplicationContext(), "android.net.conn.CONNECTIVITY_CHANGE", null));
        sendLocalBroadcast(intent);
    }

    private Boolean login(String upperCaseRealm) {
        ChatServer chatServer;
        switch (upperCaseRealm) {
            case "NA":
                chatServer = ChatServer.NA;
                break;
            case "EUW":
                chatServer = ChatServer.EUW;
                break;
            case "EUNE":
                chatServer = ChatServer.EUNE;
                break;
            case "TR":
                chatServer = ChatServer.TR;
                break;
            case "BR":
                chatServer = ChatServer.BR;
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        try {
            Log.d("debug", "Before constructing the LoLChat");
            api = new LoLChat(chatServer, Boolean.FALSE);
            Log.d("debug", "After constructing the LoLChat");
        }
        catch (IOException e) {
            launchBroadcastLostConnection();
        }
        final AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType(
                LoLin1Utils.getString(getApplicationContext(), "account_type", null));
        Account thisRealmAccount = null;
        for (Account acc : accounts) {
            if (acc.name.contentEquals(upperCaseRealm)) {
                thisRealmAccount = acc;
                break;
            }
        }
        if (thisRealmAccount == null) {
            return Boolean.FALSE;//There's no account associated to this realm
        }
        Log.d("debug", "Creating the asynctask");
        AsyncTask<Account, Void, String[]> credentialsTask =
                new AsyncTask<Account, Void, String[]>() {
                    @Override
                    protected String[] doInBackground(Account... params) {
                        String[] processedAuthToken = null;
                        try {
                            processedAuthToken =
                                    accountManager
                                            .blockingGetAuthToken(params[0], "none", Boolean.TRUE)
                                            .split(
                                                    AccountAuthenticator.TOKEN_GENERATION_JOINT);
                        }
                        catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            Crashlytics.logException(e);
                        }
                        return processedAuthToken;
                    }
                };
        Log.d("debug", "Starting the executio of the asynctask");
        credentialsTask.execute(thisRealmAccount);
        Log.d("debug", "Executing the asynctask");
        String[] processedAuthToken = new String[0];
        try {
            Log.d("debug", "Before the get");
            processedAuthToken = credentialsTask.get();
            Log.d("debug", "After the get");
        }
        catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
        }
        Boolean loginSuccess = Boolean.FALSE;
        try {
            loginSuccess = api.login(processedAuthToken[0], processedAuthToken[1]);
        }
        catch (IOException e) {
            Crashlytics.logException(e);
        }
        if (loginSuccess) {
            try {
                Thread.sleep(
                        LOG_IN_DELAY_MILLIS); //I completely hate myself for doing this, but the library is designed this way...
            }
            catch (InterruptedException e) {
                Crashlytics.logException(e);
            }
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    @Override
    public void onDestroy() {
        try {
            api.disconnect();
        }
        catch (SmackException.NotConnectedException e) {
            Crashlytics.logException(e);
        }
        api = null;
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mChatBroadcastReceiver);
        mChatBroadcastReceiver = null;
        super.onDestroy();
        mSmackAndroid.onDestroy();
    }
}
