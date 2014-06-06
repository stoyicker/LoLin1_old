/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * An SSL socket factory that will let any certifacte past, even if it's expired or
 * not singed by a root CA.
 * <p/>
 * This and all the files in the module have been developed by Bert De Geyter (https://github.com/TheHolyWaffle) and are protected by the Apache GPLv3 license.
 */
public class DummySSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory factory;

    public DummySSLSocketFactory() {

        try {
            SSLContext sslcontent = SSLContext.getInstance("TLS");
            sslcontent.init(null, // KeyManager not required
                    new TrustManager[]{new DummyTrustManager()},
                    new java.security.SecureRandom());
            factory = sslcontent.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            Log.wtf("debug", e);
        } catch (KeyManagementException e) {
            Log.wtf("debug", e);
        }
    }

    public static SocketFactory getDefault() {
        return new DummySSLSocketFactory();
    }

    public Socket createSocket(Socket socket, String s, int i, boolean flag)
            throws IOException {
        AsyncTask<Object, Void, Object> socketCreationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Object ret;
                try {
                    ret = factory.createSocket((Socket) params[0], (String) params[1],
                            (int) params[2], (boolean) params[3]);
                } catch (Exception e) {
                    Log.wtf("debug", e);
                    ret = e;
                }
                return ret;
            }
        };
//It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService socketCreationExecutor = Executors.newFixedThreadPool(1);
        socketCreationTask.executeOnExecutor(socketCreationExecutor, socket, s, i, flag);
        Object returned;
        try {
            returned = socketCreationTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.wtf("debug", e);
            throw new IOException();//Provoke a fail
        }

        if (returned instanceof Exception) {
            throw (IOException) returned;
        } else {
            return (Socket) returned;
        }
    }

    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr2, int j)
            throws IOException {
        AsyncTask<Object, Void, Object> socketCreationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Object ret;
                try {
                    ret = factory.createSocket((InetAddress) params[0], (int) params[1],
                            (InetAddress) params[2], (int) params[3]);
                } catch (Exception e) {
                    Log.wtf("debug", e);
                    ret = e;
                }
                return ret;
            }
        };
//It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService socketCreationExecutor = Executors.newFixedThreadPool(1);
        socketCreationTask.executeOnExecutor(socketCreationExecutor, inaddr, i, inaddr2, j);
        Object returned;
        try {
            returned = socketCreationTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.wtf("debug", e);
            throw new IOException();//Provoke a fail
        }

        if (returned instanceof Exception) {
            throw (IOException) returned;
        } else {
            return (Socket) returned;
        }
    }

    public Socket createSocket(InetAddress inaddr, int i) throws IOException {
        AsyncTask<Object, Void, Object> socketCreationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Object ret;
                try {
                    ret = factory.createSocket((InetAddress) params[0], (int) params[1]);
                } catch (Exception e) {
                    Log.wtf("debug", e);
                    ret = e;
                }
                return ret;
            }
        };
//It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService socketCreationExecutor = Executors.newFixedThreadPool(1);
        socketCreationTask.executeOnExecutor(socketCreationExecutor, inaddr, i);
        Object returned;
        try {
            returned = socketCreationTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.wtf("debug", e);
            throw new IOException();//Provoke a fail
        }

        if (returned instanceof Exception) {
            throw (IOException) returned;
        } else {
            return (Socket) returned;
        }
    }

    public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
        AsyncTask<Object, Void, Object> socketCreationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Object ret;
                try {
                    ret = factory.createSocket((String) params[0], (int) params[1],
                            (InetAddress) params[2], (int) params[3]);
                } catch (Exception e) {
                    Log.wtf("debug", e);
                    ret = e;
                }
                return ret;
            }
        };
//It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService socketCreationExecutor = Executors.newFixedThreadPool(1);
        socketCreationTask.executeOnExecutor(socketCreationExecutor, s, i, inaddr, j);
        Object returned;
        try {
            returned = socketCreationTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.wtf("debug", e);
            throw new IOException();//Provoke a fail
        }

        if (returned instanceof Exception) {
            throw (IOException) returned;
        } else {
            return (Socket) returned;
        }
    }

    public Socket createSocket(String s, int i) throws IOException {
        AsyncTask<Object, Void, Object> socketCreationTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Object ret;
                try {
                    ret = factory.createSocket((String) params[0], (int) params[1]);
                } catch (Exception e) {
                    Log.wtf("debug", e);
                    ret = e;
                }
                return ret;
            }
        };
//It's necessary to run the task in an executor because the main one is already full and if we add this one a livelock will occur
        ExecutorService socketCreationExecutor = Executors.newFixedThreadPool(1);
        socketCreationTask.executeOnExecutor(socketCreationExecutor, s, i);
        Object returned;
        try {
            returned = socketCreationTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.wtf("debug", e);
            throw new IOException();//Provoke a fail
        }

        if (returned instanceof Exception) {
            throw (IOException) returned;
        } else {
            return (Socket) returned;
        }
    }

    public String[] getDefaultCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }
}

/**
 * Trust manager which accepts certificates without any validation
 * except date validation.
 */
class DummyTrustManager implements X509TrustManager {

    public boolean isClientTrusted(X509Certificate[] cert) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] cert) {
        try {
            cert[0].checkValidity();
            return true;
        } catch (CertificateExpiredException e) {
            return false;
        } catch (CertificateNotYetValidException e) {
            return false;
        }
    }

    public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        // Do nothing for now.
    }

    public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        // Do nothing for now.
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
