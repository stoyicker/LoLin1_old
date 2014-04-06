package org.jorge.lolin1.io.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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
public abstract class HTTPServicesProvider {

    private static final String VERSION_SERVICE_LOCATION = "/services/champions/version/",
            LIST_SERVICE_LOCATION = "/services/champions/list/", CDN_SERVICE_LOCATION =
            "/services/champions/cdn/";

    public static void downloadFile(String whatToDownload, File whereToSaveIt) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new URL(whatToDownload).openStream());
            fileOutputStream = new FileOutputStream(whereToSaveIt);

            final byte data[] = new byte[1024];
            int count;
            while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, count);
            }
        }
        finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    public static InputStream performGetRequest(String uri)
            throws IOException, URISyntaxException, ServerIsCheckingException {
        HttpResponse response;

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(uri));
        response = client.execute(request);
        if (response.getStatusLine().getStatusCode() == 409) {
            throw new ServerIsCheckingException();
        }
        else {
            return response.getEntity().getContent();
        }
    }

    public static InputStream performVersionRequest(String serverUri, String realm)
            throws IOException, URISyntaxException, ServerIsCheckingException {
        return performGetRequest(serverUri + VERSION_SERVICE_LOCATION + realm.toLowerCase());
    }

    public static InputStream performListRequest(String serverUri, String realm, String locale)
            throws ServerIsCheckingException, IOException, URISyntaxException {
        return performGetRequest(
                serverUri + LIST_SERVICE_LOCATION + realm.toLowerCase() + "/" + locale);
    }

    public static String performCdnRequest(String serverUri, String realm)
            throws ServerIsCheckingException, IOException, URISyntaxException {
        return LoLin1Utils.inputStreamAsString(performGetRequest(
                serverUri + CDN_SERVICE_LOCATION + realm.toLowerCase()));
    }

    public static class ServerIsCheckingException extends Exception {
    }
}
