package org.jorge.lolin1.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.chat.ChatIntentService;
import org.jorge.lolin1.func.chat.ChatMessageWrapper;
import org.jorge.lolin1.func.chat.ChatRoomAdapter;
import org.jorge.lolin1.func.chat.FriendManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        String friendName = null;
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
            actionBar.setTitle(friendName = getIntent().getStringExtra(ChatOverviewActivity.KEY_FRIEND_NAME));
            actionBar.setLogo(Drawable.createFromPath(getIntent().getStringExtra(ChatOverviewActivity.KEY_PROFILE_ICON_PATH)));
            actionBar.setDisplayUseLogoEnabled(Boolean.TRUE);
        }
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

        ListView conversationListView = (ListView) findViewById(android.R.id.list);
        final ChatRoomAdapter adapter = new ChatRoomAdapter(getApplicationContext(), FriendManager.getInstance().findFriendByName(friendName));

        if (!TextUtils.isEmpty(friendName))
            conversationListView.setAdapter(adapter);

        final String friendNameAsFinal = friendName;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = messageContentsText.getText().toString();
                if (TextUtils.isEmpty(contents))
                    return;
                adapter.add(new ChatMessageWrapper(contents, System.currentTimeMillis()));
                launchBroadcastSendMessage(contents, friendNameAsFinal);
                messageContentsText.setText("");
                messageContentsText.clearFocus();
            }

            private void launchBroadcastSendMessage(String contents, String friendName) {
                Intent intent = new Intent();
                intent.setAction(ChatIntentService.ACTION_MESSAGE);
                intent.putExtra(ChatIntentService.KEY_MESSAGE_CONTENTS, contents);
                intent.putExtra(ChatIntentService.KEY_MESSAGE_DESTINATION, friendName);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        });
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
}
