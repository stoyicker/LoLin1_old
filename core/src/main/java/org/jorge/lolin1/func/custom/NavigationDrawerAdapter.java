package org.jorge.lolin1.func.custom;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 03/01/14.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private final int mResource = R.layout.list_item_navigation_drawer;
    private Context mContext;

    public NavigationDrawerAdapter(Context context) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    mResource, null);

            viewHolder = new ViewHolder();
            viewHolder.setTitleView((TextView) convertView.findViewById(R.id.navigation_drawer_section_title));
            viewHolder.setSectionIcon((ImageView) convertView.findViewById(R.id.navigation_drawer_section_image));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        int temp = position + 1;

        viewHolder.getTitleView().setText(LoLin1Utils.getString(mContext, "title_section" + +temp, ""));
        viewHolder.getSectionIcon().setBackgroundResource(LoLin1Utils.getDrawableAsId("icon_section" + temp, -1));

        return convertView;
    }

    private final static class ViewHolder {
        private TextView sectionTitle;
        private ImageView sectionIcon;

        private void setTitleView(TextView textView) {
            sectionTitle = textView;
        }

        private TextView getTitleView() {
            return sectionTitle;
        }

        public ImageView getSectionIcon() {
            return sectionIcon;
        }

        public void setSectionIcon(ImageView sectionIcon) {
            this.sectionIcon = sectionIcon;
        }
    }
}
