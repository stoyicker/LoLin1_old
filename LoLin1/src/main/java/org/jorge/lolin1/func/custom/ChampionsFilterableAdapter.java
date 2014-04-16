package org.jorge.lolin1.func.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
 * Created by JorgeAntonio on 16/04/2014.
 */
public class ChampionsFilterableAdapter extends ArrayAdapter<Champion> implements Filterable {

    private static final int LIST_ITEM_LAYOUT = R.layout.list_item_champions;
    private final Activity mActivity;

    public ChampionsFilterableAdapter(Activity activity) {
        super(activity.getApplicationContext(), LIST_ITEM_LAYOUT);
        mActivity = activity;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults ret = new FilterResults();
                Collection<Champion> allChampions = ChampionManager.getInstance().getChampions();
                if (constraint == null || constraint.length() == 0) {
                    ret.values = allChampions;
                }
                else {
                    List<Champion> validChampions = new ArrayList<>();
                    for (Champion x : allChampions) {
                        if (x.containsText(constraint)) {
                            validChampions.add(x);
                        }
                    }
                    ret.values = validChampions;
                }
                return ret;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ChampionsFilterableAdapter.this.clear();
                ChampionsFilterableAdapter.this.addAll(
                        (java.util.Collection<? extends Champion>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mActivity.getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    LIST_ITEM_LAYOUT, null);
            viewHolder = new ViewHolder();
            viewHolder.setChampionBust((ImageView) convertView.findViewById(R.id.bust_image));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(final Object... params) {
                final Bitmap bmp =
                        ChampionManager.getInstance().getImageByChampionIndex((Integer) params[2],
                                ChampionManager.ImageType.BUST,
                                mActivity.getApplicationContext());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ImageView) params[0]).setImageBitmap(bmp);
                    }
                });
                return null;
            }
        }.execute(viewHolder.getChampionBust(), position);

        return convertView;
    }

    private class ViewHolder {
        private ImageView championBust;

        public ImageView getChampionBust() {
            return championBust;
        }

        public void setChampionBust(ImageView championBust) {
            this.championBust = championBust;
        }
    }
}
