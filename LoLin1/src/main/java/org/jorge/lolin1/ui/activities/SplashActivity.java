package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.jorge.lolin1.R;
import org.jorge.lolin1.ui.frags.SplashLogFragment;

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

        simulateProgressBarLoad();
    }

    private void simulateProgressBarLoad() {
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
             * Runs on the UI thread after {@link #publishProgress} is invoked.
             * The specified values are the values passed to {@link #publishProgress}.
             *
             * @param values The values indicating progress.
             * @see #publishProgress
             * @see #doInBackground
             */
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                LOG_FRAGMENT.appendToSameLine(
                        values[0] + "" + values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0] + values[0] + values[0] + values[0] + values[0] +
                                values[0]
                );
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
                int iterations = 10;
                for (int i = 0; i < iterations; i++) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }
                    publishProgress(i);
                }
                try {
                    //Sleep a little bit so I can see the result
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }
        }.execute();
    }

    private void launchNewsReader() {
        final Intent newsIntent = new Intent(getApplicationContext(), NewsReaderActivity.class);
        finish();
        startActivity(newsIntent);
    }

}

//FUTURE Remove <item name="news_reader" type="layout">@layout/news_double_pane</item><item name="surr_reader" type="layout">@layout/surr_double_pane</item><bool name="feed_has_two_panes">true</bool> from values-land/layouts.xml to not to see the double layout on devices which are not large enough