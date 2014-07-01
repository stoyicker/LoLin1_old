package org.jorge.lolin1.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.chat.ChatBundleManager;
import org.jorge.lolin1.func.chat.ChatIntentService;
import org.jorge.lolin1.func.chat.ChatMessageWrapper;
import org.jorge.lolin1.func.chat.ChatNotificationManager;
import org.jorge.lolin1.func.chat.ChatRoomAdapter;
import org.jorge.lolin1.func.chat.FriendManager;
import org.jorge.lolin1.io.local.ProfileCacheableBitmapLoader;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.concurrent.Executors;

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
public class ChatRoomActivity extends Activity {

    private static BroadcastReceiver mChatBroadcastReceiver;
    private String friendName;
    private ChatRoomAdapter adapter;
    private ListView conversationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        friendName = null;
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
            actionBar.setTitle(friendName = getIntent().getStringExtra(ChatOverviewActivity.KEY_FRIEND_NAME));
            try {
                actionBar.setLogo(Drawable.createFromPath(ProfileCacheableBitmapLoader.getPathByID(getApplicationContext(), FriendManager.getInstance().findFriendByName(friendName).getStatus().getProfileIconId()).getAbsolutePath()));
            } catch (NullPointerException ex) {
                startActivity(new Intent(getApplicationContext(), ChatOverviewActivity.class));//Clicking notification with app closed
                finish();
                return;
            }
            actionBar.setDisplayUseLogoEnabled(Boolean.TRUE);
        }
        ChatNotificationManager.dismissNotifications(getApplicationContext(), friendName);
        setContentView(R.layout.activity_chat_room);

        final EditText messageContentsText = (EditText) findViewById(android.R.id.inputArea);
        final ImageButton sendButton = (ImageButton) findViewById(android.R.id.button1);

        messageContentsText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendButton.callOnClick();
                }
                return Boolean.FALSE; //I still want Android to run its thingies, if any
            }
        });

        conversationListView = (ListView) findViewById(android.R.id.list);
        conversationListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);

        logString("debug", "Calling adapter constructor");
        adapter = new ChatRoomAdapter(getApplicationContext(), FriendManager.getInstance().findFriendByName(friendName));

        if (!TextUtils.isEmpty(friendName))
            conversationListView.setAdapter(adapter);

        scrollListViewToBottom();

        final String friendNameAsFinal = friendName;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = messageContentsText.getText().toString();
                if (TextUtils.isEmpty(contents))
                    return;
                adapter.add(new ChatMessageWrapper(contents, System.currentTimeMillis()));
                sendMessage(contents, friendNameAsFinal);
                messageContentsText.setText("");
                messageContentsText.clearFocus();
            }

            private void sendMessage(String contents, String friendName) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        Friend target;
                        ChatMessageWrapper messageWrapper = new ChatMessageWrapper(params[0], System.currentTimeMillis());
                        logString("debug", "Sending message " + params[0] + " to " + params[1]);
                        ChatBundleManager.addMessageToFriendChat(messageWrapper, target = FriendManager.getInstance().findFriendByName(params[1]));
                        scrollListViewToBottom();
                        target.sendMessage(params[0]);
                        return null;
                    }
                }.executeOnExecutor(Executors.newSingleThreadExecutor(), contents, friendName);
            }
        });

        registerLocalBroadcastReceiver();
        scrollListViewToBottom();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        scrollListViewToBottom();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up button
                if (DrawerLayoutFragmentActivity.getLastSelectedNavDrawerIndex() == 1)
                    startActivity(
                            new Intent(getApplicationContext(), JungleTimersActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void registerLocalBroadcastReceiver() {
        if (mChatBroadcastReceiver != null) {
            return;
        }
        mChatBroadcastReceiver = new ChatMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_message_received", null));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mChatBroadcastReceiver, intentFilter);
    }

    private void scrollListViewToBottom() {
        conversationListView.post(new Runnable() {
            public void run() {
                conversationListView.smoothScrollToPosition(adapter.getCount() - 1);
            }
        });
    }

    private class ChatMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String src;
            logString("debug", "Received a message");
            if (friendName.contentEquals(src = intent.getStringExtra(ChatIntentService.KEY_MESSAGE_SOURCE))) {
                adapter.add(new ChatMessageWrapper(intent.getStringExtra(ChatIntentService.KEY_MESSAGE_CONTENTS), System.currentTimeMillis(), FriendManager.getInstance().findFriendByName(friendName)));
                scrollListViewToBottom();
            } else {
                ChatNotificationManager.createOrUpdateMessageReceivedNotification(getApplicationContext(), intent.getStringExtra(ChatIntentService.KEY_MESSAGE_CONTENTS), FriendManager.getInstance().findFriendByName(src));
            }
        }
    }
}
