package org.jorge.lolin1.func.chat;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

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
 * Created by JorgeAntonio on 04/05/2014.
 */
public class FriendManager {

    private static FriendManager instance;
    private final Collection<Friend> ONLINE_FRIENDS = new ArrayDeque<>();

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

    public static void updateOnlineFriends() {
        //TODO
    }

    public Collection<Friend> getOnlineFriends() {
        return ONLINE_FRIENDS;
    }
}
