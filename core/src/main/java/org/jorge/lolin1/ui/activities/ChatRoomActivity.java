package org.jorge.lolin1.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ListView;

import org.jorge.lolin1.R;
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

    private ListView conversationListView;

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

        conversationListView = (ListView) findViewById(android.R.id.list);

        if (!TextUtils.isEmpty(friendName))
            conversationListView.setAdapter(new ChatRoomAdapter(getApplicationContext(), FriendManager.getInstance().findFriendByName(friendName)));
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
