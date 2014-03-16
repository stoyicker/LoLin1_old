package org.jorge.lolin1.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.jorge.lolin1.R;

public class SplashFragmentActivity extends Activity {

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        onSplashRun();
    }

    private void onSplashRun() {
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        final Intent newsIntent = new Intent(this, NewsReaderActivity.class);
        startActivity(newsIntent);
        finish();
    }
}
