package com.androidstatic.lib.http;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An {@link com.android.volley.toolbox.HttpStack HttpStack} implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {
    private final OkUrlFactory mFactory;

    public OkHttpStack() {
        this(new OkUrlFactory(new OkHttpClient()));
    }

    public OkHttpStack(OkUrlFactory okUrlFactory) {
        if (okUrlFactory == null) {
            throw new NullPointerException("Client must not be null.");
        }
//        okUrlFactory.client().setSslSocketFactory(NukeSSLCerts.getSSLSocketFactory());
//        okUrlFactory.client().setHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        });
        mFactory = okUrlFactory;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return mFactory.open(url);
    }
}
