package com.wufeiqun.zeus.common.exception;


/**
 * defender.support.request
 * Created by skeeter on 2020/3/2.
 */
public class HttpRequestException extends RuntimeException{
    private String code = "5000";
    private String message;
    public HttpRequestException(int code, String message) {
        this.message = message;
    }
}
