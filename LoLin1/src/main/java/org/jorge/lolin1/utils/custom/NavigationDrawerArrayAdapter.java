package org.jorge.lolin1.utils.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
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
 * Created by JorgeAntonio on 03/01/14.
 */
public class NavigationDrawerArrayAdapter extends BaseAdapter {

    private final int mResource = R.layout.list_item_navigation_drawer;
    private Context mContext;

    public NavigationDrawerArrayAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.navigation_drawer_items).length;
    }

    @Override
    public Object getItem(int i) {
        int temp = i + 1;
        return LoLin1Utils.getString(mContext, "title_section" + temp, "");
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(mResource, parent, false);


        int temp = position + 1;

        TextView textView =
                (TextView) convertView.findViewById(R.id.navigation_drawer_section_title);
        ImageView imageView =
                (ImageView) convertView.findViewById(R.id.navigation_drawer_section_image);

        textView.setText(LoLin1Utils.getString(mContext, "title_section" + +temp, ""));
        imageView.setImageResource(LoLin1Utils.getDrawableAsId("icon_section" + temp, -1));

        return convertView;
    }
}
