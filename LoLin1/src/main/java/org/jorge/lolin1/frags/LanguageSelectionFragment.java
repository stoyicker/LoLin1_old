package org.jorge.lolin1.frags;

import android.app.Activity;
import android.app.Fragment;

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
 * Created by JorgeAntonio on 18/03/14.
 */
public class LanguageSelectionFragment extends Fragment {

    private LanguageSelectionFragmentListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (LanguageSelectionFragmentListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement LanguageSelectionFragmentListener.");
        }
    }

    public void notifyNewRealmHasBeenSelected(String newSelectedRealm) {
        mCallback.updateLanguageChooserVisibility();
        //TODO notifyNewRealmHasBeenSelected
    }

    public interface LanguageSelectionFragmentListener {
        public void onLocaleSelected(String newLocale);

        public void updateLanguageChooserVisibility();
    }

}
