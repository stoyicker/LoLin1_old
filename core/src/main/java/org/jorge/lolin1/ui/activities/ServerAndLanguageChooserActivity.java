package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.frags.LanguageListFragment;
import org.jorge.lolin1.ui.frags.RealmSelectorFragment;
import org.jorge.lolin1.ui.frags.VerificationFragment;
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
 * Created by Jorge Antonio Diaz-Benito Soriano on 18/03/14.
 */
public final class ServerAndLanguageChooserActivity extends Activity
        implements RealmSelectorFragment.RealmSelectionListener,
        LanguageListFragment.LanguageListFragmentListener,
        VerificationFragment.VerificationFragmentListener {

    private ShowcaseView realmShowcase;
    private LanguageListFragment LANGUAGE_LIST_FRAGMENT;
    private VerificationFragment VERIFICATION_FRAGMENT;
    private String currentlySelectedRealm, currentlySelectedLocale;
    private RealmSelectorFragment REALM_SELECTOR_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_and_language_chooser);

        FragmentManager fragmentManager = getFragmentManager();

        LANGUAGE_LIST_FRAGMENT =
                (LanguageListFragment) fragmentManager
                        .findFragmentById(R.id.fragment_language_list);

        REALM_SELECTOR_FRAGMENT = (RealmSelectorFragment) fragmentManager.findFragmentById(R.id.fragment_realm_list);

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .hide(LANGUAGE_LIST_FRAGMENT).addToBackStack(null).commit();

        fragmentManager.executePendingTransactions();

        VERIFICATION_FRAGMENT = (VerificationFragment) fragmentManager
                .findFragmentById(R.id.fragment_verification);

        REALM_SELECTOR_FRAGMENT.initialSetSelectedIndex(0);

        if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("showcase_realm_done", Boolean.FALSE)) {
            realmShowcase = new ShowcaseView.Builder(this).setContentText(R.string.tutorial_realm_selection_content).setContentTitle(R.string.tutorial_realm_selection_title).setStyle(R.style.CustomShowcaseThemePlusNoButton).setTarget(new ViewTarget(R.id.fragment_realm_list, this)).doNotBlockTouches().build();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (realmShowcase != null) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("showcase_realm_done", Boolean.TRUE).apply();
            realmShowcase.hide();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLocaleSelected(String newLocale) {
        currentlySelectedLocale = newLocale;
        enableVerification();
    }

    @Override
    public String onCurrentlySelectedRealmRequest() {
        return currentlySelectedRealm;
    }

    private void enableVerification() {
        VERIFICATION_FRAGMENT.setButton(Boolean.TRUE);
    }

    private void disableVerification() {
        VERIFICATION_FRAGMENT.setButton(Boolean.FALSE);
    }

    @Override
    public void updateLanguageChooserVisibility() {
        if (LANGUAGE_LIST_FRAGMENT.isHidden()) {
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .show(LANGUAGE_LIST_FRAGMENT).addToBackStack(null).commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onVerificationFired() {
        LoLin1Utils.setRealm(getBaseContext(), currentlySelectedRealm);
        LoLin1Utils.setLocale(getBaseContext(), currentlySelectedLocale);
        final Intent splashIntent = new Intent(getApplicationContext(), SplashActivity.class);
        finish();
        startActivity(splashIntent);
    }

    @Override
    public void onNewRealmSelected() {
        currentlySelectedRealm = REALM_SELECTOR_FRAGMENT.getSelectedRealm();
        disableVerification();
        LANGUAGE_LIST_FRAGMENT.notifyNewRealmHasBeenSelected(currentlySelectedRealm);
    }
}
