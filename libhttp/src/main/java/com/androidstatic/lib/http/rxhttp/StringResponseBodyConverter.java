package com.androidstatic.lib.http.rxhttp;

import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class StringResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    public static final String TAG = "ResponseBodyConverter";

    @Override
    public T convert(ResponseBody value) throws IOException {
        //InputStream is = value.byteStream();
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //int i = -1;
        //while ((i = is.read()) != -1) {
        //    baos.write(i);
        //}
        //String response = baos.toString();
        //return (T) response;
        try {
            T t = (T) value.string();
            Log.i(TAG, "convert: " + t.getClass());
            return t;
        }finally {
            value.close();
        }
    }
}

