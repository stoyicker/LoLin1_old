package org.jorge.lolin1.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.crashlytics.android.Crashlytics;

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
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

import static org.jorge.lolin1.utils.LoLin1DebugUtils.logString;

public final class SplashActivity extends Activity {

    private static final long SPLASH_MIN_DURATION_CONNECTED_MILLIS = 2500, SPLASH_MIN_DURATION_DISCONNECTED_MILLIS = 2000;
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
        if (LoLin1Utils.isInternetReachable(getApplicationContext())) {
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
                    final CountDownLatch countDownLatch = new CountDownLatch(2);

                    Runnable workerThread = new Runnable() {
                        @Override
                        public void run() {
                            runUpdate();
                            countDownLatch.countDown();
                        }
                    };

                    Runnable delayThread = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(SPLASH_MIN_DURATION_CONNECTED_MILLIS);
                            } catch (InterruptedException e) {
                                Crashlytics.logException(e);
                            }
                            countDownLatch.countDown();
                        }
                    };

                    workerThread.run();
                    delayThread.run();

                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        Crashlytics.logException(e);
                    }

                    return null;
                }
            }.execute();
        } else {
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "no_connection_on_splash",
                            null));
            ChampionManager.getInstance()
                    .setChampions(SplashActivity.this.getApplicationContext());
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(SPLASH_MIN_DURATION_DISCONNECTED_MILLIS);
                    } catch (InterruptedException e) {
                        Crashlytics.logException(e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    launchNewsReader();
                }
            }.executeOnExecutor(Executors.newSingleThreadExecutor());
        }
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
        } else {
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "no_connection_on_splash",
                            null));
        }
    }

    private boolean isOnMobileConnection() {
        final CountDownLatch alertDialogLatch = new CountDownLatch(1);
        Boolean ret;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo;
        if (connectivityManager != null) {
            mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetworkInfo == null || mobileNetworkInfo.isConnectedOrConnecting()) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(),
                                "update_delayed", null)
                );
                ret = Boolean.TRUE;
            } else ret = Boolean.FALSE;
        } else {
            LOG_FRAGMENT.appendToSameLine(
                    LoLin1Utils.getString(getApplicationContext(),
                            "update_delayed", null)
            );
            ret = Boolean.TRUE;
        }


        alertDialogLatch.countDown();
        return ret;
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
                .apply();
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
        logString("debug", "Pre-update operations finished");
        for (String locale : localesInThisRealm) {
            Log.d("init", "Locale issued: " + locale + " - Locale selected: " + LoLin1Utils.getLocale(getApplicationContext()));
            if (!locale.toLowerCase().contentEquals(LoLin1Utils.getLocale(getApplicationContext()).toLowerCase()))
                continue;
            logString("debug", "Updating locale " + locale);
            assert root != null;
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
            logString("debug", "Directories allocated");
            LOG_FRAGMENT.appendToNewLine(
                    LoLin1Utils.getString(getApplicationContext(), "list_download", null) + " " +
                            realm + "." + locale + LoLin1Utils
                            .getString(getApplicationContext(), "progress_character", null)
            );
            InputStream dataStream;
            String dataStreamAsString;
            try {
                logString("debug", "Request initialized");
                dataStream = HTTPServices.performListRequest(server, realm, locale);
                logString("debug", "Request finished");
                dataStreamAsString = LoLin1Utils.inputStreamAsString(dataStream, locale);
                if (!JsonManager.getResponseStatus(dataStreamAsString)) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                    null)
                    );
                    Crashlytics.log(Log.ERROR, "debug", "Response status was not ok");
                    return Boolean.FALSE;
                }
            } catch (IOException | URISyntaxException | HTTPServices.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                Crashlytics.logException(e);
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
                        .writeStringToFile(dataStreamAsString, dataFile, locale);
                logString("debug", "Data file written");
            } catch (IOException e) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                Crashlytics.logException(e);
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
                    String cdnResponse = HTTPServices.performCdnRequest(server, realm, locale);
                    if (JsonManager.getResponseStatus(cdnResponse)) {
                        cdn = JsonManager.getStringAttribute(cdnResponse,
                                LoLin1Utils.getString(getApplicationContext(), "cdn_key", null));
                    } else {
                        LOG_FRAGMENT.appendToSameLine(
                                LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                        null)
                        );
                        Crashlytics.log(Log.ERROR, "debug", "Response status was not ok");
                        return Boolean.FALSE;
                    }
                } catch (HTTPServices.ServerIsCheckingException | URISyntaxException | IOException e) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(), "update_fatal_error",
                                    null)
                    );
                    Crashlytics.logException(e);
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
            logString("debug", "CDN checks finished");
            if (champs.isEmpty()) {
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
                Crashlytics.log(Log.ERROR, "debug", "No champions found");
                return Boolean.FALSE;
            }
            final String finalCdn = cdn;
            final BoxedBoolean currentStatus = new BoxedBoolean(Boolean.TRUE);
            ExecutorService downloadExecutor = Executors.newFixedThreadPool(10);
            for (Champion champion : champs) {
                if (!currentStatus.getValue()) {
                    LOG_FRAGMENT.appendToSameLine(LoLin1Utils.getString(
                            getApplicationContext(), "update_fatal_error", null));
                    Crashlytics.log(Log.ERROR, "debug",
                            "An error happened on a content download AsyncTask");
                    return Boolean.FALSE;
                }
                final String bustImageName = champion.getBustImageName(), passiveImageName =
                        champion.getPassive().getImageName(), simplifiedName =
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
                                } catch (IOException e) {
                                    Crashlytics.logException(e);
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
                                } catch (IOException e) {
                                    Crashlytics.logException(e);
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
                            } catch (IOException e) {
                                Crashlytics.logException(e);
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
                                        finalCdn + pathSeparator + "img" +
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
                            } catch (IOException e) {
                                Crashlytics.logException(e);
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
            } catch (InterruptedException e) {
                Crashlytics.logException(e);
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils.getString(
                        getApplicationContext(), "update_fatal_error", null));
                Crashlytics.logException(e);
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
        if (isOnMobileConnection()) {
            return;
        }
        for (String realm : realms) {
            Log.d("init", "Realm issued: " + realm + " - Realm selected: " + LoLin1Utils.getRealm(getApplicationContext()));
            if (!LoLin1Utils.getRealm(getApplicationContext()).toLowerCase().contentEquals(realm.toLowerCase())) {
                continue;
            }
            LOG_FRAGMENT.appendToNewLine(LoLin1Utils
                    .getString(getApplicationContext(), "update_pre_version_check", null) +
                    " " + realm.toLowerCase(Locale.ENGLISH) +
                    LoLin1Utils.getString(getApplicationContext(), "progress_character", null));
            String newVersion;
            JSONObject newVersionAsJSON;
            try {
                newVersion = LoLin1Utils.inputStreamAsString(
                        HTTPServices.performVersionRequest(server, realm, "en_US"), "en_US");
            } catch (IOException | URISyntaxException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_fatal_error",
                                null));
                Crashlytics.logException(e);
                return;
            } catch (HTTPServices.ServerIsCheckingException e) {
                LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                        .getString(getApplicationContext(), "update_server_is_updating",
                                null));
                Crashlytics.logException(e);
                return;
            }
            try {
                newVersionAsJSON = new JSONObject(newVersion);
                newVersion = newVersionAsJSON.getString("version");
            } catch (JSONException e) {
                Log.e("debug", e.getClass().getName(), e);
            }
            try {
                if (Integer.parseInt(newVersion.replaceAll("[\\D]", "")) > Integer.parseInt(
                        preferences.getString("pref_version_" + realm, "0").replaceAll("[\\D]", ""))) {
                    LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                            .getString(getApplicationContext(), "update_new_version_found", null));
                    String[] localesInThisRealm =
                            LoLin1Utils.getStringArray(getApplicationContext(), LoLin1Utils.getString(
                                    getApplicationContext(), "realm_to_language_list_prefix",
                                    null) + realm.toLowerCase(Locale.ENGLISH) +
                                    LoLin1Utils.getString(getApplicationContext(),
                                            "language_to_simplified_suffix", null), null);
                    if (runInitProcedure(server, realm, localesInThisRealm, newVersion)) {
                        performPostUpdateOperations(realm, newVersion);
                    }
                } else {
                    LOG_FRAGMENT.appendToSameLine(LoLin1Utils
                            .getString(getApplicationContext(), "update_no_new_version", null));
                }
            } catch (NumberFormatException ex) {
                Crashlytics.log(1, "newVersion was " + newVersion, "newVersion is not a number but it should");
                Crashlytics.logException(ex);
                LOG_FRAGMENT.appendToSameLine(
                        LoLin1Utils.getString(getApplicationContext(), "update_fatal_error", null));
            }
            networkOperationsLatch.countDown();
        }

        try {
            networkOperationsLatch.await();
        } catch (InterruptedException e) {
            Crashlytics.logException(e);
        }
    }

    private String connectToOneOf(String[] dataProviders) {
        int firstIndex = new Random().nextInt(dataProviders.length), index = firstIndex;
        Boolean upServerFound = Boolean.FALSE;
        String target = "null";
        InputStream getContentInputStream;
        LOG_FRAGMENT.appendToSameLine(
                LoLin1Utils.getString(getApplicationContext(), "finding_server", null) +
                        LoLin1Utils.getString(getApplicationContext(), "progress_character", null)
        );
        do {
            try {
                target = dataProviders[index];
                logString("debug", "Testing " + target);
                getContentInputStream = HTTPServices.performVersionRequest(target, "euw", "en_US");
                logString("debug", "Tested " + target);
                String content = LoLin1Utils
                        .inputStreamAsString(getContentInputStream, "en_US");
                if (!content.contains(LoLin1Utils.getString(getApplicationContext(),
                        "provider_application_error_identifier", null)) &&
                        !content.contains(
                                LoLin1Utils.getString(getApplicationContext(),
                                        "provider_application_maintenance_identifier", null)
                        )) {
                    upServerFound = Boolean.TRUE;
                }
            } catch (IOException | URISyntaxException e) {
                Crashlytics.logException(e);
            } catch (HTTPServices.ServerIsCheckingException e) {
                //Server is busy checking for updates, so look for a new one
            }
            if (!upServerFound) {
                index++;
                if (index >= dataProviders.length) {
                    index = 0;
                }
                if (index == firstIndex) {
                    LOG_FRAGMENT.appendToSameLine(
                            LoLin1Utils.getString(getApplicationContext(),
                                    "no_providers_up", null)
                    );
                    Crashlytics.log(Log.ERROR, "debug", "No available data servers found");
                    return "null";
                }
            }
        }
        while (!upServerFound);

        LOG_FRAGMENT.appendToSameLine(
                LoLin1Utils.getString(getApplicationContext(), "update_task_finished", null));

        logString("debug", "Provider found: " + target);

        return target;
    }

    private void launchNewsReader() {
        final Intent newsIntent = new Intent(getApplicationContext(), NewsReaderActivity.class);
        DrawerLayoutFragmentActivity.clearNavigation();
        finish();
        startActivity(newsIntent);
    }
}