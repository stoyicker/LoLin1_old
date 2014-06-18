package org.jorge.lolin1.func.chat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LoLChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jorge.lolin1.func.auth.AccountAuthenticator;
import org.jorge.lolin1.ui.activities.ChatRoomActivity;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

import static org.jorge.lolin1.utils.LoLin1DebugUtils.logString;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 05/05/2014.
 */
public class ChatIntentService extends IntentService {

    public static final String ACTION_CONNECT = "CONNECT", ACTION_DISCONNECT = "DISCONNECT";
    public static final String KEY_MESSAGE_CONTENTS = "MESSAGE_CONTENTS";
    public static final String KEY_MESSAGE_SOURCE = "SOURCE_FRIEND";
    private final IBinder mBinder = new ChatBinder();
    private static LoLChat api;
    private SmackAndroid mSmackAndroid;
    private static Boolean isConnected = Boolean.FALSE;
    private AsyncTask<Void, Void, Void> loginTask;

    public ChatIntentService() {
        super(ChatIntentService.class.getName());
    }

    static List<Friend> getOnlineFriends() {
        return api.getOnlineFriends();
    }

    public static Boolean isLoggedIn() {
        return isConnected;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isConnected = Boolean.FALSE;
        ChatNotificationManager.dismissAll(getApplicationContext());
        logString("debug", "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            throw new IllegalArgumentException(
                    "No intent found");
        }
        if (TextUtils.isEmpty(intent.getAction())) {
            throw new IllegalArgumentException(
                    "Empty action is not supported");
        }
        logString("debug", "ChatIntentService receiving intent " + intent.getAction());
        switch (intent.getAction()) {
            case ACTION_CONNECT:
                logString("debug", "Action requested: " + ACTION_CONNECT);
                connect();
                break;
            case ACTION_DISCONNECT:
                logString("debug", "Action requested: " + ACTION_DISCONNECT);
                disconnect();
                break;
            default:
                throw new IllegalArgumentException(
                        "Action " + intent.getAction() + " not yet supported");
        }
    }

    public class ChatBinder extends Binder {
        @SuppressWarnings("unused")
        public ChatIntentService getService() {
            return ChatIntentService.this;
        }
    }

    private void connect() {
        mSmackAndroid = LoLChat.init(getApplicationContext());
        loginTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                logString("debug", "Commencing login");
                Boolean loginSuccess =
                        login(LoLin1Utils.getRealm(getApplicationContext()).toUpperCase(Locale.ENGLISH));
                if (loginSuccess) {
                    logString("debug", "Login successful");
                    launchBroadcastLoginSuccessful();
                    setUpChatOverviewListener();
                } else {
                    logString("debug", "Login unsuccessful");
                    launchBroadcastLoginUnsuccessful();
                }
                return null;
            }
        };
        loginTask.execute();
    }

    private void setUpChatOverviewListener() {
        api.addFriendListener(new FriendListener() {

            @Override
            public void onFriendLeave(Friend friend) {
                logString("debug", "onFriendLeave " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendJoin(Friend friend) {
                logString("debug", "onFriendJoin " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAvailable(Friend friend) {
                logString("debug", "onFriendAvailable " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendAway(Friend friend) {
                logString("debug", "onFriendAway " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendBusy(Friend friend) {
                logString("debug", "onFriendBusy " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }

            @Override
            public void onFriendStatusChange(Friend friend) {
                logString("debug", "onFriendStatusChange " + friend.getName());
                ChatIntentService.this.launchBroadcastFriendEvent();
            }
        });

        api.addChatListener(new ChatListener() {
            @Override
            public void onMessage(Friend friend, String message) {
                ChatMessageWrapper messageWrapper = new ChatMessageWrapper(message, System.currentTimeMillis(), friend);
                logString("debug", "Adding external message received");
                ChatBundleManager.addMessageToFriendChat(messageWrapper, friend);
                logString("debug", "Added external message received");
                launchBroadcastMessageReceived(message, friend.getName());
                logString("debug", "Class evaluating if it's on top: "+ChatRoomActivity.class.getName());
                if (!LoLin1Utils.getCurrentForegroundActivityClass(getApplicationContext()).contentEquals((ChatRoomActivity.class.getName()))) {
                    ChatNotificationManager.createOrUpdateMessageReceivedNotification(getApplicationContext(), message, friend);
                }
            }
        });
    }

    private void launchBroadcastMessageReceived(String message, String sourceFriendName) {
        Intent intent = new Intent();
        intent.setAction(LoLin1Utils
                .getString(getApplicationContext(), "event_message_received", null));
        intent.putExtra(KEY_MESSAGE_CONTENTS, message);
        intent.putExtra(KEY_MESSAGE_SOURCE, sourceFriendName);
        sendLocalBroadcast(intent);
    }

    private void sendLocalBroadcast(Intent intent) {
        logString("debug", "Sending local broadcast - " + intent.getAction());
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
            case "OCE":
                chatServer = ChatServer.OCE;
                break;
            case "LAN":
                chatServer = ChatServer.LAN;
                break;
            case "LAS":
                chatServer = ChatServer.LAS;
                break;
            case "KR":
                chatServer = ChatServer.KR;
                break;
            case "RU":
                chatServer = ChatServer.RU;
                break;
            default:
                throw new IllegalArgumentException(
                        "Region " + upperCaseRealm + " not yet implemented");
        }
        try {
            logString("debug", "Assigning api...");
            api = new LoLChat(chatServer, Boolean.FALSE);
        } catch (IOException e) {
            logString("debug", "Exception when constructing the object!");
            e.printStackTrace(System.err);
            if (!(e instanceof SSLException)) {
                launchBroadcastLoginUnsuccessful();
            }
            return Boolean.FALSE;
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
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            Crashlytics.logException(e);
                        }
                        return processedAuthToken;
                    }
                };
        //It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService loginExecutor = Executors.newFixedThreadPool(1);
        credentialsTask.executeOnExecutor(loginExecutor, thisRealmAccount);
        String[] processedAuthToken = new String[0];
        try {
            processedAuthToken = credentialsTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
        }
        Boolean loginSuccess = Boolean.FALSE;
        try {
            loginSuccess = api.login(processedAuthToken[0], processedAuthToken[1]);
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        if (loginSuccess) {
            api.reloadRoster();
            isConnected = Boolean.TRUE;
            return Boolean.TRUE;
        } else {
            isConnected = Boolean.FALSE;
            return Boolean.FALSE;
        }
    }

    private void disconnect() {
        //All the null checks are necessary because this method is run when an account is added from out of the app as well
        try {
            if (loginTask != null)
                loginTask.get(); // Disconnecting in the middle of a login may be troublesome
        } catch (InterruptedException | ExecutionException e) {
            Crashlytics.logException(e);
        }
        try {
            if (api != null) {
                api.disconnect();
                api = null;
            }
        } catch (SmackException.NotConnectedException e) {
            Crashlytics.logException(e);
        }
        if (mSmackAndroid != null)
            mSmackAndroid.onDestroy();
        isConnected = Boolean.FALSE;
    }
}
