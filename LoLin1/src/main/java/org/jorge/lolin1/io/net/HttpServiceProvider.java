package org.jorge.lolin1.io.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

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
public abstract class HttpServiceProvider {

    private static final String VERSION_SERVICE_LOCATION = "/services/champions/version/";

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

    public static class ServerIsCheckingException extends Exception {
    }
}
