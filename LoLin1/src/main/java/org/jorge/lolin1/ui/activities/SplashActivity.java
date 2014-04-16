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
import org.jorge.lolin1.io.net.HTTPServices;
import org.jorge.lolin1.ui.frags.SplashLogFragment;
import org.jorge.lolin1.utils.BoxedBoolean;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class SplashActivity extends Activity {

    private final int NONE_YET = 0, ALLOW = 1, DISALLOW = 2;
    private int dataAllowance = NONE_YET;
    private SplashLogFragment LOG_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((SmoothProgressBar) findViewById(R.id.fragment_splash_progress_bar_view))
                .setIndeterminateDrawable(
                        new SmoothProgressDrawable.Builder(getApplicationContext())
                                .color(getApplicationContext().getResources()
                                        .getColor(R.color.theme_strong_orange))
                                .interpolator(new AccelerateDecelerateInterpolator()).speed(Float
                                .parseFloat(LoLin1Utils.getString(getApplicationContext(),
                                        "splash_progress_bar_speed", null)))
                                .sectionsCount(LoLin1Utils.getInt(getApplicationContext(),
                                        "splash_progress_bar_sections", -1))
                                .build()
                );

        LOG_FRAGMENT =
                (SplashLogFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_splash_log_text);

        load();
    }

    private void load() {
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ChampionManager.getInstance()
                        .setChampions(SplashActivity.this.getApplicationContext());
                launchNewsReader();
            }

            @Override
            protected Void doInBackground(Void... params) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);

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

                workerThread.run();

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
                proceedWithUpdate(target);
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
                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            if (runInitProcedure(server, realm, localesInThisRealm,
                                                    newVersion)) {
                                                SplashActivity.this
                                                        .performPostUpdateOperations(realm,
                                                                newVersion);
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            dataAllowance = ALLOW;
                                            alertDialogLatch.countDown();
                                        }
                                    }.execute();
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
                    if (runInitProcedure(server, realm, localesInThisRealm, newVersion)) {
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
            if (runInitProcedure(server, realm, localesInThisRealm, newVersion)) {
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
        File lastVersionFolder =
                new File(root +
                        LoLin1Utils.getString(getApplicationContext(), "symbol_path_separator",
                                null) + realm +
                        LoLin1Utils.getString(getApplicationContext(), "symbol_hyphen",
                                null) + currentVersion);
        if (lastVersionFolder.exists()) {
            FileManager.recursiveDelete(lastVersionFolder);
        }
        PreferenceManager
                .getDefaultSharedPreferences(
                        getApplicationContext()).edit()
                .putString("pref_version_" + realm, newVersion)
                .commit();
    }

    @Override
    public void onBackPressed() {
    }

    private Boolean runInitProcedure(String server, String realm, String[] localesInThisRealm,
                                     final String newVersion) {
        final String symbol_hyphen = LoLin1Utils.getString(getApplicationContext(), "symbol_hyphen",
                null), pathSeparator =
                LoLin1Utils.getString(getApplicationContext(), "symbol_path_separator",
                        null);
        String cdn = null;
        LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_allocating_file_structure",
                                null) + " " + realm +
                        LoLin1Utils.getString(getApplicationContext(), "progress_character", null)
        );
        File root = getApplicationContext().getExternalFilesDir(
                LoLin1Utils.getString(getApplicationContext(), "content_folder_name", null));
        File previouslyAttemptedUpdateFolder =
                new File(root + pathSeparator + realm + "-" + newVersion);
        if (previouslyAttemptedUpdateFolder.exists() &&
                !FileManager.recursiveDelete(previouslyAttemptedUpdateFolder)) {
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
            return Boolean.FALSE;
        }
        LOG_FRAGMENT.appendToSameLine(
                LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
        for (String locale : localesInThisRealm) {
            final String bustString =
                    root.getPath() + pathSeparator + realm + symbol_hyphen + newVersion +
                            pathSeparator + locale + pathSeparator +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            pathSeparator +
                            LoLin1Utils.getString(getApplicationContext(), "bust_image_folder_name",
                                    null) + pathSeparator, splashString =
                    root.getPath() + pathSeparator + realm + symbol_hyphen + newVersion +
                            pathSeparator + locale + pathSeparator +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            pathSeparator + LoLin1Utils.getString(getApplicationContext(),
                            "splash_image_folder_name",
                            null) + pathSeparator, spellString =
                    root.getPath() + pathSeparator + realm + symbol_hyphen + newVersion +
                            pathSeparator + locale + pathSeparator +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            pathSeparator + LoLin1Utils.getString(getApplicationContext(),
                            "spell_image_folder_name",
                            null) + pathSeparator, passiveString =
                    root.getPath() + pathSeparator + realm + symbol_hyphen + newVersion +
                            pathSeparator + locale + pathSeparator +
                            LoLin1Utils.getString(getApplicationContext(), "champion_image_folder",
                                    null) +
                            pathSeparator + LoLin1Utils
                            .getString(getApplicationContext(), "passive_image_folder_name", null) +
                            pathSeparator;
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
                dataStream = HTTPServices.performListRequest(server, realm, locale);
                dataStreamAsString = LoLin1Utils.inputStreamAsString(dataStream);
                if (!JsonManager.getResponseStatus(dataStreamAsString)) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                    null)
                    );
                    return Boolean.FALSE;
                }
            }
            catch (IOException | URISyntaxException | HTTPServices.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            File dataFile = new File(
                    root.getPath() + pathSeparator + realm + symbol_hyphen + newVersion +
                            pathSeparator + locale + pathSeparator +
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
                    String cdnResponse = HTTPServices.performCdnRequest(server, realm);
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
                catch (HTTPServices.ServerIsCheckingException | URISyntaxException | IOException e) {
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
            final String finalCdn = cdn;
            final BoxedBoolean currentStatus = new BoxedBoolean(Boolean.TRUE);
            ExecutorService downloadExecutor = Executors.newFixedThreadPool(10);
            for (Champion champion : champs) {
                if (!currentStatus.getValue()) {
                    LOG_FRAGMENT.appendToSameLine(LoLin1Utils.getString(
                            getApplicationContext(), "update_fatal_error", null));
                    return Boolean.FALSE;
                }
                final String bustImageName = champion.getBustImageName(), passiveImageName =
                        champion.getPassiveImageName(), simplifiedName =
                        champion.getSimplifiedName();
                final String[] skinNames = champion.getSkinNames(), spellImageNames =
                        champion.getSpellImageNames();
                AsyncTask<Void, Void, Boolean> bustDownloadTask =
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                try {
                                    HTTPServices.downloadFile(
                                            finalCdn + pathSeparator + newVersion + pathSeparator +
                                                    "img" + pathSeparator +
                                                    LoLin1Utils
                                                            .getString(getApplicationContext(),
                                                                    "bust_remote_folder",
                                                                    null) + pathSeparator +
                                                    bustImageName,
                                            new File(bustString + bustImageName)
                                    );
                                }
                                catch (IOException e) {
                                    Log.wtf("debug", e.getClass().getName(), e);
                                    return Boolean.FALSE;
                                }
                                return Boolean.TRUE;
                            }

                            @Override
                            protected void onPostExecute(Boolean returnedBoolean) {
                                currentStatus.setValue(currentStatus.getValue() && returnedBoolean);
                            }
                        }, passiveDownloadTask =
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                try {
                                    HTTPServices.downloadFile(
                                            finalCdn + pathSeparator + newVersion + pathSeparator +
                                                    "img" + pathSeparator +
                                                    LoLin1Utils
                                                            .getString(getApplicationContext(),
                                                                    "passive_remote_folder",
                                                                    null) + pathSeparator +
                                                    passiveImageName,
                                            new File(passiveString + passiveImageName)
                                    );
                                }
                                catch (IOException e) {
                                    Log.wtf("debug", e.getClass().getName(), e);
                                    return Boolean.FALSE;
                                }
                                return Boolean.TRUE;
                            }

                            @Override
                            protected void onPostExecute(Boolean returnedBoolean) {
                                currentStatus.setValue(currentStatus.getValue() && returnedBoolean);
                            }
                        };
                for (final String spellName : spellImageNames) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            try {
                                HTTPServices.downloadFile(
                                        finalCdn + pathSeparator + newVersion + pathSeparator +
                                                "img" + pathSeparator +
                                                LoLin1Utils
                                                        .getString(getApplicationContext(),
                                                                "spell_remote_folder",
                                                                null) + pathSeparator + spellName,
                                        new File(spellString + spellName)
                                );
                            }
                            catch (IOException e) {
                                Log.wtf("debug", e.getClass().getName(), e);
                                return Boolean.FALSE;
                            }
                            return Boolean.TRUE;
                        }

                        @Override
                        protected void onPostExecute(Boolean returnedBoolean) {
                            currentStatus.setValue(currentStatus.getValue() && returnedBoolean);
                        }
                    }.executeOnExecutor(downloadExecutor);
                }
                for (int i = 0; i < skinNames.length; i++) {

                    new AsyncTask<Integer, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Integer... params) {
                            try {
                                HTTPServices.downloadFile(
                                        finalCdn + pathSeparator + pathSeparator + "img" +
                                                pathSeparator + "champion" + pathSeparator +
                                                LoLin1Utils
                                                        .getString(getApplicationContext(),
                                                                "splash_remote_folder",
                                                                null) + pathSeparator +
                                                simplifiedName + "_" +
                                                params[0] + "." + LoLin1Utils
                                                .getString(getApplicationContext(),
                                                        "splash_image_extension", null),
                                        new File(splashString + simplifiedName + "_" +
                                                params[0] + "." + LoLin1Utils
                                                .getString(getApplicationContext(),
                                                        "splash_image_extension", null))
                                );
                            }
                            catch (IOException e) {
                                Log.wtf("debug", e.getClass().getName(), e);
                                return Boolean.FALSE;
                            }
                            return Boolean.TRUE;
                        }

                        @Override
                        protected void onPostExecute(Boolean returnedBoolean) {
                            currentStatus.setValue(currentStatus.getValue() && returnedBoolean);
                        }
                    }.executeOnExecutor(downloadExecutor, i);
                }
                bustDownloadTask.executeOnExecutor(downloadExecutor);
                passiveDownloadTask.executeOnExecutor(downloadExecutor);
            }
            downloadExecutor.shutdown();
            try {
                downloadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            }
            catch (InterruptedException e) {
                Log.wtf("debug", e.getClass().getName(), e);
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils.getString(
                        getApplicationContext(), "update_fatal_error", null));
                return Boolean.FALSE;
            }
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));
        }

        return Boolean.TRUE;
    }

    private void proceedWithUpdate(String server) {
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
                        HTTPServices.performVersionRequest(server, realm));
            }
            catch (IOException | URISyntaxException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_fatal_error",
                                null));
                return;
            }
            catch (HTTPServices.ServerIsCheckingException e) {
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
                getContentInputStream = HTTPServices.performGetRequest(target);
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
            catch (HTTPServices.ServerIsCheckingException e) {
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

//FUTURE Remove <item name="news_reader" type="layout">@layout/activity_news_double_pane</item><item name="surr_reader" type="layout">@layout/activity_surr_double_pane</item><bool name="feed_has_two_panes">true</bool> from values-land/layouts.xml to not to see the double layout on devices which are not large enough