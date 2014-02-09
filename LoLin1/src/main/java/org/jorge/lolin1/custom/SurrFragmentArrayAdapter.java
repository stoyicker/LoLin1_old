package org.jorge.lolin1.custom;

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
import org.jorge.lolin1.feeds.surr.SurrEntry;
import org.jorge.lolin1.frags.SurrListFragment;
import org.jorge.lolin1.io.db.SQLiteBridge;

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
 * Created by JorgeAntonio on 25/01/14.
 */
public class SurrFragmentArrayAdapter extends ArrayAdapter<SurrEntry> {

    private static final int list_item_layout = R.layout.list_item_surr_feed;
    private static Context mContext;
    private final Animation unreadContentAnimation;

    public SurrFragmentArrayAdapter(Context context) {
        super(context, list_item_layout);
        mContext = context;
        unreadContentAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_grow);
    }

    public void updateShownSurrs() {

        int howManyIHave = this.getCount();
        final ArrayList<SurrEntry> newSurrs =
                SQLiteBridge.getSingleton().getNewSurrs(howManyIHave);
        Collections.reverse(newSurrs);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (SurrEntry x : newSurrs) {
                    insert(x, 0);
                }
                SurrFragmentArrayAdapter.this.notifyDataSetChanged();
            }
        });
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    list_item_layout, null);
        }

        assert convertView != null;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.surr_feed_item_title);

        SurrEntry thisArticle = getItem(position);

        ImageView itemWithNewContentImageView =
                (ImageView) convertView.findViewById(R.id.surr_feed_item_new_content_image);

        if (position == SurrListFragment.getSelectedIndex() &&
                mContext.getResources().getBoolean(R.bool.feed_has_two_panes)) {
            convertView.setBackgroundResource(R.color.theme_black);
        }
        else {
            convertView.setBackgroundResource(R.color.theme_surr_grey);
        }

        if (!thisArticle.hasBeenRead()) {
            itemWithNewContentImageView.setVisibility(View.VISIBLE);
            itemWithNewContentImageView.startAnimation(unreadContentAnimation);
        }
        else {
            itemWithNewContentImageView.setVisibility(View.INVISIBLE);
            unreadContentAnimation.cancel();
            unreadContentAnimation.reset();
        }

        titleTextView.setText(Html.fromHtml(thisArticle.getTitle()));

        return convertView;
    }
}
