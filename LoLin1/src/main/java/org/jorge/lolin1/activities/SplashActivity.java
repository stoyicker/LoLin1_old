package org.jorge.lolin1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.jorge.lolin1.R;

public class SplashActivity extends Activity {

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key.  The default implementation simply finishes the current activity,
     * but you can override this to do whatever you want.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        launchNewsReader();
    }

    private void launchNewsReader() {
        final Intent newsIntent = new Intent(this, NewsReaderActivity.class);
        startActivity(newsIntent);
        finish();
    }
}

//FUTURE Remote 	<item name="news_reader" type="layout">@layout/news_double_pane</item><item name="surr_reader" type="layout">@layout/surr_double_pane</item><bool name="feed_has_two_panes">true</bool> from values-land/layouts.xml