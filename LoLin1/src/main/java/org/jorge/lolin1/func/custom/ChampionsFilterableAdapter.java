package org.jorge.lolin1.func.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.utils.LoLin1Utils;

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
public class ChampionsFilterableAdapter extends BaseAdapter implements Filterable {

    private static final int LIST_ITEM_LAYOUT = R.layout.list_item_champions;
    private final List<Champion> data = new ArrayList<>();
    private final Activity mActivity;

    public ChampionsFilterableAdapter(Activity activity) {
        mActivity = activity;
        data.addAll(ChampionManager.getInstance().getChampions());
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
                        if (x.matchesFilterQuery(constraint)) {
                            validChampions.add(x);
                        }
                    }
                    ret.values = validChampions;
                }
                return ret;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ChampionsFilterableAdapter.this.data.clear();
                ChampionsFilterableAdapter.this.data.addAll(
                        (java.util.Collection<? extends Champion>) results.values);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChampionsFilterableAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        };
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Champion getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        int sideLength = LoLin1Utils
                .getInt(mActivity.getApplicationContext(), "champion_list_bust_length", -1);

        if (convertView == null) {
            convertView =
                    ((LayoutInflater) mActivity.getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(
                                    LIST_ITEM_LAYOUT, null);
            viewHolder = new ViewHolder();
            viewHolder.setChampionBust((ImageView) convertView.findViewById(R.id.bust_image));
            viewHolder.setTitleOverlay((TextView) convertView.findViewById(R.id.overlay_title));
            viewHolder.setNameOverlay((TextView) convertView.findViewById(R.id.overlay_name));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(final Object... params) {
                final Bitmap bmp =
                        ChampionManager.getInstance().getBustImageByChampion((Integer) params[2],
                                (Integer) params[2], data.get((Integer) params[1]),
                                mActivity.getApplicationContext());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewHolder viewHolder1 = (ViewHolder) params[0];
                        (viewHolder1).getChampionBust().setImageBitmap(bmp);
                    }
                });
                return null;
            }
        }.execute(viewHolder, position, sideLength);

        viewHolder.getTitleOverlay()
                .setText(data.get(position).getTitle());
        viewHolder.getNameOverlay()
                .setText(data.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        private ImageView championBust;
        private TextView titleOverlay, nameOverlay;

        public ImageView getChampionBust() {
            return championBust;
        }

        public void setChampionBust(ImageView championBust) {
            this.championBust = championBust;
        }

        public TextView getTitleOverlay() {
            return titleOverlay;
        }

        public void setTitleOverlay(TextView titleOverlay) {
            this.titleOverlay = titleOverlay;
        }

        public TextView getNameOverlay() {
            return nameOverlay;
        }

        public void setNameOverlay(TextView nameOverlay) {
            this.nameOverlay = nameOverlay;
        }
    }
}
