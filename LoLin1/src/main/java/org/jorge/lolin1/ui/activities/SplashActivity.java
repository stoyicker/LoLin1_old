package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.jorge.lolin1.R;
import org.jorge.lolin1.io.net.HttpServiceProvider;
import org.jorge.lolin1.ui.frags.SplashLogFragment;
import org.jorge.lolin1.utils.LoLin1Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class SplashActivity extends Activity {

    private final int NONE_YET = 0, ALLOW = 1, DISALLOW = 2;
    private int dataAllowance = NONE_YET;
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
        final String[] dataProviders =
                LoLin1Utils
                        .getStringArray(getApplicationContext(), "data_providers",
                                null);

        if (LoLin1Utils.isInternetReachable(getApplicationContext())) {
            String target;
            if (!(target = connectToOneOf(dataProviders)).contentEquals("null")) {
                startProcedure(target);
            }
        }
        else {
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "no_connection_on_splash",
                            null));
        }
    }

    private void askIfOnMobileConnectionAndRunDownload(final String server, final String realm,
                                                       final String[] localesInThisRealm,
                                                       final String newVersion) {
        final CountDownLatch alertDialogLatch = new CountDownLatch(1);

        if (((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()) {
            switch (dataAllowance) {
                case NONE_YET:
                    final AlertDialog.Builder alertDialogBuilder =
                            new AlertDialog.Builder(SplashActivity.this,
                                    AlertDialog.THEME_HOLO_DARK);

                    alertDialogBuilder.setTitle(
                            LoLin1Utils
                                    .getString(getApplicationContext(), "delay_update_dialog_title",
                                            null)
                    );

                    alertDialogBuilder.setMessage(LoLin1Utils
                            .getString(getApplicationContext(), "delay_update_dialog_content",
                                    null))
                            .setPositiveButton(LoLin1Utils.getString(getApplicationContext(),
                                    "delay_update_dialog_positive_button",
                                    null), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LOG_FRAGMENT.appendToSameLine(
                                            LoLin1Utils.getString(getApplicationContext(),
                                                    "update_delayed", null)
                                    );
                                    dataAllowance = DISALLOW;
                                    alertDialogLatch.countDown();
                                }
                            })
                            .setNegativeButton(LoLin1Utils.getString(getApplicationContext(),
                                    "delay_update_dialog_negative_button",
                                    null), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (runUpdate(server, realm, localesInThisRealm, newVersion)) {
                                        PreferenceManager
                                                .getDefaultSharedPreferences(
                                                        getApplicationContext()).edit()
                                                .putString("pref_version_" + realm, newVersion)
                                                .commit();
                                    }
                                    dataAllowance = ALLOW;
                                    alertDialogLatch.countDown();
                                }
                            });

                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialogBuilder.show();
                        }
                    });
                    try {
                        alertDialogLatch.await();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    }


                    break;
                case ALLOW:
                    if (runUpdate(server, realm, localesInThisRealm, newVersion)) {
                        PreferenceManager
                                .getDefaultSharedPreferences(
                                        getApplicationContext()).edit()
                                .putString("pref_version_" + realm, newVersion)
                                .commit();
                    }
                    alertDialogLatch.countDown();
                    break;
                case DISALLOW:
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(),
                                    "update_delayed", null)
                    );
                    alertDialogLatch.countDown();
                    break;
            }
        }
        else {
            if (runUpdate(server, realm, localesInThisRealm, newVersion)) {
                PreferenceManager
                        .getDefaultSharedPreferences(
                                getApplicationContext()).edit()
                        .putString("pref_version_" + realm, newVersion)
                        .commit();
            }
        }
    }

    /**
     * The success of the update for all locales in this realm.
     *
     * @param server
     * @param realm
     * @param localesInThisRealm
     * @return
     */
    private Boolean runUpdate(String server, String realm, String[] localesInThisRealm,
                              String newVersion) {
        LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_allocating_file_structure",
                                null) + " " + realm +
                        LoLin1Utils.getString(getApplicationContext(), "progress_character", null)
        );
        File root = getApplicationContext().getExternalFilesDir(
                LoLin1Utils.getString(getApplicationContext(), "content_folder_name", null));
        File previouslyAttemptedUpdateFolder = new File(root + "/" + realm + "-" + newVersion);
        if (previouslyAttemptedUpdateFolder.exists() &&
                !LoLin1Utils.recursiveDelete(previouslyAttemptedUpdateFolder)) {
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
            return Boolean.FALSE;
        }
        for (String locale : localesInThisRealm) {
            String bustString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" + "image" +
                            "/" + "bust" + "/", splashString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" + "image" +
                            "/" + "splash" + "/", spellString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" + "image" +
                            "/" + "spell" + "/", passiveString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" + "image" +
                            "/" + "passive" + "/";
            File bust = new File(bustString), splash = new File(splashString), spell =
                    new File(spellString), passive = new File(passiveString);
            if (!bust.mkdirs() || !splash.mkdirs() || !spell.mkdirs() || !passive.mkdirs()) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
            LOG_FRAGMENT.appendToNewLine(
                    LoLin1Utils.getString(getApplicationContext(), "list_download", null) + " " +
                            realm + "." + locale + LoLin1Utils
                            .getString(getApplicationContext(), "progress_character", null)
            );
            InputStream dataStream;
            try {
                dataStream = HttpServiceProvider.performGetRequest(
                        server + LoLin1Utils
                                .getString(getApplicationContext(), "service_list", null) + realm +
                                "/" + locale
                );
            }
            catch (IOException | URISyntaxException | HttpServiceProvider.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            if (!LoLin1Utils.writeInputStreamToFile(dataStream, new File(
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils
                                    .getString(getApplicationContext(), "list_file_name", null)
            ))) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
        }
        //TODO Run the update in the current thread
        return Boolean.TRUE;
    }

    private void startProcedure(String server) {
        final CountDownLatch networkOperationsLatch = new CountDownLatch(1);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String[] realms =
                LoLin1Utils.getStringArray(getApplicationContext(), "servers", null);
        for (String realm : realms) {
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "update_pre_version_check", null) +
                    " " + realm.toLowerCase() +
                    LoLin1Utils.getString(getApplicationContext(), "progress_character", null));
            String newVersion;
            JSONObject newVersionAsJSON;
            try {
                newVersion = LoLin1Utils.inputStreamAsString(
                        HttpServiceProvider.performVersionRequest(server, realm));
            }
            catch (IOException | URISyntaxException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_fatal_error",
                                null));
                return;
            }
            catch (HttpServiceProvider.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_server_is_updating",
                                null));
                return;
            }
            try {
                newVersionAsJSON = new JSONObject(newVersion);
                newVersion = newVersionAsJSON.getString("version");
            }
            catch (JSONException e) {
                e.printStackTrace(System.err);
            }
            if (!newVersion.contentEquals(preferences.getString("pref_version_" + realm, ""))) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_new_version_found", null));
                String[] localesInThisRealm =
                        LoLin1Utils.getStringArray(getApplicationContext(), LoLin1Utils.getString(
                                getApplicationContext(), "realm_to_language_list_prefix",
                                null) + realm.toLowerCase() +
                                LoLin1Utils.getString(getApplicationContext(),
                                        "language_to_simplified_suffix", null), null);
                askIfOnMobileConnectionAndRunDownload(server, realm, localesInThisRealm,
                        newVersion);
            }
            else {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_no_new_version", null));
            }
            networkOperationsLatch.countDown();
        }


        try {
            networkOperationsLatch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    private String connectToOneOf(String[] dataProviders) {
        int firstIndex = new Random().nextInt(dataProviders.length), index = firstIndex;
        Boolean upServerFound = Boolean.FALSE;
        String target = "null";
        InputStream getContentInputStream;
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
            catch (HttpServiceProvider.ServerIsCheckingException e) {
                //Server is busy checking for updates, so do nothing, simply rotate
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
                    return "null";
                }
            }
        }
        while (!upServerFound);

        return target;
    }

    private void launchNewsReader() {
        final Intent newsIntent = new Intent(getApplicationContext(), NewsReaderActivity.class);
        finish();
        startActivity(newsIntent);
    }
}

//FUTURE Remove <item name="news_reader" type="layout">@layout/news_double_pane</item><item name="surr_reader" type="layout">@layout/surr_double_pane</item><bool name="feed_has_two_panes">true</bool> from values-land/layouts.xml to not to see the double layout on devices which are not large enough