package org.jorge.lolin1.func.chat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

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

    private final IBinder mBinder = new ChatBinder();

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
        //TODO Login and stuff
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //TODO Log out and stuff
        super.onDestroy();
    }
}
