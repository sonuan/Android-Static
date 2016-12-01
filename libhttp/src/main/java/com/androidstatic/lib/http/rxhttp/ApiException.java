package com.androidstatic.lib.http.rxhttp;

public class ApiException extends Exception {
    int code;
    public ApiException(int code,String s) {
        super(s);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}