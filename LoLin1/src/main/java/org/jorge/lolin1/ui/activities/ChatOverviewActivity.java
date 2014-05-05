package org.jorge.lolin1.ui.activities;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.chat.ChatService;
import org.jorge.lolin1.ui.frags.ChatOverviewFragment;
import org.jorge.lolin1.ui.frags.ExpandableSearchFragment;
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
 * Created by JorgeAntonio on 01/05/2014.
 */
public final class ChatOverviewActivity extends DrawerLayoutFragmentActivity
        implements ExpandableSearchFragment.ExpandableSearchListener,
        ChatOverviewFragment.ChatRoomSelectionListener {

    static final String KEY_FRIEND_NAME = "FRIEND_NAME";
    private ChatOverviewFragment CHAT_OVERVIEW_FRAGMENT;
    private ExpandableSearchFragment SEARCH_FRAGMENT;
    private ChatService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((ChatService.ChatBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!LoLin1Utils.isInternetReachable(getApplicationContext())) {
            setContentView(R.layout.activity_chat_overview_no_connection);
            return;
        }
        initChatService();
        getIntent().putExtra(DrawerLayoutFragmentActivity.ACTION_BAR_MENU_LAYOUT,
                R.menu.menu_chat_overview);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                R.layout.activity_chat_overview);
        super.onCreate(savedInstanceState);
    }

    private void initChatService() {
        Intent intent = new Intent(getApplicationContext(), ChatService.class);
        if (!isChatServiceAlreadyRunning()) {
            stopService(intent);
        }
        bindService(intent, mConnection, Context.BIND_ABOVE_CLIENT);
        startService(intent);
    }

    private boolean isChatServiceAlreadyRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (ChatService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean ret = Boolean.TRUE;
        switch (item.getItemId()) {
            case R.id.action_champion_search:
                SEARCH_FRAGMENT.toggleVisibility();
                break;
            default: //Up or Settings buttons
                ret = super.onOptionsItemSelected(item);
        }
        super.restoreActionBar();
        return ret;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);
        View unconnectedView;
        if ((unconnectedView = ret.findViewById(R.id.chat_overview_no_connection)) != null) {
            unconnectedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoLin1Utils.isInternetReachable(getApplicationContext())) {
                        onCreate(null);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.error_no_connection,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return ret;
        }

        CHAT_OVERVIEW_FRAGMENT =
                (ChatOverviewFragment) getFragmentManager()
                        .findFragmentById(R.id.chat_overview_fragment);


        SEARCH_FRAGMENT =
                (ExpandableSearchFragment) getFragmentManager()
                        .findFragmentById(R.id.champion_list_search);

        return ret;
    }

    @Override
    public void onNewQuery(String query) {
        CHAT_OVERVIEW_FRAGMENT.applyFilter(query);
    }

    @Override
    public void onRoomSelected(String friendName) {
        //TODO Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        //intent.putExtra(KEY_FRIEND_NAME, friendName);
        //startActivity(intent);
    }
}
