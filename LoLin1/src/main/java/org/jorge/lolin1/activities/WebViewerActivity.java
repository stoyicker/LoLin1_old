package org.jorge.lolin1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.jorge.lolin1.R;
import org.jorge.lolin1.frags.WebViewerFragment;
import org.jorge.lolin1.io.db.SQLiteBridge;

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
public class WebViewerActivity extends FragmentActivity {

    WebViewerFragment webViewerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.has_two_panes)) {
            finish();
            return;
        }

        getActionBar().setDisplayHomeAsUpEnabled(Boolean.TRUE);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content,
                        webViewerFragment = new WebViewerFragment(
                                getParent() instanceof NewsReaderActivity ?
                                        SQLiteBridge.getSingleton().getNews()
                                                .get(getIntent().getExtras().getInt("index", 0))
                                                .getLink() :
                                        SQLiteBridge.getSingleton().getSurrs().get(
                                                getIntent().getExtras().getInt("index", 0))
                                                .getLink()))
                .addToBackStack("").commit();

        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        if (!webViewerFragment.succedeedGoingBack()) {
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
                startActivity(new Intent(this, SettingsPreferenceActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.standard, menu);
        return true;
    }

    private void protectAgainstWindowLeaks() {
        getSupportFragmentManager().beginTransaction().remove(webViewerFragment).addToBackStack("")
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
