package org.jorge.lolin1.func.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.func.champs.models.spells.ActiveSpell;
import org.jorge.lolin1.func.champs.models.spells.PassiveSpell;
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
 * Created by JorgeAntonio on 19/04/2014.
 */
public class ChampionAbilitiesListAdapter extends BaseAdapter {

    private final Champion selectedChampion;
    private static final int LIST_ITEM_LAYOUT = R.layout.list_item_champion_abilities;
    private Activity mActivity;

    public ChampionAbilitiesListAdapter(Activity activity, Champion _selectedChampion) {
        mActivity = activity;
        this.selectedChampion = _selectedChampion;
    }

    @Override
    public int getCount() {
        return selectedChampion.getSpells().length + 1;
        //+1 is for the passive
    }

    @Override
    public Object getItem(int position) {
        //If 0, the passive is requested. Otherwise, it's another spell.
        return position == 0 ? selectedChampion.getPassive() :
                selectedChampion.getSpells()[position - 1];
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            viewHolder.setCostContentsView((TextView) convertView.findViewById(R.id.cost_contents));
            viewHolder.setDetailView((TextView) convertView.findViewById(R.id.ability_detail));
            viewHolder
                    .setRangeContentsView((TextView) convertView.findViewById(R.id.range_contents));
            viewHolder.setNameView((TextView) convertView.findViewById(R.id.ability_name));
            viewHolder.setCostTitleView((TextView) convertView.findViewById(R.id.cost_title));
            viewHolder.setRangeTitleView((TextView) convertView.findViewById(R.id.range_title));
            viewHolder.setCooldownTitleView(
                    (TextView) convertView.findViewById(R.id.cooldown_title));
            viewHolder.setCooldownContentsView(
                    (TextView) convertView.findViewById(R.id.cooldown_contents));
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PassiveSpell thisSpell;

        if (position == 0) { //If it's the passive
            thisSpell = selectedChampion.getPassive();
            viewHolder.getCostTitleView().setVisibility(View.GONE);
            viewHolder.getCostContentsView().setVisibility(View.GONE);
            viewHolder.getRangeTitleView().setVisibility(View.GONE);
            viewHolder.getRangeContentsView().setVisibility(View.GONE);
            viewHolder.getCooldownTitleView().setVisibility(View.GONE);
            viewHolder.getCooldownContentsView().setVisibility(View.GONE);
        }
        else {
            thisSpell = selectedChampion.getSpells()[position - 1];
            TextView costContents = viewHolder.getCostContentsView();
            viewHolder.getCostTitleView().setVisibility(View.VISIBLE);
            costContents.setText(((ActiveSpell) thisSpell).getCostBurn());
            costContents.setVisibility(View.VISIBLE);
            viewHolder.getRangeTitleView().setVisibility(View.VISIBLE);
            TextView rangeContents = viewHolder.getRangeContentsView();
            rangeContents.setText(((ActiveSpell) thisSpell).getRangeBurn());
            rangeContents.setVisibility(View.VISIBLE);
            viewHolder.getCooldownTitleView().setVisibility(View.VISIBLE);
            TextView cooldownContents = viewHolder.getCooldownContentsView();
            cooldownContents.setText(((ActiveSpell) thisSpell).getCooldownBurn() + " " +
                    LoLin1Utils
                            .getString(mActivity.getApplicationContext(), "time_unit_second_plural",
                                    null));
            cooldownContents.setVisibility(View.VISIBLE);
        }

        viewHolder.getDetailView().setText(Html.fromHtml(thisSpell.getDetail()));
        TextView nameView = viewHolder.getNameView();
        nameView.setText(thisSpell.getName());

        int sideLength = LoLin1Utils
                .getInt(mActivity.getApplicationContext(), "champion_list_bust_length", -1);

        new AsyncTask<Object, Void, Void>() {

            @Override
            protected Void doInBackground(final Object... params) {
                final Bitmap bmp;

                if ((Integer) params[2] == 0) {
                    bmp =
                            ChampionManager.getInstance()
                                    .getPassiveImageByChampion(selectedChampion,
                                            mActivity.getApplicationContext(), (Integer) params[1],
                                            (Integer) params[1]);
                }
                else {
                    bmp = ChampionManager.getInstance()
                            .getSpellImageByChampion(selectedChampion,
                                    mActivity.getApplicationContext(), (Integer) params[1],
                                    (Integer) params[1], (Integer) params[2] - 1);
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewHolder viewHolder1 = (ViewHolder) params[0];
                        (viewHolder1).getNameView().setCompoundDrawablesWithIntrinsicBounds(
                                new BitmapDrawable(mActivity.getResources(), bmp), null, null,
                                null);
                    }
                });
                return null;
            }
        }.execute(viewHolder, sideLength, position);

        return convertView;
    }

    private class ViewHolder {
        public TextView getDetailView() {
            return detailView;
        }

        public void setDetailView(TextView detailView) {
            this.detailView = detailView;
        }

        public TextView getNameView() {
            return nameView;
        }

        public void setNameView(TextView nameView) {
            this.nameView = nameView;
        }

        public TextView getCostContentsView() {
            return costContentsView;
        }

        public void setCostContentsView(TextView costContentsView) {
            this.costContentsView = costContentsView;
        }

        public TextView getRangeContentsView() {
            return rangeContentsView;
        }

        public void setRangeContentsView(TextView rangeContentsView) {
            this.rangeContentsView = rangeContentsView;
        }

        public TextView getRangeTitleView() {
            return rangeTitleView;
        }

        public TextView getCostTitleView() {
            return costTitleView;
        }

        public void setCostTitleView(TextView costTitleView) {
            this.costTitleView = costTitleView;
        }

        public void setRangeTitleView(TextView rangeTitleView) {
            this.rangeTitleView = rangeTitleView;
        }

        public TextView getCooldownContentsView() {
            return cooldownContentsView;
        }

        public void setCooldownContentsView(TextView cooldownContentsView) {
            this.cooldownContentsView = cooldownContentsView;
        }

        public TextView getCooldownTitleView() {
            return cooldownTitleView;
        }

        public void setCooldownTitleView(TextView cooldownTitleView) {
            this.cooldownTitleView = cooldownTitleView;
        }

        private TextView nameView, detailView, costTitleView, rangeTitleView, costContentsView,
                rangeContentsView, cooldownTitleView, cooldownContentsView;
    }
}
