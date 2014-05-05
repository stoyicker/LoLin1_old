package org.jorge.lolin1.func.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
 * Created by JorgeAntonio on 05/05/2014.
 */
public class ChatOverviewBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.contentEquals(LoLin1Utils
                .getString(context.getApplicationContext(), "chat_overview_event", null))) {
            //TODO Tell the ChatOverviewActivity to invalidate/refresh the view
        }
        else if (action.contentEquals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if (!LoLin1Utils.isInternetReachable(context.getApplicationContext())) {
                //TODO Notify that Internet is gone so that the activity changes the view
            }
        }
    }
}
