package org.jorge.lolin1.ui.frags;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.jorge.lolin1.R;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.util.ArrayList;
import java.util.Arrays;

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
 * Created by JorgeAntonio on 06/01/14.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        final ListPreference serverPreference = (ListPreference) findPreference(
                LoLin1Utils.getString(getActivity().getApplicationContext(), "pref_title_server",
                        null)
        );
        serverPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final ListPreference langPreference = (ListPreference) findPreference(
                        LoLin1Utils.getString(getActivity(), "pref_title_lang",
                                null)
                );
                final Context context = getActivity();
                final String chosenServer = (String) newValue;
                String[] targetArray;

                if (!PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("pref_title_server", "dummy_helper").toUpperCase()
                        .contentEquals(chosenServer.toUpperCase())) {
                    LoLin1Utils.setRealm(context, chosenServer.toLowerCase());
                    LoLin1Utils.setLocale(context, LoLin1Utils.getStringArray(context,
                            LoLin1Utils.getString(context,
                                    "realm_to_language_list_prefix", null) +
                                    chosenServer.toLowerCase() +
                                    LoLin1Utils.getString(context,
                                            "language_to_simplified_suffix", null), null
                    )[0]);

                    targetArray = LoLin1Utils
                            .getStringArray(context, "lang_" + chosenServer.toLowerCase(), null);

                    langPreference.setEntries(targetArray);
                    langPreference.setEntryValues(targetArray);
                    langPreference.setValue(targetArray[0]);
                    getActivity().recreate();
                }

                return Boolean.TRUE;
            }
        });

        final ListPreference langPreference = (ListPreference) findPreference(
                LoLin1Utils.getString(getActivity(), "pref_title_lang", null)
        );
        langPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean ret = Boolean.TRUE;
                String chosenLang = (String) newValue, currentLocale =
                        LoLin1Utils.getLocale(getActivity().getApplicationContext()), newAsLocale;
                int langIndex =
                        new ArrayList<>(Arrays.asList(
                                LoLin1Utils.getStringArray(getActivity(), "langs", null)))
                                .indexOf(chosenLang);
                if (langIndex != -1 &&
                        !currentLocale.toUpperCase().contentEquals((newAsLocale =
                                LoLin1Utils.getStringArray(getActivity().getApplicationContext(),
                                        "langs_simplified",
                                        null)[langIndex]
                        ).toUpperCase())) {
                    LoLin1Utils.setLocale(getActivity().getApplicationContext(), newAsLocale);
                    getActivity().recreate();
                }

                return ret;
            }
        });
    }
}
