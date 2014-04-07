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
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.jorge.lolin1.R;
import org.jorge.lolin1.func.champs.ChampionManager;
import org.jorge.lolin1.func.champs.models.Champion;
import org.jorge.lolin1.io.local.FileManager;
import org.jorge.lolin1.io.local.JsonManager;
import org.jorge.lolin1.io.net.HTTPServicesProvider;
import org.jorge.lolin1.ui.frags.SplashLogFragment;
import org.jorge.lolin1.utils.LoLin1Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class SplashActivity extends Activity {

    private final int NONE_YET = 0, ALLOW = 1, DISALLOW = 2;
    private int dataAllowance = NONE_YET;
    private SplashLogFragment LOG_FRAGMENT;

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
                            Log.wtf("debug", e.getClass().getName(), e);
                        }
                    }
                };

                workerThread.run();
                delayerThread.run();

                try {
                    countDownLatch.await();
                }
                catch (InterruptedException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
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
                                        SplashActivity.this.performPostUpdateOperations(realm,
                                                newVersion);
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
                        Log.wtf("debug", e.getClass().getName(), e);
                    }


                    break;
                case ALLOW:
                    if (runUpdate(server, realm, localesInThisRealm, newVersion)) {
                        performPostUpdateOperations(realm, newVersion);
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
                performPostUpdateOperations(realm, newVersion);
            }
        }
    }

    private void performPostUpdateOperations(String realm, String newVersion) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String currentVersion = preferences.getString("pref_version_" + realm, "0");
        File root = getApplicationContext().getExternalFilesDir(
                LoLin1Utils.getString(getApplicationContext(), "content_folder_name", null));
        File lastVersionFolder = new File(root + "/" + realm + "-" + currentVersion);
        if (lastVersionFolder.exists()) {
            FileManager.recursiveDelete(lastVersionFolder);
        }
        PreferenceManager
                .getDefaultSharedPreferences(
                        getApplicationContext()).edit()
                .putString("pref_version_" + realm, newVersion)
                .commit();
        //TODO Assign the champions
    }

    private Boolean runUpdate(String server, String realm, String[] localesInThisRealm,
                              String newVersion) {
        String cdn = null;
        LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_allocating_file_structure",
                                null) + " " + realm +
                        LoLin1Utils.getString(getApplicationContext(), "progress_character", null)
        );
        File root = getApplicationContext().getExternalFilesDir(
                LoLin1Utils.getString(getApplicationContext(), "content_folder_name", null));
        File previouslyAttemptedUpdateFolder = new File(root + "/" + realm + "-" + newVersion);
        if (previouslyAttemptedUpdateFolder.exists() &&
                !FileManager.recursiveDelete(previouslyAttemptedUpdateFolder)) {
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
            return Boolean.FALSE;
        }
        LOG_FRAGMENT.appendToSameLine(
                LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
        for (String locale : localesInThisRealm) {
            String bustString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            "/" + "bust" + "/", splashString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            "/" + "splash" + "/", spellString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            "/" + "spell" + "/", passiveString =
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            "/" + "passive" + "/";
            File bust = new File(bustString), splash = new File(splashString), spell =
                    new File(spellString), passive = new File(passiveString);
            if (!bust.mkdirs() || !splash.mkdirs() || !spell.mkdirs() || !passive.mkdirs()) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            LOG_FRAGMENT.appendToNewLine(
                    LoLin1Utils.getString(getApplicationContext(), "list_download", null) + " " +
                            realm + "." + locale + LoLin1Utils
                            .getString(getApplicationContext(), "progress_character", null)
            );
            InputStream dataStream;
            String dataStreamAsString;
            try {
                dataStream = HTTPServicesProvider.performListRequest(server, realm, locale);
                dataStreamAsString = LoLin1Utils.inputStreamAsString(dataStream);
                if (!JsonManager.getResponseStatus(dataStreamAsString)) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                    null)
                    );
                    return Boolean.FALSE;
                }
            }
            catch (IOException | URISyntaxException | HTTPServicesProvider.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            File dataFile = new File(
                    root.getPath() + "/" + realm + "-" + newVersion + "/" + locale + "/" +
                            LoLin1Utils
                                    .getString(getApplicationContext(), "list_file_name", null)
            );
            try {
                FileManager
                        .writeStringToFile(dataStreamAsString, dataFile);
            }
            catch (IOException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
            if (cdn == null) {
                LOG_FRAGMENT.appendToNewLine(
                        LoLin1Utils.getString(getApplicationContext(), "cdn_download", null) + " " +
                                realm + LoLin1Utils
                                .getString(getApplicationContext(), "progress_character", null)
                );
                try {
                    String cdnResponse = HTTPServicesProvider.performCdnRequest(server, realm);
                    if (JsonManager.getResponseStatus(cdnResponse)) {
                        cdn = JsonManager.getStringAttribute(cdnResponse,
                                LoLin1Utils.getString(getApplicationContext(), "cdn_key", null));
                    }
                    else {
                        LOG_FRAGMENT.appendToSameLine(
                                LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                        null)
                        );
                        return Boolean.FALSE;
                    }
                }
                catch (HTTPServicesProvider.ServerIsCheckingException | URISyntaxException | IOException e) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                    null)
                    );
                    return Boolean.FALSE;
                }
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(
                                getApplicationContext(), "update_task_finished", null)
                );
            }
            LOG_FRAGMENT.appendToNewLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_realm_data_download",
                            null) +
                            " " +
                            realm + "." + locale + LoLin1Utils
                            .getString(getApplicationContext(), "progress_character", null)
            );
            Collection<Champion> champs = ChampionManager.getInstance().buildChampions(JsonManager
                    .getStringAttribute(dataStreamAsString, LoLin1Utils
                            .getString(getApplicationContext(), "champion_list_key", null)));
            if (champs.isEmpty()) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            for (Champion champion : champs) {
                String bustImageName = champion.getImageName(), passiveImageName =
                        champion.getPassiveImageName(), simplifiedName =
                        champion.getSimplifiedName();
                String[] skins = champion.getSkins(), spellImageNames =
                        champion.getSpellImageNames();
                try {
                    HTTPServicesProvider.downloadFile(
                            cdn + "/" + newVersion + "/" + "img" + "/" + LoLin1Utils
                                    .getString(getApplicationContext(), "bust_remote_folder",
                                            null) + "/" + bustImageName,
                            new File(bustString + bustImageName)
                    );
                    HTTPServicesProvider.downloadFile(
                            cdn + "/" + newVersion + "/" + "img" + "/" + LoLin1Utils
                                    .getString(getApplicationContext(), "passive_remote_folder",
                                            null) + "/" + passiveImageName,
                            new File(passiveString + passiveImageName)
                    );
                    //TODO Continue here downloading the spells
                }
                catch (IOException e) {
                    Log.wtf("debug", e.getClass().getName(), e);
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils
                                    .getString(getApplicationContext(), "update_fatal_error", null)
                    );
                    return Boolean.FALSE;
                }
            }
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
        }
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
                        HTTPServicesProvider.performVersionRequest(server, realm));
            }
            catch (IOException | URISyntaxException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_fatal_error",
                                null));
                return;
            }
            catch (HTTPServicesProvider.ServerIsCheckingException e) {
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
                Log.e("debug", e.getClass().getName(), e);
            }
            if (Integer.parseInt(newVersion.replaceAll("[\\D]", "")) > Integer.parseInt(
                    preferences.getString("pref_version_" + realm, "0").replaceAll("[\\D]", ""))) {
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
            Log.wtf("debug", e.getClass().getName(), e);
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
                getContentInputStream = HTTPServicesProvider.performGetRequest(target);
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
                Log.wtf("debug", e.getClass().getName(), e);
            }
            catch (HTTPServicesProvider.ServerIsCheckingException e) {
                //Server is busy checking for updates, so look for a new one
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