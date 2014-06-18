package org.jorge.lolin1.func.chat;

import android.os.Bundle;
import android.os.Parcelable;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 04/05/2014.
 */
public abstract class ChatBundleManager {

    private static final Map<String, Bundle> BUNDLES = Collections.synchronizedMap(new HashMap<String, Bundle>());
    public static final String KEY_MESSAGE_ARRAY = "LOL_CHAT_MESSAGES";

    public static Bundle getBundleByFriend(Friend f) {
        String name;
        return BUNDLES.containsKey(name = f.getName()) ? BUNDLES.get(name) : Bundle.EMPTY;
    }

    public static synchronized void addMessageToFriendChat(ChatMessageWrapper msg, Friend chatSubject) {
        Bundle currentBundle = getBundleByFriend(chatSubject);
        logString("bundles", "addMessageToFriendChat, being friend " + chatSubject + " (" + chatSubject.getName() + ")");
        ArrayList<Parcelable> messages;
        if (currentBundle.isEmpty()) {
            currentBundle = new Bundle();
            messages = new ArrayList<>();
            logString("bundles", "Current bundle found empty");
        } else {
            messages = currentBundle.getParcelableArrayList(KEY_MESSAGE_ARRAY);
            logString("bundles", "Current bundle found with " + messages.size() + " messages");
        }
        messages.add(msg);
        logString("bundles", "After putting, bundle has " + messages.size() + " messages");
        currentBundle.putParcelableArrayList(KEY_MESSAGE_ARRAY, messages);
        BUNDLES.put(chatSubject.getName(), currentBundle);
        logString("bundles", "Put messages into BUNDLES, now BUNDLES has the content below: ");
        for (String f : BUNDLES.keySet()) {
            logString("bundles", "Friend  " + f);
            ArrayList<ChatMessageWrapper> received = getBundleByFriend(FriendManager.getInstance().findFriendByName(f)).getParcelableArrayList(KEY_MESSAGE_ARRAY);
            for (ChatMessageWrapper x : received) {
                logString("bundles", "Contents: " + x.getText());
                logString("bundles", "Sender: " + x.getSender());
                logString("bundles", "Date: " + x.getTime());
            }
        }
    }
}
