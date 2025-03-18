package com.wufeiqun.zeus.common.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CommonVo<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 7564775801965409910L;
    private static String SUCCESS_CODE = "0";
    private static String DEFAULT_ERROR_CODE = "5000";

    private boolean success = true;

    /**
     * 0成功 非0具体错误原因
     */
    private String code = "0";
    /**
     * 具体错误描述or成功描述
     */
    private String message = "success";
    /**
     * 存放业务数据
     */
    private T data;

    public static <T> CommonVo<T> success() {
        return new CommonVo<>();
    }

    public static <T> CommonVo<T> success(T data) {
        CommonVo<T> ret = new CommonVo<>();
        ret.setData(data);
        return ret;
    }

    public static <T> CommonVo<T> success(T data, String message) {
        CommonVo<T> ret = new CommonVo<>();
        ret.setData(data);
        ret.setMessage(message);
        return ret;
    }

    public static <T> CommonVo<T> error(String message) {
        CommonVo<T> ret = new CommonVo<>();
        ret.setSuccess(false);
        ret.setCode(DEFAULT_ERROR_CODE);
        ret.setMessage(message);
        return ret;
    }

    public static <T> CommonVo<T> error(String code, String message) {
        CommonVo<T> ret = new CommonVo<>();
        ret.setSuccess(false);
        ret.setCode(code);
        ret.setMessage(message);
        return ret;
    }

}
