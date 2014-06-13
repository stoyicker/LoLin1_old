package org.jorge.lolin1.ui.frags;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.ArrayList;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 21/03/2014.
 */
public class LanguageListFragment extends Fragment {

    private LanguageListFragmentListener mCallback;
    private ArrayList<TextView> views = new ArrayList<>();

    public LanguageListFragment() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reloadLanguages(mCallback.onCurrentlySelectedRealmRequest());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language_list, container, Boolean.FALSE);
    }


    private void reloadLanguages(View v, String newSelectedRealm) {
        LinearLayout viewAsViewGroup =
                (LinearLayout) v.findViewById(R.id.language_list_container);
        viewAsViewGroup.removeAllViews();
        views.clear();
        String realm_composite =
                LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "realm_to_language_list_prefix", "lang_") +
                        newSelectedRealm.toLowerCase();
        String[] languages =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(), realm_composite,
                                null), languages_simplified =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(),
                                realm_composite + LoLin1Utils
                                        .getString(getActivity().getApplicationContext(),
                                                "language_to_simplified_suffix", "_simplified"),
                                null
                        );
        int languageCounter = 0;
        for (String language : languages) {
            final TextView textView =
                    new TextView(getActivity().getApplicationContext()) {
                        @Override
                        public boolean onTouchEvent(MotionEvent event) {
                            for (TextView x : views)
                                if (x != this) {
                                    x.setShadowLayer(0, 0, 0, R.color.theme_white);
                                    x.setTypeface(null, Typeface.NORMAL);
                                }
                            this.setTypeface(null, Typeface.BOLD);
                            this.setShadowLayer(3, 3, 3, R.color.theme_strong_orange);
                            mCallback.onLocaleSelected((String) this.getTag());
                            return Boolean.TRUE;
                        }
                    };
            views.add(textView);
            int verticalPadding = LoLin1Utils.getInt(getActivity().getApplicationContext(),
                    "server_and_language_text_views_vertical_margin", 10), horizontalPadding =
                    LoLin1Utils.getInt(getActivity().getApplicationContext(),
                            "server_and_language_text_views_horizontal_margin", 15);
            verticalPadding =
                    LoLin1Utils.pixelsAsDp(getActivity().getApplicationContext(), verticalPadding);
            horizontalPadding = LoLin1Utils
                    .pixelsAsDp(getActivity().getApplicationContext(), horizontalPadding);
            textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding,
                    verticalPadding);
            textView.setText(language);
            textView.setTextColor(getResources().getColor(R.color.theme_black));
            textView.setTextSize(LoLin1Utils
                    .getInt(getActivity().getApplicationContext(),
                            "language_chooser_text_size",
                            -1));
            textView.setTag(languages_simplified[languageCounter]);
            languageCounter++;
            viewAsViewGroup.addView(textView,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
            );
        }
    }


    private void reloadLanguages(String newSelectedRealm) {
        LinearLayout viewAsViewGroup =
                (LinearLayout) getView().findViewById(R.id.language_list_container);
        viewAsViewGroup.removeAllViews();
        views.clear();
        String realm_composite =
                LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "realm_to_language_list_prefix", "lang_") +
                        newSelectedRealm.toLowerCase();
        String[] languages =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(), realm_composite,
                                null), languages_simplified =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(),
                                realm_composite + LoLin1Utils
                                        .getString(getActivity().getApplicationContext(),
                                                "language_to_simplified_suffix", "_simplified"),
                                null
                        );
        int languageCounter = 0;
        for (String language : languages) {
            final TextView textView =
                    new TextView(getActivity().getApplicationContext()) {
                        @Override
                        public boolean onTouchEvent(MotionEvent event) {
                            for (TextView x : views)
                                if (x != this) {
                                    x.setShadowLayer(0, 0, 0, R.color.theme_white);
                                    x.setTypeface(null, Typeface.NORMAL);
                                }
                            this.setTypeface(null, Typeface.BOLD);
                            this.setShadowLayer(3, 3, 3, R.color.theme_strong_orange);
                            mCallback.onLocaleSelected((String) this.getTag());
                            return Boolean.TRUE;
                        }
                    };
            views.add(textView);
            int verticalPadding = LoLin1Utils.getInt(getActivity().getApplicationContext(),
                    "server_and_language_text_views_vertical_margin", 10), horizontalPadding =
                    LoLin1Utils.getInt(getActivity().getApplicationContext(),
                            "server_and_language_text_views_horizontal_margin", 15);
            verticalPadding =
                    LoLin1Utils.pixelsAsDp(getActivity().getApplicationContext(), verticalPadding);
            horizontalPadding = LoLin1Utils
                    .pixelsAsDp(getActivity().getApplicationContext(), horizontalPadding);
            textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding,
                    verticalPadding);
            textView.setText(language);
            textView.setTextColor(getResources().getColor(R.color.theme_black));
            textView.setTextSize(LoLin1Utils
                    .getInt(getActivity().getApplicationContext(),
                            "language_chooser_text_size",
                            -1));
            textView.setTag(languages_simplified[languageCounter]);
            languageCounter++;
            viewAsViewGroup.addView(textView,
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
            );
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (LanguageListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement LanguageListFragmentListener.");
        }
    }

    public void notifyNewRealmHasBeenSelected(String newSelectedRealm) {
        mCallback.updateLanguageChooserVisibility();
        reloadLanguages(newSelectedRealm);
    }

    public interface LanguageListFragmentListener {
        public void onLocaleSelected(String newLocale);

        public String onCurrentlySelectedRealmRequest();

        public void updateLanguageChooserVisibility();
    }
}
