package org.jorge.lolin1.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.chat.ChatIntentService;
import org.jorge.lolin1.func.chat.FriendManager;
import org.jorge.lolin1.ui.frags.ChatOverviewSupportFragment;
import org.jorge.lolin1.ui.frags.ExpandableSearchFragment;
import org.jorge.lolin1.ui.frags.IndefiniteFancyProgressSupportFragment;
import org.jorge.lolin1.ui.frags.NoChatConnectionSupportFragment;
import org.jorge.lolin1.ui.frags.WrongChatCredentialsSupportFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

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
 * Created by JorgeAntonio on 01/05/2014.
 */
public final class ChatOverviewActivity extends DrawerLayoutFragmentActivity
        implements ExpandableSearchFragment.ExpandableSearchListener,
        ChatOverviewSupportFragment.ChatRoomSelectionListener {

    static final String KEY_FRIEND_NAME = "FRIEND_NAME";
    private static final int VIEW_INDEX_CONNECTED = 0, VIEW_INDEX_NOT_CONNECTED = 1,
            VIEW_INDEX_WRONG_CREDENTIALS = 2, VIEW_INDEX_LOADING = 3;
    private ExpandableSearchFragment SEARCH_FRAGMENT;
    private ViewPager mViewPager;
    private BroadcastReceiver mChatBroadcastReceiver;
    private ChatStatesPagerAdapter mPagerAdapter;
    private static Boolean restartDueToRotation = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            savedInstanceState = new Bundle();
        }
        getIntent().putExtra(DrawerLayoutFragmentActivity.ACTION_BAR_MENU_LAYOUT,
                R.menu.menu_chat_overview);
        savedInstanceState.putInt(DrawerLayoutFragmentActivity.ACTIVITY_LAYOUT,
                R.layout.activity_chat_overview);
        super.onCreate(savedInstanceState);
        final View thisView =
                findViewById(android.R.id.content);
        mViewPager = (ViewPager) findViewById(R.id.chat_overview_view_pager);
        if (mViewPager.getAdapter() == null) {
            mViewPager.setAdapter(mPagerAdapter =
                    new ChatStatesPagerAdapter(getSupportFragmentManager()));
        }
        Runnable viewRunnable;
        registerLocalBroadcastReceiver();
        if (restartDueToRotation) {
            logString("debug", "Chat: rotation detected");
            if (!LoLin1Utils.isInternetReachable(getApplicationContext())) {
                thisView.post(new Runnable() {
                    @Override
                    public void run() {
                        showViewNoConnection();
                    }
                });
            } else {
                if (!LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                        getApplicationContext())) {
                    logString("debug", "Showing view loading");
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            showViewLoading();
                        }
                    });
                    runChat();
                } else {
                    logString("debug", "Showing view connected");
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            showViewConnected();
                        }
                    });
                }
            }
            return;
        }
        if (!LoLin1Utils.isInternetReachable(getApplicationContext())) {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    showViewNoConnection();
                }
            };
        } else {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    showViewLoading();
                }
            };
            if (!LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                    getApplicationContext())) {
                runChat();
            }
        }
        thisView.post(viewRunnable);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View ret = super.onCreateView(name, context, attrs);

        SEARCH_FRAGMENT =
                (ExpandableSearchFragment) getFragmentManager()
                        .findFragmentById(R.id.chat_list_search);

        if (SEARCH_FRAGMENT != null && SEARCH_FRAGMENT.getQueryField() != null) {
            SEARCH_FRAGMENT.getQueryField().setHint(
                    LoLin1Utils.getString(getApplicationContext(), "friend_search_hint", null));
        }

        return ret;
    }

    public void requestProtocolReInit() {
        final View thisView =
                findViewById(android.R.id.content);
        mViewPager = (ViewPager) findViewById(R.id.chat_overview_view_pager);
        if (mViewPager.getAdapter() == null) {
            mViewPager.setAdapter(mPagerAdapter =
                    new ChatStatesPagerAdapter(getSupportFragmentManager()));
        }
        Runnable viewRunnable;
        if (!LoLin1Utils.isInternetReachable(getApplicationContext())) {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    showViewNoConnection();
                }
            };
        } else {
            viewRunnable = new Runnable() {
                @Override
                public void run() {
                    showViewLoading();
                }
            };
            if (!LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                    getApplicationContext())) {
                runChat();
            }
        }
        thisView.post(viewRunnable);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        restartDueToRotation = Boolean.TRUE;
        super.onConfigurationChanged(newConfig);
    }

    private void runChat() {
        Intent intent = new Intent(getApplicationContext(), ChatIntentService.class);
        if (LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                getApplicationContext())) {
            stopService(intent);
        }
        Intent chatConnectIntent = new Intent(getApplicationContext(), ChatIntentService.class);
        chatConnectIntent.setAction(ChatIntentService.ACTION_CONNECT);
        logString("debug", "Chat run requested");
        startService(chatConnectIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean ret = Boolean.TRUE;
        switch (item.getItemId()) {
            case R.id.action_champion_search:
                if (LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                        getApplicationContext()))//If it's running it means that we are actually logged in
                {
                    SEARCH_FRAGMENT.toggleVisibility();
                }
                break;
            default: //Up or Settings buttons
                ret = super.onOptionsItemSelected(item);
        }
        super.restoreActionBar();
        return ret;
    }

    private void showViewConnected() {
        if (mViewPager.getCurrentItem() != VIEW_INDEX_CONNECTED) {
            mViewPager.setCurrentItem(VIEW_INDEX_CONNECTED);
        }
    }

    private void showViewNoConnection() {
        if (mViewPager.getCurrentItem() != VIEW_INDEX_NOT_CONNECTED) {
            mViewPager.setCurrentItem(VIEW_INDEX_NOT_CONNECTED);
        }
    }

    private void showViewWrongCredentials() {
        if (mViewPager.getCurrentItem() != VIEW_INDEX_WRONG_CREDENTIALS) {
            mViewPager.setCurrentItem(VIEW_INDEX_WRONG_CREDENTIALS);
        }
    }

    private void showViewLoading() {
        if (mViewPager.getCurrentItem() != VIEW_INDEX_LOADING) {
            mViewPager.setCurrentItem(VIEW_INDEX_LOADING);
        }
    }

    @Override
    public void onNewQuery(String query) {
        mPagerAdapter.applyChatFilter(query);
    }

    @Override
    public void onRoomSelected(String friendName) {
        //TODO Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        //intent.putExtra(KEY_FRIEND_NAME, friendName);
        //startActivity(intent);
    }

    private synchronized void requestListRefresh() {
        final View thisView = findViewById(android.R.id.content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (thisView != null && thisView.isShown()) {
                    thisView.invalidate();
                }
            }
        });
    }

    private void registerLocalBroadcastReceiver() {
        if (mChatBroadcastReceiver != null) {
            return;
        }
        mChatBroadcastReceiver = new ChatOverviewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_failed", null));
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_chat_overview", null));
        intentFilter.addAction(LoLin1Utils
                .getString(getApplicationContext(), "event_login_successful", null));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mChatBroadcastReceiver, intentFilter);
    }

    public class ChatOverviewBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            logString("debug", "Chat: received broadcast intent with action " + action);
            if (action.contentEquals(LoLin1Utils
                    .getString(context.getApplicationContext(), "event_chat_overview", null))) {
                requestListRefresh();
            } else {
                final View thisView =
                        findViewById(android.R.id.content);
                if (action.contentEquals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (!LoLin1Utils.isInternetReachable(context.getApplicationContext())) {
                        thisView.post(new Runnable() {
                            @Override
                            public void run() {
                                showViewNoConnection();
                            }
                        });
                        if (LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                                getApplicationContext())) {
                            stopChatService();
                        }
                    } else {
                        ChatOverviewActivity.this.runChat();
                    }
                } else if (action.contentEquals(LoLin1Utils
                        .getString(context.getApplicationContext(), "event_login_failed", null))) {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            showViewWrongCredentials();
                        }
                    });
                    if (LoLin1Utils.isServiceAlreadyRunning(ChatIntentService.class,
                            getApplicationContext())) {
                        stopChatService();
                    }
                } else if (action.contentEquals(LoLin1Utils
                        .getString(context.getApplicationContext(), "event_login_successful",
                                null))) {
                    thisView.post(new Runnable() {
                        @Override
                        public void run() {
                            FriendManager.getInstance().updateOnlineFriends();
                            showViewConnected();
                        }
                    });
                }
            }

        }
    }

    private void stopChatService() {
        logString("debug", "Stopping chat service...");
        stopService(new Intent(getApplicationContext(), ChatIntentService.class));
    }

    private class ChatStatesPagerAdapter extends FragmentStatePagerAdapter {

        private final int STATE_AMOUNT = 4;
        private ChatOverviewSupportFragment CHAT_OVERVIEW_FRAGMENT;
        private IndefiniteFancyProgressSupportFragment PROGRESS_FRAGMENT;
        private WrongChatCredentialsSupportFragment WRONG_CREDENTIALS_FRAGMENT;
        private Fragment NOT_CONNECTED_FRAGMENT;

        public ChatStatesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void applyChatFilter(String query) {
            CHAT_OVERVIEW_FRAGMENT.applyFilter(query);
        }

        @Override
        public int getCount() {
            return STATE_AMOUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment ret;
            switch (position) {
                case ChatOverviewActivity.VIEW_INDEX_CONNECTED:
                    if (CHAT_OVERVIEW_FRAGMENT == null) {
                        CHAT_OVERVIEW_FRAGMENT =
                                (ChatOverviewSupportFragment) ChatOverviewSupportFragment
                                        .instantiate(getApplicationContext(),
                                                ChatOverviewSupportFragment.class.getName());
                    }
                    ret = CHAT_OVERVIEW_FRAGMENT;
                    break;
                case ChatOverviewActivity.VIEW_INDEX_WRONG_CREDENTIALS:
                    if (WRONG_CREDENTIALS_FRAGMENT == null) {
                        WRONG_CREDENTIALS_FRAGMENT =
                                (WrongChatCredentialsSupportFragment) WrongChatCredentialsSupportFragment
                                        .instantiate(getApplicationContext(),
                                                WrongChatCredentialsSupportFragment.class
                                                        .getName()
                                        );
                    }
                    ret = WRONG_CREDENTIALS_FRAGMENT;
                    break;
                case ChatOverviewActivity.VIEW_INDEX_NOT_CONNECTED:
                    if (NOT_CONNECTED_FRAGMENT == null) {
                        NOT_CONNECTED_FRAGMENT = NoChatConnectionSupportFragment
                                .instantiate(getApplicationContext(),
                                        NoChatConnectionSupportFragment.class.getName());
                    }
                    ret = NOT_CONNECTED_FRAGMENT;
                    break;
                case ChatOverviewActivity.VIEW_INDEX_LOADING:
                    if (PROGRESS_FRAGMENT == null) {
                        PROGRESS_FRAGMENT =
                                (IndefiniteFancyProgressSupportFragment) IndefiniteFancyProgressSupportFragment
                                        .instantiate(getApplicationContext(),
                                                IndefiniteFancyProgressSupportFragment.class
                                                        .getName()
                                        );
                    }
                    ret = PROGRESS_FRAGMENT;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected page index " + position);
            }
            return ret;
        }
    }
}
