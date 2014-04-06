package org.jorge.lolin1.io.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jorge.lolin1.utils.LoLin1Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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
public abstract class CommunicationProvider {

    private static final String VERSION_SERVICE_LOCATION = "/services/champions/version/",
            LIST_SERVICE_LOCATION = "/services/champions/list/", CDN_SERVICE_LOCATION =
            "/services/champions/cdn/";

    public static void downloadFile(String whereToDownload, File whereToSaveIt) throws IOException {
        URL website = new URL(whereToDownload);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(whereToSaveIt);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
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
