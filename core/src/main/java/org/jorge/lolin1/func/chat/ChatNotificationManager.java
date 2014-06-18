package org.jorge.lolin1.func.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.activities.ChatOverviewActivity;
import org.jorge.lolin1.ui.activities.ChatRoomActivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 18/06/14.
 */
public abstract class ChatNotificationManager {

    private static Map<String, Integer> NOTIFICATION_ID_MAP = Collections.synchronizedMap(new HashMap<String, Integer>());

    public static void createOrUpdateMessageReceivedNotification(Context context, String contents, Friend friend) {
        String name;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        boolean notificationFound = NOTIFICATION_ID_MAP.containsKey(name = friend.getName());
        int id;
        if (!notificationFound) {
            builder = new NotificationCompat.Builder(context);
            id = NOTIFICATION_ID_MAP.size();
        } else
            id = NOTIFICATION_ID_MAP.get(name);
        builder.setSmallIcon(R.drawable.icon_app);
        builder.setContentTitle(name);
        builder.setContentText(contents);
        builder.setAutoCancel(Boolean.TRUE);
        Intent resultIntent = new Intent(context, ChatRoomActivity.class);
        resultIntent.putExtra(ChatOverviewActivity.KEY_FRIEND_NAME, name);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ChatRoomActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }
}
