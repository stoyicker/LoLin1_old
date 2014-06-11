package org.jorge.lolin1.ui.frags;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.func.custom.ScalableLinearLayout;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 21/04/2014.
 */
public class ChampionSkinSupportFragment extends Fragment {

    private static final String KEY_SCALE = "SCALE", KEY_CHAMPION = "CHAMPION", KEY_POSITION =
            "POSITION";
    private static Context context;

    public static Fragment newInstance(Context _context, int position, Champion champion,
                                       float scale) {
        context = _context;
        Bundle args = new Bundle();
        args.putFloat(KEY_SCALE, scale);
        args.putInt(KEY_POSITION, position);
        args.putParcelable(KEY_CHAMPION, champion);
        return Fragment.instantiate(_context, ChampionSkinSupportFragment.class.getName(), args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        LinearLayout l = (LinearLayout)
                inflater.inflate(R.layout.fragment_champion_skin, container, false);

        ScalableLinearLayout root = (ScalableLinearLayout) l.findViewById(R.id.skin_root_view);
        float scale = this.getArguments().getFloat(KEY_SCALE);
        root.setScaleBoth(scale);

        ImageView skinView = (ImageView) l.findViewById(R.id.skin_view);
        skinView.setImageDrawable(new BitmapDrawable(getResources(),
                ChampionManager.getInstance()
                        .getSplashImageByChampion(
                                (Champion) getArguments().getParcelable(KEY_CHAMPION), context, -1,
                                -1,
                                getArguments().getInt(KEY_POSITION))
        ));

        TextView skinNameView = (TextView) l.findViewById(R.id.skin_name);
        skinNameView.setText(((Champion) getArguments().getParcelable(KEY_CHAMPION))
                .getSkinNames()[getArguments().getInt(KEY_POSITION)]);

        return l;
    }
}
