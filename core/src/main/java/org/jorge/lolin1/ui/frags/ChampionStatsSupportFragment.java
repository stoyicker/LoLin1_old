package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.models.Champion;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 19/04/2014.
 */
public class ChampionStatsSupportFragment extends ChampionDetailSupportFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setLayout(R.layout.fragment_champion_stats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        final Champion selectedChampion = getSelectedChampion();

        final String placeholder = LoLin1Utils.getString(getActivity().getApplicationContext(),
                "placeholder", null), scalingString =
                LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "stat_scaling", null);

        ((TextView) view.findViewById(R.id.health_contents)).setText(
                selectedChampion.getHp() + " " +
                        scalingString.replace(placeholder, selectedChampion.getHpPerlevel())
        );
        TextView resourceTitle = (TextView) view.findViewById(R.id.resource_title),
                resourceContents = (TextView) view.findViewById(R.id.resource_contents),
                resourceRegenTitle = (TextView) view.findViewById(R.id.resourceregen_title),
                resourceRegenContents = (TextView) view.findViewById(R.id.resourceregen_contents);
        switch (selectedChampion.getUsedResource()) {
            case MANA:
                resourceTitle.setText(
                        LoLin1Utils.getString(getActivity().getApplicationContext(),
                                "resource_mana_title_text", null)
                );
                resourceTitle.setVisibility(View.VISIBLE);
                resourceContents.setText(selectedChampion.getResource() + " " +
                        scalingString.replace(placeholder, selectedChampion.getResourcePerLevel()));
                resourceContents.setVisibility(View.VISIBLE);
                resourceRegenTitle.setText(
                        LoLin1Utils.getString(getActivity().getApplicationContext(),
                                "resourceregen_mana_title_text", null)
                );
                resourceRegenTitle.setVisibility(View.VISIBLE);
                resourceRegenContents.setText(selectedChampion.getResourceRegen() + " " +
                        scalingString
                                .replace(placeholder, selectedChampion.getResourceRegenPerLevel()));
                resourceRegenContents.setVisibility(View.VISIBLE);
                break;
            case ENERGY:
                resourceTitle.setText(
                        LoLin1Utils.getString(getActivity().getApplicationContext(),
                                "resource_energy_title_text", null)
                );
                resourceTitle.setVisibility(View.VISIBLE);
                resourceContents.setText(selectedChampion.getResource());
                resourceContents.setVisibility(View.VISIBLE);
                resourceRegenTitle.setText(
                        LoLin1Utils.getString(getActivity().getApplicationContext(),
                                "resourceregen_energy_title_text", null)
                );
                resourceRegenTitle.setVisibility(View.VISIBLE);
                resourceRegenContents.setText(selectedChampion.getResourceRegen());
                resourceRegenContents.setVisibility(View.VISIBLE);
                break;
            case NONE:
                resourceTitle.setVisibility(View.GONE);
                resourceContents.setVisibility(View.GONE);
                resourceRegenTitle.setVisibility(View.GONE);
                resourceRegenContents.setVisibility(View.GONE);
                break;
            default:
                Crashlytics.logException(new IllegalArgumentException(
                        "Non-handled champion stat " + selectedChampion.getUsedResource().name()));
        }
        ((TextView) view.findViewById(R.id.ad_contents)).setText(
                selectedChampion.getAttackDamage() + " " + scalingString
                        .replace(placeholder, selectedChampion.getAttackDamagePerLevel())
        );
        ((TextView) view.findViewById(R.id.as_contents))
                .setText(selectedChampion.getAttackSpeed() + " " +
                        scalingString
                                .replace(placeholder,
                                        selectedChampion.getAttackSpeedPerLevel() + "%"));
        ((TextView) view.findViewById(R.id.movspeed_contents))
                .setText(selectedChampion.getMoveSpeed());
        ((TextView) view.findViewById(R.id.healthregen_contents)).setText(
                selectedChampion.getHpRegen() + " " +
                        scalingString.replace(placeholder, selectedChampion.getHpRegenPerLevel())
        );
        ((TextView) view.findViewById(R.id.armor_contents))
                .setText(selectedChampion.getArmor() + " " +
                        scalingString.replace(placeholder, selectedChampion.getArmorPerLevel()));
        ((TextView) view.findViewById(R.id.mr_contents))
                .setText(selectedChampion.getMagicResist() + " " +
                        scalingString
                                .replace(placeholder, selectedChampion.getMagicResistPerLevel()));
        return view;
    }
}
