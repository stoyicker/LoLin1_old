package org.jorge.lolin1.ui.frags;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;

import org.jorge.lolin1.R;
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
 */
public class WebViewerFragment extends ProgressFragment {

    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private String mUrl = null;

    //This method is indeed used when instantiating the fragment through XML
    @SuppressWarnings("UnusedDeclaration")
    public WebViewerFragment() {
    }

    public WebViewerFragment(String url) {
        mUrl = url;
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
            mWebView = null;
        }

        View ret;
        ret = inflater.inflate(R.layout.fragment_web_viewer, container,
                Boolean.FALSE);
        mWebView = (WebView) ret.findViewById(R.id.web_view);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new InnerWebViewClient());
        mIsWebViewAvailable = Boolean.TRUE;
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(Boolean.TRUE);
        settings.setLoadWithOverviewMode(Boolean.TRUE);
        settings.setUseWideViewPort(Boolean.TRUE);
        settings.setBuiltInZoomControls(Boolean.TRUE);
        settings.setLoadsImagesAutomatically(Boolean.TRUE);
        settings.setJavaScriptCanOpenWindowsAutomatically(Boolean.TRUE);

        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
        }

        if (!LoLin1Utils.isInternetReachable(getActivity())) {
            ret = inflater.inflate(R.layout.fragment_web_viewer_no_internet, container,
                    Boolean.FALSE);
        }

        if (mUrl != null) {
            loadUrl(mUrl);
        }

        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();
        setContentShown(Boolean.TRUE);
    }

    /**
     * Convenience method for loading a url.
     *
     * @param url
     */
    public void loadUrl(String url) {
        if (mIsWebViewAvailable) {
            getWebView().loadUrl(mUrl = url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void onResume() {
        if (mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
        }
        super.onDestroyView();
    }

    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    public Boolean tryToGoBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private class InnerWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                WebViewerFragment.this.setContentShown(Boolean.FALSE);
            }
            catch (IllegalStateException ex) {
                //Content view not yet created (too quick user)!
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return Boolean.TRUE;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                WebViewerFragment.this.setContentShown(Boolean.TRUE);
            }
            catch (IllegalStateException ex) {
                //Means that the webpage has been closed too quickly, and thus the host
                // activity is already finished, so just ignore this new call to onPageFinished(...)
            }
        }

        /**
         * Report an error to the host application. These errors are unrecoverable
         * (i.e. the main resource is unavailable). The errorCode parameter
         * corresponds to one of the ERROR_* constants.
         *
         * @param view        The WebView that is initiating the callback.
         * @param errorCode   The error code corresponding to an ERROR_* value.
         * @param description A String describing the error.
         * @param failingUrl  The url that failed to load.
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            final String msg = LoLin1Utils.getString(getActivity(), "error_no_connection", null);
            (getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), msg,
                            Toast.LENGTH_SHORT).show();
                }
            });
            setContentShown(Boolean.TRUE);
        }
    }
}
