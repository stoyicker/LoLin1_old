package org.jorge.lolin1.func.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;

import java.text.SimpleDateFormat;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 17/06/14.
 */
public class ChatRoomAdapter extends ArrayAdapter<ChatMessageWrapper> {

    private static final int RES_ID = R.layout.list_item_chat_message;

    public ChatRoomAdapter(Context context, Friend friend) {
        super(context, RES_ID);
        Bundle previousMessages = ChatBundleManager.getBundleByFriend(friend);
        logString("debug", "previousMessages is empty? " + previousMessages.isEmpty());
        if (!previousMessages.isEmpty())
            super.addAll(previousMessages.<ChatMessageWrapper>getParcelableArrayList(ChatBundleManager.KEY_MESSAGE_ARRAY));
    }

    @Override
    public void add(ChatMessageWrapper object) {
        super.add(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public ChatMessageWrapper getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(RES_ID, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.setContentsView((TextView) convertView.findViewById(R.id.contents_view));
            viewHolder.setWrapperLayout((LinearLayout) convertView.findViewById(R.id.wrapper_layout));
            viewHolder.setTimeView((TextView) convertView.findViewById(R.id.timestamp_view));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMessageWrapper message = getItem(position);

        Boolean left = message.getSender() != null;
        TextView contentsView = viewHolder.getContentsView(), timeView = viewHolder.getTimeView();
        contentsView.setText(message.getText());
        if (left) {
            contentsView.setBackgroundResource(R.drawable.bubble_white);
            viewHolder.getWrapperLayout().setGravity(Gravity.LEFT);
        } else {
            contentsView.setBackgroundResource(R.drawable.bubble_blue);
            viewHolder.getWrapperLayout().setGravity(Gravity.RIGHT);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        timeView.setText(simpleDateFormat.format(message.getTime()));
        return convertView;
    }

    private static class ViewHolder {
        TextView getContentsView() {
            return contentsView;
        }

        void setContentsView(TextView contentsView) {
            this.contentsView = contentsView;
        }

        private TextView contentsView;

        private TextView getTimeView() {
            return timeView;
        }

        private void setTimeView(TextView timeView) {
            this.timeView = timeView;
        }

        private TextView timeView;

        LinearLayout getWrapperLayout() {
            return wrapperLayout;
        }

        void setWrapperLayout(LinearLayout wrapperLayout) {
            this.wrapperLayout = wrapperLayout;
        }

        private LinearLayout wrapperLayout;
    }
}
