package org.jorge.lolin1.ui.activities;

import android.os.Bundle;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.frags.ChatOverviewFragment;
import org.jorge.lolin1.ui.frags.ExpandableSearchFragment;
import org.jorge.lolin1.ui.frags.WrongChatCredentialsFragment;

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
    private final int INDEX_VIEW_CONNECTED = 0, INDEX_VIEW_NOT_CONNECTED = 1,
            INDEX_VIEW_WRONG_CREDENTIALS = 2;
    private ChatOverviewFragment CHAT_OVERVIEW_FRAGMENT;
    private ExpandableSearchFragment SEARCH_FRAGMENT;
    private WrongChatCredentialsFragment WRONG_CREDENTIALS_FRAGMENT;
    //    private ChatServiceConnection mConnection = new ChatServiceConnection();
    private static ChatOverviewActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        getIntent().putExtra(DrawerLayoutFragmentActivity.ACTION_BAR_MENU_LAYOUT,
                R.menu.menu_chat_overview);
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                R.layout.activity_chat_overview);
        super.onCreate(savedInstanceState);
    }
//
//    @Override
//    protected void onResume() {
//        if (!LoLin1Utils.isInternetReachable(getApplicationContext())) {
//            if (findViewById(android.R.id.content) != null) {
//                showViewNoConnection(findViewById(android.R.id.content));
//            }
//        }
//        else {
//            restartOrRunChatService();
//        }
//        super.onResume();
//    }
//
//    private void restartOrRunChatService() {
//        Intent intent = new Intent(getApplicationContext(), ChatService.class);
//        if (isChatServiceAlreadyRunning()) {
//            stopService(intent);
//        }
//        if (mConnection.isConnected()) {
//            unbindService(mConnection);
//        }
//        bindService(intent, mConnection, Context.BIND_ABOVE_CLIENT);
//        startService(intent);
//    }
//
//    private boolean isChatServiceAlreadyRunning() {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager
//                .getRunningServices(Integer.MAX_VALUE)) {
//            if (ChatService.class.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Boolean ret = Boolean.TRUE;
//        switch (item.getItemId()) {
//            case R.id.action_champion_search:
//                if(isChatServiceAlreadyRunning)//If it's running it means that we are actually logged in
//                SEARCH_FRAGMENT.toggleVisibility();
//                break;
//            default: //Up or Settings buttons
//                ret = super.onOptionsItemSelected(item);
//        }
//        super.restoreActionBar();
//        return ret;
//    }
//
//
//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        View ret = super.onCreateView(name, context, attrs);
//        Log.d("debug", "ret is null? " + (ret == null) + "");
//        //I'm using the progress bar from this ProgressFragment to show the login procedure. The corresponding event will show which view has to be actually shown.
//        showViewWrongCredentials(findViewById(android.R.id.content));
//    WRONG_CREDENTIALS_FRAGMENT.hideContent();
//        return ret;
//    }
//
//    private void showViewConnected(View view) {
//        ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(
//                R.id.chat_overview_view_switcher);
//        if (viewSwitcher.getDisplayedChild() != INDEX_VIEW_CONNECTED) {
//            viewSwitcher.setDisplayedChild(INDEX_VIEW_CONNECTED);
//            CHAT_OVERVIEW_FRAGMENT =
//                    (ChatOverviewFragment) getFragmentManager()
//                            .findFragmentById(R.id.chat_overview_fragment);
//
//            SEARCH_FRAGMENT =
//                    (ExpandableSearchFragment) getFragmentManager()
//                            .findFragmentById(R.id.champion_list_search);
//        }
//    }
//
//    private void showViewNoConnection(View view) {
//        ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(
//                R.id.chat_overview_view_switcher);
//        if (viewSwitcher.getDisplayedChild() != INDEX_VIEW_NOT_CONNECTED) {
//            viewSwitcher.setDisplayedChild(INDEX_VIEW_NOT_CONNECTED);
//            findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (LoLin1Utils.isInternetReachable(getApplicationContext())) {
//                        onResume();
//                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), R.string.error_no_connection,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }
//
//    private void showViewWrongCredentials(View view) {
//        ViewSwitcher viewSwitcher = (ViewSwitcher) view.findViewById(
//                R.id.chat_overview_view_switcher);
//        if (viewSwitcher.getDisplayedChild() != INDEX_VIEW_WRONG_CREDENTIALS) {
//            WRONG_CREDENTIALS_FRAGMENT = (WrongChatCredentialsFragment) getFragmentManager()
//                    .findFragmentById(R.id.chat_overview_wrong_credentials);
//            viewSwitcher.setDisplayedChild(INDEX_VIEW_WRONG_CREDENTIALS);
//        }
//    }

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
//
//    private synchronized void requestListRefresh() {
//        final View thisView = findViewById(android.R.id.content);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (thisView != null && thisView.isShown()) {
//                    thisView.invalidate();
//                }
//            }
//        });
//    }
//
//    public static BroadcastReceiver instantiateBroadcastReceiver() {
//        return instance.new ChatOverviewBroadcastReceiver();
//    }
//
//    public class ChatOverviewBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.contentEquals(LoLin1Utils
//                    .getString(context.getApplicationContext(), "event_chat_overview", null))) {
//                requestListRefresh();
//            }
//            else if (action.contentEquals("android.net.conn.CONNECTIVITY_CHANGE")) {
//                if (!LoLin1Utils.isInternetReachable(context.getApplicationContext())) {
//                    showViewNoConnection(findViewById(android.R.id.content));
//                }
//                else {
//                    ChatOverviewActivity.this.restartOrRunChatService();
//                }
//            }
//            else if (action.contentEquals(LoLin1Utils
//                    .getString(context.getApplicationContext(), "event_login_failed", null))) {
//                showViewWrongCredentials(findViewById(android.R.id.content));
//    WRONG_CREDENTIALS_FRAGMENT.showContent();
//            }
//            else if (action.contentEquals(LoLin1Utils
//                    .getString(context.getApplicationContext(), "event_login_successful", null))) {
//                showViewConnected(findViewById(android.R.id.content));
//            }
//        }
//    }
//
//    private class ChatServiceConnection implements ServiceConnection {
//        private ChatService mChatService;
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mChatService = ((ChatService.ChatBinder) service).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            mChatService = null;
//        }
//
//        private Boolean isConnected() {
//            return mChatService == null;
//        }
//    }
}
