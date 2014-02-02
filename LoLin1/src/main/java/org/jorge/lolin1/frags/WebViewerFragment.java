package org.jorge.lolin1.frags;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewFragment;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jorge.lolin1.R;
import org.jorge.lolin1.custom.ProgressSpinnerWebViewClient;
import org.jorge.lolin1.utils.Utils;

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
 * Created by JorgeAntonio on 02/02/14.
 */
public class WebViewerFragment extends WebViewFragment {

    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = (View) inflater.inflate(R.layout.web_viewer_layout, container, false);
        webView = (WebView) mainView.findViewById(R.id.web_viewer);
        webView.loadUrl("http://www.google.com");

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(Boolean.TRUE);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(Boolean.TRUE);
        settings.setUseWideViewPort(Boolean.TRUE);
        settings.setLoadsImagesAutomatically(Boolean.TRUE);
        settings.setJavaScriptCanOpenWindowsAutomatically(Boolean.TRUE);
        ProgressBar progressBar = (ProgressBar) mainView.findViewById(R.id.web_viewer_progress_bar);
        webView.setWebViewClient(new ProgressSpinnerWebViewClient(progressBar));
        webView.loadUrl(getArguments().getString("url"));

        return mainView;
    }

    public void showUrl(String url) {
        Activity activity = getActivity();
        if (Utils.isInternetReachable(activity)) {
            webView.loadUrl(url);
        }
        else {
            final String msg = Utils.getString(activity, "error_no_internet", "ERROR");
            (activity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
