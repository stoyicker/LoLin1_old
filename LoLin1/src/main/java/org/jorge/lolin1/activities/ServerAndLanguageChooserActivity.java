package org.jorge.lolin1.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.LanguageSelectionFragment;
import org.jorge.lolin1.frags.RealmChooserFragment;
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
 * Created by JorgeAntonio on 18/03/14.
 */
public class ServerAndLanguageChooserActivity extends Activity
        implements RealmChooserFragment.RealmChooserFragmentListener,
        LanguageSelectionFragment.LanguageSelectionFragmentListener {

    private LanguageSelectionFragment LANGUAGE_CHOOSER_FRAGMENT;
    private String currentlySelectedRealm;

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(android.net.Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p/>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method. If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_and_language_chooser);

        FragmentManager fragmentManager = getFragmentManager();

        LANGUAGE_CHOOSER_FRAGMENT =
                (LanguageSelectionFragment) fragmentManager
                        .findFragmentById(R.id.fragment_language_selection_text);

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .hide(LANGUAGE_CHOOSER_FRAGMENT).addToBackStack(null).commit();

        fragmentManager.executePendingTransactions();
    }

    @Override
    public void onLocaleSelected(String newLocale) {
        enableAcceptButton();
    }

    private void enableAcceptButton() {
        //TODO Enable the button if it's not already enabled

        //TODO Move this stuff to the onPressed listener of the button
        LoLin1Utils.setRealm(currentlySelectedRealm);
        LoLin1Utils.setLocale(getBaseContext(), newLocale);
        showFeedbackToast();
        finish();
    }


    @Override
    public void updateLanguageChooserVisibility() {
        if (LANGUAGE_CHOOSER_FRAGMENT.isHidden()) {
            getFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .show(LANGUAGE_CHOOSER_FRAGMENT).addToBackStack(null).commit();
            getFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onRealmSelected(String newSelectedRealm) {
        currentlySelectedRealm = newSelectedRealm;
        LANGUAGE_CHOOSER_FRAGMENT.notifyNewRealmHasBeenSelected(currentlySelectedRealm);
    }

    public void showFeedbackToast() {
        final String msg =
                LoLin1Utils.getString(getApplicationContext(), "initial_configuration_saved",
                        "SETTINGS_SAVED_DEFAULT");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
