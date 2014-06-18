package org.jorge.lolin1.func.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.theholywaffle.lolchatapi.ChatMode;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.local.ProfileCacheableBitmapLoader;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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
public class ChatFilterableListAdapter extends BaseAdapter implements Filterable {

    private static final int LIST_ITEM_LAYOUT = R.layout.list_item_chat_overview;
    private final Set<Friend> data = new LinkedHashSet<>();
    private final Activity mActivity;
    private final ProfileCacheableBitmapLoader profileImageLoader =
            new ProfileCacheableBitmapLoader();

    public ChatFilterableListAdapter(Activity activity) {
        mActivity = activity;
        data.addAll(FriendManager.getInstance().getOnlineFriends());
    }

    @Override
    public void notifyDataSetChanged() {
        data.clear();
        data.addAll(FriendManager.getInstance().getOnlineFriends());
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        final Friend thisFriend = (Friend) getItem(position);

        if (convertView == null || convertView.getTag() == null || !((ViewHolder) convertView.getTag()).getName().getText().equals(thisFriend.getName())) {
            convertView =
                    ((LayoutInflater) mActivity.getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    LIST_ITEM_LAYOUT, null);
            viewHolder = new ViewHolder();
            viewHolder.setChatStatus((ImageView) convertView.findViewById(R.id.friend_chat_status));
            viewHolder.setLevel((TextView) convertView.findViewById(R.id.friend_level));
            viewHolder.setName((TextView) convertView.findViewById(R.id.friend_name));
            viewHolder
                    .setProfileIcon((ImageView) convertView.findViewById(R.id.friend_profile_icon));
            viewHolder.setRankedDivision(
                    (TextView) convertView.findViewById(R.id.friend_ranked_division));
            viewHolder.setTextStatus((TextView) convertView.findViewById(R.id.friend_text_status));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (viewHolder != null) {
            profileImageLoader
                    .assignImageToProfileView(mActivity, Math.max(0, thisFriend.getStatus().getProfileIconId()),
                            viewHolder.getProfileIcon());
            viewHolder.getName().setText(thisFriend.getName());
            final LolStatus thisStatus = thisFriend.getStatus();
            viewHolder.getLevel().setText(thisStatus.getLevel() + "");
            LolStatus.Division thisDivision = thisStatus.getRankedLeagueDivision();
            viewHolder.getRankedDivision()
                    .setText(thisStatus.getRankedLeagueTier().name() + " " + thisDivision.name());
            LolStatus.GameStatus gameStatus = thisStatus.getGameStatus();
            viewHolder.getTextStatus().setTextColor(mActivity.getApplicationContext().getResources().getColor(R.color.theme_strong_orange));
            if (gameStatus == LolStatus.GameStatus.SPECTATING) {
                viewHolder.getTextStatus().setText(R.string.status_spectating);
            } else if (gameStatus == LolStatus.GameStatus.CHAMPION_SELECT) {
                viewHolder.getTextStatus().setText(R.string.status_champion_select);
            } else if (gameStatus == LolStatus.GameStatus.HOSTING_NORMAL_GAME) {
                viewHolder.getTextStatus().setText(R.string.status_hosting_normal_game);
            } else if (gameStatus == LolStatus.GameStatus.HOSTING_PRACTICE_GAME) {
                viewHolder.getTextStatus().setText(R.string.status_hosting_practice_game);
            } else if (gameStatus == LolStatus.GameStatus.IN_GAME) {
                viewHolder.getTextStatus().setText(thisStatus.getSkin() + " @ " + thisStatus.getGameQueueType());
            } else if (gameStatus == LolStatus.GameStatus.TEAM_SELECT) {
                viewHolder.getTextStatus().setText(R.string.status_team_select);
            } else if (gameStatus == LolStatus.GameStatus.IN_QUEUE) {
                viewHolder.getTextStatus().setText(R.string.status_in_queue);
            } else if (gameStatus == LolStatus.GameStatus.HOSTING_RANKED_GAME) {
                viewHolder.getTextStatus().setText(R.string.status_hosting_ranked_game);
            } else {
                viewHolder.getTextStatus().setTextColor(Color.GREEN);
                viewHolder.getTextStatus().setText(thisStatus.getStatusMessage());
            }
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void[] params) {
                Drawable drawable;
                ChatMode mode = thisFriend.getChatMode();
                logString("debug", "Chat mode for " + thisFriend.getName() + ": " + mode);
                if (mode == ChatMode.AVAILABLE) {
                    drawable = mActivity.getResources().getDrawable(R.drawable.chat_status_green);
                } else if (mode == ChatMode.BUSY) {
                    drawable = mActivity.getResources().getDrawable(R.drawable.chat_status_yellow);
                } else if (mode == ChatMode.AWAY) {
                    drawable = mActivity.getResources().getDrawable(R.drawable.chat_status_red);
                } else {
                    drawable = mActivity.getResources().getDrawable(R.drawable.chat_status_gray);
                }
                final Drawable drawableAsFinal = drawable;
                if (viewHolder != null)
                    viewHolder.getChatStatus().post(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.getChatStatus().setImageDrawable(drawableAsFinal);
                        }
                    });
                return null;
            }
        }.execute();

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults ret = new FilterResults();
                Collection<Friend> allFriends = FriendManager.getInstance().getOnlineFriends();
                if (constraint == null || constraint.length() == 0) {
                    ret.values = allFriends;
                } else {
                    Collection<Friend> validFriends = new ArrayDeque<>();
                    for (Friend x : allFriends) {
                        if (x.matchesFilterQuery(constraint)) {
                            validFriends.add(x);
                        }
                    }
                    ret.values = validFriends;
                }
                return ret;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ChatFilterableListAdapter.this.data.clear();
                ChatFilterableListAdapter.this.data.addAll(
                        (Collection<? extends Friend>) results.values);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChatFilterableListAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    private class ViewHolder {
        private TextView level, name, textStatus, rankedDivision;
        private ImageView profileIcon, chatStatus;

        public TextView getLevel() {
            return level;
        }

        public void setLevel(TextView level) {
            this.level = level;
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getTextStatus() {
            return textStatus;
        }

        public void setTextStatus(TextView textStatus) {
            this.textStatus = textStatus;
        }

        public TextView getRankedDivision() {
            return rankedDivision;
        }

        public void setRankedDivision(TextView rankedDivision) {
            this.rankedDivision = rankedDivision;
        }

        public ImageView getProfileIcon() {
            return profileIcon;
        }

        public void setProfileIcon(ImageView profileIcon) {
            this.profileIcon = profileIcon;
        }

        public ImageView getChatStatus() {
            return chatStatus;
        }

        public void setChatStatus(ImageView chatStatus) {
            this.chatStatus = chatStatus;
        }
    }
}
