package org.jorge.lolin1.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.BaseEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.ui.frags.WebViewerProgressFragment;

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
 * Created by Jorge Antonio Diaz-Benito Soriano on 07/02/14.
 */
public final class WebViewerActivity extends FragmentActivity {

    WebViewerProgressFragment webViewerProgressFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);

        ArrayList<BaseEntry> elements = new ArrayList<>();

        switch (DrawerLayoutFragmentActivity.getLastSelectedNavDrawerIndex()) {
            case 0:
                elements.addAll(SQLiteDAO.getSingleton().getNews());
                break;
            case 5:
                elements.addAll(SQLiteDAO.getSingleton().getSurrs());
                break;
            default:
                Crashlytics.log(Log.ERROR, "debug",
                        "Should never happen - DrawerLayoutFragmentActivity.getLastSelectedNavDrawIndex() is " +
                                DrawerLayoutFragmentActivity.getLastSelectedNavDrawerIndex()
                );
        }

        webViewerProgressFragment = new WebViewerProgressFragment();
        Bundle args = new Bundle();
        args.putString(WebViewerProgressFragment.KEY_URL, elements.isEmpty() ? null :
                elements.get(getIntent().getExtras().getInt("index", 0))
                        .getLink());

        getFragmentManager().beginTransaction()
                .add(android.R.id.content,
                        webViewerProgressFragment).addToBackStack(
                "").commit();

        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if (!webViewerProgressFragment.tryToGoBack()) {
            protectAgainstWindowLeaks();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up button
                protectAgainstWindowLeaks();
                finish();
                return true;
            case R.id.action_settings:
                startActivity(
                        new Intent(getApplicationContext(), SettingsPreferenceActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void protectAgainstWindowLeaks() {
        getFragmentManager().beginTransaction().remove(webViewerProgressFragment)
                .addToBackStack("")
                .commit();
        getFragmentManager().executePendingTransactions();
    }
}
