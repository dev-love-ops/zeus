package com.wufeiqun.zeus.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务通用的异常, 业务逻辑中抛异常, 全局异常拦截器拦截并给到前端
 * @author 吴飞群
 * @createTime 2022/05/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException{
    private String code = "5000";
    private String message;

    public ServiceException(String message){
        this.message = message;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        //此异常作为业务异常,不需要记录trace, 如果需要打印, 注释该方法即可
        return this;
    }
}
