package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.net.HttpServiceProvider;
import org.jorge.lolin1.ui.frags.SplashLogFragment;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class SplashActivity extends Activity {

    private SplashLogFragment LOG_FRAGMENT;

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ((SmoothProgressBar) findViewById(R.id.fragment_splash_progress_bar_view))
                .setIndeterminateDrawable(
                        new SmoothProgressDrawable.Builder(getApplicationContext())
                                .color(getApplicationContext().getResources()
                                        .getColor(R.color.theme_strong_orange))
                                .interpolator(new AccelerateDecelerateInterpolator()).build()
                );

        LOG_FRAGMENT =
                (SplashLogFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_splash_log_text);

        load();
    }

    private void load() {
        new AsyncTask<Void, Integer, Void>() {
            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.</p>
             * <p/>
             * <p>This method won't be invoked if the task was cancelled.</p>
             *
             * @param aVoid The result of the operation computed by {@link #doInBackground}.
             * @see #onPreExecute
             * @see #doInBackground
             * @see #onCancelled(Object)
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                launchNewsReader();
            }

            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p/>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param params The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Void doInBackground(Void... params) {
                final CountDownLatch countDownLatch = new CountDownLatch(2);

                Runnable workerThread = new Runnable() {
                    /**
                     * Calls the <code>run()</code> method of the Runnable object the receiver
                     * holds. If no Runnable is set, does nothing.
                     *
                     * @see Thread#start
                     */
                    @Override
                    public void run() {
                        runUpdate();
                        countDownLatch.countDown();
                    }
                };

                Runnable delayerThread = new Runnable() {
                    /**
                     * Calls the <code>run()</code> method of the Runnable object the receiver
                     * holds. If no Runnable is set, does nothing.
                     *
                     * @see Thread#start
                     */
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(LoLin1Utils
                                    .getInt(getApplicationContext(),
                                            "minimum_splash_showtime_mills", 0));
                            countDownLatch.countDown();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                };

                workerThread.run();
                delayerThread.run();

                try {
                    countDownLatch.await();
                }
                catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }

                return null;
            }
        }.execute();
    }

    private void runUpdate() {
        String[] dataProviders =
                LoLin1Utils
                        .getStringArray(getApplicationContext(), "data_providers",
                                null);

        if (LoLin1Utils.isInternetReachable(getApplicationContext())) {
            int firstIndex = new Random().nextInt(dataProviders.length), index = firstIndex;
            Boolean upServerFound = Boolean.FALSE;
            String target;
            InputStream getContentInputStream = null;
            do {
                try {
                    target = dataProviders[index];
                    getContentInputStream = HttpServiceProvider.performGetRequest(target);
                    String content = LoLin1Utils.inputStreamAsString(getContentInputStream);
                    if (!content.contains(LoLin1Utils.getString(getApplicationContext(),
                            "provider_application_error_identifier", null)) &&
                            !content.contains(
                                    LoLin1Utils.getString(getApplicationContext(),
                                            "provider_application_maintenance_identifier", null)
                            )) {
                        upServerFound = Boolean.TRUE;
                    }
                }
                catch (IOException | URISyntaxException e) {
                    e.printStackTrace(System.err);
                }
                if (!upServerFound) {
                    index++;
                    if (index >= dataProviders.length) {
                        index = 0;
                    }
                    if (index == firstIndex) {
                        LOG_FRAGMENT.appendToNewLine(
                                LoLin1Utils.getString(getApplicationContext(),
                                        "no_providers_up", null)
                        );
                        return;
                    }
                }
            }
            while (!upServerFound);
            LOG_FRAGMENT.appendToNewLine(
                    LoLin1Utils.inputStreamAsString(getContentInputStream));
        }
        else {
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "no_connection_on_splash",
                            null));
        }
    }

    private void launchNewsReader() {
        final Intent newsIntent = new Intent(getApplicationContext(), NewsReaderActivity.class);
        finish();
        startActivity(newsIntent);
    }

}

//FUTURE Remove <item name="news_reader" type="layout">@layout/news_double_pane</item><item name="surr_reader" type="layout">@layout/surr_double_pane</item><bool name="feed_has_two_panes">true</bool> from values-land/layouts.xml to not to see the double layout on devices which are not large enough