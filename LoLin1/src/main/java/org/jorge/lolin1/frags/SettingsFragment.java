package org.jorge.lolin1.frags;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

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
                        "error")
        );
        serverPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final ListPreference langPreference = (ListPreference) findPreference(
                        LoLin1Utils.getString(getActivity(), "pref_title_lang",
                                "error")
                );
                final Context context = getActivity();
                final String chosenServer = (String) newValue;
                int targetArray = -1;

                if (chosenServer
                        .contentEquals(LoLin1Utils.getString(context, "server_na", "error"))) {
                    targetArray = R.array.lang_na;
                }
                else if (chosenServer
                        .contentEquals(LoLin1Utils.getString(context, "server_euw", "error"))) {
                    targetArray = R.array.lang_euw;
                }
                else if (chosenServer
                        .contentEquals(LoLin1Utils.getString(context, "server_eune", "error"))) {
                    targetArray = R.array.lang_eune;
                }
                else if (chosenServer
                        .contentEquals(LoLin1Utils.getString(context, "server_br", "error"))) {
                    targetArray = R.array.lang_br;
                }
                else if (chosenServer
                        .contentEquals(LoLin1Utils.getString(context, "server_tr", "error"))) {
                    targetArray = R.array.lang_tr;
                }

                langPreference.setEntries(targetArray);
                langPreference.setEntryValues(targetArray);
                langPreference.setValue(langPreference.getEntries()[0].toString());

                return Boolean.TRUE;
            }
        });

        final ListPreference langPreference = (ListPreference) findPreference(
                LoLin1Utils.getString(getActivity(), "pref_title_lang",
                        "error")
        );
        langPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean ret = Boolean.FALSE;
                String chosenLang = (String) newValue;
                int langIndex =
                        new ArrayList<>(Arrays.asList(
                                LoLin1Utils.getStringArray(getActivity(), "langs",
                                        new String[]{"error"})
                        ))
                                .indexOf(chosenLang);
                if (langIndex != -1) {
                    LoLin1Utils.setLocale(getActivity().getBaseContext(),
                            LoLin1Utils.getStringArray(getActivity().getApplicationContext(),
                                    "langs_simplified",
                                    new String[]{"error"})[langIndex]
                    );
                    ret = Boolean.TRUE;
                }
                return ret;
            }
        });
    }
}
