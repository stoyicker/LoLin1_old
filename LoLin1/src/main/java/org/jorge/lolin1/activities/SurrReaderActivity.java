package org.jorge.lolin1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.jorge.lolin1.R;
import org.jorge.lolin1.feeds.surr.SurrEntry;
import org.jorge.lolin1.frags.SurrListFragment;
import org.jorge.lolin1.frags.WebViewerFragment;
import org.jorge.lolin1.io.db.SQLiteBridge;

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
 * Created by JorgeAntonio on 09/02/14.
 */
public class SurrReaderActivity extends DrawerLayoutFragmentActivity implements
        SurrListFragment.SurrListFragmentListener {
    private static Boolean isDualPane = Boolean.FALSE;
    private SurrListFragment SURR_FRAGMENT;
    private WebViewerFragment WEB_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Boolean wasSavedInstanceStateNull = savedInstanceState == null;
        if (wasSavedInstanceStateNull) {
            savedInstanceState = new Bundle();
        }
        savedInstanceState.putInt("layout", R.layout.surr_reader);

        super.onCreate(savedInstanceState);

        SURR_FRAGMENT =
                (SurrListFragment) getFragmentManager().findFragmentById(R.id.fragment_list);
        WEB_FRAGMENT =
                (WebViewerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_web_viewer);

        getFragmentManager().executePendingTransactions();
        getSupportFragmentManager().executePendingTransactions();

        isDualPane = WEB_FRAGMENT != null && WEB_FRAGMENT.getView() != null &&
                WEB_FRAGMENT.getView().getVisibility() == View.VISIBLE;
        if (wasSavedInstanceStateNull) {
            savedInstanceState = null;
        }
        restoreState(savedInstanceState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int index = savedInstanceState.getInt("index", 0);
            SURR_FRAGMENT.setSelection(index);
            onSurrArticleSelected(index);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index", SurrListFragment.getSelectedIndex());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSurrArticleSelected(int index) {
        showUrlInWebViewerFragment(index);
    }

    private void showUrlInWebViewerFragment(int index) {
        ArrayList<SurrEntry> surrs;
        if (isDualPane) {
            if (isDualPane) {
                if (!(surrs = SQLiteBridge.getSingleton().getSurrs()).isEmpty()) {
                    WEB_FRAGMENT.loadUrl(surrs.get(index).getLink());
                }
            }
        }
        else {
            Intent singleViewIntent = new Intent(this, WebViewerActivity.class);
            singleViewIntent.putExtra("index", index);
            startActivity(singleViewIntent);
        }
    }
}
