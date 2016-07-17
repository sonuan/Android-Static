package com.androidstatic.lib.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.androidstatic.lib.http.params.HttpHeader;
import com.androidstatic.lib.http.params.HttpParams;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * MyApplication
 * Created by acer_april
 * on 2015/7/20
 * Description:customVolleyRequest
 */

public class CustomRequest<T> extends Request<T> {
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private HttpParams mHttpParams;
    private HttpHeader mHttpHeader;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url    URL of the request to make
     * @param clazz  Relevant class object, for Gson's reflection
     * @param params Map of request httpParams
     */
    public CustomRequest(String url, Class<T> clazz, HttpParams httpParams,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        mHttpHeader = null;
        mHttpParams = httpParams;
        this.listener = listener;
    }

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request httpHeader
     */
    public CustomRequest(int method, String url, Class<T> clazz, HttpHeader httpHeader,
                         HttpParams httpParams,
                         Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.mHttpHeader = httpHeader;
        this.mHttpParams = mHttpParams;
        this.listener = listener;
    }

    /**
     * @param builder requestBuilder
     */
    public CustomRequest(RequestBuilder builder) {
        super(builder.method, builder.url, builder.errorListener);
        clazz = builder.clazz;
        mHttpHeader = builder.httpHeader;
        listener = builder.successListener;
        mHttpParams = builder.httpParams;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHttpHeader != null && mHttpHeader.getHeaders() != null ? mHttpHeader.getHeaders() : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mHttpParams != null && mHttpParams.getParams() != null ? mHttpParams.getParams() : super.getParams();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if (clazz == null) {
                return (Response<T>) Response.success(parsed,
                        HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(gson.fromJson(parsed, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }

    }

    /**
     * requestBiulder  使用方法参见httpClientRequest
     */
    public static class RequestBuilder {
        private int method = Method.GET;
        private String url;
        private Class clazz;
        private Response.Listener successListener;
        private Response.ErrorListener errorListener;
        private HttpHeader httpHeader;
        private HttpParams httpParams;

        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder clazz(Class clazz) {
            this.clazz = clazz;
            return this;
        }

        public RequestBuilder successListener(Response.Listener successListener) {
            this.successListener = successListener;
            return this;
        }

        public RequestBuilder errorListener(Response.ErrorListener errorListener) {
            this.errorListener = errorListener;
            return this;
        }

        public RequestBuilder post() {
            this.method = Method.POST;
            return this;
        }

        public RequestBuilder method(int method) {
            this.method = method;
            return this;
        }

        public RequestBuilder addHeader(String key, String value) {
            if (httpHeader == null)
                httpHeader = new HttpHeader();
            httpHeader.addHeader(key, value);
            return this;
        }

        public RequestBuilder headers(HttpHeader header) {
            this.httpHeader = header;
            return this;
        }

        public RequestBuilder params(HttpParams httpParams) {
            post();
            this.httpParams = httpParams;
            return this;
        }

        public RequestBuilder addParams(String key, String value) {
            if (httpParams == null) {
                httpParams = new HttpParams();
                post();
            }
            httpParams.put(key, value);
            return this;
        }

        public RequestBuilder addMethodParams(String method) {
            if (httpParams == null) {
                httpParams = new HttpParams();
                post();
            }
//            httpParams.put("method", method);
            return this;
        }

        public CustomRequest build() {
            return new CustomRequest(this);
        }
    }
}
