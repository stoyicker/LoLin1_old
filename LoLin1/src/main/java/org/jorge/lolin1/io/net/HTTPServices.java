package org.jorge.lolin1.io.net;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * Created by JorgeAntonio on 24/03/2014.
 */
public abstract class HTTPServices {

    private static final String VERSION_SERVICE_LOCATION = "/services/champions/version",
            LIST_SERVICE_LOCATION = "/services/champions/list", CDN_SERVICE_LOCATION =
            "/services/champions/cdn";
    private static final ExecutorService fileDownloadExecutor = Executors.newFixedThreadPool(5);

    public static void downloadFile(final String whatToDownload, final File whereToSaveIt)
            throws IOException {
        Log.d("debug", "Downloading url " + whatToDownload);
        AsyncTask<Void, Void, Object> imageDownloadAsyncTask = new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                BufferedInputStream bufferedInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    Log.d("debug", "Opening stream for " + whatToDownload);
                    bufferedInputStream = new BufferedInputStream(
                            new URL(URLDecoder.decode(whatToDownload, "UTF-8")
                                    .replaceAll(" ", "%20"))
                                    .openStream()
                    );
                    Log.d("debug", "Opened stream for " + whatToDownload);
                    fileOutputStream = new FileOutputStream(whereToSaveIt);

                    final byte data[] = new byte[1024];
                    int count;
                    Log.d("debug", "Loop-writing " + whatToDownload);
                    while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
                        fileOutputStream.write(data, 0, count);
                    }
                    Log.d("debug", "Loop-written " + whatToDownload);
                } catch (IOException e) {
                    return e;
                } finally {
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e) {
                            return e;
                        }
                    }
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            return e;
                        }
                    }
                }
                return null;
            }
        };
        imageDownloadAsyncTask.executeOnExecutor(fileDownloadExecutor);
        Object returned = null;
        try {
            returned = imageDownloadAsyncTask.get();
        } catch (ExecutionException | InterruptedException e) {
            Crashlytics.logException(e);
        }
        if (returned != null) {
            throw (IOException) returned;
        }
    }

    public static InputStream performGetRequest(String uri, String locale)
            throws IOException, URISyntaxException, ServerIsCheckingException {
        HttpResponse response;

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, LoLin1Utils.getLocaleCharset(locale).name());
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(uri));
        response = client.execute(request);
        if (response.getStatusLine().getStatusCode() == 409) {
            throw new ServerIsCheckingException();
        } else {
            return response.getEntity().getContent();
        }
    }

    public static InputStream performVersionRequest(String serverUri, String realm, String locale)
            throws IOException, URISyntaxException, ServerIsCheckingException {
        return performGetRequest(
                serverUri + VERSION_SERVICE_LOCATION + "?realm=" + realm.toLowerCase(), locale);
    }

    public static InputStream performListRequest(String serverUri, String realm, String locale)
            throws ServerIsCheckingException, IOException, URISyntaxException {
        return performGetRequest(
                serverUri + LIST_SERVICE_LOCATION + "?realm=" + realm.toLowerCase() + "&locale=" +
                        locale, locale
        );
    }

    public static String performCdnRequest(String serverUri, String realm, String locale)
            throws ServerIsCheckingException, IOException, URISyntaxException {
        return LoLin1Utils.inputStreamAsString(performGetRequest(
                        serverUri + CDN_SERVICE_LOCATION + "?realm=" + realm.toLowerCase(), locale),
                locale
        );
    }

    public static class ServerIsCheckingException extends Exception {
    }
}
