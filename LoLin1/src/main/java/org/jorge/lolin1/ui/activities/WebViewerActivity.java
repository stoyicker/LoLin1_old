package org.jorge.lolin1.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.feeds.BaseEntry;
import org.jorge.lolin1.io.db.SQLiteDAO;
import org.jorge.lolin1.ui.frags.WebViewerFragment;

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
 * Created by JorgeAntonio on 07/02/14.
 */
public final class WebViewerActivity extends FragmentActivity {

    WebViewerFragment webViewerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.feed_has_two_panes)) {
            finish();
            return;
        }

        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);

        ArrayList<BaseEntry> elements = new ArrayList<>();

        switch (DrawerLayoutFragmentActivity.getLastSelectedNavDavIndex()) {
            case 0:
                elements.addAll(SQLiteDAO.getSingleton().getNews());
                break;
            case 5:
                elements.addAll(SQLiteDAO.getSingleton().getSurrs());
                break;
            default:
                Log.wtf("debug",
                        "Should never happen - NewsReaderActivity.getLastSelectedNavDavIndex() is " +
                                DrawerLayoutFragmentActivity.getLastSelectedNavDavIndex()
                );
        }

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content,
                        webViewerFragment = new WebViewerFragment(
                                elements.isEmpty() ? null :
                                        elements.get(getIntent().getExtras().getInt("index", 0))
                                                .getLink()
                        )
                )
                .addToBackStack("").commit();

        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if (!webViewerFragment.tryToGoBack()) {
            if (!getResources().getBoolean(R.bool.feed_has_two_panes)) {
                protectAgainstWindowLeaks();
                finish();
            }
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
        getSupportFragmentManager().beginTransaction().remove(webViewerFragment).addToBackStack("")
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
