package org.jorge.lolin1.func.chat;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

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
public class FriendManager {

    private static FriendManager instance;
    private final Collection<Friend> ONLINE_FRIENDS = Collections.synchronizedSortedSet(new TreeSet<Friend>());

    public static FriendManager getInstance() {
        if (instance == null) {
            instance = new FriendManager();
        }
        return instance;
    }

    private FriendManager() {
    }

    public Friend findFriendByName(String friendName) {
        for (Friend f : ONLINE_FRIENDS)
            if (f.getName().contentEquals(friendName)) {
                return f;
            }
        return null;
    }

    public synchronized void updateOnlineFriends() {
        logString("debug", "Updating online friends");
        Collection<Friend> onlineFriends = ChatIntentService.getOnlineFriends();
        ONLINE_FRIENDS.clear();
        for (Friend f : onlineFriends) {
            if (f.getChatMode() != null && f.isOnline()) { //Prevention check
                logString("debug", "Adding friend online: " + f.getName());
                ONLINE_FRIENDS.add(f);
            }
        }
        logString("debug", "Adding friend online: -----------------------");
    }

    public Collection<Friend> getOnlineFriends() {
        return ONLINE_FRIENDS;
    }
}
