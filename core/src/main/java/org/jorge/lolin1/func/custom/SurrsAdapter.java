package org.jorge.lolin1.func.custom;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.surr.SurrEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;

import java.util.ArrayList;
import java.util.Collections;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 25/01/14.
 */
public class SurrsAdapter extends ArrayAdapter<SurrEntry> {

    private static final int list_item_layout = R.layout.list_item_surr_feed;
    private static Context mContext;
    private final Animation unreadContentAnimation;

    public SurrsAdapter(Context context) {
        super(context, list_item_layout);
        mContext = context;
        unreadContentAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_grow);
    }

    public void updateShownSurrs() {
        final ArrayList<SurrEntry> allSurrs =
                SQLiteDAO.getSingleton().getSurrs();
        Collections.reverse(allSurrs);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clear();
                for (SurrEntry x : allSurrs) {
                    insert(x, 0);
                }
                SurrsAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    list_item_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.setTitleView((TextView) convertView.findViewById(R.id.surr_feed_item_title));
            viewHolder.setItemWithNewContentImageView(
                    (ImageView) convertView.findViewById(R.id.surr_feed_item_new_content_image));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SurrEntry thisArticle = getItem(position);

//        if (position == PreferenceManager.getDefaultSharedPreferences(mContext).getInt(
//                "lastSelectedSurrIndex", -1) &&
//                mContext.getResources().getBoolean(R.bool.feed_has_two_panes)) {
//            convertView.setBackgroundResource(R.color.theme_black);
//        }

        if (!thisArticle.hasBeenRead()) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.theme_black));
            viewHolder.getItemWithNewContentImageView().setVisibility(View.VISIBLE);
            viewHolder.getItemWithNewContentImageView().startAnimation(unreadContentAnimation);
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.theme_surr_gray));
            viewHolder.getItemWithNewContentImageView().setVisibility(View.INVISIBLE);
            unreadContentAnimation.cancel();
            unreadContentAnimation.reset();
        }

        viewHolder.getTitleView().setText(Html.fromHtml(thisArticle.getTitle()));

        return convertView;
    }

    private final static class ViewHolder {
        private TextView titleView;
        private ImageView itemWithNewContentImageView;

        public TextView getTitleView() {
            return titleView;
        }

        public void setTitleView(TextView title) {
            this.titleView = title;
        }

        public ImageView getItemWithNewContentImageView() {
            return itemWithNewContentImageView;
        }

        public void setItemWithNewContentImageView(ImageView itemWithNewContentImageView) {
            this.itemWithNewContentImageView = itemWithNewContentImageView;
        }
    }
}
