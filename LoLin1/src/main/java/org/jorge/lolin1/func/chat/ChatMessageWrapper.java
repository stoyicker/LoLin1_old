package org.jorge.lolin1.func.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import java.util.Date;

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
public class ChatMessageWrapper implements Parcelable {

    private final String text;
    private final Date time;
    private final Friend sender; //Null sender means it was me

    public ChatMessageWrapper(String _text, long _time) {
        this(_text, _time, null);
    }

    public ChatMessageWrapper(String _text, long _time, Friend _sender) {
        text = _text;
        time = new Date(_time);
        sender = _sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeLong(time.getTime());
        dest.writeString(sender == null ? "" : sender.getName());
    }

    public static final Parcelable.Creator<ChatMessageWrapper> CREATOR
            = new Parcelable.Creator<ChatMessageWrapper>() {
        public ChatMessageWrapper createFromParcel(Parcel in) {
            return new ChatMessageWrapper(in);
        }

        public ChatMessageWrapper[] newArray(int size) {
            return new ChatMessageWrapper[size];
        }
    };

    private ChatMessageWrapper(Parcel in) {
        text = in.readString();
        time = new Date(in.readLong());
        String friendName = in.readString();
        if (!TextUtils.isEmpty(friendName)) {
            sender = FriendManager.findFriendByName(friendName);
        }
        else {
            sender = null;
        }
    }
}
