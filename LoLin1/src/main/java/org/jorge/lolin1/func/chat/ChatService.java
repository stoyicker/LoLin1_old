package org.jorge.lolin1.func.chat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.LolChat;

import org.jorge.lolin1.func.auth.AccountAuthenticator;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.IOException;

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
    private LolChat api;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ChatBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean loginSuccess = login(LoLin1Utils.getRealm(getApplicationContext()).toUpperCase());
        if (loginSuccess) {
            //TODO Register listener with this code
            //Intent i = new Intent();
            //i.setAction(ChatOverviewBroadcastReceiver.CHAT_OVERVIEW_EVENT);
            //getApplicationContext().sendBroadcast(i);
        }
        else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
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
        api = new LolChat(chatServer, Boolean.FALSE);
        AccountManager accountManager = AccountManager.get(getApplicationContext());
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
        String[] processedAuthToken;
        try {
            processedAuthToken =
                    accountManager.blockingGetAuthToken(thisRealmAccount, "none", Boolean.TRUE)
                            .split(
                                    AccountAuthenticator.TOKEN_GENERATION_JOINT);
            if (api.login(processedAuthToken[0], processedAuthToken[1])) {
                try {
                    Thread.sleep(
                            LOG_IN_DELAY_MILLIS); //I completely hate myself, but the library is designed this way...
                }
                catch (InterruptedException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                }
                return Boolean.TRUE;
            }
            else {
                return Boolean.FALSE;
            }
        }
        catch (OperationCanceledException | IOException | AuthenticatorException e) {
            return Boolean.FALSE;
        }
    }

    @Override
    public void onDestroy() {
        api.disconnect();
        super.onDestroy();
    }
}
