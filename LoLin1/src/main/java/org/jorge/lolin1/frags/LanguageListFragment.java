package org.jorge.lolin1.frags;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by JorgeAntonio on 21/03/2014.
 */
public class LanguageListFragment extends Fragment {

    private LanguageListFragmentListener mCallback;
    private LanguageListControlledView controlledView;

    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reloadLanguages();
        }
    }

    private void reloadLanguages() {
        ViewGroup viewAsViewGroup = (ViewGroup) getView();
        viewAsViewGroup.removeAllViews();
        String realm_composite =
                new StringBuilder(LoLin1Utils.getString(getActivity().getApplicationContext(),
                        "realm_to_language_list_prefix", "lang_"))
                        .append(
                                mCallback.onCurrentlySelectedRealmRequest().toLowerCase())
                        .toString();
        String[] languages =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(), realm_composite,
                                new String[]{"NO_LANGUAGE_FOUND"}), languages_simplified =
                LoLin1Utils
                        .getStringArray(getActivity().getApplicationContext(),
                                new StringBuilder(realm_composite).append(LoLin1Utils
                                        .getString(getActivity().getApplicationContext(),
                                                "language_to_simplified_suffix", "_simplified"))
                                        .toString(),
                                new String[]{"NO_LANGUAGE_FOUND"}
                        );
        int languageCounter = 0;
        for (String language : languages) {
            TextView textView =
                    new TextView(getActivity().getApplicationContext(),
                            Xml.asAttributeSet(getResources().getXml(
                                    R.xml.server_and_language_text_views_attribute_set))
                    ) {
                        @Override
                        public boolean onTouchEvent(MotionEvent event) {
                            mCallback.onLocaleSelected(/*TODO Calculate selected locale*/);
                            /*TODO Highlight this TextView with the ServerAndLanguageChooserUnselectedStyle - http://stackoverflow.com/questions/11723881/android-set-view-style-programatically*/
                            return Boolean.TRUE;
                        }
                    };
            textView.setText(language);
            textView.setTag(languages_simplified[languageCounter]);
            viewAsViewGroup.addView(textView);
            languageCounter++;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (LanguageListFragmentListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement LanguageListFragmentListener.");
        }
    }

    public void notifyNewRealmHasBeenSelected(String newSelectedRealm) {
        mCallback.updateLanguageChooserVisibility();
        reloadLanguages();
    }

    public interface LanguageListFragmentListener {
        public void onLocaleSelected(String newLocale);

        public String onCurrentlySelectedRealmRequest();

        public void updateLanguageChooserVisibility();
    }

    private class LanguageListControlledView extends View {
        /**
         * Simple constructor to use when creating a view from code.
         *
         * @param context The Context the view is running in, through which it can
         *                access the current theme, resources, etc.
         */
        public LanguageListControlledView(Context context) {
            super(context);
        }
    }
}
