package org.jorge.lolin1.customs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.ReflectedRes;

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
 * Created by JorgeAntonio on 03/01/14.
 */
public class FeedFragmentArrayAdapter extends BaseAdapter {

    private final int mResource = R.layout.news_feed_list_item;
    private Context mContext;

    public FeedFragmentArrayAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.navigation_drawer_items).length;
    }

    @Override
    public Object getItem(int i) {
        int temp = i + 1;
        return ReflectedRes.string(mContext, "title_section" + temp, "");
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mResource, parent, false);

        int temp = position + 1;

        ImageView image = (ImageView) convertView.findViewById(R.id.feed_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.news_feed_item_title);
        TextView desc = (TextView) convertView.findViewById(R.id.news_feed_item_desc);

        int title_proportion = Integer.parseInt(ReflectedRes.string(mContext, "feed_item_title_proportion", "-1"));

        image.setImageResource(ReflectedRes.drawableAsId("icon_section" + temp, -1)); //TODO These three values must be retrieved from the database.
        title.setTextSize(image.getHeight() * title_proportion);
        desc.setTextSize(image.getHeight() * (1 - title_proportion));
        title.setText(ReflectedRes.string(mContext, "title_section" + +temp, ""));
        desc.setText(ReflectedRes.string(mContext, "title_section" + +temp, ""));

        return convertView;
    }
}
