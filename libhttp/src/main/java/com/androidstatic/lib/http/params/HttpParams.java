package com.androidstatic.lib.http.params;


import com.android.volley.Request;
import com.androidstatic.lib.utils.L;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网络请求参数
 * <p/>
 * 提供网络请求的参数赋值，如请求地址、请求方式、数据编码格式、请求参数等。
 *
 * @author kycq
 */
public class HttpParams {
    private static final String TAG = "com.kycq.library.http.HttpParams";

    /**
     * 请求地址
     */
    private String mHttpUrl;
    /**
     * 请求方式
     */
    private int mHttpMethod = HttpMethod.POST;

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * 数据编码格式
     */
    private String ENCODE = DEFAULT_PARAMS_ENCODING;
    /**
     * 请求参数
     */
    protected ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
    /**
     * 文件下载地址
     */
    private File mDownloadFile;

    /**
     * 构造方法
     */
    public HttpParams() {
    }

    /**
     * 构造方法
     *
     * @param httpUrl 请求地址
     */
    public HttpParams(String httpUrl) {
        mHttpUrl = httpUrl;
    }

    /**
     * 构造方法
     *
     * @param httpUrl    请求地址
     * @param httpMethod 请求方式
     */
    public HttpParams(String httpUrl, int httpMethod) {
        mHttpUrl = httpUrl;
        mHttpMethod = httpMethod;
    }

    /**
     * 设置请求地址
     *
     * @param httpUrl 请求地址
     * @return HttpParams实例
     */
    public HttpParams setHttpUrl(String httpUrl) {
        mHttpUrl = httpUrl;
        return this;
    }

    /**
     * 获取请求地址
     *
     * @return HttpParams实例
     */
    public String getHttpUrl() {
        return mHttpUrl;
    }

    /**
     * 获取最终请求地址(GET方式请求包含请求参数)
     *
     * @return 请求地址
     */
    public String getConnectUrl() {
        L.i(TAG, "httpParams # httpUrl = " + mHttpUrl);
        L.i(TAG, "httpParams # encode = " + ENCODE);
        L.i(TAG, "httpParams # method = " + mHttpMethod);
        if (mHttpMethod == HttpMethod.DOWNLOAD) {
            L.i(TAG, "httpParams # downloadFile = " + mDownloadFile);
        }

        if (mHttpMethod == HttpMethod.GET || mHttpMethod == HttpMethod.DOWNLOAD) {
            String params = getParamString();
            if (params != null) {
                return mHttpUrl + "?" + params;
            }
        }
        return mHttpUrl;
    }

    /**
     * 设置请求方式
     *
     * @param httpMethod 请求方式
     * @return HttpParams实例
     */
    public HttpParams setHttpMethod(int httpMethod) {
        mHttpMethod = httpMethod;
        return this;
    }

    /**
     * 获取请求方式
     *
     * @return 请求方式
     */
    public int getHttpMethod() {
        return mHttpMethod;
    }

    /**
     * 设置数据编码格式
     *
     * @param encode 编码格式
     * @return HttpParams实例
     */
    public HttpParams setEncode(String encode) {
        ENCODE = encode;
        return this;
    }

    /**
     * 获取数据编码格式
     *
     * @return 编码格式
     */
    public String getEncode() {
        return ENCODE;
    }

    /**
     * 设置下载文件存储路径(请求方式为DOWNLOAD该字段有效)
     *
     * @param downloadFile 文件存储路径
     * @return HttpParams实例
     */
    public HttpParams setDownloadFile(File downloadFile) {
        mDownloadFile = downloadFile;
        return this;
    }

    /**
     * 获取下载文件存储路径
     *
     * @return 文件存储路径
     */
    public File getDownloadFile() {
        return mDownloadFile;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, int value) {
        urlParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, boolean value) {
        urlParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, float value) {
        urlParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, double value) {
        urlParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, long value) {
        urlParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * 添加网络请求参数
     *
     * @param key   键
     * @param value 值
     * @return HttpParams实例
     */
    public HttpParams put(String key, String value) {
        if (value != null) {
            urlParams.put(key, value);
        }
        return this;
    }


    /**
     * 获取网络请求参数
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return urlParams.get(key);
    }

    /**
     * 删除网络请求参数
     *
     * @param key 键
     * @return HttpParams实例
     */
    public HttpParams remove(String key) {
        urlParams.remove(key);
        return this;
    }


    /**
     * 获取网络请求参数
     *
     * @return 网络请求参数
     */
    private String getParamString() {
        Map<String, String> params = getParams();
        String urlParams = encodeParameters(params, ENCODE);
        L.i(TAG, "httpParams # urlParams = " + urlParams);
        return urlParams;
    }

    public Map<String, String> getParams() {
        return urlParams;
    }

    private String encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            //			return encodedParams.toString().getBytes(paramsEncoding);
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * 网络请求方法
     *
     * @author kycq
     */
    public interface HttpMethod extends Request.Method {
        int UPLOAD = 11;
        int DOWNLOAD = 12;
    }
}
